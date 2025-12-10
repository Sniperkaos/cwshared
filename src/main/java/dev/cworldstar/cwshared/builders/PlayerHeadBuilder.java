package dev.cworldstar.cwshared.builders;

import java.util.UUID;

import org.bukkit.Material;

import dev.cworldstar.cwshared.utils.SkullCreator;

public class PlayerHeadBuilder extends ItemStackBuilder {

	public PlayerHeadBuilder() {
		super(Material.PLAYER_HEAD);
	}
	
	public PlayerHeadBuilder(String texture) {
		super(Material.PLAYER_HEAD);
		texture(texture);
	}
	
	public PlayerHeadBuilder texture(String texture) {
		SkullCreator.loadBase64(item, texture);
		return this;
	}
	
	public PlayerHeadBuilder player(String uuid) {
		SkullCreator.loadPlayerHead(item, UUID.fromString(uuid));
		return this;
	}

}
