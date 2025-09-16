package dev.cworldstar.cwshared.utils;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.cworldstar.cwshared.events.TickerTickEvent;

public class Ticker {
	
	private BukkitTask tickerTask;
	private ArrayList<Consumer<Integer>> handlers = new ArrayList<Consumer<Integer>>();
	
	
	public Ticker(JavaPlugin plugin) {
		tickerTask = new BukkitRunnable() {
			
			private long ticks = 0;
			
			@Override
			public void run() {
				if(ticks >= Integer.MAX_VALUE) {
					ticks = 0;
				}
				ticks += 1;
				Ticker.this.handlers.forEach((Consumer<Integer> handler) -> {
					handler.accept(this.getTaskId());

				});
				Bukkit.getPluginManager().callEvent(new TickerTickEvent(ticks));
			}
			
		}.runTaskTimer(plugin, 0, 20L);
	}
	
	public BukkitTask getTickerTask() {
		return tickerTask;
	}

	public void registerTask(Consumer<Integer> task) {
		this.handlers.add(task);
	}
	
	
}
