package de.zabuza.rewiki.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public final class StreamFetcher extends Thread {
	private static final long WAIT_INTERVAL = 100L;
	private final InputStream mInput;
	private final OutputStream mOutput;

	private boolean mShouldStop;

	public StreamFetcher(final InputStream input) {
		this(input, null);
	}

	public StreamFetcher(final InputStream input, final OutputStream output) {
		this.mInput = input;
		this.mOutput = output;

		this.mShouldStop = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		PrintWriter pw = null;
		if (this.mOutput != null) {
			pw = new PrintWriter(this.mOutput);
		}

		final BufferedReader br = new BufferedReader(new InputStreamReader(this.mInput));
		try {
			while (!this.mShouldStop) {
				final String line = br.readLine();
				if (line == null) {
					Thread.sleep(WAIT_INTERVAL);
					continue;
				}

				if (pw != null) {
					pw.println(line);
				}
			}
		} catch (final IOException | InterruptedException e) {
			e.printStackTrace();
		}

		if (pw != null) {
			pw.flush();
		}
	}

	public void terminate() {
		this.mShouldStop = true;
	}
}
