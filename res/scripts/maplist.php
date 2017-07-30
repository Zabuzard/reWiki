<?php
header('Content-Type: text/plain; charset=utf-8;');
error_reporting(E_ALL ^ E_NOTICE);

define('TEMPLATE_GET_KEY',   1);
define('TEMPLATE_GET_VALUE', 2);

function is_obsolete($wiki_text) {
    // Veraltetes Feld=none liefert true!
    return (bool)preg_match('/Veraltetes Feld=[^}]+/', $wiki_text);
}

function get_areas($url, $prefix) {
    preg_match_all("/>$prefix:([^<]+)</", file_get_contents($url), $areas);

    return $areas[1];
}

function get_templates($template, $wiki_text) {
    $pattern = '/\{\{(Vorlage:)?' . preg_quote($template, '/') . '/';

    $templates = preg_split($pattern, $wiki_text);

    return array_slice($templates, 1);
}

function parse_field_article($area, $host) {
    $fields = array();

    // Artikel fetchen
    $field_url = "$host/index.php/Felder:" . rawurlencode($area) . "?action=raw";
    $html = file_get_contents($field_url);

    if (is_obsolete($html) === true) { // veraltet
        return array();
    } else {
        // Layout Vorlagen matchen
        $field_templates = get_templates('Feldzusammenfassung/Layout', $html);

        // Layout Vorlagen durchlaufen
        foreach ($field_templates as $field_template) {
            // init und parsen
            $field = array_merge(array('area' => $area), parse_field_template($field_template));

            // push
            $fields[] = $field;
        }

        return $fields;
    }
}

function parse_field_template($wiki_text) {
    // Standard-Werte
    $field = array(
        'accessible' => 1,
        'pos_x'      => -10,
        'pos_y'      => -9,
        'npc' 	     => array(),
        'url'        => '',
        'passages'   => array()
    );

    // Vorlage als Array: Parameter => Wert
    $template = parse_template($wiki_text);

    // Vorlagewerte maschinenlesbar machen
    $field['pos_x'] = (int)$template['X'];
    $field['pos_y'] = (int)$template['Y'];
    $field['url']   = $template['Bild'];

    // Passagen lesen
    $passage_templates = get_templates('Feldzusammenfassung/Passage', $wiki_text);

    // Passagen durchlaufen
    foreach ($passage_templates as $passage_template) {
        $passage = parse_template($passage_template);

        // keine Koordinaten gesetzt
        if (strcasecmp($passage['Nach'], 'zufall') && !isset($passage['X'], $passage['Y'])) {
            /* Warnung deaktiviert - Es gibt einige Passagen in denen das beabsichtigt ist (Buran), solange nicht speziell gefiltert wird stört die Meldung nur
			trigger_error('Keine Koordinatenangabe in Passage von '.
                          $template['Name'] . ' nach ' . $passage['Nach'], E_USER_WARNING);
			*/
        } else {
            $field['passages'][] = $passage['X'] . ',' . $passage['Y'];
        }
    }

    // npcs lesen
    preg_match_all('/\[\[([^]]+)\]\]/', $template['NPC'], $npc_matches);
    $field['npc'] = $npc_matches[1];

    return $field;
}

function parse_template($text) {
    $template = array();

    /* nicht kompatibel mit verschachtelten Vorlagen
    // Key-Value Paare spliten
    $lines = array_filter(explode('|', $template_text));

    foreach ($lines as $line) {
        // Key/Value trennen
        $keyval = explode('=', $line, 2);
        // und entsprechend ins Array eintragen
        $template[$keyval[0]] = trim($keyval[1]); // 'Parameter=' wirft undefined offset 1
    }//*/

    $key = '';
    $mode = TEMPLATE_GET_KEY;
    $depth = 0;

    for ($i = 1, $length = strlen($text); $i < $length; ++$i) {
        if ($text[$i] === '{' && $text[$i+1] === '{') { // weitere Vorlage
            ++$depth;
            ++$i;
            $template[$key] .= '{';
        } else if ($text[$i] === '}' && $text[$i+1] === '}') { // geschlossene Vorlage

            if ($depth === 0) {
                break;
            } else {
                --$depth;
                ++$i;
                $template[$key] .= '}';
            }
        } else if ($text[$i] === '[' && $text[$i+1] === '[') { // geöffneter Link
            ++$depth;
            ++$i;
            $template[$key] .= '[';
        } else if ($text[$i] === ']' && $text[$i+1] === ']') { // geschlossener Link

            if ($depth === 0) {
                break;
            } else {
                --$depth;
                ++$i;
                $template[$key] .= ']';
            }
        }

        if ($text[$i] === '=' && $depth === 0) { // Wertzuweisung beginnt
            $mode = TEMPLATE_GET_VALUE;
            $depth = 0;
            $template[$key] = '';
        } else if ($text[$i] === '|' && $depth === 0) { // Parameter Sparierung
            $mode = TEMPLATE_GET_KEY;
            $key = '';
        } else if ($mode === TEMPLATE_GET_KEY) {
            $key .= $text[$i];
        } else if ($mode === TEMPLATE_GET_VALUE) { // Wert wird geschrieben
            $template[$key] .= $text[$i];
        }
    }

    return array_map('trim',$template);
}

$host            = 'http://www.fwwiki.de';
$prefix          = 'Felder';                           // Wiki-Namespace
$parser_function = 'parse_field_article';              // Parser Funktion des Skripts
$category_url    = "$host/index.php/Kategorie:Felder"; // Gebietskategorie

// init
$fields = array();

// Gebiete fetchen
$areas = get_areas($category_url, $prefix);

// durchlaufen
foreach ($areas as $area) {
    $fields = array_merge($fields, $parser_function($area, $host));
}

// und ausgeben
$delimiter = ';';

// head
#echo implode($delimiter, array_keys($fields[0])) . "\n";

// body
foreach ($fields as $field) {
    // output wie maplist.pl
    $field['npc'] = implode('/', $field['npc']);
    $field['passages'] = implode('/', $field['passages']);

    echo implode($delimiter, $field) . "\n";
}