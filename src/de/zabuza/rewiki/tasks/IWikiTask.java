package de.zabuza.rewiki.tasks;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;

public interface IWikiTask {
	public void executeCommand() throws UnexpectedIOException;

}
