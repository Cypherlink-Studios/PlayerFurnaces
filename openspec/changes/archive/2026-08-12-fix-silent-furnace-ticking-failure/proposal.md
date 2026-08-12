# Proposal: Fix Silent Virtual Furnace Ticking Failure

## Why
Currently, an uncaught runtime exception in `FurnaceManager.startTickTask()` (such as a `ConcurrentModificationException` during Bukkit recipe iteration, internal Bukkit `RecipeChoice` failure on complex item stacks, or a null open inventory reference) permanently kills Bukkit's background timer. Once the task dies, all virtual furnaces across all players silently stop processing items, burning fuel, or updating GUIs, with no subsequent error log printed. Additionally, potential zero-step tick conditions in `FurnaceEngine` can lock the main thread in an infinite loop.

## What Changes
- **Task Exception Isolation**: Wrap the repeating execution of `startTickTask` in `FurnaceManager` in defensive `try-catch(Throwable)` blocks so that an exception in a single furnace or GUI update is logged cleanly without cancelling Bukkit's background task.
- **Safe Recipe Resolution & Caching**: Refactor `SmeltingManager.getSmeltingRecipe()` to catch exceptions during Bukkit recipe iteration, provide safe fallbacks, and prevent Bukkit iterator concurrency crashes.
- **Infinite Loop Safeguards**: Ensure `stepTicks` inside `FurnaceEngine.updateFurnaceState()` always makes positive progress (`stepTicks > 0`) or breaks early to avoid infinite while-loops.
- **Defensive GUI Refresh**: Add null-safety checks when querying `player.getOpenInventory()` and top inventory holders to prevent NPEs during GUI inventory updates.

## Capabilities

### New Capabilities
*(None)*

### Modified Capabilities
- `virtual-furnaces`: Add error isolation, safe recipe lookup, and tick guard requirements for background virtual furnace processing.

## Impact
- `dev.darkblade.playerfurnaces.manager.FurnaceManager`
- `dev.darkblade.playerfurnaces.engine.FurnaceEngine`
- `dev.darkblade.playerfurnaces.engine.SmeltingManager`
- `dev.darkblade.playerfurnaces.gui.FurnaceViewGui` / `FurnaceHubGui`
