package dev.cworldstar.cwshared.serialization;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import dev.cworldstar.cwshared.annotations.YamlSerializable;

public class SerializableWorld extends AbstractSerializable<World> {
	
	private String worldName;
	
	public SerializableWorld(World world) {
		super(world);
		worldName = world.getName();
	}
	
	@Override
	public Map<String, Object> serialize() {
		return Map.of("name", worldName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends YamlSerializable> T deserialize(Map<String, Object> serialized) {
		return (T) new SerializableWorld(Bukkit.getWorld((String) serialized.get("name")));
	}
}