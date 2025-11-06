package dev.cworldstar.cwshared.hologram;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import dev.cworldstar.cwshared.annotations.YamlSerializable;
import dev.cworldstar.cwshared.utils.FormatUtils;

public class ArmorStandHologram implements YamlSerializable {
	private World world;
	private ArmorStand stand;
	private List<String> text;
	
	public ArmorStandHologram(Location loc, List<String> text) {
		world = loc.getWorld();
		stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		this.text = text;
		
		// apply entity edits to armor stand
		stand.setGravity(false);
		stand.setInvulnerable(true);
		stand.setInvisible(true);
	}
	
	private void render() {
		for(String t : text) {
			stand.customName(FormatUtils.of(t));
		}
	}
	
	public void set(int line, String content) {
		text.set(line, content);
		render();
	}
	
	public void append(String content) {
		text.add(content);
		render();
	}
	
	public ArmorStandHologram(World world, UUID entityId, List<String> text) throws NoSuchObjectException {
		this.world = world;
		ArmorStand stand = (ArmorStand) world.getEntity(entityId);
		if(stand == null) {
			throw new NoSuchObjectException("Tried to get entity " + entityId.toString() + ", but it did not exist.");
		} else {
			this.stand = stand;
		}
		this.text = text;
	}
	
	@Override
	public Map<String, Object> serialize() {
		return Map.of("world", world.getName(), "stand", stand.getUniqueId().toString(), "text", text);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends YamlSerializable> T deserialize(Map<String, Object> serialized) {
		try {
			return (T) new ArmorStandHologram(Bukkit.getWorld((String) serialized.get("world")), UUID.fromString((String) serialized.get("stand")), (List<String>) serialized.get("text"));
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
		return null;
	}
}
