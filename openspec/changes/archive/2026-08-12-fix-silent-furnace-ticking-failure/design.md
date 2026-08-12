# Design: Fix Silent Virtual Furnace Ticking Failure

## Context
The virtual furnace engine uses Bukkit's scheduler (`runTaskTimer`) to update furnace smelting progress every 10 ticks (configurable). When an unhandled exception occurs inside `updateFurnaceState` or `SmeltingManager.getSmeltingRecipe`, Bukkit's scheduler permanently stops the repeating task. This causes all virtual furnaces across all online/offline players to stop smelting without producing additional log entries.

## Proposed Architecture & Changes

### 1. Robust Ticking Loop in `FurnaceManager`
- Wrap the inner per-furnace loop and GUI refresh loop in individual `try-catch(Throwable t)` blocks.
- If a specific furnace or player inventory throws an exception:
  - Log a severe error containing furnace ID and owner UUID.
  - Continue processing remaining furnaces and players in the collection.
- Ensure Bukkit's `runTaskTimer` task never terminates prematurely due to isolated errors.

```
┌─────────────────────────────────────────────────────────────┐
│                 startTickTask() Runnable                    │
├─────────────────────────────────────────────────────────────┤
│  for each furnace in cache:                                 │
│     try {                                                   │
│         FurnaceEngine.updateFurnaceState(f);                │
│     } catch (Throwable t) {                                 │
│         plugin.getLogger().log(SEVERE, "Error...", t);      │
│     }                                                       │
│                                                             │
│  for each online player:                                    │
│     try {                                                   │
│         if (player.getOpenInventory() != null ...) ...      │
│     } catch (Throwable t) { ... }                           │
└─────────────────────────────────────────────────────────────┘
```

### 2. Exception-Safe Recipe Iteration in `SmeltingManager`
- Wrap `Bukkit.recipeIterator()` loop in a `try-catch` block.
- Cache matching recipes by `Material` to avoid repeated full-registry iterations on every tick.
- Safely handle null inputs, invalid metadata, or `ConcurrentModificationException`.

### 3. Step Ticks Guard in `FurnaceEngine`
- In `FurnaceEngine.updateFurnaceState()`, compute `stepTicks`:
  ```java
  long stepTicks = Math.min(elapsedTicks, Math.min((long) furnace.getBurnTime(), (long) cookNeeded));
  if (stepTicks <= 0) {
      break;
  }
  ```
- Guaranteeing `stepTicks > 0` ensures `elapsedTicks` strictly decreases every iteration, eliminating infinite loop risks.

### 4. Null-Safe Inventory Access in GUI Refresh
- Verify `player.getOpenInventory()` is non-null before checking `getTopInventory()`.
- Wrap `hubGui.refresh()` and `viewGui.refresh()` calls to prevent GUI holder NPE crashes.

## Verification Plan
- Unit & Gradle test execution (`./gradlew test`).
- Verify furnace ticking under simulated recipe search exceptions.
- Verify furnace GUI refresh with mock players and null open inventories.
