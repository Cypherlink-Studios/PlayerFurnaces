package dev.darkblade.playerfurnaces;

import dev.darkblade.playerfurnaces.command.AdminCommand;
import dev.darkblade.playerfurnaces.command.PlayerFurnaceCommand;
import dev.darkblade.playerfurnaces.database.DatabaseManager;
import dev.darkblade.playerfurnaces.engine.SmeltingManager;
import dev.darkblade.playerfurnaces.gui.GuiListener;
import dev.darkblade.playerfurnaces.importer.RecipeImporterRegistry;
import dev.darkblade.playerfurnaces.importer.impl.CraftorithmRecipeImporter;
import dev.darkblade.playerfurnaces.listener.PlayerListener;
import dev.darkblade.playerfurnaces.manager.FuelManager;
import dev.darkblade.playerfurnaces.manager.FurnaceManager;
import dev.darkblade.playerfurnaces.manager.MenuManager;
import dev.darkblade.playerfurnaces.manager.MessageManager;
import dev.darkblade.playerfurnaces.manager.RecipeManager;
import dev.darkblade.playerfurnaces.provider.ItemResolverRegistry;
import dev.darkblade.playerfurnaces.provider.impl.CraftorithmItemProvider;
import dev.darkblade.playerfurnaces.provider.impl.ExecutableItemsItemProvider;
import dev.darkblade.playerfurnaces.provider.impl.VanillaItemProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerFurnacesPlugin extends JavaPlugin {

    private static PlayerFurnacesPlugin instance;

    private DatabaseManager databaseManager;
    private FurnaceManager furnaceManager;
    private ItemResolverRegistry itemResolverRegistry;
    private RecipeImporterRegistry recipeImporterRegistry;
    private RecipeManager recipeManager;
    private FuelManager fuelManager;
    private MessageManager messageManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveDefaultRecipesAndFuels();

        this.messageManager = new MessageManager(this);
        this.menuManager = new MenuManager(this);

        this.itemResolverRegistry = new ItemResolverRegistry();
        this.itemResolverRegistry.registerProvider(new VanillaItemProvider());
        CraftorithmItemProvider craftorithmProvider = new CraftorithmItemProvider();
        this.itemResolverRegistry.registerProvider(craftorithmProvider);
        this.itemResolverRegistry.registerAlias("crafthorim", craftorithmProvider);

        ExecutableItemsItemProvider executableItemsProvider = new ExecutableItemsItemProvider();
        this.itemResolverRegistry.registerProvider(executableItemsProvider);
        this.itemResolverRegistry.registerAlias("ei", executableItemsProvider);
        this.itemResolverRegistry.registerAlias("executableitem", executableItemsProvider);

        this.recipeImporterRegistry = new RecipeImporterRegistry();
        this.recipeImporterRegistry.registerImporter(new CraftorithmRecipeImporter());

        this.recipeManager = new RecipeManager(this, itemResolverRegistry);
        this.recipeManager.loadRecipes();

        this.fuelManager = new FuelManager(this, itemResolverRegistry);
        this.fuelManager.loadFuels();

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.init();

        this.furnaceManager = new FurnaceManager(this);
        this.furnaceManager.startTickTask();

        // Load existing online players (handles /reload or late enable)
        for (org.bukkit.entity.Player onlinePlayer : getServer().getOnlinePlayers()) {
            this.furnaceManager.loadPlayer(onlinePlayer.getUniqueId());
        }

        // Initialize SmeltingManager recipe cache
        SmeltingManager.rebuildRecipeCache();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        PluginCommand furnaceCmd = getCommand("furnace");
        if (furnaceCmd != null) {
            PlayerFurnaceCommand executor = new PlayerFurnaceCommand(this);
            furnaceCmd.setExecutor(executor);
            furnaceCmd.setTabCompleter(executor);
        }

        PluginCommand adminCmd = getCommand("playerfurnacesadmin");
        if (adminCmd != null) {
            AdminCommand executor = new AdminCommand(this);
            adminCmd.setExecutor(executor);
            adminCmd.setTabCompleter(executor);
        }

        getLogger().info("PlayerFurnaces v" + getDescription().getVersion() + " has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (furnaceManager != null) {
            furnaceManager.saveAll();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("PlayerFurnaces has been disabled.");
    }

    private void saveDefaultRecipesAndFuels() {
        File recipesFolder = new File(getDataFolder(), "recipes");
        if (!recipesFolder.exists()) {
            recipesFolder.mkdirs();
            try {
                saveResource("recipes/example.yml", false);
            } catch (Exception e) {
                getLogger().warning("Could not save default example.yml recipe: " + e.getMessage());
            }
        }

        File fuelsFolder = new File(getDataFolder(), "fuels");
        if (!fuelsFolder.exists()) {
            fuelsFolder.mkdirs();
            try {
                saveResource("fuels/hyper_coal.yml", false);
            } catch (Exception e) {
                getLogger().warning("Could not save default hyper_coal.yml fuel: " + e.getMessage());
            }
        }
    }

    public static PlayerFurnacesPlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public FurnaceManager getFurnaceManager() {
        return furnaceManager;
    }

    public ItemResolverRegistry getItemResolverRegistry() {
        return itemResolverRegistry;
    }

    public RecipeImporterRegistry getRecipeImporterRegistry() {
        return recipeImporterRegistry;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public FuelManager getFuelManager() {
        return fuelManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
