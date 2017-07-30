<?php
/*
 * WIKI Vorlagen Parser
 * 
 */
define('TEMPLATE_GET_KEY',   1);
define('TEMPLATE_GET_VALUE', 2);

function which_char_comes_first($s, $c1, $c2) {
    $pos1 = strpos($s, $c1);
    $pos2 = strpos($s, $c2);
    
    if ($pos1 !== false && $pos2 !== false) {
        return $pos1 < $pos2 ? $c1 : $c2;
    } else if ($pos1 === false && $pos2 === false) {
        return null;
    } else if ($pos2 === false) {
        return $c1;
    } else { // $pos1 === false
        return $c2;
    }
}

function get_templates($template, $wiki_text) {
    $pattern = '/\{\{(Vorlage:)?' . preg_quote($template, '/') . '/';

    $templates = preg_split($pattern, $wiki_text);

    return array_slice($templates, 1);
}

function parse_template($text) {
    $template = array();
    $numeric_key = 1;
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

    for ($i = 0, $length = strlen($text); $i < $length; ++$i) {
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
            $key = trim($key);
            $template[$key] = '';
        } else if ($text[$i] === '|' && $depth === 0) { // Parameter Sparierung
            // kein `key=value` paar sondern nur `value`
            if (which_char_comes_first(substr($text, $i + 1), '|', '=') === '=') { 
                $mode = TEMPLATE_GET_KEY;
                $key = '';
            } else {
                $mode = TEMPLATE_GET_VALUE;
                $key = $numeric_key++;
            }
        } else if ($mode === TEMPLATE_GET_KEY) {
            $key .= $text[$i];
        } else if ($mode === TEMPLATE_GET_VALUE) { // Wert wird geschrieben
            $template[$key] .= $text[$i];
        }
    }
    
    return array_map('trim', $template);
}

/*
 * Artikeln in Kategorie
 */

// verwendung in array_filter, prüfung ob seite und nicht etwa unterkat
function is_page($data) {
    return $data['type'] == 'page';
}

// seiten in kategorie $name
function get_cm($name) {
    $cm = array();
    // api url
    $url = 'http://' . WIKI_HOST . '/api.php?action=query&list=categorymembers'.
           '&cmtitle=Kategorie:' . urlencode($name) . '&cmlimit=max'.
           '&cmstartsortkey=0&cmprop=ids|title|type&format=json&cmsort=sortkey';
    
    $continue_token = '';
    
    do {
        if ($continue_token) { // fortsetzungsseite
            $url .= "&cmcontinue=$continue_token";
        }
        
        // holen, parsen
        $response = json_decode(file_get_contents($url), true);
        
        $continue_token = @$response['continue']['cmcontinue'];
        $cm = array_merge($cm, array_map('extract_data', array_filter($response['query']['categorymembers'], 'is_page')));
    } while ($continue_token);
    
    return $cm;
}

// npclist.php
error_reporting(E_ALL ^ E_NOTICE);
header('Content-Type: text/plain; charset=utf-8;');

# verwendung in array_filter, nur pageids holen, ähnlich array_column
function extract_data($a) {
    return $a['pageid'];
}

# Entfernen von formatnum
function formatnum_r($number) {
    return +intval(str_replace('.', '', $number));
}

# Holt Seitentitel aus mehreren Wikilinks in einer Liste
function filter_wiki_links($s) {
    $links = array();
    foreach (explode("*", $s) as $entry) {
        if (preg_match("/\[\[([^|\]]+)(\|([^|\]]+))?\]\]/", $entry, $matches)) {
            $links[] = isset($matches[2]) ? $matches[3] : $matches[1];
        }
    }
    
    return $links;
}

# parst npc-typ aus `Typ` Parameter
# bitte nach Vorlage:NPC/Layout vardefine:Typ richten
function get_npc_type($type) {
    $type_clean = strtolower(str_replace('-', '', $type));
    switch ($type_clean) {
        case 'gruppe':
        case 'gruppen':
            $type_clean = 'gruppennpc';
            break;
        case 'unique':
            $type_clean = 'uniquenpc';
            break;
    }
    
    $types = [
        'npc',
        'uniquenpc',
        'gruppennpc',
        'resistenznpc',
        'superresistenznpc'
    ];
    
    if (!in_array($type_clean, $types)) {
        return $types[0];
    } else {
        return $type_clean;
    }
}

define('WIKI_HOST', 'fwwiki.de');
define('CSV_DELIMITER', ';');
define('CSV_DELIMITER_INTER', '/');
#  Seiten pro API Abfrage
define('CHUNK_LENGTH', 40);

$npcs = array();

$page_chunks = array_chunk(get_cm('NPCs'), CHUNK_LENGTH);

foreach ($page_chunks as $pages) {
    $api_url = "http://" . WIKI_HOST . "/api.php?action=query&format=json" . 
               "&pageids=" . implode('|', $pages) . "&prop=revisions" . 
               "&rvprop=content";
    $api = json_decode(file_get_contents($api_url), true);
    
    /* Wir wollen die ursprüngliche Sortierung von get_cm
     * auch in den Seiten aus api.php.
     * array_merge vergibt aber neue Schlüssel wenn diese numerisch sind.
     * Der Union Operator $a1 + $a2 bevorzugt allerdings die Werte von $a1.
     * array_replace ist daher die einzige Variante.
     */
    $pages_sorted = array_replace(array_flip($pages), $api['query']['pages']);
    
    foreach ($pages_sorted as $pageid => $info) {
        $raw = $info['revisions'][0]['*'];
        
        # NPC Vorlage
        $base = array_map('parse_template', get_templates('NPC/Layout', $raw));
        $base = $base[0];
        
        # unangreifbar
        $unangreifbar = isset($base['unangreifbar']) && $base['unangreifbar'] !== 'none';
        
        # Name setzen
        $name = isset($base['name']) ? $base['name'] : $info['title'];
        
        # vorkommen
        $vklist = filter_wiki_links($base['Vorkommen']);
        
        # felder zu vorkommen
        foreach (array_map('parse_template', get_templates('Feld', $raw)) as $field_template) {
            $vklist[] = $field_template[3] . "," . $field_template[4];
        }
        
        # Basiseintrag
        $base_entry = array(
            'name' => $name,
            'A' => formatnum_r($base['Stärke']),
            'LP' => formatnum_r($base['Lebenspunkte']),
            'XP' => formatnum_r($base['XP']),
            'GM' => formatnum_r($base['Gold']),
            'vklist' => implode(CSV_DELIMITER_INTER, $vklist),
            'itemlist' => implode(CSV_DELIMITER_INTER, filter_wiki_links($base['Items'])),
            'bild' => preg_replace("/(\r)?\n/", " " , $base['Bild']),
            'autor' => $base['BildAutor'],
            'unangreifbar' => (int)$unangreifbar,
            'typ' => get_npc_type($base['Typ'])
        );
        echo implode(CSV_DELIMITER, $base_entry) . "\n";

        # Varianten
        $variants = array_map('parse_template', get_templates('NPC/Ausnahme', $raw));

        # eintragen
        foreach ($variants as $variant) {
            echo implode(CSV_DELIMITER, array_merge($base_entry, $variant)) . "\n";
        }
    }
}