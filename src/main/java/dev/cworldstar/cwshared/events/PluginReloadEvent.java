package dev.cworldstar.cwshared.events;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * This event is called whenever the ReloadCommand is used. While this event
 * implements cancellable, cancelling this event does nothing by itself.
 * You must implement logic for when {@link #isCancelled()} returns true for {@link #setCancelled(boolean)}
 * or {@link #cancel()} to do anything.
 * @author cworldstar
 *
 */
public class PluginReloadEvent extends Event implements Cancellable {

	private static final HandlerList handlerList = new HandlerList();
	
	@Getter
	private CommandSender source;
	private boolean cancelled;
	@Getter
	private JavaPlugin plugin;
	
	@ParametersAreNonnullByDefault
	public PluginReloadEvent(@NotNull JavaPlugin plugin, @NotNull CommandSender source) {
		this.source = source;
		this.plugin = plugin;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	public void cancel() {
		setCancelled(true);
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return getHandlerList();
	}
	
	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
