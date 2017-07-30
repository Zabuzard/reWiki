#!/usr/bin/perl

use strict;
use LWP::UserAgent;
use URI::Escape;
use HTTP::Request;
use constant CUnknown => "?";

#Zur Verwendung in http://www.fwwiki.de/index.php/Orte_%28Liste%29
#Dieses Skript geht alle Unterkategorien in der Kategorie:Orte im FreewarWiki durch und sucht dort nach Ortsartikeln.
#Da es nur die Unterkategorien durchsucht, findet es keine Ortsartikel welche z.B. nur der Kategorie:Orte hinzugefügt
#sind, aber keiner weiteren Unterkategorie, wie z.B. Bombenkrater.
#Die Ausgabe erfolgt über print und kann weitergeleitet werden.

my $ua = LWP::UserAgent->new();
my @locationList;
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $date = sprintf("%d.%d.%04d", $mday, $mon+1, $year+1900);
my $host = "http://www.fwwiki.de";
my $url = $host . "/index.php/Kategorie:Orte";
my $intro = "<!-- ACHTUNG: Diese Seite wird von einem Bot aktualisiert. Wenn Du Veraenderungen am Aufbau dieser Seite vornimmst, hinterlasse bitte eine Nachricht auf der Diskussionsseite, sonst werden die Aenderungen vom Bot ueberschrieben. -->Diese Seite listet, nach [[Gebiet]]en geordnet, alle im Wiki eingetragenen [[Ort]]e mit deren Koordinatenangaben. (Stand $date)\n";
my $preLocation = "<!--\n-->{{Ueberschriftensimulation 2|1={{Gebietslink|";
my $endLocation = "}}}}";
my $prePlace = "[[";
my $preX = "]]: ";
my $preY = ",";
my $separator = ";";
my $end = "[[Kategorie:Orte|!Orte (Liste)]]";

my $request = HTTP::Request->new("GET", $url);
my $response = $ua->simple_request($request);
my $c = $response->content();


print "$intro";


#Hole alle Links welche (Orte) im Namen enthalten
while($c =~ /<a([^>]*)>([^<]*\(Orte\))<\/a>/gm) {
  #$anchor enthält alle Attribute des a-Tags, $text enthält den Inhalt zwischen dem a-Tag
  my ($anchor, $text) = ($1, $2);
  my $href;
  #$href enthält nun den Link des Ankers
  $href = $1 if ($anchor =~ /href\s*=\s*"([^"]*)"/);
  $href =~ s/\N{U+0026}amp;/&/g;
  my $title;
  #$title enthält den Wert des title-Attributes
  $title = $1 if ($anchor =~ /title\s*=\s*"([^"]*)"/);
  #Brich ab wenn $href nicht ermittelt werden konnte
  next if ($href eq "");
  #Gehe in die Unterkategorien welche die Ortsartikel zu den Gebieten enthalten
  getUrl($text, $host.$href) if ($title eq "Kategorie:" . $text);
}


print "$end";


#Entfernt Leerzeichen vor und hinter dem String
sub trim($) {
    my $string = shift;
    $string =~ s/^\s+//;
    $string =~ s/\s+$//;
    return $string;
}


#Sucht die Ortsartikel in den Unterkategorien
sub getUrl {
  #$location beinhaltet den Gebietsnamen
  my ($location, $href) = @_;
  $location =~ s/\(Orte\)//;
  $location = trim($location);
  #@filter beinhaltet Strings welche nicht im $title des vermeintlichen Ortsartikels enthalten sein dürfen
  my @filter = ("", "Gebiet", $location);
  #@excFilter beinhaltet alle Ausnahmen, welche durch die Filter als kein Ortsartikel markiert wurden aber doch welche sein sollen
  my @excFilter = ("Dummyplace");
  my $request = HTTP::Request->new("GET", $href);
  my $response = $ua->simple_request($request);
  my $c = $response->content();
  #Hole alle Links aus der Unterkategorie
  while($c =~ /<li><a([^>]*)>([^<]*)<\/a>/gm) {
    my ($anchor, $text) = ($1, $2);
    my $href;
    $href = $1 if ($anchor =~ /href\s*=\s*"([^"]*)"/);
    $href =~ s/\N{U+0026}amp;/&/g;
    my $title;
    $title = $1 if ($anchor =~ /title\s*=\s*"([^"]*)"/);
    #$flag gibt Auskunft ob ein Link ein Ortsartikel ist, 1 für ja
    my $flag = 1;
     if (($title eq $text)) {
      #Gehe den Filter durch, bei einem Treffer ist der Link kein Ortsartikel
      foreach(@filter) {
        if($title eq $_) {
          $flag = 0;
          #Gehe den Ausnahme Filter durch, bei einem Treffer ist der Link doch ein Ortsartikel
          foreach(@excFilter) {
            if($title eq $_) {
              $flag = 1;
            }
          }
        }
      }
      #Prüfe nochmals auf verschiedene Zeichenketten in $title
      if(($href =~ /FreewarWiki:/gm) or ($href =~ /Felder:/gm)) {
        $flag = 0;
      }
      #Wenn der Link ein Ortsartikel ist, hole seine Daten
      if($flag eq 1) {
        registerLocation($text, $host.$href, $location);
      }
     }
  }
}


#Holt Daten aus einem Ortsartikel
sub registerLocation {
  my ($text, $href, $location) = @_;
  my $request = HTTP::Request->new("GET", $href."?action=edit");
  my $response = $ua->simple_request($request);
  my $c = $response->content();
  $c =~ tr/\n/ /;

  my $x = CUnknown;
  my $y = CUnknown;


  #Suche die X-Koordinate aus dem Edit-Fenster
  if ($c =~ /\|\s*X\s*=\s*([\-0-9.]+)/i) {
    $x = $1;
    $x =~ s/\.//g;
  }
  
  #Suche die Y-Koordinate aus dem Edit-Fenster
  if ($c =~ /\|\s*Y\s*=\s*([\-0-9.]+)/i) {
    $y = $1;
    $y =~ s/\.//g;
  }
  # Nur ausgeben, wenn mindestens eine Eigenschaft erkannt wurde
  if (($x ne CUnknown) || $y ne CUnknown) {
  #$flag bestimmt ob ein Gebiet noch nicht aufgetreten ist, 1 für ja
  my $flag = 1;
  #Gehe alle bekannten Gebiete durch und setze ggf. $flag
  foreach(@locationList) {
    if($location eq $_) {
      $flag = 0;
    }
  }
  #Ist ein Gebiet noch nicht aufgetreten, drucke es
  if($flag eq 1) {
    print "$preLocation$location$endLocation";
    push(@locationList, $location);
  }
  #Drucke alle Daten des Ortes
  print "$prePlace$text$preX$x$preY$y$separator";
  }
}