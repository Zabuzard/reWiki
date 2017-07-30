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

public final class WikiTaskUtil {
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
	private static final String DATE_PLACEHOLDER = "DATE";
	private static final int MAX_INTERRUPTS = 5;
	private static final String SCRIPT_PATH = "res/scripts/";

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

	public static String getPathToScript(final String script) {
		return SCRIPT_PATH + script;
	}

	public static String insertDateTemplateReady(final String text) {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String formattedTime = currentTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
		return text.replaceAll(DATE_PLACEHOLDER, formattedTime);
	}

	public static String readContent(final String fileName) throws UnexpectedIOException {
		try {
			return Files.lines(Paths.get(fileName)).collect(Collectors.joining("\n"));
		} catch (final IOException e) {
			throw new UnexpectedIOException(e);
		}
	}
}
