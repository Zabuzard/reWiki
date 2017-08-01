package de.zabuza.rewiki;

import java.util.LinkedList;
import java.util.List;

import de.zabuza.rewiki.settings.SettingsController;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.map.AreaListTask;
import de.zabuza.rewiki.tasks.map.CoordinateListTask;
import de.zabuza.rewiki.tasks.map.LocationListTask;
import de.zabuza.rewiki.tasks.map.MapListTask;
import de.zabuza.rewiki.tasks.npc.FightCalcDataTask;
import de.zabuza.rewiki.tasks.npc.NpcImagesTask;
import de.zabuza.rewiki.tasks.npc.NpcListDataTask;
import de.zabuza.rewiki.tasks.npc.NpcListTask;

/**
 * Tool that updates several commonly used data page of the
 * <tt>FreewarWiki</tt>.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ReWiki {
	/**
	 * Executes the tool and by that updates several commonly used data page of
	 * the <tt>FreewarWiki</tt>.
	 * 
	 * @param args
	 *            Not supported
	 */
	public static void main(final String[] args) {
		System.out.println("Initializing...");

		// Fetch the settings
		final SettingsController settings = new SettingsController();
		settings.initialize();

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

		// Execute them and push results
		executeTasks(tasks, "");
		pushingTasksResults(tasks, wiki, "");

		// Handle dependent tasks
		final LinkedList<IWikiTask> dependentTasks = new LinkedList<>();
		dependentTasks.add(new AreaListTask());
		// TODO Temporarily disabled because it contains bugs that are not
		// likely to be fixed in the near future
		// dependentTasks.add(new LocateRegionTask());

		// Execute them and push results
		executeTasks(dependentTasks, "dependent ");
		pushingTasksResults(dependentTasks, wiki, "dependent ");

		System.out.println("Terminated.");
	}

	/**
	 * Executes all given tasks and prints debugging information.
	 * 
	 * @param tasks
	 *            The tasks to execute
	 * @param loggingAdjective
	 *            Adjective that can be used to describe the tasks in logging
	 *            messages
	 */
	private static void executeTasks(final List<IWikiTask> tasks, final String loggingAdjective) {
		// Execute all tasks
		System.out.println("Executing " + loggingAdjective + "tasks...");
		int counter = 0;
		for (final IWikiTask task : tasks) {
			task.executeCommand();

			counter++;
			System.out.println("\tFinished (" + counter + "/" + tasks.size() + ")");
		}
	}

	/**
	 * Pushes the results of the given tasks to the given wiki and prints
	 * debugging information.
	 * 
	 * @param tasks
	 *            The tasks to push results of
	 * @param wiki
	 *            The wiki to push the results to
	 * @param loggingAdjective
	 *            Adjective that can be used to describe the tasks in logging
	 *            messages
	 */
	private static void pushingTasksResults(final List<IWikiTask> tasks, final WikiHub wiki,
			final String loggingAdjective) {
		// Push all task results to the wiki
		System.out.println("Pushing " + loggingAdjective + "results...");
		int counter = 0;
		for (final IWikiTask task : tasks) {
			task.pushToWiki(wiki);

			counter++;
			System.out.println("\tFinished (" + counter + "/" + tasks.size() + ")");
		}
	}

	/**
	 * Utility class. No implementation.
	 */
	private ReWiki() {

	}
}
