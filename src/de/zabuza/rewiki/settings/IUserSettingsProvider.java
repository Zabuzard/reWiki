package de.zabuza.rewiki.settings;

/**
 * Interface for objects that provide settings for users.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public interface IUserSettingsProvider {
	/**
	 * Gets the password of the user.
	 * 
	 * @return The password of the user or <tt>null</tt> if not set
	 */
	public String getPassword();

	/**
	 * Gets the username of the user
	 * 
	 * @return The username of the user or <tt>null</tt> if not set
	 */
	public String getUsername();
}
