package dev.cworldstar.cwshared.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;


public class CommandsClass {
	
	@Getter
	private MainCommand mainCommand;
	
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
	
	public CommandsClass(JavaPlugin plugin, String command) {
		mainCommand = new MainCommand(plugin.getCommand(command));
	}
}
