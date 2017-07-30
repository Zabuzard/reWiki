package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

public final class CoordinateListTask implements IWikiTask {
	private static final String COMMAND = "php SCRIPT > wikimaplist.txt";
	private static final String SCRIPT = "maplist2wiki.php";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		WikiTaskUtil.executeCommand(COMMAND, WikiTaskUtil.getPathToScript(SCRIPT));
	}

}
