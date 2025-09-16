package dev.cworldstar.cwshared.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandConsumer {

	private String permission;
	private boolean hide = false;
	
	public boolean hidden() {
		return hide;
	}
	
	public void hide() {
		hide = true;
	}
	
	protected abstract void execute(CommandSender player, ArrayList<String> args);
	
	public CommandConsumer() {
		
	}
	
	public String help() {
		return "The command does not implement a help string.";
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public boolean hasPermission(Player player) {
		
		if(this.permission == null) {
			return true;
		}
		
		return player.hasPermission(this.permission);
	}
	 
	public void accept(CommandSender player, ArrayList<String> args) {
		if(!(args.size() == 0)) {
			args.remove(0);
		}

		this.execute(player, args);
	}

	protected List<String> getCompletions(int length) {
		return null;
	};

	protected List<String> getCompletions(Player p, int length) {
		return null;
	}
}
