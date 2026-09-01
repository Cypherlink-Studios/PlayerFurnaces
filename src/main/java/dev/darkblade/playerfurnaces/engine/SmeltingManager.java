package dev.darkblade.playerfurnaces.engine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SmeltingManager {

    private static final Map<Material, Integer> FUEL_BURN_TIMES = new HashMap<>();
    private static final Map<Material, CookingRecipe<?>> MATERIAL_RECIPE_CACHE = new ConcurrentHashMap<>();
    private static final List<CookingRecipe<?>> DYNAMIC_RECIPES = new CopyOnWriteArrayList<>();
    private static volatile boolean initialized = false;

    static {
        // High-efficiency & Special fuels
        FUEL_BURN_TIMES.put(Material.LAVA_BUCKET, 20000);
        FUEL_BURN_TIMES.put(Material.COAL_BLOCK, 16000);
        FUEL_BURN_TIMES.put(Material.DRIED_KELP_BLOCK, 4000);
        FUEL_BURN_TIMES.put(Material.BLAZE_ROD, 2400);
        FUEL_BURN_TIMES.put(Material.COAL, 1600);
        FUEL_BURN_TIMES.put(Material.CHARCOAL, 1600);
        FUEL_BURN_TIMES.put(Material.BAMBOO_BLOCK, 300);
        FUEL_BURN_TIMES.put(Material.SCAFFOLDING, 400);
        FUEL_BURN_TIMES.put(Material.BAMBOO, 50);

        // Blocks & Containers
        FUEL_BURN_TIMES.put(Material.CRAFTING_TABLE, 300);
        FUEL_BURN_TIMES.put(Material.BOOKSHELF, 300);
        FUEL_BURN_TIMES.put(Material.CHEST, 300);
        FUEL_BURN_TIMES.put(Material.TRAPPED_CHEST, 300);
        FUEL_BURN_TIMES.put(Material.JUKEBOX, 300);
        FUEL_BURN_TIMES.put(Material.NOTE_BLOCK, 300);
        FUEL_BURN_TIMES.put(Material.DAYLIGHT_DETECTOR, 300);
        FUEL_BURN_TIMES.put(Material.LADDER, 300);
        FUEL_BURN_TIMES.put(Material.BOW, 300);
        FUEL_BURN_TIMES.put(Material.CROSSBOW, 300);
        FUEL_BURN_TIMES.put(Material.FISHING_ROD, 300);

        // Logs & Wood
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
        FUEL_BURN_TIMES.put(Material.MANGROVE_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.CHERRY_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.BAMBOO_PLANKS, 300);
        FUEL_BURN_TIMES.put(Material.STICK, 100);
    }

    public static synchronized void rebuildRecipeCache() {
        try {
            Map<Material, CookingRecipe<?>> newMaterialMap = new HashMap<>();
            List<CookingRecipe<?>> newDynamicList = new ArrayList<>();

            Iterator<Recipe> iter = Bukkit.recipeIterator();
            while (iter.hasNext()) {
                Recipe r = iter.next();
                if (r instanceof CookingRecipe<?> cookingRecipe) {
                    RecipeChoice choice = cookingRecipe.getInputChoice();
                    if (choice instanceof RecipeChoice.MaterialChoice matChoice) {
                        for (Material mat : matChoice.getChoices()) {
                            CookingRecipe<?> existing = newMaterialMap.get(mat);
                            // Prefer FurnaceRecipe over other cooking types (blast/smoker/campfire)
                            if (existing == null || (!(existing instanceof FurnaceRecipe) && cookingRecipe instanceof FurnaceRecipe)) {
                                newMaterialMap.put(mat, cookingRecipe);
                            }
                        }
                    } else if (choice != null) {
                        newDynamicList.add(cookingRecipe);
                    }
                }
            }

            MATERIAL_RECIPE_CACHE.clear();
            MATERIAL_RECIPE_CACHE.putAll(newMaterialMap);
            DYNAMIC_RECIPES.clear();
            DYNAMIC_RECIPES.addAll(newDynamicList);
            initialized = true;
        } catch (Throwable ignored) {
            // In unit tests or during server lifecycle transitions where Bukkit.recipeIterator is unavailable
        }
    }

    public static CookingRecipe<?> getSmeltingRecipe(ItemStack input) {
        if (input == null || input.getType().isAir()) {
            return null;
        }

        if (!initialized) {
            rebuildRecipeCache();
        }

        // 1. Check dynamic / ExactChoice recipes if the item has custom metadata
        if (input.hasItemMeta()) {
            for (CookingRecipe<?> recipe : DYNAMIC_RECIPES) {
                try {
                    if (recipe.getInputChoice() != null && recipe.getInputChoice().test(input)) {
                        return recipe;
                    }
                } catch (Throwable ignored) {}
            }
        }

        // 2. Fast O(1) lookup by material
        CookingRecipe<?> cached = MATERIAL_RECIPE_CACHE.get(input.getType());
        if (cached != null) {
            try {
                if (cached.getInputChoice() != null && cached.getInputChoice().test(input)) {
                    return cached;
                }
            } catch (Throwable ignored) {}
        }

        // 3. Fallback check on dynamic recipes if not matched yet
        for (CookingRecipe<?> recipe : DYNAMIC_RECIPES) {
            try {
                if (recipe.getInputChoice() != null && recipe.getInputChoice().test(input)) {
                    return recipe;
                }
            } catch (Throwable ignored) {}
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
                || name.endsWith("_TRAPDOOR") || name.endsWith("_BOAT") || name.endsWith("_PRESSURE_PLATE")
                || name.endsWith("_SIGN") || name.endsWith("_HANGING_SIGN") || name.endsWith("_CHEST_BOAT")) {
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
