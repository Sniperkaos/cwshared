package dev.cworldstar.cwshared;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.utils.FormatUtils;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
/**
 * A method for allowing different interchangeable languages.
 * Easily changed in config & easily usable for developers.
 * Does not auto-generate a lang file! This must be supplied in
 * the resources folder.
 * <br><br>
 * Use {@code Lang.get()} to get the non-static Lang instance.
 * <br><br>
 * Example usage to get a lang: <br>
 * {@code Lang.get().getWithoutPrefix(player, "EXAMPLE_LANG")}
 * 
 * @author cworldstar
 */
public class Lang implements Listener {
	
	@Getter
	private Map<UUID, String> languageMap = new HashMap<UUID, String>();
	
	private static Lang lang;
	
	@Getter
	private boolean prefixEnabled = false;
	private YamlConfiguration langConfig;
	@Getter
	private Component prefix;
	@Getter
	private String stringPrefix;
	private static HashMap<String, ConfigurationSection> LANG_CACHE = new HashMap<String, ConfigurationSection>();
	private static String DEFAULT_LANG = "<red><bold>An error occured finding lang section %lang%.</bold><red>";
	/**
	 * Gets the current Lang instance.
	 * @return The non-static Lang instance.
	 */
	public static @Nonnull Lang get() {
		return lang;
	}
	
	/**
	 * Shorthand of Map.of(String toLook, String replace)
	 * @param toLook
	 * @param replace
	 * @return
	 */
	public static Map<String, String> replacement(String toLook, String replace) {
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put(toLook, replace);
		return replacements;
	}
	
	@EventHandler
	/**
	 * Listens to a player joining and supplies it 
	 * @param e The player join event.
	 */
	public void onPlayerJoin(PlayerJoinEvent e) {
		languageMap.putIfAbsent(e.getPlayer().getUniqueId(), "en-us");
	}
	
	/**
	 * 
	 * Deprecated. Use {@link #get(Player, String, Map, boolean)} instead.
	 * 
	 * Gets a MiniMessage component of a given lang lookup string with optional replacements.
	 * @param lang
	 * @param replacements
	 * @return
	 */
	@Deprecated
	public Component getWithoutPrefix(@Nonnull Player player, @Nonnull String lang, @Nullable Map<String, String> replacements) {
		Validate.notNull(lang, "Method Lang#getWithoutPrefix must contain a lang lookup key.");
		String value = null;
		String language = languageMap.get(player.getUniqueId());
		
		if(LANG_CACHE.get(language).getString(lang) != null) {
			value = LANG_CACHE.get(language).getString(lang);
		} else if(LANG_CACHE.get(language).getStringList(lang).size() != 0) {
			List<String> toPick = LANG_CACHE.get(language).getStringList(lang);
			value = toPick.get(RandomUtils.nextInt(0, toPick.size()-1));
		}
		
		// handle keys
		if(replacements != null) {
			for(Entry<String, String> replacement : replacements.entrySet()) {
				value = value.replace(replacement.getKey(), replacement.getValue());
			}
		}
		
		if(value == null) {
			return FormatUtils.createMiniMessageComponent(DEFAULT_LANG.replace("%lang%", lang));
		}
		
		value = PlaceholderAPI.setPlaceholders(player, value);
		return FormatUtils.createMiniMessageComponent(value);
	}
	
	public Component getWithoutPrefix(@Nonnull Player player, @Nonnull String lang) {
		return getWithoutPrefix(player, lang, null);
	}
	
	/**
	 * Gets a MiniMessage component of a given lang lookup string with optional replacements.
	 * This should be the ONLY version of Lang#get() that should be used, unless you require
	 * {@link Lang#getList(OfflinePlayer, String)}.
	 * @param lang
	 * @param replacements
	 * @return
	 */
	public Component get(@Nonnull Player player, @Nonnull String lang, @Nullable Map<String, String> replacements) {
		Validate.notNull(lang, "Method Lang#get must contain a lang lookup key.");
		String value = null;
		String language = languageMap.get(player.getUniqueId());
		
		if(LANG_CACHE.get(language).getString(lang) != null) {
			value = LANG_CACHE.get(language).getString(lang);
		} else if(LANG_CACHE.get(language).getStringList(lang).size() != 0) {
			List<String> toPick = LANG_CACHE.get(language).getStringList(lang);
			int next = 0;
			if(toPick.size() == 0) {
				next=0;
			} else {
				next= toPick.size()-1;
			}
			value = toPick.get(RandomUtils.nextInt(0, next));
		}
		
		// handle keys
		if(replacements != null) {
			for(Entry<String, String> replacement : replacements.entrySet()) {
				value = value.replace(replacement.getKey(), replacement.getValue());
			}
		}
		
		if(value == null) {
			return FormatUtils.createMiniMessageComponent(DEFAULT_LANG.replace("%lang%", lang));
		}
		value = PlaceholderAPI.setPlaceholders(player, value);
		return FormatUtils.createMiniMessageComponent(value);
	}
	
	public Component get(@Nonnull Player player, @Nonnull String lang) {
		return get(player, lang, null);
	}
	
	public Component get(@Nonnull Player player, @Nonnull String lang, @Nullable Map<String, String> replacements, boolean prefix) {
		Component after = get(player, lang, replacements);
		return getPrefix().append(after);
	}
	
	public List<Component> getList(@Nonnull OfflinePlayer player, @Nonnull String lang, @Nullable Map<String, String> replacements) {
		Validate.notNull(lang, "Method Lang#getList must contain a lang lookup key.");
		String language = languageMap.get(player.getUniqueId());
		List<String> lore = LANG_CACHE.get(language).getStringList(lang);
		lore = PlaceholderAPI.setPlaceholders(player, lore);
		
		if(replacements != null) {
			for(Entry<String, String> replacement : replacements.entrySet()) {
				lore = lore.stream().map(str -> str.replace(replacement.getKey(), replacement.getValue())).toList();
			}
		}
		
		List<Component> result = lore.stream().map(line -> FormatUtils.mm(line)).collect(Collectors.toList());
		return result;
	}
	
	public List<Component> getList(OfflinePlayer player, String lang) {
		return getList(player, lang, null);
	}
	
	/**
	 * Use {@link #getAsString(Player, String)} instead. The lang class no longer
	 * appends the prefix.
	 * @param player
	 * @param lang
	 * @param replacements
	 * @return
	 */
	@Deprecated
	public String getAsStringWithoutPrefix(@Nonnull Player player, @Nonnull String lang, @Nullable Map<String, String> replacements) {
		Validate.notNull(lang, "Method Lang#getAsStringWithoutPrefix must contain a lang lookup key.");
		String value = null;
		String language = languageMap.get(player.getUniqueId());
		
		if(LANG_CACHE.get(language).getString(lang) != null) {
			value = LANG_CACHE.get(language).getString(lang);
		} else if(LANG_CACHE.get(language).getStringList(lang) != null) {
			List<String> toPick = LANG_CACHE.get(language).getStringList(lang);
			value = toPick.get(RandomUtils.nextInt(0, toPick.size()-1));
		}
		
		// handle keys
		if(replacements != null) {
			for(Entry<String, String> replacement : replacements.entrySet()) {
				value = value.replace(replacement.getKey(), replacement.getValue());
			}
		}
		
		if(value == null) {
			return DEFAULT_LANG;
		}
		
		value = PlaceholderAPI.setPlaceholders(player, value);
		
		return value;
	}
	
	public String getAsStringWithoutPrefix(@Nonnull Player player, @Nonnull String lang) {
		return getAsStringWithoutPrefix(player, lang, null);
	}
	
	public String getAsString(@Nonnull Player player, @Nonnull String lang, @Nullable Map<String, String> replacements) {
		Validate.notNull(lang, "Method Lang#getString must contain a lang lookup key.");
		String value = null;
		String language = languageMap.get(player.getUniqueId());
		
		if(LANG_CACHE.get(language).getString(lang) != null) {
			value = LANG_CACHE.get(language).getString(lang);
		} else if(LANG_CACHE.get(language).getStringList(lang) != null) {
			List<String> toPick = LANG_CACHE.get(language).getStringList(lang);
			value = toPick.get(RandomUtils.nextInt(0, toPick.size()-1));
		}
		
		// handle keys
		if(replacements != null) {
			for(Entry<String, String> replacement : replacements.entrySet()) {
				value = value.replace(replacement.getKey(), replacement.getValue());
			}
		}
		
		if(value == null) {
			return DEFAULT_LANG;
		}
		
		value = PlaceholderAPI.setPlaceholders(player, value);
	
		return value;
	}
	
	public String getAsString(@Nonnull Player player, @Nonnull String lang) {
		return getAsString(player, lang, null);
	}
	
	/**
	 * Reloads the lang file.
	 */
	public void reload(JavaPlugin plugin) {
		
		// release existing cache
		LANG_CACHE = new HashMap<String, ConfigurationSection>();
		
		// start again
		
		File langDirectory = new File(plugin.getDataFolder() + "/lang");
		if(!langDirectory.exists()) {
			langDirectory.mkdirs();
		}
		File langFile = new File(langDirectory + "/lang.yml");
		langConfig = YamlConfiguration.loadConfiguration(langFile);
		ConfigurationSection prefixSettings = langConfig.getConfigurationSection("prefix");
		prefixEnabled = prefixSettings.getBoolean("enabled");
		prefix = FormatUtils.createMiniMessageComponent(prefixSettings.getString("value"));
		stringPrefix = prefixSettings.getString("value");
		
		for(Entry<String, Object> set : langConfig.getValues(false).entrySet()) {
			if(set.getValue() instanceof ConfigurationSection) {
				if(set.getKey().contains("prefix")) continue;
				LANG_CACHE.put(set.getKey(), (ConfigurationSection) set.getValue());
			}
		}
	}
	
	public Lang(JavaPlugin plugin) {
		
		if(lang != null) {
			throw new UnsupportedOperationException("Lang can only be initialized once! Did you mean to call #reload()?");
		}
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		File langDirectory = new File(plugin.getDataFolder() + "/lang");
		if(!langDirectory.exists()) {
			langDirectory.mkdirs();
		}
		File langFile = new File(langDirectory + "/lang.yml");
		if(!langFile.exists()) {
			try {
				langFile.createNewFile();
				InputStreamReader reader = new InputStreamReader(plugin.getResource("lang.yml"));
				YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(reader);
				langConfig.save(langFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		langConfig = YamlConfiguration.loadConfiguration(langFile);
		ConfigurationSection prefixSettings = langConfig.getConfigurationSection("prefix");
		prefixEnabled = prefixSettings.getBoolean("enabled");
		prefix = FormatUtils.createMiniMessageComponent(prefixSettings.getString("value"));
		stringPrefix = prefixSettings.getString("value");
		
		for(Entry<String, Object> set : langConfig.getValues(false).entrySet()) {
			if(set.getValue() instanceof ConfigurationSection) {
				if(set.getKey().contains("prefix")) continue;
				LANG_CACHE.put(set.getKey(), (ConfigurationSection) set.getValue());
			}
		}
		
		lang = this;
	}

	/**
	 * Checks whether or not a given lang index exists in the cache.
	 * @param lang The lang to lookup.
	 * @return Whether or not the lang exists.
	 */
	public boolean langExists(String lang) {
		return LANG_CACHE.containsKey(lang);
	}

	
	/**
	 * Changes a lang for a player to the given lang.
	 * @param player The player to change.
	 * @param lang The lang to replace.
	 */
	public void change(CommandSender player, String lang) {
		Validate.isTrue(langExists(lang), "The given lang does not exist!");
		if(!(player instanceof Player)) return;
		Player p = (Player) player;
		Map<UUID, String> langMap = getLanguageMap();
		langMap.put(p.getUniqueId(), lang);
	}
}
