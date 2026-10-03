# Tasks

## 1. Metadata Configuration

- [x] 1.1 Create `docs/metadata.yml` conforming to `RemotePluginMetadataSchema` with active release `1.7.7`, historical versions (`1.0.0`-`1.4.0`) flagged `hasDocs: false`, and verify schema syntax

## 2. English Documentation Suite (`docs/en/`)

- [x] 2.1 Create `docs/en/index.md` with Starlight frontmatter, overview, core mechanics, and delta-time offline calculations
- [x] 2.2 Create `docs/en/configuration.md` covering `config.yml`, `messages.yml`, MiniMessage, and HEX formatting
- [x] 2.3 Create `docs/en/gui-menus.md` documenting `menus.yml` dynamic layout engine, slot states, CustomModelData, and player heads
- [x] 2.4 Create `docs/en/commands-and-permissions.md` documenting player and admin commands (`/pfadmin view`, `force-open`, `import`, `reload`), permissions, and LuckPerms setups
- [x] 2.5 Create `docs/en/custom-recipes.md` covering recipe YAML syntax, recursive subdirectories, input/result metadata, and vanilla overrides
- [x] 2.6 Create `docs/en/custom-fuels.md` covering fuel YAML syntax, burn durations, and fuel resolution precedence
- [x] 2.7 Create `docs/en/external-item-providers.md` covering `ItemResolverRegistry`, Craftorithm, ExecutableItems, and custom `ItemProvider` API
- [x] 2.8 Create `docs/en/database-and-storage.md` covering SQLite, H2, HikariCP, and binary NBT anti-duplication persistence

## 3. Spanish Documentation Suite (`docs/es/`)

- [x] 3.1 Create `docs/es/index.md` mirroring English content with 1:1 slug parity
- [x] 3.2 Create `docs/es/configuration.md` mirroring English content with 1:1 slug parity
- [x] 3.3 Create `docs/es/gui-menus.md` mirroring English content with 1:1 slug parity
- [x] 3.4 Create `docs/es/commands-and-permissions.md` mirroring English content with 1:1 slug parity
- [x] 3.5 Create `docs/es/custom-recipes.md` mirroring English content with 1:1 slug parity
- [x] 3.6 Create `docs/es/custom-fuels.md` mirroring English content with 1:1 slug parity
- [x] 3.7 Create `docs/es/external-item-providers.md` mirroring English content with 1:1 slug parity
- [x] 3.8 Create `docs/es/database-and-storage.md` mirroring English content with 1:1 slug parity

## 4. Cleanup & Parity Validation

- [x] 4.1 Validate strict 1:1 filename parity between `docs/en/` and `docs/es/` and verify Starlight frontmatter in all 16 files
- [x] 4.2 Safely remove legacy flat files in `docs/` (`Commands-and-Permissions.md`, `Configuration.md`, `Custom-Fuels.md`, `Custom-Recipes.md`, `Database-and-Storage.md`, `External-Item-Providers.md`, and old `docs/README.md`)
- [x] 4.3 Update repository root `README.md` links to point to the modern documentation guides
