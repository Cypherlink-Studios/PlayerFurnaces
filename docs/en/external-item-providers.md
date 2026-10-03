---
title: External Item Providers
description: Complete architecture manual for ItemResolverRegistry, native Craftorithm and ExecutableItems integration, and the ItemProvider developer API.
sidebar:
  order: 7
---

# External Item Providers & Developer API

PlayerFurnaces features an extensible item abstraction architecture powered by **`ItemResolverRegistry`**. This enables server owners to define recipes and fuels utilizing custom items created by third-party plugins (such as **Craftorithm**, **ExecutableItems**, **Oraxen**, and **ItemsAdder**), preserving custom NBT, PDC tags, and textures throughout the entire smelting pipeline.

---

## 🔍 Namespaced Item Resolution (`namespace:item_id`)

When an item is specified in a recipe or fuel definition using the format:

```text
namespace:item_id
```

*(For example: `craftorithm:ruby_gem` or `executableitems:excalibur`)*

PlayerFurnaces delegates resolution and matching to the registered `ItemProvider` associated with that namespace:

```text
+-------------------------------------------------------------+
|                     ITEM RESOLVER PIPELINE                  |
+-------------------------------------------------------------+
|                                                             |
|                    ItemResolverRegistry                     |
|                              │                              |
|        ┌─────────────────────┼─────────────────────┐        |
|        ▼                     ▼                     ▼        |
|  [Vanilla Provider]  [Craftorithm Provider] [ExecutableItems] |
|   namespace: vanilla   namespace: craftorithm  namespaces:  |
|                         & crafthorim            executableitems,
|                                                 ei,         |
|                                                 executableitem
+-------------------------------------------------------------+
```

---

## 🛠️ Native Plugin Integrations

### 1. Craftorithm Integration
* **Namespaces**: `craftorithm`, `crafthorim`
* **Features**:
  * Automatically registered on server startup if the Craftorithm plugin is detected.
  * Inputs and results resolve native `ItemStack` instances retaining custom textures, custom model data, and Craftorithm PDC tags.
  * Seamlessly compatible with the `/pfadmin import craftorithm` recipe importer command.

### 2. ExecutableItems Integration
* **Namespaces**: `executableitems`, `ei`, `executableitem`
* **Features**:
  * Automatically registered on startup when ExecutableItems and SCore are active.
  * Multi-strategy reflection dynamically matches `buildItem` methods across different ExecutableItems API releases.
  * Custom abilities, cooldown tags, and internal variables are retained safely inside furnace storage.
  * Output slot stacking: PlayerFurnaces intelligently normalizes runtime random metadata/UUID tags so identical ExecutableItems properly stack in the output slot up to their maximum stack size.
  * Recipe decoration: Recipe authors can supplement output items with MiniMessage/HEX names, additional lore lines, or custom PDC tags.

---

## 💻 Developer Guide: Creating a Custom `ItemProvider`

Developers can easily integrate custom item plugins or proprietary server items with PlayerFurnaces by implementing the `ItemProvider` interface.

### 1. Implement the `ItemProvider` Interface

```java
package dev.darkblade.playerfurnaces.provider;

import org.bukkit.inventory.ItemStack;

public interface ItemProvider {

    /**
     * Returns the primary namespace handled by this provider.
     * Example: "myplugin" for items referenced as "myplugin:item_id".
     */
    String getNamespace();

    /**
     * Constructs and returns an ItemStack corresponding to the item ID and stack amount.
     */
    ItemStack getItem(String id, int amount);

    /**
     * Evaluates whether an in-game ItemStack matches the given provider item ID.
     */
    boolean isSimilar(ItemStack itemStack, String id);
}
```

### 2. Example Implementation

```java
import dev.darkblade.playerfurnaces.provider.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomSwordProvider implements ItemProvider {

    @Override
    public String getNamespace() {
        return "myswords";
    }

    @Override
    public ItemStack getItem(String id, int amount) {
        if ("fire_blade".equalsIgnoreCase(id)) {
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, amount);
            // Apply custom meta, PDC, enchantments...
            return sword;
        }
        return null;
    }

    @Override
    public boolean isSimilar(ItemStack itemStack, String id) {
        if (itemStack == null) return false;
        // Verify custom PDC tags or lore identifying fire_blade
        return "fire_blade".equalsIgnoreCase(id);
    }
}
```

### 3. Registering the Provider

Register your provider during `onEnable()`:

```java
ItemResolverRegistry registry = PlayerFurnacesPlugin.getInstance().getItemResolverRegistry();
registry.registerProvider(new CustomSwordProvider());
```
