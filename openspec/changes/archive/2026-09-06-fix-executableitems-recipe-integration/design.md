## Context

In `PlayerFurnaces`, custom furnace recipes and custom fuels can reference items provided by external plugins through the `ItemResolverRegistry` and `ItemProvider` implementations using the format `namespace:item_id` (e.g. `executableitems:id` or `ei:id`).

Currently, when users define recipes involving ExecutableItems (such as `input: id: "ei:basic-mezcla-acero"` and `result: id: "ei:mat-lingote-acero"`), the integration fails on modern versions of ExecutableItems (7.x) and SCore (5.x):
1. Item matching via `ExecutableItemsItemProvider.isSimilar()` instantiates a new `ItemStack` through `getItem()` and compares it to the furnace input using Bukkit's `itemStack.isSimilar()`. Because ExecutableItems writes dynamic instance metadata (UUIDs, timestamps, usage counts) to item NBT, Bukkit's equality check always returns false.
2. Result item generation via `ExecutableItemsItemProvider.getItem()` searches for `buildItem` methods via reflection, halting at the first match. Modern SCore/EI defines `buildItem(int amount, Optional<Integer> usage, Optional<Player> creator)` with 3 parameters, causing reflection to fail to match the 1- or 2-parameter expectations and returning `null`.
3. `plugin.yml` lacks `softdepend: [ExecutableItems, SCore, Craftorithm]`, risking initialization before ExecutableItems is enabled.
4. `FurnaceEngine` output slot validation uses strict Bukkit `isSimilar()`, which would halt smelting when stacking output items if each generated item carries a distinct UUID.

## Goals / Non-Goals

**Goals:**
- Implement stack inspection using `ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(ItemStack)` to accurately identify ExecutableItems input and fuel items by ID.
- Dynamically invoke `buildItem` on `ExecutableItemInterface` supporting 3-arg, 2-arg, and 1-arg signatures to reliably produce recipe results.
- Implement lazy hook verification in `ExecutableItemsItemProvider` to handle plugin reload and late initialization gracefully.
- Declare `softdepend: [ExecutableItems, SCore, Craftorithm]` in `plugin.yml`.
- Register `executableitem` (singular) as an alias alongside `executableitems` and `ei`.
- Enhance `FurnaceEngine` output stacking to permit stacking if the existing output item matches the custom recipe result identifier.

**Non-Goals:**
- Add a hard compile-time dependency on `SCore` or `ExecutableItems` JARs (continue using pure reflection to allow PlayerFurnaces to run standalone).
- Change how vanilla recipes or Craftorithm recipes are evaluated.

## Decisions

### Decision 1: Inspect ItemStacks via `getExecutableItem(ItemStack)` rather than `ItemStack.isSimilar()`
*Rationale*: SCore attaches unique internal UUIDs and timestamps to each created item. Comparing newly built items with existing items via Bukkit `isSimilar()` fails. The official API method `manager.getExecutableItem(itemStack)` returns `Optional<ExecutableItemInterface>`, which provides the canonical item ID through `.getId()`.
*Alternative considered*: Stripping NBT tags before comparing. Rejected because SCore NBT format varies between versions and direct NBT manipulation requires NMS or third-party NBT libraries.

### Decision 2: Multi-Signature Dynamic Invocation for `buildItem`
*Rationale*: Different versions of ExecutableItems/SCore expose varying overloads of `buildItem` (1 argument `amount`, 2 arguments `amount, Optional<Player>`, or 3 arguments `amount, Optional<Integer>, Optional<Player>`). By checking candidate methods with name `buildItem` and matching parameter counts dynamically, the provider remains backwards- and forwards-compatible.
*Alternative considered*: Hardcoding only the 3-argument signature. Rejected because older server environments running legacy EI versions would break.

### Decision 3: Enhanced Output Slot Compatibility in `FurnaceEngine`
*Rationale*: When smelting multiple custom items consecutively, if `output.isSimilar(result)` fails due to differing runtime UUIDs, `FurnaceEngine` should verify if `itemResolverRegistry.matches(output, resDef.getId())` is true and material matches. If so, it treats the output item as the same item type and increments the stack amount up to max stack size.
*Alternative considered*: Forcing `cachedResult` to remain indefinitely without re-resolution. Rejected because player interactions and furnace persistence reload items from storage.

### Decision 4: Lazy Provider Initialization & `softdepend`
*Rationale*: Adding `softdepend: [ExecutableItems, SCore, Craftorithm]` in `plugin.yml` ensures Paper loads dependencies first. Adding an internal `ensureInitialized()` method ensures that even if a server admin uses `/reload` or `/plugman reload ExecutableItems`, the provider re-establishes reflection hooks automatically on the next access.

## Risks / Trade-offs

- **[Risk] ExecutableItems API class changes in future major version** → *Mitigation*: We maintain multiple fallback strategies: manager instance reflection, static API reflection, and PDC key inspection.
- **[Risk] Output items stacking with different internal attributes** → *Mitigation*: Output stacking only occurs when the recipe explicitly specifies the same custom item ID and material.
