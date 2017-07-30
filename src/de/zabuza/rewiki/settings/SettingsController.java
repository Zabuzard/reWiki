package de.zabuza.rewiki.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * The controller of the settings.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class SettingsController implements ISettingsProvider, IUserSettingsProvider, IServerSettingsProvider {
	/**
	 * Text to save for a value if a key is unknown.
	 */
	public static final String UNKNOWN_KEY_VALUE = "";
	/**
	 * Key identifier for the password.
	 */
	private static final String KEY_IDENTIFIER_PASSWORD = "password";
	/**
	 * Key identifier for service settings.
	 */
	private static final String KEY_IDENTIFIER_SERVER_ADDRESS = "serverAddress";
	/**
	 * Key identifier for the username.
	 */
	private static final String KEY_IDENTIFIER_USERNAME = "username";

	/**
	 * Utility main method to create settings.
	 * 
	 * @param args
	 *            Not supported
	 */
	public static void main(final String[] args) {
		final SettingsController settings = new SettingsController();
		settings.initialize();

		settings.setUsername("username");
		settings.setPassword("password");

		settings.setServerAddress("http://www.example.org");

		settings.saveSettings();
	}

	/**
	 * The object for the settings.
	 */
	private final Settings mSettings;

	/**
	 * Structure which saves all currently loaded settings.
	 */
	private final Map<String, String> mSettingsStore;

	/**
	 * Creates a new controller of the settings.
	 */
	public SettingsController() {
		this.mSettingsStore = new HashMap<>();
		this.mSettings = new Settings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.settings.ISettingsProvider#getAllSettings()
	 */
	@Override
	public Map<String, String> getAllSettings() {
		return this.mSettingsStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.settings.IUserSettingsProvider#getPassword()
	 */
	@Override
	public String getPassword() {
		return getSetting(KEY_IDENTIFIER_PASSWORD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.settings.IServerSettingsProvider#getServerAddress()
	 */
	@Override
	public String getServerAddress() {
		final String value = getSetting(KEY_IDENTIFIER_SERVER_ADDRESS);
		if (value != null) {
			return value;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.rewiki.settings.ISettingsProvider#getSetting(java.lang.String)
	 */
	@Override
	public String getSetting(final String key) {
		String value = this.mSettingsStore.get(key);
		if (value == null) {
			value = UNKNOWN_KEY_VALUE;
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.settings.IUserSettingsProvider#getUsername()
	 */
	@Override
	public String getUsername() {
		return getSetting(KEY_IDENTIFIER_USERNAME);
	}

	/**
	 * Initializes the controller.
	 */
	public void initialize() {
		this.mSettings.loadSettings(this);
	}

	/**
	 * Call whenever the save action is to be executed. This will save all
	 * settings.
	 */
	public void saveSettings() {
		try {
			// Save settings
			this.mSettings.saveSettings(this);
		} catch (final Exception e) {
			// Log the error but continue
			System.err.println("Error while saving settings: " + e.getStackTrace());
		}
	}

	/**
	 * Sets the password of the user
	 * 
	 * @param password
	 *            The password to set
	 */
	public void setPassword(final String password) {
		if (password != null) {
			final String key = KEY_IDENTIFIER_PASSWORD;
			setSetting(key, password);
		}
	}

	/**
	 * Sets the server address to use.
	 * 
	 * @param serverAddress
	 *            The server address to set
	 */
	public void setServerAddress(final String serverAddress) {
		if (serverAddress != null) {
			final String key = KEY_IDENTIFIER_SERVER_ADDRESS;
			setSetting(key, serverAddress);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.rewiki.settings.ISettingsProvider#setSetting(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setSetting(final String key, final String value) {
		this.mSettingsStore.put(key, value);
	}

	/**
	 * Sets the username of the user.
	 * 
	 * @param username
	 *            The username to set
	 */
	public void setUsername(final String username) {
		if (username != null) {
			final String key = KEY_IDENTIFIER_USERNAME;
			setSetting(key, username);
		}
	}
}
