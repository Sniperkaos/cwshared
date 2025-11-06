package dev.cworldstar.cwshared.utils;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;


public class PDCHelper {
	public static @Nullable Optional<PersistentDataContainer> getPDC(@Nonnull ItemMeta meta) {
		Validate.notNull(meta, "The ItemMeta cannot be null.");
		return Optional.of(meta.getPersistentDataContainer());
	}
	
	public static void setPDC(ItemMeta meta, ItemStack i) {
		i.setItemMeta(meta);
	}
}
