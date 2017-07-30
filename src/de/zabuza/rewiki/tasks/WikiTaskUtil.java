package de.zabuza.rewiki.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.zabuza.rewiki.exceptions.MaximalInterruptsExceededException;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;

public final class WikiTaskUtil {
	private static final int MAX_INTERRUPTS = 5;
	private static final String SCRIPT_PATH = "res/scripts/";
	private static final String SCRIPT_PLACEHOLDER = "SCRIPT";

	public static void executeCommand(final String command, final String target)
			throws UnexpectedIOException, MaximalInterruptsExceededException {
		// Create an output stream to the target file
		final File outputFile = new File(target);
		try (final FileOutputStream fileOutputStream = new FileOutputStream(outputFile, false)) {
			// Execute the command
			final Runtime runtime = Runtime.getRuntime();

			Process process;
			try {
				process = runtime.exec(command);
			} catch (final IOException e) {
				// Re-throw as unexpected exception
				throw new UnexpectedIOException(e);
			}

			StreamFetcher errorStream = new StreamFetcher(process.getErrorStream());
			StreamFetcher outputStream = new StreamFetcher(process.getInputStream(), fileOutputStream);

			errorStream.start();
			outputStream.start();

			int interruptCounter = 0;
			while (process.isAlive() && interruptCounter < MAX_INTERRUPTS) {
				try {
					process.waitFor();
					break;
				} catch (final InterruptedException e) {
					// Ignore and continue to wait
					interruptCounter++;
				}
			}

			errorStream.terminate();
			outputStream.terminate();

			if (interruptCounter >= MAX_INTERRUPTS) {
				throw new MaximalInterruptsExceededException();
			}
		} catch (final IOException e) {
			throw new UnexpectedIOException(e);
		}
	}

	public static void executeCommand(final String command, final String target, final String script)
			throws UnexpectedIOException {
		executeCommand(command.replaceAll(SCRIPT_PLACEHOLDER, script), target);
	}

	public static String getPathToScript(final String script) {
		return SCRIPT_PATH + script;
	}
}
