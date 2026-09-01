package dev.darkblade.playerfurnaces;

import dev.darkblade.playerfurnaces.engine.SmeltingManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmeltingManagerTest {

    @Test
    public void testFuelBurnTimes() {
        assertEquals(20000, SmeltingManager.getFuelBurnTime(new ItemStack(Material.LAVA_BUCKET)));
        assertEquals(16000, SmeltingManager.getFuelBurnTime(new ItemStack(Material.COAL_BLOCK)));
        assertEquals(4000, SmeltingManager.getFuelBurnTime(new ItemStack(Material.DRIED_KELP_BLOCK)));
        assertEquals(2400, SmeltingManager.getFuelBurnTime(new ItemStack(Material.BLAZE_ROD)));
        assertEquals(1600, SmeltingManager.getFuelBurnTime(new ItemStack(Material.COAL)));
        assertEquals(1600, SmeltingManager.getFuelBurnTime(new ItemStack(Material.CHARCOAL)));
        assertEquals(400, SmeltingManager.getFuelBurnTime(new ItemStack(Material.SCAFFOLDING)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.BAMBOO_BLOCK)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_LOG)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_PLANKS)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.CRAFTING_TABLE)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.BOOKSHELF)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.CHEST)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.BOW)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_SIGN)));
        assertEquals(300, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_HANGING_SIGN)));
        assertEquals(150, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_SLAB)));
        assertEquals(100, SmeltingManager.getFuelBurnTime(new ItemStack(Material.STICK)));
        assertEquals(100, SmeltingManager.getFuelBurnTime(new ItemStack(Material.OAK_SAPLING)));
        assertEquals(100, SmeltingManager.getFuelBurnTime(new ItemStack(Material.WHITE_WOOL)));
        assertEquals(67, SmeltingManager.getFuelBurnTime(new ItemStack(Material.WHITE_CARPET)));
        assertEquals(50, SmeltingManager.getFuelBurnTime(new ItemStack(Material.BAMBOO)));
        assertEquals(0, SmeltingManager.getFuelBurnTime(new ItemStack(Material.STONE)));
        assertEquals(0, SmeltingManager.getFuelBurnTime(null));
    }

    @Test
    public void testSafeLookupNullInput() {
        assertNull(SmeltingManager.getSmeltingRecipe(null));
        assertNull(SmeltingManager.getSmeltingRecipe(new ItemStack(Material.AIR)));
    }
}
