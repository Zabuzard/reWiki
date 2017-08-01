package de.zabuza.rewiki.tasks.map;

import java.util.LinkedList;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

/**
 * Task that calls a script which creates intermediate map results that are
 * later used by other tasks.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class MapListTask implements IWikiTask {
	/**
	 * The command to use.
	 */
	private static final String COMMAND = "php";
	/**
	 * The name of the script to execute.
	 */
	private static final String SCRIPT = "maplist.php";
	/**
	 * The file to redirect the produced standard output of the script to.
	 */
	private static final String TARGET = "maplist.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		final LinkedList<String> command = new LinkedList<>();
		command.add(COMMAND);
		command.add(WikiTaskUtil.getPathToScript(SCRIPT));

		WikiTaskUtil.executeCommand(command, TARGET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.rewiki.tasks.IWikiTask#pushToWiki(de.zabuza.rewiki.WikiHub)
	 */
	@Override
	public void pushToWiki(final WikiHub wiki) throws UnexpectedIOException {
		// This task does not push anything to the wiki, it just provides data
		// for the other tasks
	}

}
