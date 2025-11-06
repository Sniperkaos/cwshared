package dev.cworldstar.cwshared.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandConsumer {

	public static enum PermissionBehavior {
		HIDE_WITHOUT_PERMISSION,
		LEAVE_VISIBLE
	}
	
	private String permission;
	private boolean hide = false;
	private PermissionBehavior behavior = PermissionBehavior.LEAVE_VISIBLE;
	
	public PermissionBehavior behavior() {
		return behavior;
	}
	
	/**
	 * @return If this command is hidden.
	 */
	public boolean hidden() {
		return hide;
	}
	
	/**
	 * Hides the command. This means that it will not show up in
	 * autocomplete.
	 */
	public void hide() {
		hide = true;
	}
	
	protected abstract void execute(CommandSender player, ArrayList<String> args);
	
	/**
	 * @return The command's help string. Must be overridden to be implemented.
	 */
	public String help() {
		return "The command does not implement a help string.";
	}
	
	/**
	 * Sets this command's permission
	 * @param permission The {@link String} permission.
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	/**
	 * Tests the player for this command's permission. 
	 * @param player The player to test.
	 * @return Whether or not the player can use this command.
	 */
	public boolean hasPermission(Player player) {
		
		if(this.permission == null) {
			return true;
		}
		
		return player.hasPermission(this.permission);
	}
	 
	/**
	 * Used internally.
	 * @param player
	 * @param args
	 */
	public void accept(CommandSender player, ArrayList<String> args) {
		if(!(args.size() == 0)) {
			args.remove(0);
		}

		execute(player, args);
	}

	//-- One of the following MUST be overwritten to use a CommandConsumer.
	
	protected List<String> getCompletions(int length) {
		return null;
	};

	protected List<String> getCompletions(List<String> args, int length) {
		return null;
	};
	
	protected List<String> getCompletions(Player p, int length) {
		return null;
	}
}
