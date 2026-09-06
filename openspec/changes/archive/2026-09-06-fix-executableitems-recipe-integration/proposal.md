## Why

Custom furnace recipes using ExecutableItems items (`executableitems:<id>` or `ei:<id>`) fail during runtime in modern Paper/Spigot environments (such as Paper 1.21 with ExecutableItems 7.x and SCore 5.x). Specifically:
1. Furnace inputs and fuels fail to match because `ExecutableItemsItemProvider.isSimilar()` attempts to compare a freshly generated item instance against the input item using Bukkit's strict `isSimilar()`. Because ExecutableItems assigns unique runtime UUIDs, creation timestamps, and NBT data to each item instance, `isSimilar()` always returns `false`.
2. Recipe result items fail to generate because modern SCore/ExecutableItems `ExecutableItemInterface.buildItem` requires 3 parameters (`int amount, Optional<Integer> usage, Optional<Player> creator`), while the reflection logic only accommodated 1 or 2 parameters and halted on the first `buildItem` method found.
3. `plugin.yml` lacks `softdepend` declarations for `ExecutableItems`, `SCore`, and `Craftorithm`, creating race conditions in startup order.

Fixing this ensures server owners can seamlessly use ExecutableItems in inputs, fuels, and outputs of virtual furnace recipes.

## What Changes

- Update `ExecutableItemsItemProvider.isSimilar()` to use the official `ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(ItemStack)` method to inspect and match ExecutableItems by ID, with robust fallback strategies.
- Update `ExecutableItemsItemProvider.getItem()` to dynamically detect and invoke any `buildItem` method signature (3-arg, 2-arg, or 1-arg) on `ExecutableItemInterface`.
- Update `ExecutableItemsItemProvider` lifecycle to support lazy initialization and self-healing if ExecutableItems/SCore enables after or reloads during server runtime.
- Update `plugin.yml` to declare `softdepend: [ExecutableItems, SCore, Craftorithm]` to enforce correct startup order.
- Register `executableitem` (singular) as an alias in `ItemResolverRegistry` alongside `executableitems` and `ei`.
- Update `FurnaceEngine` output slot validation to allow stacking when the existing output item matches the custom recipe result identifier even if individual item instances differ in runtime metadata.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `external-item-providers`: Update ExecutableItems item matching and item construction specifications to use official ExecutableItems API stack inspection, dynamic multi-signature building, and resilient plugin lifecycle handling.

## Impact

- `ExecutableItemsItemProvider.java`: Overhaul reflection and matching logic.
- `PlayerFurnacesPlugin.java`: Register `executableitem` singular alias.
- `FurnaceEngine.java`: Enhance output item stacking comparison for custom namespaced items.
- `plugin.yml`: Add `softdepend` list.
- Dependencies: Soft-dependency on `ExecutableItems` and `SCore` maintained via reflection without adding hard compile-time dependencies.
