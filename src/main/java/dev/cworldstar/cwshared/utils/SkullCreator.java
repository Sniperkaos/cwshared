package dev.cworldstar.cwshared.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

public class SkullCreator {
	public static ItemStack skullFromBase64(String base64) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		UUID profileID = UUID.randomUUID();
		PlayerProfile profile = Bukkit.getServer().createProfileExact(profileID, profileID.toString().substring(0, 16));
		profile.setProperty(new ProfileProperty("textures", base64));
		skull.editMeta(SkullMeta.class, meta -> {
			meta.setPlayerProfile(profile);
		});
		return skull;
	}

	public static ItemStack loadBase64(ItemStack item, String texture) {
		Validate.isInstanceOf(SkullMeta.class, item.getItemMeta(), "#loadBase64(item, texture) passed an item without SkullMeta.");
		UUID profileID = UUID.randomUUID();
		PlayerProfile profile = Bukkit.getServer().createProfileExact(profileID, profileID.toString().substring(0, 16));
		profile.setProperty(new ProfileProperty("textures", texture));
		item.editMeta(SkullMeta.class, meta -> {
			meta.setPlayerProfile(profile);
		});
		return item;
	}
	
	public static ItemStack loadPlayerHead(ItemStack item, UUID uuid) {
		Validate.isInstanceOf(SkullMeta.class, item.getItemMeta(), "#loadPlayerHead(item, texture) passed an item without SkullMeta.");
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		PlayerProfile profile = player.getPlayerProfile();
		profile.complete();
		
		item.editMeta(SkullMeta.class, meta -> {
			meta.setPlayerProfile(profile);
		});
		return item;
	}
	
	public static ItemStack loadPlayerHead(ItemStack item, String uuid) {
		return loadPlayerHead(item, UUID.fromString(uuid));
	}

	/**
	 * Creates a skull ItemStack from a skin hash. To get the hash, see {@link #loadToBlockFromHash(Block, String)}
	 * @param hash The string hash.
	 * @return A skull item.
	 */
	public static ItemStack skullFromHash(String hash) {
		return loadPlayerHead(new ItemStack(Material.PLAYER_HEAD), UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8)));
	}
	
	/**
	 * Loads a skin hash to a placed block.
	 * @param b The block to load
	 * @param hash The skin hash, where the ID is equal to [number], of the link http://textures.minecraft.net/texture/[number],
	 */
	public static void loadToBlockFromHash(Block b, String hash) {
		Validate.isTrue(b.getState() instanceof Skull, "The block must be a skull!");
		Skull meta = (Skull) b.getState();
		UUID profileID = UUID.randomUUID();
		PlayerProfile profile = Bukkit.getServer().createProfileExact(profileID, profileID.toString().substring(0, 16));
		profile.setProperty(new ProfileProperty("textures", Base64.getEncoder().encodeToString((("{\"textures\":{\"SKIN\":{\"url\":\"" + "http://textures.minecraft.net/texture/" + hash + "\"}}}").getBytes(StandardCharsets.UTF_8)))));
		profile.complete();
		meta.setPlayerProfile(profile);
		meta.update(true, false);
	}
}
