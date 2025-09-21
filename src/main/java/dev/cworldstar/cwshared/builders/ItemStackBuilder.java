package dev.cworldstar.cwshared.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.utils.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

/**
 * Creates an ItemStack. Contains methods to easily edit common ItemStack properties.
 * Supports both MiniMessage formatting and Color codes.
 * @author cworldstar
 */
public class ItemStackBuilder {
	
	public static ItemStackBuilder create(Material m) {
		return new ItemStackBuilder(m);
	}
	
	private static final Pattern REGEXP_PATTERN = Pattern.compile("[&§]+[a-zA-Z1-9]");
	
	protected ItemStack item;
	
	public ItemStackBuilder(Material m) {
		item = new ItemStack(m);
	}
	
	public ItemStackBuilder(ItemStack i) {
		this.item = i;
	}
	
	public ItemStackBuilder setCustomModelData(CustomModelDataComponent component) {
		item.editMeta(meta -> {
			meta.setCustomModelDataComponent(component);
		});
		return this;
	}
	
	public @Nonnull ItemMeta getMeta() {
		return this.item.getItemMeta();
	}
	
	private void setMeta(@Nonnull ItemMeta meta) {
		this.item.setItemMeta(meta);
	}
	
	public ItemStackBuilder setAmount(int amount) {
		this.item.setAmount(amount);
		return this;
	}
	
	public ItemStackBuilder setName(String name) {
		ItemMeta meta = getMeta();
		meta.displayName(FormatUtils.of(name));
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
			if(REGEXP_PATTERN.matcher(line).find()) {
				mlore.add(FormatUtils.mm(line));
				continue;
			} else {
				mlore.add(MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.color()).resolver(StandardTags.decorations()).resolver(StandardTags.gradient()).build()).build().deserialize("<!italic>" + line));
			}
		}
		meta.lore(mlore);
		setMeta(meta);
		return this;
	}
	
	public ItemStack get() {
		return this.item;
	}
	
	public ItemStack item() {
		return get();
	}
	
	public ItemStack build() {
		return get();
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

	public ItemStackBuilder glowing() {
		this.item.editMeta(meta -> {
			meta.setEnchantmentGlintOverride(true);
		});
		return this;
	}

	public ItemStackBuilder attribute(JavaPlugin plugin, String key, Attribute attribute, double value, EquipmentSlotGroup group, Operation type) {
		item.editMeta(meta -> {
			meta.addAttributeModifier(attribute, new AttributeModifier(new NamespacedKey(plugin,key), value, type, group));
		});
		return this;
	}
	
}
