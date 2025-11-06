package dev.cworldstar.cwshared;

import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.input.InputListeners;
import lombok.Getter;

public class Setup {
	@Getter
	private static JavaPlugin plugin;
	
	public static void setup(JavaPlugin plugin) {
		Setup.plugin = plugin;
		new InputListeners(plugin);
	}
}
