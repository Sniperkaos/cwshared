package dev.cworldstar.cwshared;

import java.util.Optional;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.utils.PDCHelper;
import lombok.Setter;
/**
 * This static method class requires ProtocolLib and is automatically initialized in {@link AbstractSFAddon}.
 * To use it, first call {@link #setPlugin(JavaPlugin)}, then simply use the ItemStack wrapper {@link #setup(ItemStack, Long)}, where ItemStack is the Item to edit and Long is the max durability of the given item.
 * @author cworldstar
 *
 */
public class CustomDurabilityHandler {
	
	@Setter
	private static JavaPlugin plugin;
	
	public static ItemStack setup(ItemStack i, @Nullable Long durability) {
		ItemMeta meta = i.getItemMeta();
		if(meta instanceof Damageable) {
			Damageable dmeta = (Damageable) meta;
			Optional<PersistentDataContainer> option = PDCHelper.getPDC(dmeta);
			if(option.isPresent()) {
				PersistentDataContainer container = option.get();
				if(durability != null) {
					container.set(new NamespacedKey(plugin, "MAX_DURABILITY"), PersistentDataType.LONG, durability);
					container.set(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG, durability);
				} else {
					container.set(new NamespacedKey(plugin, "MAX_DURABILITY"), PersistentDataType.LONG, (long) i.getType().getMaxDurability());
					container.set(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG, (long) i.getType().getMaxDurability());
				}

				i.setItemMeta(dmeta);
			}
		}
		return i;
	}
	
	public static ItemStack setMaxDurability(ItemStack i, int number, boolean refreshDurability) {
		ItemMeta meta = i.getItemMeta();
		if(meta instanceof Damageable) {
			Damageable dmeta = (Damageable) meta;
			Optional<PersistentDataContainer> option = PDCHelper.getPDC(dmeta);
			if(option.isPresent()) {
				PersistentDataContainer container = option.get();
				container.set(new NamespacedKey(plugin, "MAX_DURABILITY"), PersistentDataType.LONG, (long) number + i.getType().getMaxDurability());
				if(refreshDurability) {
					container.set(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG, (long) number + i.getType().getMaxDurability());
				}
				i.setItemMeta(dmeta);
			}
		}
		return i;
	}
	
	
	public static boolean durabilityGainHandler(PlayerItemMendEvent e, Player p, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof Damageable) {
			Damageable dmeta = (Damageable) meta;
			if(meta.isUnbreakable()) {
				e.setCancelled(true);
				return false;
			}
			Optional<PersistentDataContainer> option = PDCHelper.getPDC(dmeta);
			if(option.isPresent()) {
				PersistentDataContainer container = option.get();
				long maxItemDurability = container.get(new NamespacedKey(plugin, "MAX_DURABILITY"), PersistentDataType.LONG);
				long itemDurability = container.get(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG);
				
				itemDurability += e.getRepairAmount();
				
				if(itemDurability > maxItemDurability) {
					itemDurability = maxItemDurability;
				}
				
				// update visuals
				int newDamage = (int) (Math.round(((int) item.getType().getMaxDurability() * ((double) itemDurability / maxItemDurability))));
				if(newDamage <= 0 && !(itemDurability <= 0)) {
					newDamage = 1;
				}
				dmeta.setDamage(item.getType().getMaxDurability() - newDamage);
				container.set(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG, (long) itemDurability);
				item.setItemMeta(meta);
				return true;
			}
		}
		return false;
	}
	
	public static boolean durabilityLossHandler(PlayerItemDamageEvent e, Player p, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof Damageable) {
			Damageable dmeta = (Damageable) meta;
			if(meta.isUnbreakable()) {
				e.setCancelled(true);
				return false;
			}
			Optional<PersistentDataContainer> option = PDCHelper.getPDC(dmeta);
			if(option.isPresent()) {
				PersistentDataContainer container = option.get();
				long maxItemDurability = container.get(new NamespacedKey(plugin, "MAX_DURABILITY"), PersistentDataType.LONG);
				long itemDurability = container.get(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG);
				
				itemDurability -= e.getDamage();
				// update visuals
				int newDamage = (int) (Math.round(((int) item.getType().getMaxDurability() * ((double) itemDurability / maxItemDurability))));
				if(newDamage <= 0 && !(itemDurability <= 0)) {
					newDamage = 1;
				}
				dmeta.setDamage(item.getType().getMaxDurability() - newDamage);
				container.set(new NamespacedKey(plugin, "DURABILITY"), PersistentDataType.LONG, (long) itemDurability);
				item.setItemMeta(meta);
				return true;
			}
		}
		return false;
	}
}
