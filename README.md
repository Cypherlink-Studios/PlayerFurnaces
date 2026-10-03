# ━━━━━━━ 🔥 PlayerFurnaces 🔥 ━━━━━━━

> **Advanced Virtual & Seamless Furnace System for Minecraft Servers (Paper/Spigot 1.20+)**
> *Inspired by premium virtual storage systems (like AxVaults), bringing item smelting to the next level.*

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20%2B-brightgreen.svg)](https://papermc.io)
[![Java Version](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Database](https://img.shields.io/badge/Database-SQLite%20%7C%20H2-yellow.svg)](https://github.com)

---

## 📖 Table of Contents
- [Showcase & Applications](#-showcase--applications)
- [Key Features](#-key-features)
- [Architecture & Technical Details](#-architecture--technical-details)
- [Commands & Permissions](#-commands--permissions)
- [Quick Start](#-quick-start)
- [Documentation & Wiki](#-documentation--wiki)

---

## 🌟 Showcase & Applications

**PlayerFurnaces** revolutionizes traditional Minecraft smelting mechanics by eliminating the need to place physical furnace blocks in the world. It provides every player with an interactive GUI menu (**Virtual Furnace Hub**) with access to multiple independent personal virtual furnaces, protected by permissions and featuring real-time offline processing.

```
                   ┌──────────────────────────────────────┐
                   │    PLAYER COMMAND: /furnace          │
                   └──────────────────┬───────────────────┘
                                      │
                                      ▼
    ┌──────────────────────────────────────────────────────────────────┐
    │                    VIRTUAL FURNACE HUB                           │
    │  [Furnace #1] [Furnace #2] [Furnace #3] ... [Furnace #14] (Up 54)│
    └─────────────────────────────────┬────────────────────────────────┘
                                      │ (Click Furnace)
                                      ▼
    ┌──────────────────────────────────────────────────────────────────┐
    │                   VIRTUAL FURNACE INTERFACE                      │
    │  [ Inputs ] ➔ ⚡ [ Progress % ] ➔ [ Processed Outputs ]        │
    │  [ Active Fuel (s) ]             [ 📦 Collect All ]              |
    └──────────────────────────────────────────────────────────────────┘
```

### 🎯 Server Applications
* **Survival / RPG / Towny**: Eliminates lag caused by thousands of physical furnace blocks ticking and interacting in the world.
* **Skyblock / OneBlock**: Saves valuable island space and prevents item theft in shared areas.
* **Advanced Economies & MMOs**: Allows creating custom recipes for custom ores (e.g., Craftorithm, Oraxen, ItemsAdder) with custom cooking durations and fuels.
* **VIP Rewards / Ranks**: Grants additional virtual furnaces progressively via permission nodes (`playerfurnaces.furnace.<id>`).

---

## ✨ Key Features

* 🟢 **Interactive GUI Hub**: Visually displays the real-time status of each furnace:
  * **Smelting** (`BLAST_FURNACE`): Displays processed item, quantity, and remaining fuel seconds.
  * **No Fuel** (`FURNACE`): Indicates fuel is required to continue.
  * **Idle** (`FURNACE`): Ready for input items.
  * **Locked** (`RED_STAINED_GLASS_PANE`): Displays the required permission node to unlock.
* ⚡ **Intelligent Offline Smelting**: Furnaces automatically calculate elapsed time (delta-time) upon reopening the menu or reconnecting, ensuring production never stalls.
* 📦 **Anti-Dupe Item Persistence (Intact NBT/PDC)**: Binary database storage (**SQLite** or **H2**) powered by HikariCP. Preserves Custom Model Data, lore, custom names, Hex/MiniMessage colors, and PDC data without item duplication risks.
* 🧪 **Modular Custom Recipes & Vanilla Controls (`recipes/*.yml` & `config.yml`)**:
  * Override vanilla recipes or create completely custom recipes.
  * Globally toggle vanilla smelting fallback (`recipes.vanilla-smelting.enabled`) or blacklist specific materials (`disabled-materials`).
  * Modularly disable specific items via recipe files (`disabled: true`).
  * Define input/output items, cooking time (`cook-time-ticks`), experience payout, and recipe-specific fuel constraints.
* 🔥 **Custom Fuel Definitions (`fuels/*.yml`)**:
  * Register global custom fuels with custom burn durations (`burn-time-ticks`).
* 🔌 **Extensible Item Provider Integration (ItemProvider API)**:
  * Native & reflection hook support for **Craftorithm** (`crafthorim:item_id`).
  * Fallback item matching using PDC (*PersistentDataContainer*) tags.
* 🎨 **Rich Formatting & Color Support**: Full native support for Kyori MiniMessage (`<red>`, `<gradient>`, `<bold>`), HEX codes (`&#RRGGBB` / `<#RRGGBB>`), and legacy ampersand codes (`&a`) across all messages, GUI titles, item names, and lore lines.
* 👤 **GUI Customization & Player Heads**: Full support in `menus.yml` for `custom_model_data`, player head owners (`skull_owner: "{player}"`), and base64 skin textures (`skull_texture`).
* 🛡️ **Admin Tools (`/pfadmin`)**:
  * Real-time inspection of any player's hub or individual furnace (online or offline) via `/pfadmin view <player> [id]`.
  * Force an online player to open a furnace via `/pfadmin force-open <player> <index> [--bypass-perms]`.
  * Hot reload of configurations, messages, menus, and recipes via `/pfadmin reload`.

---

## 🛠️ Architecture & Technical Details

The plugin is designed with a modular, reactive architecture:

```
  ┌──────────────────┐      ┌────────────────────┐      ┌──────────────────────┐
  │ Player / Admin   │ ───► │  GuiListener &     │ ───► │   FurnaceEngine      │
  │ Commands         │      │  FurnaceViewGui    │      │  (Delta-Time Smelt)  │
  └──────────────────┘      └────────────────────┘      └──────────┬───────────┘
                                                                   │
                                                                   ▼
  ┌──────────────────┐      ┌────────────────────┐      ┌──────────────────────┐
  │ ItemProvider     │ ◄─── │  RecipeManager &   │ ◄─── │  DatabaseManager     │
  │ Registry         │      │  FuelManager       │      │  (HikariCP/SQLite/H2)│
  └──────────────────┘      └────────────────────┘      └──────────────────────┘
```

1. **`FurnaceEngine`**: Core smelting engine. Independent of physical block ticks in the world. Uses timestamps (`lastUpdatedTimestamp`) to compute mathematically exact elapsed smelting cycles.
2. **`ItemResolverRegistry`**: Extensible `ItemProvider` registry resolving namespaced identifiers (`namespace:item_id`, e.g., `crafthorim:ruby_ingot`) or vanilla/local items.
3. **`DatabaseManager`**: Asynchronous data layer using HikariCP to serialize Bukkit item stacks to binary byte arrays for maximum NBT/PDC fidelity.

---

## 📜 Commands & Permissions

### 🎮 Player Commands
| Command | Aliases | Permission | Description |
| :--- | :--- | :--- | :--- |
| `/furnace` | `/furnaces`, `/horno`, `/hornos`, `/pf` | `playerfurnaces.command.use` | Opens the main virtual furnace hub. |
| `/furnace <id>` | - | `playerfurnaces.furnace.<id>` | Directly opens the specified virtual furnace. |

### 👮 Admin Commands
| Command | Permission | Description |
| :--- | :--- | :--- |
| `/pfadmin view <player> [id]` | `playerfurnaces.admin` | Inspects a player's hub or specific virtual furnace. |
| `/pfadmin force-open <player> <id> [-b]` | `playerfurnaces.admin` | Forces a target player to open a furnace (checks target's furnace permission unless `--bypass-perms` is passed). |
| `/pfadmin import <plugin> [-f]` | `playerfurnaces.admin` | Imports smelting recipes from external plugins (e.g. Craftorithm) into subfolder `recipes/<plugin>/`. |
| `/pfadmin reload` | `playerfurnaces.admin` | Reloads `config.yml`, `messages.yml`, `menus.yml`, recipes, and fuels. |

### 🔑 Permission Nodes
* `playerfurnaces.command.use` (Default: `true`): Grants access to the base `/furnace` command.
* `playerfurnaces.admin` (Default: `op`): Access to `/pfadmin` commands.
* `playerfurnaces.furnace.<1-54>` (Default for 1 & 2: `op`): Unlocks virtual furnace number `<id>`.

---

## 🚀 Quick Start

1. Download the compiled **PlayerFurnaces** `.jar` file.
2. Place the `.jar` inside your Paper / Spigot server's `plugins/` directory (1.20+).
3. Start the server to generate initial configuration files and databases.
4. *(Optional)* Customize `plugins/PlayerFurnaces/config.yml`, `recipes/`, and `fuels/` as needed.
5. Run `/pfadmin reload` to apply changes live.

---

## 📚 Documentation & Wiki

For detailed guides, syntax breakdowns, and integration manuals, visit our official documentation guides:

### 🇬🇧 English Documentation (`docs/en/`)
* 📘 [Introduction & Architecture](docs/en/index.md)
* ⚙️ [General Configuration (`config.yml` & `messages.yml`)](docs/en/configuration.md)
* 🎨 [GUI Menus & Dynamic Layouts (`menus.yml`)](docs/en/gui-menus.md)
* 🔑 [Commands & Permissions Reference](docs/en/commands-and-permissions.md)
* 🧪 [Custom Recipes Guide (`recipes/*.yml`)](docs/en/custom-recipes.md)
* 🔥 [Custom Fuels Guide (`fuels/*.yml`)](docs/en/custom-fuels.md)
* 🧩 [External Item Providers & Developer API](docs/en/external-item-providers.md)
* 💾 [Database & Storage Architecture](docs/en/database-and-storage.md)

### 🇪🇸 Documentación en Español (`docs/es/`)
* 📘 [Introducción y Arquitectura](docs/es/index.md)
* ⚙️ [Configuración General (`config.yml` y `messages.yml`)](docs/es/configuration.md)
* 🎨 [Menús y Layouts GUI (`menus.yml`)](docs/es/gui-menus.md)
* 🔑 [Referencia de Comandos y Permisos](docs/es/commands-and-permissions.md)
* 🧪 [Guía de Recetas Personalizadas (`recipes/*.yml`)](docs/es/custom-recipes.md)
* 🔥 [Guía de Combustibles Personalizados (`fuels/*.yml`)](docs/es/custom-fuels.md)
* 🧩 [Proveedores de Ítems Externos y API](docs/es/external-item-providers.md)
* 💾 [Base de Datos y Persistencia](docs/es/database-and-storage.md)

---
*Built with ❤️ for high-performance Minecraft communities.*
