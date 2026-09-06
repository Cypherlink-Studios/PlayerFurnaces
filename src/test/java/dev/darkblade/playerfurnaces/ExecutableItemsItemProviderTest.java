package dev.darkblade.playerfurnaces;

import dev.darkblade.playerfurnaces.provider.ItemResolverRegistry;
import dev.darkblade.playerfurnaces.provider.impl.ExecutableItemsItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutableItemsItemProviderTest {

    public static class DummyExecutableItem {
        private final String id;

        public DummyExecutableItem(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        // 3-arg signature (modern SCore 5.x / ExecutableItems 7.x)
        public ItemStack buildItem(int amount, Optional<Integer> usage, Optional<Object> creator) {
            return new ItemStack(Material.IRON_INGOT, amount);
        }
    }

    public static class DummyManager {
        public Optional<DummyExecutableItem> getExecutableItem(String id) {
            if ("mat-lingote-acero".equalsIgnoreCase(id)) {
                return Optional.of(new DummyExecutableItem("mat-lingote-acero"));
            }
            return Optional.empty();
        }

        public Optional<DummyExecutableItem> getExecutableItem(ItemStack stack) {
            if (stack != null && stack.getType() == Material.IRON_INGOT) {
                return Optional.of(new DummyExecutableItem("mat-lingote-acero"));
            }
            return Optional.empty();
        }
    }

    @Test
    public void testProviderNamespaceAndNullSafety() {
        ExecutableItemsItemProvider provider = new ExecutableItemsItemProvider();
        assertEquals("executableitems", provider.getNamespace());

        assertNull(provider.getItem(null, 1));
        assertNull(provider.getItem("", 1));
        assertFalse(provider.isSimilar(null, "some_id"));
        assertFalse(provider.isSimilar(null, null));
    }

    @Test
    public void testRegistryAliases() {
        ItemResolverRegistry registry = new ItemResolverRegistry();
        ExecutableItemsItemProvider provider = new ExecutableItemsItemProvider();

        registry.registerProvider(provider);
        registry.registerAlias("ei", provider);
        registry.registerAlias("executableitem", provider);

        assertSame(provider, registry.getProvider("executableitems"));
        assertSame(provider, registry.getProvider("ei"));
        assertSame(provider, registry.getProvider("executableitem"));
    }

    @Test
    public void testReflectionWith3ArgBuildItemAndStackInspection() throws Exception {
        ExecutableItemsItemProvider provider = new ExecutableItemsItemProvider();
        DummyManager manager = new DummyManager();

        Method byIdMethod = manager.getClass().getMethod("getExecutableItem", String.class);
        Method byStackMethod = manager.getClass().getMethod("getExecutableItem", ItemStack.class);

        Field availableField = ExecutableItemsItemProvider.class.getDeclaredField("available");
        availableField.setAccessible(true);
        availableField.set(provider, true);

        Field targetField = ExecutableItemsItemProvider.class.getDeclaredField("targetInstance");
        targetField.setAccessible(true);
        targetField.set(provider, manager);

        Field byIdField = ExecutableItemsItemProvider.class.getDeclaredField("getExecutableItemByIdMethod");
        byIdField.setAccessible(true);
        byIdField.set(provider, byIdMethod);

        Field byStackField = ExecutableItemsItemProvider.class.getDeclaredField("getExecutableItemByStackMethod");
        byStackField.setAccessible(true);
        byStackField.set(provider, byStackMethod);

        // Test getItem with 3-arg buildItem signature
        ItemStack built = provider.getItem("mat-lingote-acero", 5);
        assertNotNull(built);
        assertEquals(Material.IRON_INGOT, built.getType());
        assertEquals(5, built.getAmount());

        // Test isSimilar with official stack inspection
        ItemStack testStack = new ItemStack(Material.IRON_INGOT, 1);
        assertTrue(provider.isSimilar(testStack, "mat-lingote-acero"));
        assertTrue(provider.isSimilar(testStack, "MAT-LINGOTE-ACERO"));
        assertFalse(provider.isSimilar(testStack, "different-id"));

        ItemStack otherStack = new ItemStack(Material.DIAMOND, 1);
        assertFalse(provider.isSimilar(otherStack, "mat-lingote-acero"));
    }
}
