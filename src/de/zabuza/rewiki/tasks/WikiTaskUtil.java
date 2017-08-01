package de.zabuza.rewiki.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import de.zabuza.rewiki.exceptions.MaximalInterruptsExceededException;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;

/**
 * Utility class that offers methods for {@link IWikiTask} implementations.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class WikiTaskUtil {
	/**
	 * The format that is used for outputting date information on a wiki.
	 */
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	/**
	 * The placeholder that is used to get replaced by the current date.
	 */
	private static final String DATE_PLACEHOLDER = "DATE";
	/**
	 * The maximal amount of allowed interrupts until the process aborts waiting
	 * for a subprocess and throws an exception.
	 */
	private static final int MAX_INTERRUPTS = 5;
	/**
	 * The path pointing to the location that contains the scripts to be
	 * executed.
	 */
	private static final String SCRIPT_PATH = "res/scripts/";

	/**
	 * Executes the given command and pushes the standard output the command
	 * produces to the given target file.
	 * 
	 * @param command
	 *            The command to execute together with all its arguments
	 * @param target
	 *            The file to forward the produced standard output to
	 * @throws UnexpectedIOException
	 *             If an unexpected I/O-Exception occurred
	 * @throws MaximalInterruptsExceededException
	 *             If the maximal amount of allowed interrupts was exceeded
	 *             while waiting for the subprocess of the command to finish
	 */
	public static void executeCommand(final List<String> command, final String target)
			throws UnexpectedIOException, MaximalInterruptsExceededException {
		// Prepare the command
		final ProcessBuilder pb = new ProcessBuilder(command);
		final File outputFile = new File(target);
		pb.redirectErrorStream(true);
		pb.redirectOutput(outputFile);

		final Process process;
		try {
			process = pb.start();
		} catch (final IOException e) {
			throw new UnexpectedIOException(e);
		}

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

		if (interruptCounter >= MAX_INTERRUPTS) {
			throw new MaximalInterruptsExceededException();
		}
	}

	/**
	 * Gets the path to the given script assuming it is contained in the regular
	 * script location.
	 * 
	 * @param script
	 *            The name of the script to get the path to
	 * @return The complete path to the given script
	 */
	public static String getPathToScript(final String script) {
		return SCRIPT_PATH + script;
	}

	/**
	 * Replaces {@link #DATE_PLACEHOLDER} in the given text by the current date
	 * in a format that is applicable for a wiki.
	 * 
	 * @param text
	 *            The text to insert the date
	 * @return The given text where the placeholder was replaced by the current
	 *         date
	 */
	public static String insertDateTemplateReady(final String text) {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String formattedTime = currentTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
		return text.replaceAll(DATE_PLACEHOLDER, formattedTime);
	}

	/**
	 * Reads and returns the content from the given file.
	 * 
	 * @param fileName
	 *            The name of the file to read
	 * @return The read content
	 * @throws UnexpectedIOException
	 *             If an unexpected I/O-Exception occurred
	 */
	public static String readContent(final String fileName) throws UnexpectedIOException {
		try {
			return Files.lines(Paths.get(fileName)).collect(Collectors.joining("\n"));
		} catch (final IOException e) {
			throw new UnexpectedIOException(e);
		}
	}

	/**
	 * Utility class. No implementation.
	 */
	private WikiTaskUtil() {

	}
}
