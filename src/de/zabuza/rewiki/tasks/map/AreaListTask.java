package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

public final class AreaListTask implements IWikiTask {
	private static final String COMMAND = "javac SCRIPT_UNCOMPILED && java SCRIPT_COMPILED";
	private static final String SCRIPT_COMPILED = "arealist";
	private static final String SCRIPT_UNCOMPILED = "arealist.java";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		final String command = COMMAND.replaceAll("SCRIPT_UNCOMPILED", WikiTaskUtil.getPathToScript(SCRIPT_UNCOMPILED))
				.replaceAll("SCRIPT_COMPILED", WikiTaskUtil.getPathToScript(SCRIPT_COMPILED));
		WikiTaskUtil.executeCommand(command);
	}

}
