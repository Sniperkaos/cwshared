package dev.cworldstar.cwshared.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class ConfigurationSound {
	
	private Sound sound;
	
	public ConfigurationSound(String key, float volume, float pitch) {
		sound = Sound.sound().type(Key.key(key)).volume(volume).pitch(pitch).build();
	}
	
	public void play(Player player) {
		player.playSound(sound);
	}
	
	public void play(Location location) {
		location.getWorld().playSound(location, sound.name().asString(), sound.volume(), sound.pitch());
	}
}
