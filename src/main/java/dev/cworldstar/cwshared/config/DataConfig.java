package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * This class creates a YamlConfiguration which 
 * should be used for data storage.
 * @author cworldstar
 *
 */
public class DataConfig {
	
	private File cfg;
	private YamlConfiguration config;
	
	public DataConfig(File config) {
		if(!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cfg = config;
	}
	
	public YamlConfiguration asYamlConfiguration() {
		if(config == null) {
			config = YamlConfiguration.loadConfiguration(cfg);
		}
		return config;
	}
	
	public void reload() {
		config = YamlConfiguration.loadConfiguration(cfg);
	}
	
	public YamlConfiguration load() {
		return asYamlConfiguration();
	}
	
	public File asFile() {
		return cfg;
	}
	
	public void save() {
		try {
			config.save(cfg);
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
