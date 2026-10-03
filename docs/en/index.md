---
title: Introduction to PlayerFurnaces
description: Overview of PlayerFurnaces, architecture, virtual furnace concepts, delta-time calculations, and key features.
sidebar:
  order: 1
---

# Introduction to PlayerFurnaces

Welcome to the official documentation for **PlayerFurnaces**, a modern, high-performance virtual furnace engine designed for Paper and Purpur Minecraft servers.

PlayerFurnaces provides players with accessible, secure virtual furnaces accessible through customizable GUI menus. It eliminates the need for massive physical furnace arrays in player bases, optimizing server performance while offering rich gameplay mechanics.

---

## ⚡ Core Features

```text
+-----------------------------------------------------------------------------+
|                          PLAYERFURNACES ARCHITECTURE                        |
+-----------------------------------------------------------------------------+
|                                                                             |
|  [GUI LAYER]             /furnace & /pfadmin                                |
|                          Dynamic MenuLayouts (menus.yml)                    |
|                          Interactive slot states & HEX formatting           |
|                                     │                                       |
|                                     ▼                                       |
|  [SMELTING ENGINE]       Delta-Time Offline Calculation                     |
|                          Asynchronous background processing                 |
|                          Anti-theft & shift-click transaction safety        |
|                                     │                                       |
|                                     ▼                                       |
|  [RECIPES & FUELS]       Modular YAML definitions (recipes/ & fuels/)       |
|                          Recursive directory scanning                       |
|                          ItemResolverRegistry (Craftorithm, ExecutableItems)|
|                                     │                                       |
|                                     ▼                                       |
|  [STORAGE LAYER]         HikariCP Connection Pool (SQLite / H2)             |
|                          Binary NBT Serialization (byte[] BLOB)             |
|                          Zero dupe & complete PDC attribute preservation    |
|                                                                             |
+-----------------------------------------------------------------------------+
```

### 1. Delta-Time Offline Smelting Calculations
Unlike traditional physical furnaces that require chunk loading and continuous tick cycles, PlayerFurnaces utilizes an efficient **delta-time algorithm**:
* When a player disconnects, closes the GUI, or unloads the furnace, ticking stops.
* Upon reopening the furnace or reconnecting, the engine computes the exact elapsed timestamp difference in milliseconds (`System.currentTimeMillis() - lastSmeltTime`).
* It accurately fast-forwards the exact quantity of items cooked, experience accumulated, and fuel consumed during the absence.
* **Result**: Zero tick overhead on empty chunks and perfectly realistic smelting progression.

### 2. Complete Custom Item & PDC Tag Preservation
All items placed inside virtual furnaces are serialized directly into compressed binary arrays (`byte[] BLOB`) via native Bukkit NBT serialization:
* Preserves `CustomModelData`, display names (MiniMessage, Hex, legacy), lore, enchantments, and damage values.
* Preserves `PersistentDataContainer` (PDC) tags created by item plugins such as **Craftorithm**, **ExecutableItems**, **Oraxen**, **ItemsAdder**, or **MMOItems**.
* Guarantees total protection against item duplication, cursor desynchronization, and illegal inventory swapping.

### 3. Modular Recipes, Custom Fuels & Third-Party Resolution
* **Custom Recipes**: Define custom smelting recipes in `recipes/` (with recursive subfolders) specifying input items, outputs, custom burn ticks, experience, and custom model data.
* **Vanilla Overrides**: Override standard Minecraft smelting recipes or disable specific vanilla materials.
* **Custom Fuels**: Configure unique fuel types in `fuels/` with custom burn durations (`burn-time-ticks`).
* **Item Providers**: Built-in resolution for third-party namespaces (`craftorithm:item_id`, `executableitems:item_id`, `ei:item_id`).

### 4. Dynamic Menu Layouts
* Fully customizable GUIs configured in `menus.yml`.
* Design unique Hub and Furnace interfaces using ASCII symbol grids, custom filler items, dynamic furnace slot states (`smelting`, `idle`, `no_fuel`, `locked`), and player head textures.

---

## 🗺️ Documentation Guide

Explore the sections of the manual to configure and maximize your server setup:

1. [**General Configuration**](configuration/) - Detailed options for `config.yml` and `messages.yml`.
2. [**GUI Menus & Layouts**](gui-menus/) - Customize menu grids, slot states, and filler items in `menus.yml`.
3. [**Commands & Permissions**](commands-and-permissions/) - Player commands, administrative inspection, recipe importing, and permission hierarchies.
4. [**Custom Recipes**](custom-recipes/) - Syntax guide for custom cooking recipes, overrides, and recursive directory loading.
5. [**Custom Fuels**](custom-fuels/) - Setting up custom fuels and burn duration precedence rules.
6. [**External Item Providers**](external-item-providers/) - Integrating items from Craftorithm, ExecutableItems, and custom plugins via the developer API.
7. [**Database & Persistence**](database-and-storage/) - Configuring SQLite/H2 engines, HikariCP, and understanding binary persistence.
