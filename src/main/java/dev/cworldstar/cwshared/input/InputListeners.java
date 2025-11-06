package dev.cworldstar.cwshared.input;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class InputListeners implements Listener {
	
	@SuppressWarnings("unused")
	private JavaPlugin plugin;
	
	public InputListeners(JavaPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEvent(AsyncChatEvent event) {
		Player player = event.getPlayer();
		if(ChatInput.hasAwaitingChatInput(player)) {
			event.setCancelled(true);
			ChatInput.getHandlerList(player).forEach(input -> {
				input.complete(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
				input.resolve();
			});
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(ChatInput.hasAwaitingChatInput(player)) {
			ChatInput.getHandlerList(player).forEach(input -> {
				input.remove();
			});
		}
	}
}
