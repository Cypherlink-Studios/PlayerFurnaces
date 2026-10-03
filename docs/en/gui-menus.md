---
title: Menus & GUI Layouts
description: Detailed manual for customizing menus.yml, dynamic ASCII layout grids, slot types, furnace states, custom model data, and player heads.
sidebar:
  order: 3
---

# Menus & GUI Layouts (`menus.yml`)

PlayerFurnaces features a fully modular, dynamic GUI layout engine powered by `menus.yml`. Server administrators can customize inventory sizes, rearrange button layouts using ASCII character maps, modify visual states (`smelting`, `idle`, `no_fuel`, `locked`), and apply CustomModelData or custom textured player heads.

---

## 🗺️ Layout Engine Concepts

Each menu in `menus.yml` consists of three core components:
1. **`title`**: The inventory title string (supports MiniMessage tags, HEX `#RRGGBB`, and legacy `&` codes).
2. **`layout`**: A list of strings where each line represents an inventory row of **9 slots** (up to 6 rows for 54 slots). Each character maps to a definition in the `legend`. Empty spaces (`" "`) remain completely blank and accessible.
3. **`legend`**: A dictionary defining what each character in the `layout` represents (material, display name, lore, click action, or furnace slot).

---

## 🗂️ 1. Main Hub Menu (`furnace_hub`)

The `furnace_hub` interface displays the player's collection of virtual furnaces.

### Default Configuration

```yaml
furnace_hub:
  title: "Your Virtual Furnaces"
  layout:
    - "XXXXXXXXX"
    - "X#######X"
    - "X#######X"
    - "X#######X"
    - "X#######X"
    - "XXXXXXXXX"
  
  legend:
    'X': 
      type: FILLER
      material: BLACK_STAINED_GLASS_PANE
      name: " "
    
    '#':
      type: FURNACE_SLOT
      states:
        smelting:
          material: BLAST_FURNACE
          name: "&aFurnace {id} &7(Smelting)"
          lore:
            - "&7Item: &f{item} &8x{amount}"
            - "&7Time left: &f{time}s"
            - ""
            - "&eClick to open!"
        idle:
          material: FURNACE
          name: "&aFurnace {id} &7(Idle)"
          lore:
            - "&7This furnace is not smelting anything."
            - ""
            - "&eClick to open!"
        no_fuel:
          material: FURNACE
          name: "&cFurnace {id} &7(Out of Fuel)"
          lore:
            - "&7This furnace has run out of coal."
            - ""
            - "&eClick to open!"
        locked:
          material: RED_STAINED_GLASS_PANE
          name: "&cFurnace {id} &7(Locked)"
          lore:
            - "&7You do not have permission"
            - "&7to use this furnace."
            - "&7(Requires higher rank)"
```

### Furnace Slot States (`type: FURNACE_SLOT` or `#`)
The plugin automatically evaluates each virtual furnace and dynamically displays the corresponding state:

| State Key | Trigger Condition | Available Placeholders |
| :--- | :--- | :--- |
| `smelting` | Actively cooking an item with available fuel. | `{id}`, `{item}`, `{amount}`, `{time}` |
| `idle` | Unlocked and ready, but input slot is empty or all items are processed. | `{id}` |
| `no_fuel` | Has items in the input slot, but the fuel slot is empty and fire is extinguished. | `{id}` |
| `locked` | The player lacks permission (`playerfurnaces.furnace.<id>`) for this slot index. | `{id}` |

---

## 🔥 2. Furnace View Menu (`furnace_view`)

The `furnace_view` interface is the active smelting interface opened when clicking an unlocked furnace or running `/furnace <id>`.

### Default Configuration

```yaml
furnace_view:
  title: "Furnace {id} - {status}"
  layout:
    - "XXXXXXXXX"
    - "XXIXXYYXX"
    - "XXFXPOYXX"
    - "XXSXCYYXX"
    - "XXXXBXXXX"
    
  legend:
    'X': 
      type: FILLER
      material: BLACK_STAINED_GLASS_PANE
      name: " "
    'Y': 
      type: FILLER
      material: GRAY_STAINED_GLASS_PANE
      name: " "
    'I': 
      type: INPUT
    'O': 
      type: OUTPUT
    'S':
      type: FUEL_SLOT
    'C': 
      type: COLLECT
      material: HOPPER
      name: "&aCollect Items"
      lore:
        - "&7Click here to send"
        - "&7everything to your inventory."
    'P': 
      type: PROGRESS
      active_material: LIME_STAINED_GLASS_PANE
      active_name: "&aProgress: &f{pct}%"
      waiting_material: RED_STAINED_GLASS_PANE
      waiting_name: "&7Waiting..."
    'F': 
      type: FUEL_INDICATOR
      active_material: FIRE_CHARGE
      active_name: "&6Active Fuel"
      active_lore:
        - "&7Time left: &f{time}s"
      inactive_material: COAL
      inactive_name: "&7No Fuel"
    'B': 
      type: BACK
      material: ARROW
      name: "&cBack"
```

### Functional Slot Types

| Type Name | Purpose & Behavior |
| :--- | :--- |
| `INPUT` | The slot where players place raw items/ores to be cooked. |
| `OUTPUT` | The slot where cooked products accumulate. Shift-clicking safely collects stacks. |
| `FUEL_SLOT` | The slot where players insert fuel (coal, blaze rods, buckets, custom fuels). |
| `COLLECT` | Clickable button that transfers all output items directly into the player's inventory in a single click. |
| `PROGRESS` | Visual progress bar displaying smelting percentage via `{pct}%`. Toggles between `active_*` and `waiting_*` states. |
| `FUEL_INDICATOR` | Displays remaining flame duration with `{time}s`. Toggles between `active_*` and `inactive_*` states. |
| `BACK` | Returns the player to the main `furnace_hub` menu. |
| `FILLER` | Decorative background item. Clicks are completely cancelled to prevent theft or item injection. |

---

## 🎨 Advanced Item Properties

You can customize any legend item or state with custom textures, model data, or player heads:

```yaml
'H':
  type: FILLER
  material: PLAYER_HEAD
  name: "&6Custom Skull"
  # Set skull owner by player name:
  skull_owner: "MHF_Chest"
  # OR set skull owner via Base64 texture value:
  # skull_texture: "eyJ0ZXh0dXJlcyI6..."
  # Set CustomModelData for resource packs:
  custom_model_data: 1042
  lore:
    - "&7Custom styled GUI item"
```

* **`custom_model_data`** (or `custom-model-data`): Assigns an integer value read by custom resource packs for 3D GUI textures.
* **`skull_owner`** (or `owner`): Fetches the skin of the specified player name.
* **`skull_texture`** (or `texture`): Applies a Base64-encoded skin texture directly to `PLAYER_HEAD` items.
