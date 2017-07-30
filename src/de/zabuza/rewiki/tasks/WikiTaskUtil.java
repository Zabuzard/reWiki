package de.zabuza.rewiki.tasks;

import java.io.IOException;

import de.zabuza.rewiki.exceptions.UnexpectedIOException;

public final class WikiTaskUtil {
	private static final String SCRIPT_PATH = "/res/scripts/";
	private static final String SCRIPT_PLACEHOLDER = "SCRIPT";

	public static void executeCommand(final String command) throws UnexpectedIOException {
		// Execute the command
		final Runtime runtime = Runtime.getRuntime();
		Process process;

		try {
			process = runtime.exec(command);
		} catch (final IOException e) {
			// Re-throw as unexpected exception
			throw new UnexpectedIOException(e);
		}

		while (process.isAlive()) {
			try {
				process.waitFor();
				break;
			} catch (final InterruptedException e) {
				// Ignore and continue to wait
				e.printStackTrace();
			}
		}
	}

	public static void executeCommand(final String command, final String script) throws UnexpectedIOException {
		executeCommand(command.replaceAll(SCRIPT_PLACEHOLDER, script));
	}

	public static String getPathToScript(final String script) {
		return SCRIPT_PATH + script;
	}
}
