# Proposal

## Why

The current documentation for PlayerFurnaces consists of flat, legacy markdown files in English only, lacking metadata configuration and missing multiple key features introduced up to version `v1.7.7` (such as `menus.yml` custom layout configuration, `/pfadmin import`, `/pfadmin force-open`, recursive recipe/fuel directory loading, and expanded ExecutableItems provider options). Furthermore, the DarkBladeDev documentation portal requires a unified `docs/metadata.yml` and strict 1:1 locale file parity (`docs/es/` and `docs/en/`) conforming to the `/plugin-docs-versioning` specification for automated Starlight ingestion and multi-version management.

## What Changes

- Add `docs/metadata.yml` conforming to `RemotePluginMetadataSchema` with active release `1.7.7` and historical catalog entries (`1.0.0` to `1.4.0` marked with `hasDocs: false`).
- Reorganize documentation into localized subdirectories `docs/es/` and `docs/en/` with strict 1:1 filename slug parity and Starlight frontmatter (`title`, `description`, `sidebar.order`).
- Create 8 comprehensive documentation pages in both languages:
  - `index.md`: Overview, architecture, delta-time smelting, and key capabilities.
  - `configuration.md`: Breakdown of `config.yml` and `messages.yml`, supporting MiniMessage, HEX `#RRGGBB`, and legacy codes.
  - `gui-menus.md`: Detailed guide for `menus.yml` dynamic layout engine, slot states (`smelting`, `idle`, `no_fuel`, `locked`), fillers, custom model data, and player heads.
  - `commands-and-permissions.md`: Player and admin commands (including `/pfadmin view`, `force-open [-b]`, `import`, and `reload`), permission nodes, and LuckPerms setups.
  - `custom-recipes.md`: Smelting recipe definitions, recursive directory structure, ingredients, results, PDC/metadata tags, cooking ticks, and vanilla recipe overrides.
  - `custom-fuels.md`: Global fuel definitions, burn time in ticks, and fuel resolution precedence order.
  - `external-item-providers.md`: `ItemResolverRegistry` architecture, native support for Craftorithm and ExecutableItems (`executableitems`, `ei`, `executableitem`), and custom `ItemProvider` API.
  - `database-and-storage.md`: SQLite and H2 engine configurations, HikariCP connection pool, binary NBT serialization (`byte[] BLOB`), anti-duplication guarantees, and asynchronous persistence.
- Safely remove legacy flat files in `docs/` (`Commands-and-Permissions.md`, `Configuration.md`, `Custom-Fuels.md`, `Custom-Recipes.md`, `Database-and-Storage.md`, `External-Item-Providers.md`, and old `README.md`) while preserving repository-level files.

## Capabilities

### New Capabilities
- `plugin-documentation`: Specifies portal metadata compliance, localized documentation structure with 1:1 slug parity, and feature coverage for PlayerFurnaces.

### Modified Capabilities
<!-- None -->

## Impact

- Documentation structure in `docs/` is completely modernized to support DarkBladeDev Astro/Starlight documentation portal and historical version cataloging.
- No Java source code or plugin binary behavior is modified.
