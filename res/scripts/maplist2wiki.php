<?php
$date = date("d.m.Y");  

echo "Einige Zauber und Funktionen in Freewar verraten die aktuelle Position eines Charakters in Form von Koordinaten. Gerade bei Feldern, die nicht zur oberirdischen Hauptlandmasse von Freewar gehören, ist es oft schwer, herauszufinden, zu welchem Gebiet diese Koordinaten gehören.<br />Die folgende Liste hilft dabei. Alle Koordinaten sind in der Form '''X''','''Y''' unter dem Namen des Gebiets gelistet, zu dem sie gehören. So kann mit der Suchfunktion des Browsers leicht das Gebiet zu einer bestimmten Position ermittelt werden.<br />Die Liste ist automatisch aus den Wiki-Kartendaten erstellt (Stand ".$date.") und wird evtl. bei Kartenänderungen oder Fehlern auch automatisch wieder neu generiert; Änderungen an der Liste sind nicht sinnvoll. Stattdessen, wenn etwas auffällt, bitte auf der Diskussionsseite vermerken.<br />";


$data = file('maplist.txt');


foreach($data as $value) {
	$var = explode(';',$value);
	$area[$var[0]][] = $var[2] . ",". $var[3];
}

foreach($area as $key => $value) {
	$count = count($value);
	echo "<!--\n-->{{Überschriftensimulation 2|1={{Gebietslink|".$key."}} (".$count." ".(($count == 1) ? 'Feld' : 'Felder').")}}";
	echo implode('; ',$value);
}
echo "<!--\n-->[[Kategorie:Allgemeines]][[Kategorie:Karten|!Koordinaten (Liste)]]\n";

?>