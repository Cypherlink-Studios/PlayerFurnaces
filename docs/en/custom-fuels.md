---
title: Custom Fuels
description: Guide to defining custom furnace fuels, burn durations, recursive file loading, and fuel resolution priority.
sidebar:
  order: 6
---

# Custom Fuels Guide (`fuels/*.yml`)

PlayerFurnaces allows server administrators to define custom fuels inside `plugins/PlayerFurnaces/fuels/`. Custom fuels can modify standard vanilla burn durations, register completely new fuel materials, or integrate with custom third-party items.

---

## 📁 Directory Structure & Subfolders

Like the recipe engine, fuel files support recursive subdirectory scanning (`Files.walk`), enabling clean organization:

```text
plugins/PlayerFurnaces/fuels/
├── hyper_coal.yml
├── magic/
│   ├── fire_essence.yml
│   └── dragon_breath.yml
└── industry/
    └── enriched_uranium.yml
```

---

## 📄 Configuration Syntax

Each `.yml` file in `fuels/` can define one or more unique fuel identifiers:

```yaml
hyper_coal:
  # Bukkit material name OR namespaced provider ID:
  type: "COAL"
  # Duration in server ticks that one unit of this fuel burns (20 ticks = 1 second):
  burn-time-ticks: 1000
```

### Advanced Namespaced Fuel Example

```yaml
infernal_core:
  type: "craftorithm:infernal_core"
  burn-time-ticks: 24000 # Burns for 20 minutes (smelts 120 items)
```

### Parameter Breakdown

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `type` | String | Yes | Material or item identifier. Can be a Bukkit material (e.g., `COAL`, `BLAZE_ROD`), a custom item namespace (e.g., `craftorithm:infernal_core`, `executableitems:super_coal`), or matched via registered `ItemProvider` implementations. |
| `burn-time-ticks` | Integer | Yes | The total duration in ticks that a single fuel unit burns in a virtual furnace. |

---

## 🔄 Fuel Resolution Precedence

When a player places an item into the fuel slot (Slot 29), PlayerFurnaces determines its burn duration according to this strict priority cascade:

```text
+-------------------------------------------------------------+
|                FUEL RESOLUTION PRIORITY CASCADE             |
+-------------------------------------------------------------+

  1. ACTIVE RECIPE FUEL OVERRIDE
     └── 'fuel.burn-time-ticks' defined inside the active recipe.
     └── Takes absolute priority if present.
             │ (if not defined)
             ▼
  2. GLOBAL CUSTOM FUEL DEFINITION
     └── Matches 'type' against custom fuel definitions in 'fuels/*.yml'.
     └── Takes precedence over vanilla durations.
             │ (if no custom fuel matches)
             ▼
  3. VANILLA BUKKIT BURN DURATION
     └── Standard Minecraft fuel values (Coal = 1600t, Lava = 20000t, etc.).
     └── Returns 0 ticks if the item is not a valid Minecraft fuel.
+-------------------------------------------------------------+
```

---

## ⏱️ Burn Duration Reference

| Minecraft Fuel | Standard Burn Ticks | Duration | Items Smelted (at 200 ticks/item) |
| :--- | :--- | :--- | :--- |
| Wood Wooden Tools | `200 ticks` | 10 seconds | 1 item |
| Coal / Charcoal | `1,600 ticks` | 80 seconds | 8 items |
| Blaze Rod | `2,400 ticks` | 120 seconds | 12 items |
| Dried Kelp Block | `4,000 ticks` | 200 seconds | 20 items |
| Block of Coal | `16,000 ticks` | 800 seconds | 80 items |
| Lava Bucket | `20,000 ticks` | 1,000 seconds | 100 items |
