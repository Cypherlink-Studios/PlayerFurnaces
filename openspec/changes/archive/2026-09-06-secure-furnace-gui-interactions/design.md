## Context

See `proposal.md` - Why.
In `GuiListener.java`, `onClick(InventoryClickEvent event)` handles interactions when `event.getInventory().getHolder()` is an instance of `FurnaceViewGui`.
Currently, lines 69-95 run `if (event.isShiftClick())` without checking whether `event.getRawSlot()` belongs to the top GUI inventory or the bottom player inventory. It unconditionally clones the clicked item into either the input slot or fuel slot, setting the original item amount to 0. This enables players to duplicate/steal decorative panel items (such as filler glass, progress indicators, or buttons) and causes output items to be redirected into the input slot. Furthermore, `ClickType.NUMBER_KEY` and `ClickType.DOUBLE_CLICK` can bypass slot filters if not explicitly checked against top GUI slots.

## Goals / Non-Goals

**Goals:**
- Eliminate all avenues for moving non-inventory decorative or button items out of `FurnaceViewGui`.
- Prevent hotbar swap keys (1-9) from placing items into the output slot or any other top GUI slot.
- Prevent double-click cursor collection from pulling items from the GUI inventory.
- Ensure the legitimate collection pathway (`COLLECT` button) and standard cursor placement into input/fuel slots work reliably without regressions.

**Non-Goals:**
- Implementing smart/contextual shift-click routing from player inventory into furnace slots (Option A chosen: shift-click is completely disabled for simplicity and bulletproof security).
- Altering the hub menu (`FurnaceHubGui`), which already cancels all clicks.
- Changing database persistence or furnace tick mechanics.

## Decisions

### Decision 1: Complete cancellation of Shift-Click in `FurnaceViewGui`
- **Choice**: If `event.isShiftClick()` is true in `FurnaceViewGui`, immediately set `event.setCancelled(true)` and `return;`.
- **Rationale**:
  - Eliminates 25 lines of buggy item-cloning logic.
  - Zero risk of packet desynchronization or item duplication.
  - The plugin already has a dedicated `COLLECT` button specifically designed to transfer output items to player inventory safely.
- **Alternatives Considered**:
  - *Option B (Shift-click only from player inventory)*: Complex slot resolution (`rawSlot >= inv.getSize()`), edge cases with partial stack sizes and fuel checks. Can introduce desyncs across Paper/Spigot versions.
  - *Option C (Full bidirectional shift-click)*: Requires complex simulation of Bukkit's shift-click item moving algorithm. Overkill when `COLLECT` button exists.

### Decision 2: Guarding against Hotbar Number Key Swaps (`ClickType.NUMBER_KEY`)
- **Choice**: When `rawSlot < viewGui.getInventory().getSize()` and `event.getClick() == ClickType.NUMBER_KEY`, cancel the event immediately.
- **Rationale**:
  - Hovering over the `OUTPUT` slot and pressing 1-9 swaps the player's hotbar item into the output slot, bypassing `cursor != null && !cursor.getType().isAir()`.
  - Hovering over filler or status items could also trigger edge-case swaps if Bukkit handles number keys before general cancellation.
  - Number keys on `INPUT` and `FUEL_SLOT` can also bypass fuel validation or cause out-of-order state updates. Disabling number key swaps across all top GUI slots guarantees deterministic cursor interactions.

### Decision 3: Guarding against Double Click (`ClickType.DOUBLE_CLICK`)
- **Choice**: If `event.getClick() == ClickType.DOUBLE_CLICK`, set `event.setCancelled(true)` and return.
- **Rationale**:
  - Prevents Bukkit's collect-to-cursor action from aggregating similar items from the GUI slots (e.g. decorative glass panes, coal, iron) into the player's cursor.

## Risks / Trade-offs

- [Player convenience reduced for fast loading] → Players must place items into input/fuel using drag or regular cursor clicks. This trade-off is widely accepted in custom Bukkit GUIs to ensure economic and item security.
- [Bukkit event firing quirks across versions] → Cancelling `isShiftClick()`, `NUMBER_KEY`, and `DOUBLE_CLICK` as early guard clauses at the top of the event handler guarantees that Bukkit cannot execute default slot manipulation logic.
