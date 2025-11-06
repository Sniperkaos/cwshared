package dev.cworldstar.cwshared.serialization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dev.cworldstar.cwshared.annotations.YamlSerializable;

/**
 * A SerializeableInventory is a snapshot of a {@link Inventory}
 * which can be used in {@link YamlConfiguration}s.
 * @author cworldstar
 *
 */
public class SerializableInventory implements YamlSerializable {
	
	private int size;
	private List<ItemStack> contents;
	
	public SerializableInventory(Inventory inv) {
		register();
		size = inv.getSize();
		contents = Arrays.asList(inv.getContents());
	}
	
	public Inventory asInventory() {
		Inventory inv = Bukkit.createInventory(null, size);
		inv.setContents(contents.toArray(ItemStack[]::new));
		return inv;
	}
	
	@Override
	public Map<String, Object> serialize() {
		return Map.of("size", size, "contents", contents);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends YamlSerializable> T deserialize(Map<String, Object> serialized) {
		Inventory inventory = Bukkit.createInventory(null, (int) serialized.get("size"));
		inventory.setContents(((List<ItemStack>) serialized.get("contents")).toArray(ItemStack[]::new));
		return (T) new SerializableInventory(inventory);
	}
}
