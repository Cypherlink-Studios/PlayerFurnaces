---
title: Custom Recipes
description: Complete guide for configuring custom furnace recipes, recursive subdirectory scanning, item metadata, timings, experience, and vanilla overrides.
sidebar:
  order: 5
---

# Custom Recipes Guide (`recipes/*.yml`)

PlayerFurnaces includes a modular, extensible smelting recipe engine that allows server administrators to define custom cooking recipes, apply detailed item metadata (PDC, CustomModelData, formatted lore), organize recipes into subdirectories, and override or disable vanilla Minecraft recipes.

---

## 📁 Directory Structure & Recursive Loading

Recipes are loaded from `plugins/PlayerFurnaces/recipes/`. Starting in `v1.4.0`, PlayerFurnaces uses recursive directory scanning (`Files.walk`), allowing you to cleanly organize recipes into custom subfolders:

```text
plugins/PlayerFurnaces/recipes/
├── default.yml
├── magic/
│   ├── runes.yml
│   └── potions.yml
├── metallurgy/
│   ├── bronze.yml
│   └── steel.yml
└── craftorithm/
    └── imported_recipes.yml
```

Each `.yml` file can contain one or multiple unique recipe keys.

---

## 📄 Complete Recipe Example

Below is a complete recipe definition demonstrating all available fields:

```yaml
mythic_ruby_smelting:
  # Input requirements (Slot 11)
  input:
    id: ruby_ore
    material: REDSTONE_ORE
    name: "<red>Ruby Ore"
    lore:
      - ""
      - "<gray>Unrefined gemstone mined from deep underground."
    custom-model-data: 1005
    pdc:
      "mining:ore_type": "ruby"

  # Result product (Slot 15)
  result:
    id: "craftorithm:ruby_ingot"
    material: REDSTONE
    amount: 1
    name: "<gradient:#ff0055:#ffaa00><b>Refined Ruby Ingot</b></gradient>"
    lore:
      - "<gray>Radiates intense heat."
    custom-model-data: 2001
    pdc:
      "quality:tier": "mythic"

  # Smelting parameters
  cook-time-ticks: 100 # Cooking duration: 100 ticks = 5 seconds (20 ticks = 1s)
  experience: 3.5      # Experience points awarded upon smelting

  # Fuel constraint (Optional)
  fuel:
    type: "hyper_coal"  # Fuel requirement (Custom fuel ID, material, or namespaced item)
    burn-time-ticks: 800 # Overrides burn duration when cooking this specific recipe
```

---

## ⚙️ Configuration Attributes

### 1. Input Specification (`input`)
Defines the required item properties in the input slot:

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `id` | String | No | Provider ID or namespaced identifier (e.g., `craftorithm:ruby_ore`, `ei:fire_gem`). If namespaced, verification is handled by the registered `ItemProvider`. |
| `material` | Material | Yes* | Vanilla Bukkit material (e.g., `REDSTONE_ORE`, `RAW_IRON`, `COPPER_ORE`). (*Not required if using a registered provider ID). |
| `name` | String | No | Formatted item name required on the input item. Supports MiniMessage, HEX, and `&`. |
| `lore` | List<String> | No | Required lore lines matching the input stack. |
| `custom-model-data` | Integer | No | Required `CustomModelData` integer tag. |
| `pdc` | Map<String, String> | No | Key-value pairs required inside the item's `PersistentDataContainer`. |

### 2. Result Specification (`result`)
Defines the output produced in the furnace output slot:

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `id` | String | No | Third-party namespaced identifier (e.g., `craftorithm:ruby_ingot`, `executableitems:gold_coin`). Resolves exact NBT/PDC directly from the external plugin. |
| `material` | Material | Yes* | Bukkit material name of the generated result stack. (*Required if no provider ID is provided). |
| `amount` | Integer | No | Stack size produced per completed smelting cycle (default: `1`). |
| `name` | String | No | Custom display name applied to the result item. |
| `lore` | List<String> | No | Custom lore lines applied to the result item. |
| `custom-model-data` | Integer | No | CustomModelData applied to the output item. |
| `pdc` | Map<String, String> | No | Custom PDC string keys and values injected into the output item. |

### 3. Timings & Experience
* **`cook-time-ticks`**: Number of server ticks required to smelt one item. (Default: `200` ticks = 10 seconds).
* **`experience`**: Amount of floating-point experience awarded when the player takes the output item. (Default: `0.0`).

### 4. Fuel Constraint (`fuel`)
Allows restricting a recipe to only smelt when supplied with a specific fuel type:
* **`type`**: Identifier of the required fuel. Can be a custom fuel ID (`hyper_coal`), a Bukkit material (`BLAZE_ROD`), or a namespaced item (`craftorithm:hellfire_shard`).
* **`burn-time-ticks`**: (Optional) Overrides the standard burn time of the fuel exclusively while cooking this specific recipe.

---

## 🚫 Overriding and Disabling Vanilla Recipes

### 1. Overriding Vanilla Minecraft Recipes
Any custom recipe in `recipes/` that targets a vanilla input material (e.g., `RAW_IRON`) takes precedence over default Bukkit smelting:

```yaml
fast_iron_smelt:
  input:
    material: RAW_IRON
  result:
    material: IRON_INGOT
    amount: 2          # Yields double iron!
  cook-time-ticks: 60  # Cooks in 3 seconds instead of 10
  experience: 1.0
```

### 2. Disabling Vanilla Smelting Globally or Selectively
In `config.yml`, administrators can disable all vanilla recipes or block specific materials:

```yaml
recipes:
  vanilla-smelting:
    # Set to false to disable all standard Minecraft smelting in virtual furnaces:
    enabled: true
    # Blacklist specific materials while keeping the rest active:
    disabled-materials:
      - RAW_IRON
      - RAW_GOLD
      - ANCIENT_DEBRIS
```
