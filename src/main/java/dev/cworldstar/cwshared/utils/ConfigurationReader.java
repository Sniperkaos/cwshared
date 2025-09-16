package dev.cworldstar.cwshared.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ConfigurationReader {
	public static Map<String, ConfigurationSection> readSectioned(YamlConfiguration config) {
		HashMap<String, ConfigurationSection> toReturn = new HashMap<String, ConfigurationSection>();
		for(String key : config.getKeys(false)) {
			toReturn.put(key, config.getConfigurationSection(key));
		}
		return toReturn;
	}
	public static ArrayList<ConfigurationSection> readSection(ConfigurationSection config) {
		ArrayList<ConfigurationSection> toReturn = new ArrayList<ConfigurationSection>();
		for(String key : config.getKeys(false)) {
			toReturn.add(config.getConfigurationSection(key));
		}
		return toReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> ArrayList<T> read(YamlConfiguration config) {
		ArrayList<T> toReturn = new ArrayList<T>();
		try {
			for(String key : config.getKeys(false)) {
				toReturn.add((T) config.get(key));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
}
