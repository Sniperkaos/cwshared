package dev.cworldstar.cwshared.utils;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	
	/**
	 * 
	 * {@link InventoryUtils#locateMutableStack(Inventory, ItemStack)} takes an Inventory and an ItemStack.
	 * If it does not find an itemstack matching the given item, it will return -1. This method cannot be null.
	 * 
	 * @param {@link Inventory}
	 * @param {@link ItemStack}
	 * @return {@link Integer}
	 */
	
	@Nonnull
	public static Integer locateMutableStack(Inventory toLook, @Nonnull final ItemStack toCompare) {
		for(int i=0; i<toLook.getSize(); i++) {
			ItemStack at = toLook.getItem(i);
			if(at == null) continue;
			if(toCompare.isSimilar(at)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void pushItems(Inventory i, ItemStack ...stacks) {
		Map<Integer, ItemStack> noAdd = i.addItem(stacks);
		if(noAdd.size() > 0) {
			for(Entry<Integer, ItemStack> items : noAdd.entrySet()) {
				InventoryHolder holder = i.getHolder();
				if(holder instanceof LivingEntity) {
					LivingEntity entity = ((LivingEntity) holder);
					World w = entity.getWorld();
					w.dropItem(entity.getLocation(), items.getValue());
				}
			}
		}
	}
	
}


