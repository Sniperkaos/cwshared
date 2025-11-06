package dev.cworldstar.cwshared.utils;

import java.util.function.Consumer;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import dev.cworldstar.cwshared.ui.MenuHandler;
import dev.cworldstar.cwshared.ui.SimpleMenuHandler;

public class MenuHandlerUtils {
	public static final MenuHandler<InventoryClickEvent> EMPTY_HANDLER = new SimpleMenuHandler<InventoryClickEvent>() {
		@Override
		public void handler(InventoryClickEvent event) {
			event.setCancelled(true);
		}
	};
	
	public static <T extends Event> MenuHandler<T> createHandler(Consumer<T> handler) {
		return new MenuHandler<T>(handler);
	}
}
