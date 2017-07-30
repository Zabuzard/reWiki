package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

public final class LocateRegionTask implements IWikiTask {
	private static final String COMMAND = "php SCRIPT";
	private static final String SCRIPT = "LocateRegion.php";
	private static final String TARGET = "LocateRegion.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		WikiTaskUtil.executeCommand(COMMAND, TARGET, WikiTaskUtil.getPathToScript(SCRIPT));
	}

}
