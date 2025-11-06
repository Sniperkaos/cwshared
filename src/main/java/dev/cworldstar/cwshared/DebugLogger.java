package dev.cworldstar.cwshared;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * Simple way to create debug logging without needing to edit your
 * config.yml.
 * 
 * @author cworldstar
 *
 */
@NoArgsConstructor
public class DebugLogger {
	
	private static JavaPlugin plugin;
	@Getter
	private static boolean active = false;
	
	public static void setup(JavaPlugin plugin) {
		FileConfiguration config = plugin.getConfig();
		if(!config.isSet("debug")) {
			config.set("debug", false);
		}
		try {
			config.save(new File(plugin.getDataFolder() + "/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		active = config.getBoolean("debug");
		DebugLogger.plugin = plugin;
	}
	
	public static void log(Level level, Object... strings) {
		if(plugin != null) {
			if(active) {
				for(Object str : strings) {
					if(str instanceof String) {
						Bukkit.getLogger().log(level, "[" + plugin.getName() + "]: " + (String) str);
					} else {
						Bukkit.getLogger().log(level, "[" + plugin.getName() + "]: " + str.toString());
					}
				}
			}
		}
	}
	
	public static void log(Object...strings) {
		log(Level.INFO, strings);
	}
}
