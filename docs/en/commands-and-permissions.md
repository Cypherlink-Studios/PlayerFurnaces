---
title: Commands & Permissions
description: Complete reference for player commands, administrator inspection, force-opening, recipe importing, and permission hierarchies.
sidebar:
  order: 4
---

# Commands & Permissions

PlayerFurnaces provides player commands with contextual tab completion, powerful administrative utilities for inspecting and managing player furnaces, and a granular permission node structure.

---

## 🎮 Player Commands

### `/furnace`
* **Aliases**: `/furnaces`, `/horno`, `/hornos`, `/pf`
* **Permission**: `playerfurnaces.command.use` (default: `true`)
* **Description**: Opens the personal Virtual Furnace Hub menu displaying all accessible furnaces.
* **Usage**:
  ```bash
  # Open the furnace hub
  /furnace

  # Directly open furnace #1
  /furnace 1
  ```
* **Contextual Tab Completion**: Suggests unlocked furnace IDs based on the player's assigned permissions.

---

## 👮 Admin Commands

### `/playerfurnacesadmin`
* **Aliases**: `/pfadmin`, `/furnacesadmin`, `/pfa`
* **Permission**: `playerfurnaces.admin` (default: `op`)
* **Description**: Root command for administrative monitoring, management, and configuration.

### Subcommands

#### 1. `/pfadmin view <player> [id]`
* **Description**: Allows administrators to view the furnace hub or open an individual furnace belonging to any target player, whether online or offline.
* **Examples**:
  ```bash
  # View steve's furnace hub
  /pfadmin view steve

  # Inspect steve's furnace #3 directly
  /pfadmin view steve 3
  ```

#### 2. `/pfadmin force-open <player> <id> [--bypass-perms|-b]`
* **Description**: Forces an online target player to immediately open their specified virtual furnace interface.
* **Permission Check**: By default, checks if the target player has permission for that furnace (`playerfurnaces.furnace.<id>`). If the player lacks permission, the command halts and notifies the admin.
* **Bypass Flag**: Append `--bypass-perms` or `-b` to bypass permission checks and force-open the furnace regardless of player rank.
* **Examples**:
  ```bash
  # Force Steve to open furnace #1 (verifies Steve's permissions)
  /pfadmin force-open Steve 1

  # Force Alex to open furnace #4 bypassing permission requirements
  /pfadmin force-open Alex 4 --bypass-perms
  /pfadmin force-open Alex 4 -b
  ```

#### 3. `/pfadmin import <plugin> [--overwrite|-f]`
* **Description**: Automatically scans and imports custom cooking recipes from supported third-party plugins (e.g., **Craftorithm**) into modular `.yml` files inside `plugins/PlayerFurnaces/recipes/<plugin>/` and performs an automatic hot-reload.
* **Overwrite Flag**: Use `--overwrite` or `-f` to overwrite pre-existing recipe files.
* **Examples**:
  ```bash
  # Import all Craftorithm smelting recipes
  /pfadmin import craftorithm

  # Force overwrite existing imported recipes
  /pfadmin import craftorithm --overwrite
  /pfadmin import craftorithm -f
  ```

#### 4. `/pfadmin reload`
* **Description**: Performs a comprehensive hot-reload of `config.yml`, `messages.yml`, `menus.yml`, all recipe files (`recipes/` and subdirectories), and custom fuels (`fuels/`).
* **Example**:
  ```bash
  /pfadmin reload
  ```

---

## 🔒 Permission Node Reference

PlayerFurnaces features a structured permission hierarchy:

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `playerfurnaces.command.use` | `true` (All players) | Allows execution of `/furnace`. |
| `playerfurnaces.admin` | `op` (Operators) | Grants access to `/playerfurnacesadmin` and all subcommands. |
| `playerfurnaces.furnace.1` | `op` | Grants access to Virtual Furnace #1. |
| `playerfurnaces.furnace.2` | `op` | Grants access to Virtual Furnace #2. |
| `playerfurnaces.furnace.<3-54>` | `false` | Unlocks individual virtual furnace `<id>`. Ideal for VIP tiers, donor ranks, or quest unlocks. |

---

## 💡 LuckPerms Configuration Examples

### Basic Player Access (Furnaces 1 & 2)
```bash
/lp group default permission set playerfurnaces.command.use true
/lp group default permission set playerfurnaces.furnace.1 true
/lp group default permission set playerfurnaces.furnace.2 true
```

### VIP Tier (Furnaces 1 through 5)
```bash
/lp group vip permission set playerfurnaces.furnace.3 true
/lp group vip permission set playerfurnaces.furnace.4 true
/lp group vip permission set playerfurnaces.furnace.5 true
```

### Granting Global Admin Access to Staff
```bash
/lp group admin permission set playerfurnaces.admin true
```
