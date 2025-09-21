package dev.cworldstar.cwshared.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.sun.source.util.Plugin;

import lombok.Getter;


public class CommandsClass {
	
	@Getter
	private MainCommand mainCommand;
	
	public MainCommand command() {
		return getMainCommand();
	}
	
	public static void executeServer(String command) {
		Server server = Bukkit.getServer();
		ConsoleCommandSender executor = server.getConsoleSender();
		server.dispatchCommand(executor, command);
	}
	
	public List<String> getRegisteredCommands() {
		return mainCommand.getSubCommands().stream().map(Entry::getKey).toList();
	}
	
	public String getHelpForCommand(String command) {
		return mainCommand.getCommand(command).help();
	}
	
	public <T extends CommandConsumer> void registerCommand(String subCommand, Class<T> clazz) {
		try {
			mainCommand.registerCommand(subCommand, clazz.getDeclaredConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will dynamically register a command at runtime if it does not exist.
	 * @param plugin The owning plugin.
	 * @param command The string representation of the command.
	 */
	public CommandsClass(JavaPlugin plugin, String command) {
		PluginCommand pcommand = null;
		try {
			pcommand = plugin.getCommand(command);
		} catch(UnsupportedOperationException e) {
			try {
				CommandMap commandMap = Bukkit.getCommandMap();
				Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				constructor.setAccessible(true);
				pcommand = constructor.newInstance(command, plugin);
				commandMap.register(command, pcommand);
			} catch(Exception err) {
				err.printStackTrace();
			}
		}
		mainCommand = new MainCommand(pcommand) {
			
		};
	}
}
