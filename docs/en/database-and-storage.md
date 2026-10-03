---
title: Database & Persistence
description: Complete guide for SQLite and H2 storage engines, HikariCP connection pooling, binary NBT serialization, and anti-duplication guarantees.
sidebar:
  order: 8
---

# Database & Persistence Architecture

PlayerFurnaces is built from the ground up to guarantee zero item loss, eliminate item duplication vulnerabilities, and preserve intricate custom item data across server restarts and player sessions.

---

## 🗄️ Supported Database Engines

The database engine is configured via the `database` section in `config.yml`:

```yaml
database:
  # Supported options: SQLITE, H2
  type: SQLITE
  file: furnaces.db
```

### 1. SQLite (`type: SQLITE`)
* **Recommended for**: Small to medium servers, survival networks, or local testing.
* **Storage**: Single local database file located at `plugins/PlayerFurnaces/furnaces.db`.
* **Benefits**: Zero installation or external configuration required. Lightweight, reliable, and easily backed up by copying the `.db` file.

### 2. H2 Database Engine (`type: H2`)
* **Recommended for**: High-traffic servers with heavy concurrent smelting activity.
* **Storage**: Embedded relational database engine storing indexed table files.
* **Benefits**: Superior concurrent write throughput and lower latency under simultaneous asynchronous furnace operations.

---

## ⚡ Connection Pooling (HikariCP)

All database communication is managed through **HikariCP**, an industry-standard, ultra-high-performance JDBC connection pool.
* **Asynchronous I/O**: Operations (saving furnace states, updating item stacks, loading player profiles) are executed asynchronously on dedicated background worker threads using `CompletableFuture`.
* **Zero TPS Impact**: Server tick rate remains smooth and uninhibited during heavy saving and loading operations.

---

## 📦 Binary NBT Item Serialization (`byte[] BLOB`)

Traditional plugins often serialize items into YAML or JSON strings, which can strip complex NBT tags, corrupt custom textures, or truncate multi-byte characters. PlayerFurnaces avoids this entirely by using **native Bukkit binary byte arrays**:

```text
+-------------------------------------------------------------+
|               BINARY ITEM PERSISTENCE PIPELINE              |
+-------------------------------------------------------------+

  1. IN-GAME ITEMSTACK
     └── Contains lore, enchantments, CustomModelData, PDC tags
             │
             ▼
  2. BUKKIT NATIVE BINARY ENCODING
     └── Serialized directly to compressed byte[] BLOB
             │
             ▼
  3. ASYNCHRONOUS HIKARICP POOL
     └── Dispatched to SQLite/H2 background worker thread
             │
             ▼
  4. PERSISTENT SQL STORAGE
     └── Stored in 'player_furnaces' table with atomic guarantees
+-------------------------------------------------------------+
```

### Key Guarantees:
1. **Total Metadata Preservation**: All `CustomModelData`, custom lores, RGB display names, enchantments, durability, and `PersistentDataContainer` (PDC) tags from plugins like **Craftorithm**, **ExecutableItems**, **Oraxen**, and **ItemsAdder** remain completely intact.
2. **Anti-Dupe Transaction Safety**: Shift-clicks and cursor pickups are managed via atomic transaction states. The plugin prevents double-click collection and number-key hotbar swapping from injecting or duplicating items.
3. **Delta-Time Offline Synchronization**: Even after days of offline absence or server downtime, the difference between the recorded timestamp and current time is calculated, delivering accurate cooked yields with zero lost fuel or items.
