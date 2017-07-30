<?php
$json = json_decode(file_get_contents("http://www.fwwiki.de/api.php?action=query&prop=info|revisions&titles=Koordinaten%20(Liste)&rvprop=content&format=json"), true);
$page = current($json['query']['pages']);

print <<<'EOT'
<!--

Minimap-Setup — immer {{{{{Vorlage}}}|X1={obere linke Ecke}|X2={obere rechte Ecke}|Y1={untere linke Ecke}|Y2={untere rechte Ecke}}}

Bitte immer automatisch mittels [[FreewarWiki:Bot/Skripts/LocateRegion.php]] generieren lassen (erfordert mindestens PHP 5.3)

EOT;

$gebiete = explode("\n", $page['revisions'][0]['*']);
for ($i = 1; $i < count($gebiete); $i++)
{
	preg_match("#\{\{Gebietslink\|(.+?)\}\}#", $gebiete[$i], $match);
	print "\n-->{{{{{Vorlage}}}|{$match[1]}";

	preg_match_all("#([0-9-]+?,[0-9-]+?)#U", $gebiete[$i], $match);
	$minY = $minX = PHP_INT_MAX;
	$maxY = $maxX = ~PHP_INT_MAX;
	foreach ($match[1] as $koordpair)
	{
		$koord = explode(",", $koordpair);
		$y = $koord[1];
		$x = $koord[0];
		$minY = min($minY, $y);
		$minX = min($minX, $x);
		$maxY = max($maxY, $y);
		$maxX = max($maxX, $x);
	}
	print "|X1=$minX|X2=$maxX|Y1=$minY|Y2=$maxY";

	print "}}";
	print "<!--";
}
print "\n--><noinclude>{{Dokumentation}}</noinclude>";
?>