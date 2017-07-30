package de.zabuza.rewiki.settings;

/**
 * Interface for objects that provide settings for servers.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IServerSettingsProvider {
	/**
	 * Gets the address of the server.
	 * 
	 * @return The address of the server or <tt>null</tt> if not set
	 */
	public String getServerAddress();
}
