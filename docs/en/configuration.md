---
title: General Configuration
description: Complete guide for config.yml, messages.yml, database settings, limits, vanilla smelting options, and message formatting.
sidebar:
  order: 2
---

# General Configuration

PlayerFurnaces stores its global settings, database connections, furnace boundaries, and localized messages in two root configuration files: `config.yml` and `messages.yml`.

---

## 📁 `config.yml`

The `config.yml` file controls the underlying persistence storage engine, furnace slot limits per player, interface refresh rates, and vanilla recipe fallback rules.

### Default `config.yml`

```yaml
database:
  # Database engine: SQLITE or H2
  type: SQLITE
  # File name inside plugins/PlayerFurnaces/
  file: furnaces.db

settings:
  # Default furnace count displayed in the main Hub GUI (1 to 54)
  default-furnace-count: 14
  # Absolute maximum furnace count limit in the system
  max-furnace-count: 54
  # GUI refresh rate in server ticks (20 ticks = 1 second)
  gui-refresh-ticks: 10

recipes:
  vanilla-smelting:
    # Set to false to disable standard Bukkit smelting recipes in virtual furnaces
    enabled: true
    # List of vanilla material names that cannot be smelted
    disabled-materials:
      - RAW_IRON
      - ANCIENT_DEBRIS
```

### Parameter Breakdown

| Key | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `database.type` | String | `SQLITE` | Database persistence engine. Supported: `SQLITE` (single local file) or `H2` (high-concurrency relational file). |
| `database.file` | String | `furnaces.db` | Storage file name located inside `plugins/PlayerFurnaces/`. |
| `settings.default-furnace-count` | Integer | `14` | Default number of furnace slots shown in the player Hub menu (up to 54). |
| `settings.max-furnace-count` | Integer | `54` | Absolute system cap for total virtual furnaces per player. |
| `settings.gui-refresh-ticks` | Integer | `10` | Frequency in server ticks (10 ticks = 0.5s) for updating animated progress bars and item states in open menus. |
| `recipes.vanilla-smelting.enabled` | Boolean | `true` | When `true`, standard vanilla Minecraft smelting recipes can be processed as fallbacks if no custom recipe matches. |
| `recipes.vanilla-smelting.disabled-materials` | List<String> | `[]` | List of Bukkit material names blocked from smelting even when vanilla fallback is enabled. |

> [!NOTE]
> Menu layouts and visual titles are configured separately in `menus.yml`. See [GUI Menus & Layouts](gui-menus/) for menu layout instructions.

---

## 💬 `messages.yml`

The `messages.yml` file controls all player notifications, administrative feedback, and error messages.

### Message Formatting Standards

PlayerFurnaces supports three complementary formatting standards across all strings and message lists:
1. **MiniMessage Tags**: `<gradient:#ff5555:#ffaa00>PlayerFurnaces</gradient>`, `<red><b>Error!</b></red>`, `<hover:show_text:'Click here'>Text</hover>`.
2. **Direct HEX Codes**: `#FF5733Text` or `&#FF5733Text`.
3. **Legacy Formatting Codes**: Standard Minecraft ampersand color codes (`&a`, `&e`, `&7`, `&l`, `&r`).

### Default `messages.yml`

```yaml
prefix: "&8[&ePlayerFurnaces&8] "
no-permission: "&cYou do not have permission to perform this action."
no-furnace-permission: "&cYou do not have permission to access Furnace #{id}."
furnace-not-found: "&cThe specified furnace does not exist or is unavailable."
player-not-found: "&cThe player was not found or their data has not loaded."
player-offline: "&cPlayer {player} is not online."
reload-success: "&aConfiguration and messages successfully reloaded!"
collect-success: "&aProcessed items collected successfully!"
only-players: "&cThis command can only be executed by a player."
furnace-id-invalid: "&cFurnace ID must be a number between 1 and {max}."
usage-furnace: "&cUsage: /furnace [id]"
usage-admin-view: "&cUsage: /pfadmin view <player> [id]"
admin-furnace-id-number: "&cFurnace ID must be an integer."

admin-help:
  - "&e&lPlayerFurnaces Admin Commands:"
  - "&f/pfadmin view <player> [id] &7- View a player's furnace"
  - "&f/pfadmin force-open <player> <id> &7- Force open a player's furnace"
  - "&f/pfadmin import <plugin> [--overwrite] &7- Import recipes from external plugin"
  - "&f/pfadmin reload &7- Reload configuration and recipes"

import-usage: "&cUsage: /pfa import <plugin> [--overwrite|-f]"
import-plugin-not-found: "&cPlugin '{plugin}' is not installed, enabled, or supported for import."
import-success: "&aSuccessfully imported {imported} recipes from {plugin} ({skipped} skipped)."

collection:
  success: "&aSuccessfully collected processed items!"
  partial: "&eCollected partial production (inventory is full)."
```

### Available Placeholders

| Placeholder | Where Available | Description |
| :--- | :--- | :--- |
| `{id}` | `no-furnace-permission`, GUI titles | The numeric identifier of the furnace (e.g., `1`, `2`). |
| `{player}` | `player-offline`, GUI Hub title | Name of the target player. |
| `{max}` | `furnace-id-invalid` | The maximum furnace count allowed (`max-furnace-count`). |
| `{plugin}` | `import-plugin-not-found`, `import-success` | The external plugin name targeted for import. |
| `{imported}` | `import-success` | Number of recipes successfully converted and written. |
| `{skipped}` | `import-success` | Number of recipes skipped (e.g., duplicates without overwrite). |
