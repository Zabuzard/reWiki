package de.zabuza.rewiki.tasks;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;

/**
 * Interface for general wiki tasks. Such tasks can be executed and their
 * results can be pushed to a wiki.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IWikiTask {

	/**
	 * Executes the command for this task.
	 * 
	 * @throws UnexpectedIOException
	 *             If an unexpected I/O-Exception occurred
	 */
	public void executeCommand() throws UnexpectedIOException;

	/**
	 * Pushes the results of this task to the given wiki.
	 * 
	 * @param wiki
	 *            The wiki to pushes the results to
	 * @throws UnexpectedIOException
	 *             If an unexpected I/O-Exception occurred
	 */
	public void pushToWiki(final WikiHub wiki) throws UnexpectedIOException;
}
