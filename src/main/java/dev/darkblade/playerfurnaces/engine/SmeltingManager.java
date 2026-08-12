package dev.darkblade.playerfurnaces.engine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SmeltingManager {

    private static final Map<Material, Integer> FUEL_BURN_TIMES = new HashMap<>();

    static {
        FUEL_BURN_TIMES.put(Material.LAVA_BUCKET, 20000);
        FUEL_BURN_TIMES.put(Material.COAL_BLOCK, 16000);
        FUEL_BURN_TIMES.put(Material.BLAZE_ROD, 2400);
        FUEL_BURN_TIMES.put(Material.COAL, 1600);
        FUEL_BURN_TIMES.put(Material.CHARCOAL, 1600);
        FUEL_BURN_TIMES.put(Material.OAK_LOG, 300);
        FUEL_BURN_TIMES.put(Material.SPRUCE_LOG, 300);
        FUEL_BURN_TIMES.put(Material.BIRCH_LOG, 300);
        FUEL_BURN_TIMES.put(Material.JUNGLE_LOG, 300);
        FUEL_BURN_TIMES.put(Material.ACACIA_LOG, 300);
        FUEL_BURN_TIMES.put(Material.DARK_OAK_LOG, 300);
        FUEL_BURN_TIMES.put(Material.MANGROVE_LOG, 300);
        FUEL_BURN_TIMES.put(Material.CHERRY_LOG, 300);
        FUEL_BURN_TIMES.put(Material.OAK_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.SPRUCE_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.BIRCH_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.JUNGLE_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.ACACIA_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.DARK_OAK_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.STICK, 100);
    }

    public static CookingRecipe<?> getSmeltingRecipe(ItemStack input) {
        if (input == null || input.getType().isAir()) {
            return null;
        }

        // Fast lookup via Bukkit's recipe index
        try {
            List<Recipe> recipes = Bukkit.getRecipesFor(input);
            if (recipes != null && !recipes.isEmpty()) {
                for (Recipe r : recipes) {
                    if (r instanceof FurnaceRecipe furnaceRecipe) {
                        return furnaceRecipe;
                    }
                }
                for (Recipe r : recipes) {
                    if (r instanceof CookingRecipe<?> cookingRecipe) {
                        return cookingRecipe;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // Fallback: full iterator traversal for dynamic / non-indexed custom Bukkit recipes
        try {
            Iterator<Recipe> iter = Bukkit.recipeIterator();
            while (iter.hasNext()) {
                Recipe recipe = iter.next();
                if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                    try {
                        if (furnaceRecipe.getInputChoice() != null && furnaceRecipe.getInputChoice().test(input)) {
                            return furnaceRecipe;
                        }
                    } catch (Throwable ignored) {
                    }
                } else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                    try {
                        if (cookingRecipe.getInputChoice() != null && cookingRecipe.getInputChoice().test(input)) {
                            return cookingRecipe;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    public static int getFuelBurnTime(ItemStack fuel) {
        if (fuel == null || fuel.getType().isAir()) {
            return 0;
        }
        Material type = fuel.getType();
        if (FUEL_BURN_TIMES.containsKey(type)) {
            return FUEL_BURN_TIMES.get(type);
        }

        String name = type.name();
        if (name.endsWith("_LOG") || name.endsWith("_WOOD") || name.endsWith("_PLANKS") || name.endsWith("_STAIRS") 
                || name.endsWith("_FENCE") || name.endsWith("_FENCE_GATE") || name.endsWith("_DOOR") 
                || name.endsWith("_TRAPDOOR") || name.endsWith("_BOAT") || name.endsWith("_PRESSURE_PLATE")) {
            return 300;
        }
        if (name.endsWith("_SLAB")) {
            return 150;
        }
        if (name.endsWith("_SAPLING") || name.endsWith("_WOOL")) {
            return 100;
        }
        if (name.endsWith("_CARPET")) {
            return 67;
        }
        if (name.startsWith("WOODEN_")) {
            return 200;
        }

        return 0;
    }
}
