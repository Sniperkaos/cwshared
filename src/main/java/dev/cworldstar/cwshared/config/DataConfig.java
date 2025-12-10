package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.Setup;

/**
 * This class creates a YamlConfiguration which 
 * should be used for data storage. Data configurations auto-save,
 * meaning you do not need to call {@link Config#save()} in your {@link JavaPlugin#onDisable()}
 * @author cworldstar
 */
public class DataConfig extends Config {
	public DataConfig(File config) {
		super(config);
		if(!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		autoSave(Setup.getPlugin());
		config(YamlConfiguration.loadConfiguration(config));
		for(String key : asYamlConfiguration().getKeys(true)) {
			Bukkit.getLogger().log(Level.WARNING, key);
		}
	}
	@Override
	public void reload() {
		save();
		super.reload();
	}
}
