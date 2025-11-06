package dev.cworldstar.cwshared.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 
 * The frontend methods that this plugin uses to
 * interface with Minecraft's crafting recipes without using
 * NMS.
 * 
 * @author cworldstar
 */

public class RecipeUtils {
		
	private static JavaPlugin plugin;

	public static <T extends JavaPlugin> void plugin(T plugin) {
		RecipeUtils.plugin = plugin;
	}
	
	public static void add(@Nonnull CraftingRecipe recipe) {
		if(Bukkit.getServer().getRecipe(recipe.getKey()) != null) {
			return;
		}
		Bukkit.getServer().addRecipe(recipe);
	}
	
	
	/**
	 * 
	 * @param outcome {@link ItemStack} The ItemStack outcome of the expected recipe.
	 * @return {@link Recipe} The recipe that matches the outcome. Can be null.
	 */
	@Nullable
	public static Recipe getRecipe(ItemStack outcome) {
		Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
		while (recipeIterator.hasNext()) {
			Recipe recipe = recipeIterator.next();
			if(recipe.getResult().isSimilar(outcome)) {
				return recipe;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return {@link List}<Recipe> A list of all the crafting recipes registered by this plugin.
	 */
	@Nonnull
	public static List<Recipe> allRecipes() {
		ArrayList<Recipe> recipes = new ArrayList<>();
		Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
		while (recipeIterator.hasNext()) {
			Recipe recipe = recipeIterator.next();
			recipes.add(recipe);
		}
		return recipes;
	}
	
	
	/**
	 * 
	 * This method creates a {@link CraftingRecipe} from a {@link ItemStack}, a {@link String} array,  
	 * and a {@link Map} made from a {@link char} index and either a {@link RecipeChoice} or {@link Material} value.
	 * This method is never null.
	 * 
	 * @param result {@link ItemStack} The result of the recipe.
	 * @param shape {@link String}[] An array of strings, should be 3x3.
	 * @param recipe {@link Map} <Character, Object> A Character, RecipeChoice map. Can also use Materials.
	 * @return {@link CraftingRecipe} The created recipe.
	 */
	
	@Nonnull
	public static CraftingRecipe createShapedRecipe(String recipe_id, @Nonnull ItemStack result, @Nonnull String[] shape, @Nonnull Map<Character, Object> recipe) {
		ShapedRecipe cRecipe = new ShapedRecipe(createKey(recipe_id), result);
		cRecipe.shape(shape);
		for(Entry<Character, Object> key : recipe.entrySet()) {
			Object value = key.getValue();	
			if(value instanceof Material) {
				Material material = (Material) value;
				cRecipe.setIngredient(key.getKey(), material);
			} else if(value instanceof RecipeChoice) {
				RecipeChoice choice = (RecipeChoice) value;
				cRecipe.setIngredient(key.getKey(), choice);
			}

		}
		return cRecipe;
	}
	
	private static @NotNull NamespacedKey createKey(String key) {
		return new NamespacedKey(plugin, key);
	}

	@Nonnull
	public static CraftingRecipe createShapelessRecipe(String recipe_id, @Nonnull ItemStack result, @Nonnull Object[] shape) {
		ShapelessRecipe cRecipe = new ShapelessRecipe(createKey(recipe_id), result);
		for(Object o : shape) {
			// I have to do this ugly shit for my compiler
			if(o instanceof Material) {
				cRecipe.addIngredient((Material) o);
			} else if(o instanceof RecipeChoice) {
				cRecipe.addIngredient((RecipeChoice) o);
			}
		}
		
		return cRecipe;
	}
	
	/**
	 * 
	 * This method takes a {@link ItemStack} and returns a {@link RecipeChoice} to be 
	 * used in {@link Crafting#createShapedRecipe(ItemStack, String[], Map)}.
	 * 
	 * @param item {@link ItemStack} The item to create a choice from.
	 * @return {@link RecipeChoice} The created choice.
	 */ 
	@Nonnull
	public static RecipeChoice createExactChoice(@Nonnull ItemStack item) {
		return new RecipeChoice.ExactChoice(item);
	}
	
	/**
	 * 
	 * This method takes a {@link List} of {@link Material}s and returns a {@link RecipeChoice} to be 
	 * used in {@link Crafting#createShapedRecipe(ItemStack, String[], Map)}.
	 * 
	 * @param choices {@link List} The list of materials.
	 * @return {@link RecipeChoice} The created choice.
	 */
	
	@Nonnull
	public static RecipeChoice createMaterialChoice(@Nonnull List<Material> choices) {
		return new RecipeChoice.MaterialChoice(choices);
	}

	public static @NotNull RecipeChoice exact(ItemStack item) {
		return createExactChoice(item);
	}
	
	public static @NotNull RecipeChoice material(@Nonnull List<Material> choices) {
		return createMaterialChoice(choices);
	}
}
