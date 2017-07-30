import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/*
 * Zur Verwendung in http://www.fwwiki.de/index.php/Gebiete_%28Liste%29
 * Das Programm liest den Inhalt von:
 * http://www.fwwiki.de/index.php/Koordinaten_%28Liste%29
 * aus und verwendet diesen um Gebiete, Dungeons und deren Felderanzahl zu ermitteln
 * Optional kann ein Argument als Pfadangabe für die Output-Datei angegeben werden
 */
public class arealist {
	static String host = "http://www.fwwiki.de";
	static String ressource = host + "/index.php/Koordinaten_(Liste)";
	static List<Area> areas = new ArrayList<Area>();
	static int allAreaFields;
	static int allDungeonFields;
	static List<Dungeon> dungeons = new ArrayList<Dungeon>();
	static Calendar now = new GregorianCalendar();
	static String nl = System.getProperty("line.separator");
	static String seperator = "|-" + nl;
	static String preArea = "|[[";
	static String endArea = "]]" + nl;
	static String preNumber = "|";
	static String endNumber = nl;
	static String outro = "|}" + nl + "[[Kategorie:Gebiete|!Gebiete (Liste)]]";
	
	
	public static void main(String[] args) {
		//Falls ein Argument übergeben wurde wird dieses als Datei angenommen
		String file = "arealist.txt";
		if(args.length > 0) file = args[0];
		
		//Hole Inhalt der Koordinanten-Liste
		String site = readWeb(ressource);
		//Trenne Seiteninhalt vom Rest
		site = site.substring(site.indexOf("mw-content-ltr"), site.indexOf("printfooter"));
		registerAllAreas(site);
		
		
		PrintStream target = null;
		try {
			target = new PrintStream(new FileOutputStream(file));
			//Berechne die Anzahl aller Gebietsfelder
			for(Area area : areas) {
				allAreaFields += area.numberOfFields;
			}
			//Berechne die Anzahl aller Dungeonfelder
			for(Dungeon dungeon : dungeons) {
				allDungeonFields += dungeon.numberOfFields;
			}
			
			//Einleitungstext
			String intro = "<!-- ACHTUNG: Diese Seite wird von einem Bot aktualisiert. Wenn Du Veraenderungen am Aufbau dieser Seite vornimmst, hinterlasse bitte eine Nachricht auf der Diskussionsseite, sonst werden die Aenderungen vom Bot ueberschrieben. -->Freewar beinhaltet derzeit "
					+ areas.size()
					+ " verschiedene Gebiete. In ihnen wiederum sind "
					+ dungeons.size()
					+ " Dungeons enthalten. Insgesamt gibt es "
					+ allAreaFields
					+ " Oberflaechen- und "
					+ allDungeonFields
					+ " Untergrundfelder (Stand "
					+ now.get(Calendar.DAY_OF_MONTH) + "."
					+ (now.get(Calendar.MONTH) + 1) + "."
					+ now.get(Calendar.YEAR)
					+ "). Diese "
					+ (allAreaFields + allDungeonFields)
					+ " Felder verteilen sich wie folgt:" + nl + nl
					+ "{| class=\"sortable wikitable mw-datatable\"" + nl
					+ "!colspan=\"1\" | Gebiet" + nl
					+ "!colspan=\"1\" | Oberflaechenfelder" + nl
					+ "!colspan=\"1\" | Dungeons" + nl
					+ "!colspan=\"1\" | Untergrundfelder" + nl
					+ "!colspan=\"1\" | Gesamtfelder" + nl;
			
			
			target.print(intro);
			
			//Gehe alle Gebiete durch um unter anderem deren Dungeons zu ermitteln
			for(Area area : areas) {
				target.print(seperator);
				target.print(preArea + area.name + endArea);
				target.print(preNumber + area.numberOfFields + endNumber);
				
				
				target.print("|");
				String areaDungeons = "";
				int areaDungeonFields = 0;
				//Ermittle nun alle Dungeons und berechne die Anzahl der Dungeonfelder des Gebiets
				for(Dungeon dungeon : dungeons) {
					if(area.name.equalsIgnoreCase(dungeon.area)) {
						areaDungeons += "[[" + dungeon.name + "]] (" + dungeon.numberOfFields + "), ";
						areaDungeonFields += dungeon.numberOfFields;
					}
				}
				//Hat das Gebiet kein Dungeon wird keine geschrieben
				areaDungeons = areaDungeons.equals("") ? "keine" + nl
								: areaDungeons.substring(0, areaDungeons.length() - 2) + nl;
				target.print(areaDungeons);
				
				
				target.print(preNumber + areaDungeonFields + endNumber);
				target.print(preNumber + (area.numberOfFields + areaDungeonFields) + endNumber);
			}
			
			
			target.print(outro);
		} catch (FileNotFoundException e) {
			System.err.println("Error at writing in file: " + file);
		} finally {
			target.close();
		}
	}

	
	//Liest den Inhalt einer Webseite komplett ein
	private static String readWeb(String ressource) {
		BufferedReader web = null;
		String site = "";
		try {
			URL u = new URL(ressource);
			web = new BufferedReader(new InputStreamReader(u.openStream()));

			
			String line = web.readLine();
			while (line != null){
				site += line;
				line = web.readLine();
			}
			if(site.equals("")) throw new IOException();
		} catch (IOException e) {
			System.err.println("No valid URL: " + ressource);
			System.exit(0);
		} finally {
			try {
				web.close();
			} catch (IOException e) {
				System.err.println("Error in closing webressource.");
				System.exit(0);
			}
		}
		return site;
	}


	//Verarbeitet die Informationen aus der Koordinaten-Liste und registriert alle Gebiete und Dungeons
	private static void registerAllAreas(String site) {
		String indicator = "Vorlage_Ueberschriftensimulation_2";
		int limit = 1000;
		
		
		int oldIndex;
		int index = site.indexOf(indicator);
		//Hole jedes Gebiet durch den Indikator
		for(int i = 0; ; i++) {
			oldIndex = index + indicator.length();
			index = site.indexOf(indicator, oldIndex);
			//Wahr wenn das Gebiet das letzte in der Liste ist
			if(index < oldIndex) {
				registerArea(site.substring(oldIndex, site.length()));
				break;
			}
			//Gib den Abschnitt mit den Gebietsinformationen weiter
			registerArea(site.substring(oldIndex, index));
			//Erreicht die Schleife ein Limit wird abgebrochen
			if(i > limit) {
				System.err.println(limit + " runs, something is wrong, abort!");
				break;
			}
		}
	}

	
	//Seperiert Informationen über ein Gebiet aus dem begrenzten Inhalt
	private static void registerArea(String content) {
		String hrefIndicator = "<a href=\"";
		String titleIndicator = "title=\"";
		String fieldIndicator = "</span> (";
		String[] contentIndicator = {"name=\"wpTextbox1\">", "</textarea>"};
		
		
		content = content.substring(0, content.indexOf("</div>"));
		
		
		//Hole die Adresse des Gebiets
		int index = content.indexOf(hrefIndicator) + hrefIndicator.length();
		String href = content.substring(index, content.indexOf("\"", index));
		href = href.replaceAll("index\\.php/", "index\\.php\\?title=");
		
		
		//Hole den Titel des Gebiets
		index = content.indexOf(titleIndicator) + titleIndicator.length();
		String title = content.substring(index, content.indexOf("\"", index));
		
		
		//Hole die Anzahl der Felder des Gebiets
		index = content.indexOf(fieldIndicator) + fieldIndicator.length();
		int numberOfFields = Integer.valueOf(content.substring(index).replaceAll("Feld(er)?\\)", "").trim());
		
		
		//Folge dem Link des Gebiets und lese aus dem Bearbeitungsfenster aus
		content = readWeb(host + href + "&action=edit");
		content = content.substring(content.indexOf(contentIndicator[0]) + contentIndicator[0].length(),
									content.indexOf(contentIndicator[1]));
		
		
		//Falls die Seite eine Weiterleitung zum eigentlichen Gebiet ist
		if(content.toLowerCase().contains("#redirect")
				|| content.toLowerCase().contains("#weiterleitung")) {
			index = content.indexOf("[[") + 2;
			href = "/index.php?title=" + content.substring(index, content.indexOf("]]"));
			href = href.replaceAll(" ", "_");
			content = readWeb(host + href + "&action=edit");
			content = content.substring(content.indexOf(contentIndicator[0]) + contentIndicator[0].length(),
					content.indexOf(contentIndicator[1]));
		}
		
		
		//Falls das Gebiet ein Dungeon und kein Gebiet ist
		if(content.contains("{{Dungeon/Layout")) {
			//Registriere den Dungeon
			registerDungeon(content, title, numberOfFields);
		}else {
			//Registriere das Gebiet
			areas.add(new Area(title, numberOfFields));
		}
	}


	//Registriert einen Dungeon, nutzt dazu die Informationen aus dem Bearbeitungsfenster
	private static void registerDungeon(String content, String title, int numberOfFields) {
		String dungeonIndicator = "|Gebiet=";
		String[] endIndicator = {"]]", "|", "none"};
		
		
		//Ermittle das übergeordnete Gebiet zum Dungeon
		int index = content.indexOf(dungeonIndicator) + dungeonIndicator.length();
		int endIndex = content.length();
		for(String indicator : endIndicator) {
			int tempEndIndex = content.indexOf(indicator, index);
			//Der End-Indicator welcher als erster nach dem Indikator kommt wird übernommen
			endIndex = tempEndIndex == -1 ? Math.min(endIndex, content.length()) :
											Math.min(endIndex, tempEndIndex);
		}
		String dungeonArea = content.substring(index, endIndex).replaceAll("\\[\\[", "");
		/*
		 * Hat der Dungeon kein bekanntes übergeordnetes Gebiet, wie z.B. die Bernsteinhöhle
		 * wird ein gleichnamiges Gebiet mit 0 Feldern angelegt welches den Dungeon beinhaltet
		 */
		if(dungeonArea.equals("")) {
			dungeonArea = title;
			//Füge das platzhaltende Gebiet hinzu
			areas.add(new Area(dungeonArea, 0));
		}
		//Füge den Dungeon hinzu
		dungeons.add(new Dungeon(title, dungeonArea, numberOfFields));
	}
}


//Gebietsklasse
class Area{
	String name;
	int numberOfFields;
	
	
	Area(String name, int numberOfFields) {
		this.name = name;
		this.numberOfFields = numberOfFields;
	}
}


//Dungeonklasse
class Dungeon{
	String name;
	int numberOfFields;
	String area;
	
	
	Dungeon(String name, String area, int numberOfFields) {
		this.name = name;
		this.numberOfFields = numberOfFields;
		this.area = area;
	}
}