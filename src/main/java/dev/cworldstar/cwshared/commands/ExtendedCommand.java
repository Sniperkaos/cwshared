package dev.cworldstar.cwshared.commands;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public abstract class ExtendedCommand {
	private String command;
	private String description;
	private String perm_string;
	
	public ExtendedCommand(String command, String description, String permission) {
		
	}
	
	protected boolean checkPermission(CommandSender sender) {
		
		if(sender instanceof Server) {
			return true;
		}
		
		if(sender.hasPermission(perm_string)) {
			return true;
		}
		return false;
	}
	
	protected String getDescription() {
		return description;
	}
	
	protected String getCommandName() {
		return command;
	}
	
    protected abstract void execute(CommandSender sender, String[] args);

    protected abstract void complete(CommandSender sender, String[] args, List<String> completions);
}
