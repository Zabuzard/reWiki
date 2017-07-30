#!/usr/bin/perl
#input: npclist
use constant CUnknown => "?";

while(<>)
{
  next if (/^GET/);
  my ($name, $atk, $lp) = split(/;/);
 
  if ($name ne CUnknown && $atk ne CUnknown && $lp ne CUnknown) {
    printf("%s;%s;%s\n", $name, $atk, $lp);
  }
}