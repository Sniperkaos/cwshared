package dev.cworldstar.cwshared.commands.defaults;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.commands.CommandConsumer;
import dev.cworldstar.cwshared.events.PluginReloadEvent;
import dev.cworldstar.cwshared.utils.FormatUtils;
import dev.cworldstar.cwshared.annotations.ErrorsIf;

public class ReloadCommand extends CommandConsumer {	
	private JavaPlugin plugin;
	
	public ReloadCommand(JavaPlugin plugin, String permission) {
		setPermission(permission);
		this.plugin = plugin;
	}
	
	@Override
	@ErrorsIf(Reason = "You do not properly catch possible errors in event handlers.")
	protected void execute(CommandSender player, ArrayList<String> args) {
		PluginReloadEvent event = new PluginReloadEvent(plugin, player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			player.sendMessage(FormatUtils.mm("<red>An error occured reloading! Check console."));
		}
	}
}
