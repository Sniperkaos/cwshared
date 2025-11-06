package dev.cworldstar.cwshared.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.cworldstar.cwshared.annotations.YamlSerializable;

public class Config implements Listener {
	
	private File cfg;
	private YamlConfiguration config;
	private JavaPlugin owningPlugin;
	boolean autoSave = false;
	
	public void autoSave(JavaPlugin plugin) {
		owningPlugin = plugin;
		autoSave = true;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void autoSaveEnabled(boolean save) {
		autoSave = save;
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		// no need to nullcheck owning plugin, this listener will never fire unless owning plugin != null, see #autoSave.
		if(e.getPlugin().getName().contentEquals(owningPlugin.getName()) && autoSave) {
			save();
		}
	}
	
	protected void cfg(File cfg) {
		this.cfg = cfg;
	}
	
	protected void config(YamlConfiguration config) {
		this.config = config;
	}
	
	@Nullable
	public boolean getBoolean(@NotNull String loc) {
		return config.getBoolean(loc);
	}
	
	@Nullable
	public String getString(@NotNull String loc) {
		return config.getString(loc);
	}
	
	@Nullable
	public ItemStack getItem(@NotNull String loc) {
		return config.getItemStack(loc);
	}
	
	@Nullable
	public int getInt(@NotNull String loc) {
		return config.getInt(loc);
	}
	
	@Nullable
	public long getLong(@NotNull String loc) {
		return config.getLong(loc);
	}
	
	@NotNull
	public List<String> getStringList(@NotNull String loc) {
		return config.getStringList(loc);
	}
	
	@NotNull
	public List<Integer> getIntList(@NotNull String loc) {
		return config.getIntegerList(loc);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends ConfigurationSerializable> T getSerializable(@NotNull String loc, Class<T> clazz) {
		return (T) config.get(loc, clazz);
	}
	
	public void set(@NotNull String path, Object value) {
		if(!(value.getClass().isPrimitive() ^ (value instanceof YamlSerializable) ^ (value instanceof List) ^ (value instanceof Map))) { //TODO: stronger checks on generics for list and map
			Bukkit.getLogger().log(Level.WARNING, "A plugin attempted to set an object value which was not a primitive or an instance of YamlSerializable! It is not cancelled, but this may cause issues.");
			Thread.dumpStack();
		}
		config.set(path, value);
	}
	
	public void write(@NotNull String path, Object value) {
		set(path, value);
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
			if(config == null) {
				config = YamlConfiguration.loadConfiguration(cfg);
			}
			config.save(cfg);
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
