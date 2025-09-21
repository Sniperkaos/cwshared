package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class creates a YamlConfiguration which 
 * should be used for configuration.
 * @author cworldstar
 *
 */
public class ResourceConfiguration {
	
	private File loc;
	private YamlConfiguration config;
	
	public ResourceConfiguration(JavaPlugin plugin, File location, String resource) {
		if(!location.exists()) {
			try {
				location.createNewFile();
				InputStream stream = plugin.getResource(resource);
				if(stream == null) {
					// create empty file as resource instead.
					stream = new FileInputStream(location);
					Bukkit.getLogger().log(Level.SEVERE, "attempted to grab resource " + resource + ", but it did not exist!");
				}
				InputStreamReader reader = new InputStreamReader(stream);
				YamlConfiguration.loadConfiguration(reader).save(location);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loc = location;
	}
	
	public void reload() {
		config = YamlConfiguration.loadConfiguration(loc);
	}
	
	public File asFile() {
		return loc;
	}
	
	public YamlConfiguration asYamlConfiguration() {
		if(config == null) {
			config = YamlConfiguration.loadConfiguration(loc);
		}
		return config;
	}
	
	public YamlConfiguration load() {
		return asYamlConfiguration();
	}
	
	public void save() {
		try {
			config.save(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
