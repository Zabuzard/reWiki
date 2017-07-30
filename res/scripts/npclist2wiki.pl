#!/usr/bin/perl
#inout: npclist
use constant CUnknown => "?";
use POSIX;
setlocale(LC_NUMERIC, "de_DE");


($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = localtime(time);
my $myTimestamp = sprintf("%.2d.%.2d.%d %.2d:%.2d", $mday, $mon+1, $year+1900, $hour, $min);

print "{{Vorlage:BotUpdate|Datum=". $myTimestamp ."|Skripte=[[FreewarWiki:Bot/Skripts/npclist2wiki.pl]]}}

Die Buttons neben den \N{U+0026}Uuml;berschriften k\N{U+0026}ouml;nnen zum Sortieren angeklickt werden. Da die Liste sehr lang ist, kann dies allerdings einige Sekunden dauern.

{| class=\"sortable wikitable\" style=\"text-align:right;\"
 |- style=\"text-align:center;\"
 ! Name || data-sort-type=\"number\" | Angr. || data-sort-type=\"number\" | LP || data-sort-type=\"number\" | XP || data-sort-type=\"number\" | Gold || data-sort-type=\"number\" | XP/LP || data-sort-type=\"number\" | Gold/LP || data-sort-type=\"number\" | Gold/XP\n";


sub GetQuotient {
  my ($dividend, $divisor, $format) = @_;

  if (($dividend eq CUnknown) || ($divisor eq CUnknown)) { return CUnknown; }
  if ($divisor == 0) {return "-"; }
  return sprintf($format, $dividend / $divisor);
}

while(<>)
{
    next if (/^GET/);
    
    my ($name, $atk, $lp, $xp, $gm, $vk, $items, $bild, $autor, $unangreifbar, $typ) = split(/;/);

    my @css_classes = ($typ, ($unangreifbar) ? "unangreifbar" : "angreifbar");
    chomp(@css_classes);

    printf " |-\n | style=\"text-align:left;\" class=\"%s\" | [[%s]] || %s || %s || %s || %s || %s || %s || %s\n", join(" ", @css_classes), $name, $atk, $lp, $xp, $gm, GetQuotient($xp, $lp, "%.2f"), GetQuotient($gm, $lp, "%.2f"), GetQuotient($gm, $xp, "%.0f");

}

print " |}

[[Kategorie:NPC-Listen]]";