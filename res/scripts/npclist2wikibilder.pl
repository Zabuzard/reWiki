#!/usr/bin/perl
use Digest::MD5 qw(md5_hex);

# Usage:
# input: "npclist" as created by script "npclist.pl"
# output: table with NPC pics in wiki format; change LANGUAGE_CODE to desired output language

# change log:
# 2010/06/03, Arbiedz
# - introduced support for multiple languages
# - grouped items by npc picture

# set language code for output: 0=en, 1=de
$LANGUAGE_CODE = 1;

# Define the constant strings. Each string is stored in an array indexed by the above LANGUAGE_CODE
@STR_PICTURE           = ("Picture", "Bild");
@STR_NPC               = ("NPC(s)", "NPC(s)");
@STR_AUTHOR          = ("created by", "Autor");
@STR_HINT              = ("<div style='background-color:orange; padding: 1px; border: 4px dashed black;'>\n"
                          . "{| border='0' cellspacing='8' cellpadding='0' "
                          . "style='background-color: #f9f9f9; border: 1px solid #e9e9e9; font-size: 95%; margin-top: 2px; margin-bottom: 2px; clear: both;'\n"
                          . " | '''Hint:'''\n"
                          . "This table is created automagically by the NPC articles from time to time.<br/>\n"
                          . "'''You should not change data here, because your modifications will be oberwritten by the next update.'''<br/>\n"
                          . "Instead change the data on the NPC article!<br/><br/>\n"
                          . "(Use the template parameter \"ImageAuthor\" for the author of the pic. The image author is then shown "
                          . "in the article also.)\n"
                          . "|}\n"
                          . "</div>\n"
                          ,
                          "<div style='background-color: orange; padding: 1px; border: 4px dashed black;'>\n"
                          . "{| {{Bausteindesign3}}\n"
                          . " | '''Hinweis:'''\n"
                          . "Diese Tabelle wird von Zeit zu Zeit automatisch aus den Daten auf den einzelnen NPC-Seiten erstellt.<br/>\n"
                          . "'''Die Daten hier sollten nicht direkt ge\N{U+0026}auml;ndert werden, weil solche \N{U+0026}Auml;nderungen bei einer Neuerstellung "
                          . "verloren gehen.'''<br/>Stattdessen bitte die Daten auf den Seiten des jeweiligen NPC korrigieren!<br/><br/>\n"
                          . "(F\N{U+0026}uuml;r den Autor gibt es ein Vorlagenfeld \"BildAutor\", das dann auch im NPC-Artikel angezeigt wird.)\n"
                          . "|}\n"
                          . "</div>\n");
@STR_CATEGORIES        = ("[[Category: NPC-Lists]]\n[[Category: NPCs with Image|!]]\n",
                         "[[Kategorie: NPC-Listen]]\n[[Kategorie:NPCs mit Bild|!]]\n");


#################### no user settings necessary below ####################


print $STR_HINT[$LANGUAGE_CODE];

print "{| class=\"wikitable\" \n";
print "|-\n";
print "!" . $STR_PICTURE[$LANGUAGE_CODE] . "||" . $STR_NPC[$LANGUAGE_CODE] . "||" . $STR_AUTHOR[$LANGUAGE_CODE] . "\n";

while(<>) {
    chomp;
    ($text,$atk,$lp,$xp,$gm,$area,$drop,$bild,$autor)=split(/;/);
    # print "text=$text, atk=$atk, lp=$lp, xp=$xp, gm=$gm, area=$area, drop=$drop, bild=$bild, autor=$autor\n";
    if ($bild ne "") {
      # use only the filename as key, not the complete url, because different worlds can be given
      $picfilename = $bild;
      $picfilename =~ s/.*\///;
      $md5_hexURL = md5_hex($picfilename);
      
      if ($autor ne "" ) {
        if ($autor ne "none" ) {
          $autors{$md5_hexURL}  = $autor;
        }
      };
      $urls{$md5_hexURL}  = $bild;

      $NPCs{$md5_hexURL}[$#{$NPCs{$md5_hexURL}}+1] = $text;
    }  
    
    next unless($bild =~ /^http:/);
}


foreach (keys(%urls)) {
  print " |-\n";
  print " |" . $urls{$_} . " || [[";
  print @{$NPCs{$_}}[0];
  
  for ($i=1; $i < $#{$NPCs{$_}} + 1; $i++) {
    print "]], [[" . $NPCs{$_}[$i];
  }
  print "]] || " . $autors{$_} . "\n";
}  

print "|}\n";

print $STR_CATEGORIES[$LANGUAGE_CODE]