package dev.cworldstar.cwshared.packets;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import dev.cworldstar.cwshared.Setup;
import dev.cworldstar.cwshared.annotations.YamlSerializable;
import dev.cworldstar.cwshared.events.TickerTickEvent;
import lombok.Getter;

public class PacketEntity implements YamlSerializable, Listener {

	private static final AtomicInteger entityCounter = new AtomicInteger(1337);
	
	private Vector location;
	
	private World world;
	@Getter
	private EntityType entityType;
	@Getter
	private int yaw;
	@Getter
	private int pitch;
	@Getter
	private int entityId;
	@Getter
	private UUID entityUUID;
	
	public void setYaw(int angle) {
		Validate.isTrue(angle <= 360 && angle >= -360, "The angle cannot exceed 360 degrees!");
		yaw = angle;
	}
	
	public void setPitch(int angle) {
		Validate.isTrue(angle <= 360 && angle >= -360, "The angle cannot exceed 360 degrees!");
		pitch = angle;
	}
	
	public Location getLocation() {
		return new Location(world, location.getX(), location.getY(), location.getZ(), yaw, pitch);
	}
	
	@EventHandler
	public void onTickerTick(TickerTickEvent e) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			tryDisplay(player);
		});
	}

	
	/**
	 * Creates a new packet entity at a location.
	 * @param at The location to spawn this entity at.
	 * @param entity The entity type to spawn.
	 */
	public PacketEntity(Location at, EntityType entity) {
		register();
		location = new Vector(at.getX(), at.getY(), at.getZ());
		world = at.getWorld();
		entityType = entity;
		yaw = 0;
		pitch = 0;
		entityId = -entityCounter.getAndIncrement();
		entityUUID = UUID.randomUUID();
		Bukkit.getPluginManager().registerEvents(this, Setup.getPlugin());
	}
	
	/**
	 * This method sends the packet entity to the given player. To
	 * call it, see {@link #tryDisplay(Player)}, which determines whether
	 * or not a player is able to view the entity before sending the packet.
	 * @see <link>https://minecraft.wiki/w/Java_Edition_protocol/Packets#Spawn_Entity</link>
	 * @param player The player to send the packet to.
	 * @author cworldstar
	 */
	@SuppressWarnings("removal")
	private void display(Player player) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
		
		// entity ID
		container.getIntegers().write(0, entityId);
		
		// entity UUID
		container.getUUIDs().write(0, entityUUID);
		
		// entity type
		container.getIntegers().write(1, (int) entityType.getTypeId());
		
		// position
		container.getDoubles().write(0, location.getX()).write(1, location.getY()).write(2, location.getZ());
		
		// pitch and yaw
		container.getBytes().write(4, (byte) (yaw * (256F/360F))).write(5, (byte) (pitch * (256F/360F))).write(5, (byte) (yaw * (256F/360F)));
		
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
		} catch(Exception | Error e) {
			e.printStackTrace();
		}
	}
	
	public void tryDisplay(Player player) {
		if(player.hasLineOfSight(getLocation())) {
			display(player);
		}
	}
	
	private PacketEntity(World world, Vector location, EntityType type, int yaw, int pitch, int entityId, UUID entityUUID) {
		this.world = world;
		this.location = location;
		entityType = type;
		this.yaw = yaw;
		this.pitch = pitch;
		this.entityId = entityId;
		this.entityUUID = entityUUID;
		Bukkit.getPluginManager().registerEvents(this, Setup.getPlugin());
	}
	
	@Override
	public Map<String, Object> serialize() {
		return Map.of("world", world.getName(), 
			"location", location, 
			"entityType", entityType.toString(), 
			"yaw", yaw, 
			"pitch", pitch, 
			"entityId", entityId, 
			"entityUUID", entityUUID.toString()
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends YamlSerializable> T deserialize(Map<String, Object> serialized) {
		return (T) new PacketEntity(
			Bukkit.getWorld((String) serialized.get("world")),
			(Vector) serialized.get("location"), 
			EntityType.valueOf((String) serialized.get("entityType")),
			(int) serialized.get("yaw"),
			(int) serialized.get("pitch"),
			(int) serialized.get("entityId"),
			UUID.fromString((String) serialized.get("entityUUID"))
		);
	}

}
