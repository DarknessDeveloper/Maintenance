package net.dxtrus.maintenance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.dxtrus.maintenance.commands.CommandMaintenance;
import net.dxtrus.maintenance.events.PlayerEventListener;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Maintenance extends JavaPlugin {

	private static Maintenance instance;

	private File messagesFile;
	private FileConfiguration messagesConfig;

	public void onEnable() {
		instance = this;

		if (!setupConfigs(false)) {
			getLogger().severe("Error setting up configs. Disabling.");
			setEnabled(false);
			return;
		}

		registerCommands();

		registerEvents();
		lateEnable();
	}

	/**
	 * 
	 * @param isReload If this method is being called due to {@link #reloadConfig()}
	 * @return
	 */
	private boolean setupConfigs(boolean isReload) {
		if (!isReload && getResource("config.yml") != null)
			saveDefaultConfig();
		else
			super.reloadConfig();

		messagesFile = new File(getDataFolder(), "messages.yml");
		messagesConfig = loadConfig(messagesFile, "messages.yml");

		return true;
	}

	private final void registerCommands() {
		getCommand("maintenance").setExecutor(new CommandMaintenance());
	}

	private final void registerEvents() {
		getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

	}

	private final void lateEnable() {

	}

	/**
	 * 
	 * @param f        The file to load from.
	 * @param resource The internal resource (the file inside the JAR). Set to null
	 *                 for no defaults.
	 * @return An instance of
	 *         {@link org.bukkit.configuration. file.FileConfiguration}, or null if
	 *         an error occurrs.
	 */
	public final FileConfiguration loadConfig(File f, String resource) {

		Validate.notNull(f, "File cannot be null.");

		try {
			if (!f.exists())
				f.createNewFile();

			InputStream defaults = getResource(resource);

			FileConfiguration config = YamlConfiguration.loadConfiguration(f);
			if (defaults != null) {
				config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaults)));
				config.options().copyDefaults(true);
				config.save(f);
			}

			return config;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @see {@link #getMessagesConfig()}
	 * @return The file for messages.yml. <br/>
	 *         Note: This is not the config, but the instance of the File itself.
	 */
	public File getMessagesFile() {
		return messagesFile;
	}

	/**
	 * @see {@link #getMessagesFile()}
	 * @return The config file for messages.yml
	 */
	public FileConfiguration getMessagesConfig() {
		return messagesConfig;
	}

	public void reloadConfig() {
		setupConfigs(true);
	}

	/**
	 * Set the status of maintenance.
	 * 
	 * @param maintenance Whether maintenance should be enabled or not.
	 */
	public static void setMaintenance(boolean maintenance) {

		instance.getConfig().set("maintenance.enabled", maintenance);
		instance.saveConfig();

	}

	/**
	 * Wrapper for
	 * {@code org.bukkit.ChatColor.tranlsateAlternateColorCodes(char, String)}
	 * 
	 * @param msg The message to add colour to.
	 * @return The same message, with appropriate colours added.
	 */
	public static String tl(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Internationalisation. Used to fetch messages from "messages.yml"
	 * 
	 * @param alias        The messages.yml alias
	 * @param replacements Any replacements. for example, {0} would be the first
	 *                     replacement argument.
	 * @return The string gathered from the messages.yml, or null if none was found.
	 */
	public static String i18n(String alias, Object... replacements) {
		String msg = instance.messagesConfig.getString(alias);

		for (int i = 0; i < replacements.length; i++) {
			if (msg.contains("{" + i + "}"))
				msg = msg.replace("{" + i + "}", String.valueOf(replacements[i]));
		}

		return tl(msg);
	}

	/**
	 * Gets the kick message from messages.yml
	 * 
	 * @return The kick message from messages.yml
	 *         {@link (messages.disallow-message)}, as a string.
	 */
	public static String getKickMessageString() {
		StringBuilder builder = new StringBuilder();

		for (String str : instance.messagesConfig.getStringList("messages.disallow-message")) {
			builder.append(tl(str) + '\n');
		}

		return builder.toString();
	}

	public static Maintenance getInstance() {
		return instance;
	}

}
