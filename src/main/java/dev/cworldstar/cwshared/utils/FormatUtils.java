package dev.cworldstar.cwshared.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

/**
 * A static class containing common Component utilities for
 * working with kyori adventure library. Should not be instanced.
 * @author cworldstar
 *
 */
public class FormatUtils {
	
	private static final MiniMessage MINI_MESSAGE_FORMATTER = MiniMessage.builder()
			.tags(TagResolver.standard())
			.build();
	
	public static enum MessageType {
		MINI_MESSAGE,
		COLOR_CODES
	}

	/**
	 * Converts MiniMessage formatting to legacy color codes.
	 * @param from The string to query
	 * @return The legacy color coded string
	 */
	public static String convertFromColorCodes(String from) {
		for(Character c : ChatColor.ALL_CODES.toCharArray()) {
			from = from.replace("" + ChatColor.COLOR_CHAR + c, getChatColorReplacement(c));
		}
		return from;
	}
	
	private static Map<Character, String> replacement = new HashMap<Character, String>();
	static {
		replacement.put('0', "<black>");
		replacement.put('1', "<dark_blue>");
	    replacement.put('2', "<dark_green>");
	    replacement.put('3', "<dark_aqua>");
	    replacement.put('4', "<dark_red>");
	    replacement.put('5', "<dark_purple>");
	    replacement.put('6', "<gold>");
	    replacement.put('7', "<gray>");
	    replacement.put('8', "<dark_gray>");
	    replacement.put('9', "<blue>");
	    replacement.put('a', "<green>");
	    replacement.put('b', "<aqua>");
	    replacement.put('c', "<red>");
	    replacement.put('d', "<light_purple>");
	    replacement.put('e', "<yellow>");
	    replacement.put('f', "<white>");
	    replacement.put('m', "<st>");
	    replacement.put('k', "<obf>");
	    replacement.put('o', "<i>");
	    replacement.put('l', "<b>");
	    replacement.put('r', "<r>");
	}
	
	private static String getChatColorReplacement(char c) {
		return replacement.getOrDefault(c, "<red>");
	}
	
	/**
	 * Creates a status bar.
	 * @param work The amount of progress.
	 * @param requiredWork The max amount of progress
	 * @return A string representing the progress completion in graphical bars.
	 */
	public static String makeMachineCompletion(int work, int requiredWork) {
		double workCompletedPercent = ((double) work) / requiredWork;
		if(workCompletedPercent > 1) {
			return "<green>||||||||||||";
		}
		String processItemName = "||||||||||||";
		int substr = (int) Math.round(processItemName.length() * workCompletedPercent);
		String completed = "<green>" + processItemName.substring(0, substr) + "<red>";
		completed.replaceAll("|", "I");
		for(int x=substr; x<= processItemName.length(); x++) {
			completed = completed + "|";
		}
		return completed;
	}
	
	private static final Pattern cmatcher = Pattern.compile("(§[a-zA-Z0-9])|(&[a-zA-Z0-9])");
	
	public FormatUtils() {
		throw new UnsupportedOperationException("This is a static class!");
	}
	
	/**
	 * Converts a given string of text from MiniMessage formatting
	 * to a legacy color coded string.
	 * @param text The input
	 * @return The result of {@link #mm(convertFromColorCodes(text))}
	 */
	public static Component convert(String text) {
		return mm(convertFromColorCodes(text));
	}
	
	public static MessageType findMessageType(String string) {
		Matcher matcher = cmatcher.matcher(string);
		if(matcher.find()) {
			return MessageType.COLOR_CODES;
		}
		return MessageType.MINI_MESSAGE;
	}
	
	/**
	 * This method creates a MiniMessage component from
	 * a text input. It supports legacy tags with &, and
	 * modern kyori tags as well.
	 * 
	 * @param text The minimessage text
	 * @return The resulting component from deserializing with standard tags, 
	 * or from translating color codes if legacy color codes are matched.
	 */
	public static Component createMiniMessageComponent(String text) {
		Matcher matcher = cmatcher.matcher(text);
		if(matcher.find()) {
			return LegacyComponentSerializer.builder().hexColors().extractUrls().character('&').build().deserialize(text);
		}
		return MINI_MESSAGE_FORMATTER.deserialize(text).decoration(TextDecoration.ITALIC, false);
	}
	
	/**
	 * Shorthand version of {@link #createMiniMessageComponent(String)}
	 * @param s
	 * @return
	 */
	public static Component mm(String text) {
		return createMiniMessageComponent(text);
	}
	
	/**
	 * Deprecated legacy method. Use {@link #mm(String)} instead.
	 * @param s
	 * @return
	 */
	@Deprecated
	public static TextComponent createComponent(String text) {
		return Component.text(ChatColor.translateAlternateColorCodes('&', text));
	}
	
	/**
	 * Replaces a list of strings with a list of mini message components.
	 * @param lore The lore strings to replace.
	 * @return A list of components.
	 */
	public static List<Component> loreComponent(List<String> lore) {
		return lore.stream().map(str -> mm(str)).collect(Collectors.toList());
	}
	
	/**
	 * Translates alternate color codes.
	 * @param s
	 * @return
	 */
	public static String formatString(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	/**
	 * Deprecated legacy method. Use {@link #mm(String)} instead.
	 * @param s
	 * @return
	 */
	@Deprecated
	public static Component formatAndCast(String s) {
		return Component.text(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	@Deprecated
	public static List<TextComponent> getLore(ItemStack i) {
		ItemMeta meta = i.getItemMeta();
		List<TextComponent> lore = meta.lore().stream().map(component->(TextComponent) component).collect(Collectors.toList());
		return lore;
	}
	
	public static Component replace(Component toReplace, String pattern, String value) {
		return toReplace.replaceText(builder -> {
			builder.match(pattern).replacement(value);
		});
	}
	
	/**
	 * Deprecated legacy method. Use {@link #replace(Component, String, String)} instead.
	 */
	@Deprecated
	public static TextComponent replace(TextComponent toReplace, String pattern, String newValue) {
		return (TextComponent) toReplace.replaceText(TextReplacementConfig.builder().match(pattern).replacement(newValue).build());
	}
	
	public static List<TextComponent> replaceAll(List<TextComponent> toReplace, String pattern, String newValue) {
		toReplace.replaceAll(textComponent -> {
			 return replace(textComponent, pattern, newValue);
		});
		return toReplace;
	}
	
	/**
	 * Deprecated legacy method. Use {@link #mm(String)} instead.
	 * @param s
	 * @return
	 */
	@Deprecated
	public static TextComponent asText(String s) {
		return Component.text(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	/**
	 * Deprecated legacy method. Use {@link #mm(String)} instead.
	 * @param s
	 * @return
	 */
	@Deprecated
	public static TextComponent format(String s) {
		return asText(s);
	}
	
	/**
	 * Creates lore from a string array.
	 * @param lore
	 * @return
	 */
	public static @NotNull List<Component> lore(String[] lore) {
		return loreComponent(Arrays.asList(lore));
	}
	
	/**
	 * Returns an empty minimessage component.
	 * @return An empty minimessage component.
	 */
	public static Component empty() {
		return mm("");
	}
	
    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {
    	map.put(1000, "M");map.put(900, "CM");map.put(500, "D");map.put(400, "CD");map.put(100, "C");map.put(90, "XC");map.put(50, "L");map.put(40, "XL");map.put(10, "X");map.put(9, "IX");map.put(5, "V");map.put(4, "IV");map.put(1, "I");
    }

    /**
     * Converts a number to roman numerals. Extremely
     * high numbers will fail.
     * @param number The number to convert
     * @return The string representation.
     */
	public static String toRoman(int number) {
        Integer l = map.floorKey(number);
        if(l == null) {
        	l = 0;
        }
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }
}
