package dev.cworldstar.cwshared.input;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * A ChatInput is a {@link CompletableFuture} which is
 * hard coded to only accept string inputs from a {@link AsyncChatEvent}.
 * @author cworldstar
 *
 */
public class ChatInput extends CompletableFuture<String> {
	
	@Getter
	private static final HashMap<UUID, ConcurrentLinkedQueue<ChatInput>> handlerList = new HashMap<UUID, ConcurrentLinkedQueue<ChatInput>>();
	
	public static ConcurrentLinkedQueue<ChatInput> getHandlerList(Player player) {
		return handlerList.getOrDefault(player.getUniqueId(), new ConcurrentLinkedQueue<>());
	}
	
	public static boolean hasAwaitingChatInput(Player who) {
		return handlerList.get(who.getUniqueId()).size() > 0;
	}
	
	public void resolve() {
		handlerList.get(owner).remove(this);
	}
	
	public void remove() {
		handlerList.get(owner).remove(this);
	}
	
	@Getter
	private UUID owner;
	
	public ChatInput(Player who) {
		owner = who.getUniqueId();
		if(!handlerList.containsKey(who.getUniqueId())) {
			handlerList.put(owner, new ConcurrentLinkedQueue<>());
		}
		handlerList.get(owner).add(this);
	}


}
