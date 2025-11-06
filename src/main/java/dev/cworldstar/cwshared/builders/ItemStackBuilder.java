package dev.cworldstar.cwshared.builders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import dev.cworldstar.cwshared.CustomDurabilityHandler;
import dev.cworldstar.cwshared.utils.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ItemStackBuilder {
	
	public static ItemStackBuilder create(Material m) {
		return new ItemStackBuilder(m);
	}
		
	protected ItemStack item;
	
	/**
	 * Creates a {@link ItemStack} with specific methods for
	 * ease-of-access.
	 * @author cworldstar
	 * @param m The material to create the item stack from.
	 */
	public ItemStackBuilder(Material m) {
		item = new ItemStack(m);
	}
	
	public ItemStackBuilder condition(boolean check, Consumer<ItemStackBuilder> run) {
		if(check) {
			run.accept(this);
		}
		return this;
	}
	
	public ItemStackBuilder condition(boolean check, Method m, Object...args) {
		if(!check) return this;
		try {
			m.invoke(this, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public ItemStackBuilder(ItemStack i) {
		this.item = i;
	}
	
	/**
	 * WARNING: This will not work if ProtocolLib is not installed!
	 * @param durability The durability the item will have.
	 * @return The current ItemStackBuilder.
	 */
	public ItemStackBuilder setMaxDurability(long durability) {
		Validate.isTrue(item.getItemMeta() instanceof Damageable);
		CustomDurabilityHandler.setup(item, durability);
		return this;
	}
	
	public ItemStackBuilder setCustomModelData(CustomModelDataComponent component) {
		item.editMeta(meta -> {
			meta.setCustomModelDataComponent(component);
		});
		return this;
	}
	
	private @Nonnull ItemMeta getMeta() {
		return item.getItemMeta();
	}
	
	private void setMeta(@Nonnull ItemMeta meta) {
		item.setItemMeta(meta);
	}
	
	public ItemStackBuilder setAmount(int amount) {
		this.item.setAmount(amount);
		return this;
	}
	
	public ItemStackBuilder setName(String name) {
		ItemMeta meta = getMeta();
		
		meta.displayName(
			FormatUtils.mm(name)
		);
		
		setMeta(meta);
		return this;
	}
	
	public ItemStackBuilder setLore(String[] lore) {
		ItemMeta meta = getMeta();
		List<Component> mlore;
		if(meta.hasLore()) {
			mlore = meta.lore();
		} else {
			mlore = new ArrayList<Component>();
		}
		for(String line : lore) {
			mlore.add(FormatUtils.mm(line));
		}
		meta.lore(mlore);
		setMeta(meta);
		return this;
	}
	
	public ItemStack get() {
		return item;
	}
	
	public ItemStack item() {
		return get();
	}
	
	public ItemStack build() {
		return get();
	}
	
	public ItemStackBuilder amount(int amount) {
		Validate.isTrue(amount <= 64, "You cannot set a stack size higher than the maximum amount!");
		item.setAmount(amount);
		return this;
	}

	public ItemStackBuilder empty() {
		this.item.editMeta(meta -> {
			meta.displayName(FormatUtils.mm(""));
		});
		return this;
	}

	public ItemStackBuilder name(String string) {
		return setName(string);
	}

	public ItemStackBuilder lore(String[] lore) {
		return setLore(lore);
	}
	
	public ItemStackBuilder lores(String...lore) {
		return setLore(lore);
	}

	/**
	 * This method casts a {@link ItemStackBuilder} to a given extended method. Fails
	 * if this builder is not originally an instance of T.
	 * @param <T> The extended class to cast.
	 * @param clazz The class instance, equal to Builder.class.
	 * @return This builder casted to T, or itself if it is not an instance of T.
	 */
	public <T extends ItemStackBuilder> T cast(Class<T> clazz) {
		boolean extendable = clazz.isInstance(this);
		Validate.isTrue(extendable, "When using the #cast method in ItemStackBuilder, you may only cast when this is an instance of clazz!");
		return clazz.cast(this);
	}
	
	public ItemStackBuilder glowing() {
		this.item.editMeta(meta -> {
			meta.setEnchantmentGlintOverride(true);
		});
		return this;
	}

	public ItemStackBuilder attribute(JavaPlugin plugin, String key, Attribute attribute, double value, EquipmentSlotGroup group, Operation type) {
		item.editMeta(meta -> {
			meta.addAttributeModifier(attribute, new AttributeModifier(new NamespacedKey(plugin, key), value, type, group));
		});
		return this;
	}

	public ItemStackBuilder attribute(Attribute attribute, AttributeModifier modifier) {
		item.editMeta(meta -> {
			meta.addAttributeModifier(attribute, modifier);
		});
		return this;
	}
	
	public ItemStackBuilder stackSize(int i) {
		Validate.isTrue(i <= 64, "Stack size cannot be above 64!");
		item.editMeta(meta -> {
			meta.setMaxStackSize(i);
		});
		return this;
	}

	public ItemStackBuilder meta(Consumer<ItemMeta> run) {
		ItemMeta meta = getMeta();
		run.accept(meta);
		item.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder durability(long durability) {
		setMaxDurability(durability);
		return this;
	}

	public ItemStackBuilder glowing(boolean glowing) {
		item.editMeta(meta -> {
			meta.setEnchantmentGlintOverride(glowing);
		});
		return this;
	}

	public ItemStackBuilder enchant(@Nonnull Enchantment ench, int level) {
		item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.addUnsafeEnchantment(ench, level);
		List<Component> lore = item.lore();
		if(lore == null) {
			lore = new ArrayList<Component>();
		}
		String enchant = "<gray>" + PlainTextComponentSerializer.plainText().serialize(ench.displayName(20)).replace("enchantment.level.20", FormatUtils.toRoman(level));
		lore.add(0, FormatUtils.mm(enchant));
		item.lore(lore);
		return this;
	}

	public ItemStackBuilder enchants(@Nullable Map<Enchantment, Integer> enchants) {
		if(enchants == null) return this;
		for(Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
			enchant(enchant.getKey(), enchant.getValue());
		}
		return this;
	}
	
}
