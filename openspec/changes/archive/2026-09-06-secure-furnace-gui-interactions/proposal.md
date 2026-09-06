## Why

Players reported an exploit in the virtual furnace interface where shift-clicking decorative panels or functional buttons (such as filler glass panes, progress indicators, the collect button, or output slots) caused the clicked item to be cloned into the input slot while emptying the original panel slot. This allowed players to extract and keep non-obtainable GUI items. Additionally, hotbar number key swaps (`NUMBER_KEY`) and double-clicks (`DOUBLE_CLICK`) exposed potential item insertion and cursor-collection exploits in the furnace view GUI.

## What Changes

- Disable Shift-Click actions entirely within `FurnaceViewGui`, requiring players to place input and fuel items via cursor placement/drag and retrieve output items using the dedicated `COLLECT` button or standard cursor pickup.
- Cancel `ClickType.NUMBER_KEY` (hotbar swap keys 1-9) interactions targeting the top inventory GUI slots, preventing hotbar items from being swapped into the output slot, fuel slot, input slot, or decorative slots.
- Cancel `ClickType.DOUBLE_CLICK` within `FurnaceViewGui` to prevent Bukkit's cursor collection mechanism from pulling GUI items.
- Ensure valid GUI interactions remain intact: normal clicks on `INPUT`, `FUEL_SLOT`, `OUTPUT` (pickup only), `COLLECT` button (full output transfer), and `BACK` button.

## Capabilities

### New Capabilities

### Modified Capabilities
- `virtual-furnaces`: Specifies secure GUI interaction handling in the virtual furnace interface, enforcing cancellation of shift-clicks, hotbar number swaps, and double-clicks to protect custom items, decorative panels, and furnace outputs from theft and desynchronization.

## Impact

- **Affected Code**: `dev.darkblade.playerfurnaces.gui.GuiListener.java`.
- **User Experience**: Shift-click shortcuts are disabled in the furnace view GUI. Players collect output items via the `COLLECT` button or regular cursor pickup, and deposit input/fuel items using regular cursor clicks.
- **Dependencies & Storage**: No database schema, configuration file, or third-party dependency changes.
