package de.zabuza.rewiki.tasks.npc;

import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;

public final class FightCalcDataTask implements IWikiTask {
	private static final String COMMAND = "perl SCRIPT npclist.txt > wikifightcalcdata.txt";
	private static final String SCRIPT = "npclist2fightcalcdata.pl";

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
