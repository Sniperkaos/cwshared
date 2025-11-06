package dev.cworldstar.cwshared.utils;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.cworldstar.cwshared.events.TickerTickEvent;
import lombok.Getter;
import lombok.Setter;

public class Ticker {
	

	
	@Getter
	@Setter
	private static long tickRate = 20;
	
	private BukkitTask tickerTask;
	private ArrayList<Consumer<Integer>> handlers = new ArrayList<Consumer<Integer>>();
	private JavaPlugin plugin;
	private BukkitRunnable tickerRunnable = new TickerRunnable();
	
	public Ticker(JavaPlugin plugin) {
		this.plugin = plugin;
		tickerTask = tickerRunnable.runTask(plugin);
	}
	
	public BukkitTask getTickerTask() {
		return tickerTask;
	}

	public void registerTask(Consumer<Integer> task) {
		this.handlers.add(task);
	}


	public class TickerRunnable extends BukkitRunnable {
		public long ticks = 0;
		
		@Override
		public void run() {
			if(ticks >= Integer.MAX_VALUE) {
				ticks = 0;
			}
			ticks += 1;
			handlers.forEach((Consumer<Integer> handler) -> {
				handler.accept(getTaskId());

			});
			Bukkit.getPluginManager().callEvent(new TickerTickEvent(ticks));
			tickerTask = new TickerRunnable().runTaskLater(plugin, tickRate);
		}
	}
}
