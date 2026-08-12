package dev.darkblade.playerfurnaces.engine;

import dev.darkblade.playerfurnaces.PlayerFurnacesPlugin;
import dev.darkblade.playerfurnaces.manager.FuelManager;
import dev.darkblade.playerfurnaces.manager.RecipeManager;
import dev.darkblade.playerfurnaces.model.CustomRecipe;
import dev.darkblade.playerfurnaces.model.RecipeItemDefinition;
import dev.darkblade.playerfurnaces.model.VirtualFurnace;
import dev.darkblade.playerfurnaces.provider.ItemResolverRegistry;
import dev.darkblade.playerfurnaces.util.RecipeItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;

public class FurnaceEngine {

    public static void updateFurnaceState(VirtualFurnace furnace) {
        PlayerFurnacesPlugin plugin = PlayerFurnacesPlugin.getInstance();
        RecipeManager recipeManager = plugin != null ? plugin.getRecipeManager() : null;
        FuelManager fuelManager = plugin != null ? plugin.getFuelManager() : null;
        ItemResolverRegistry registry = plugin != null ? plugin.getItemResolverRegistry() : null;

        updateFurnaceState(furnace, recipeManager, fuelManager, registry);
    }

    public static void updateFurnaceState(VirtualFurnace furnace, RecipeManager recipeManager, FuelManager fuelManager, ItemResolverRegistry itemResolverRegistry) {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - furnace.getLastUpdatedTimestamp();
        long elapsedTicks = elapsedMillis / 50;

        if (elapsedTicks <= 0) {
            return;
        }

        furnace.setLastUpdatedTimestamp(now);

        while (elapsedTicks > 0) {
            ItemStack input = furnace.getInputItem();
            if (input == null || input.getAmount() <= 0) {
                furnace.setCookTime(0);
                if (furnace.getBurnTime() > 0) {
                    long burnDecay = Math.min(elapsedTicks, (long) furnace.getBurnTime());
                    furnace.setBurnTime(furnace.getBurnTime() - (int) burnDecay);
                }
                break;
            }

            CustomRecipe customRecipe = recipeManager != null ? recipeManager.findMatchingRecipe(input) : null;
            CookingRecipe<?> vanillaRecipe = null;
            ItemStack result = null;
            int totalCookTicks = 200;

            if (customRecipe != null) {
                if (customRecipe.isDisabled()) {
                    furnace.setCookTime(0);
                    break;
                }
                totalCookTicks = customRecipe.getCookTimeTicks();
                RecipeItemDefinition resDef = customRecipe.getResult();
                if (resDef != null) {
                    result = RecipeItemBuilder.build(resDef, itemResolverRegistry);
                }
            } else {
                boolean isVanillaEnabled = recipeManager == null || (recipeManager.isVanillaSmeltingEnabled() && !recipeManager.isVanillaMaterialDisabled(input.getType()));
                if (isVanillaEnabled) {
                    vanillaRecipe = SmeltingManager.getSmeltingRecipe(input);
                    if (vanillaRecipe != null) {
                        result = vanillaRecipe.getResult();
                        totalCookTicks = vanillaRecipe.getCookingTime();
                    }
                }
            }

            if (result == null) {
                furnace.setCookTime(0);
                break;
            }

            furnace.setTotalCookTime(totalCookTicks);

            ItemStack output = furnace.getOutputItem();
            if (output != null && output.getAmount() > 0) {
                if (!output.isSimilar(result) || output.getAmount() + result.getAmount() > output.getMaxStackSize()) {
                    break;
                }
            }

            ItemStack fuel = furnace.getFuelItem();
            if (customRecipe != null && customRecipe.getFuelType() != null) {
                String reqFuel = customRecipe.getFuelType();
                if (fuel == null || fuel.getAmount() <= 0 || itemResolverRegistry == null || !itemResolverRegistry.matches(fuel, reqFuel)) {
                    furnace.setCookTime(0);
                    break;
                }
            }

            if (furnace.getBurnTime() <= 0) {
                int fuelBurnTicks = 0;
                if (customRecipe != null && customRecipe.getFuelBurnTicks() != null) {
                    fuelBurnTicks = customRecipe.getFuelBurnTicks();
                } else if (fuelManager != null) {
                    fuelBurnTicks = fuelManager.getBurnTime(fuel);
                } else {
                    fuelBurnTicks = SmeltingManager.getFuelBurnTime(fuel);
                }

                if (fuelBurnTicks > 0) {
                    if (fuel.getType() == Material.LAVA_BUCKET) {
                        furnace.setFuelItem(new ItemStack(Material.BUCKET));
                    } else {
                        fuel.setAmount(fuel.getAmount() - 1);
                        if (fuel.getAmount() <= 0) {
                            furnace.setFuelItem(null);
                        }
                    }
                    furnace.setBurnTime(fuelBurnTicks);
                    furnace.setTotalBurnTime(fuelBurnTicks);
                } else {
                    furnace.setCookTime(0);
                    break;
                }
            }

            int cookNeeded = furnace.getTotalCookTime() - furnace.getCookTime();
            long stepTicks = Math.min(elapsedTicks, Math.min((long) furnace.getBurnTime(), (long) cookNeeded));

            if (stepTicks <= 0) {
                break;
            }

            furnace.setCookTime(furnace.getCookTime() + (int) stepTicks);
            furnace.setBurnTime(furnace.getBurnTime() - (int) stepTicks);
            elapsedTicks -= stepTicks;

            if (furnace.getCookTime() >= furnace.getTotalCookTime()) {
                furnace.setCookTime(0);
                input.setAmount(input.getAmount() - 1);
                if (input.getAmount() <= 0) {
                    furnace.setInputItem(null);
                }

                if (output == null || output.getAmount() <= 0) {
                    furnace.setOutputItem(result.clone());
                } else {
                    output.setAmount(output.getAmount() + result.getAmount());
                }
            }
        }
    }
}
