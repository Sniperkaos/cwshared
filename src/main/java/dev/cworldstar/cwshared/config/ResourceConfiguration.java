package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
				InputStreamReader reader = new InputStreamReader(stream);
				YamlConfiguration.loadConfiguration(reader).save(location);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
