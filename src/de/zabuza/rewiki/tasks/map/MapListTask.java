package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

public final class MapListTask implements IWikiTask {
	private static final String COMMAND = "php SCRIPT";
	private static final String SCRIPT = "maplist.php";
	private static final String TARGET = "maplist.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		WikiTaskUtil.executeCommand(COMMAND, TARGET, WikiTaskUtil.getPathToScript(SCRIPT));
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
