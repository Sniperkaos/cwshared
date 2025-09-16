package dev.cworldstar.cwshared.events;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class TickerTickEvent extends Event {
	
	@Getter
	private long tick = 0;
	
	private static final HandlerList handlers = new HandlerList();
	
	public TickerTickEvent(@NotNull long tick) {
		this.tick = tick;
	}
	
	@Override
	public @Nonnull HandlerList getHandlers() {
		return getHandlerList();
	}

	public static @Nonnull HandlerList getHandlerList() {
		return handlers;
	}

}
