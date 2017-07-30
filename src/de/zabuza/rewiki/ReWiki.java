package de.zabuza.rewiki;

import java.util.LinkedList;
import de.zabuza.rewiki.settings.SettingsController;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.map.AreaListTask;
import de.zabuza.rewiki.tasks.map.CoordinateListTask;
import de.zabuza.rewiki.tasks.map.LocateRegionTask;
import de.zabuza.rewiki.tasks.map.LocationListTask;
import de.zabuza.rewiki.tasks.map.MapListTask;
import de.zabuza.rewiki.tasks.npc.FightCalcDataTask;
import de.zabuza.rewiki.tasks.npc.NpcImagesTask;
import de.zabuza.rewiki.tasks.npc.NpcListDataTask;
import de.zabuza.rewiki.tasks.npc.NpcListTask;

/**
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ReWiki {
	/**
	 * 
	 * @param args
	 *            Not supported
	 */
	public static void main(final String[] args) {
		// Fetch the settings
		final SettingsController settings = new SettingsController();

		// Create the Wiki hub
		final WikiHub wiki = new WikiHub(settings.getServerAddress(), settings.getUsername(), settings.getPassword());

		// Create all tasks
		final LinkedList<IWikiTask> tasks = new LinkedList<>();
		tasks.add(new NpcListDataTask());
		tasks.add(new FightCalcDataTask());
		tasks.add(new NpcListTask());
		tasks.add(new NpcImagesTask());

		tasks.add(new MapListTask());
		tasks.add(new CoordinateListTask());
		tasks.add(new LocationListTask());
		tasks.add(new AreaListTask());
		tasks.add(new LocateRegionTask());

		// Execute all tasks
		tasks.stream().sequential().forEach(task -> task.executeCommand());
	}
}
