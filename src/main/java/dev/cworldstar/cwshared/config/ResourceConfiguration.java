package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.FileInputStream;
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
public class ResourceConfiguration extends Config {
	
	private void loadConfig(JavaPlugin plugin, File location, String resource) {
		try {
			location.createNewFile();
			InputStream stream = plugin.getResource(resource);
			if(stream == null) {
				// create empty file as resource instead.
				stream = new FileInputStream(location);
				// print a NPE to inform the developer
				new NullPointerException("The given resource did not exist! Resource path: " + resource).printStackTrace();
			}
			
			InputStreamReader reader = new InputStreamReader(stream);
			YamlConfiguration.loadConfiguration(reader).save(location);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ResourceConfiguration(JavaPlugin plugin, File location, String resource) {
		if(!location.exists()) {
			loadConfig(plugin, location, resource);
		}
		cfg(location);
	}
}
