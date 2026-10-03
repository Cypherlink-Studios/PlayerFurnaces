# Design

## Context

The repository currently contains 7 flat markdown files in `docs/` in PascalCase without localized subdirectories or metadata. See `proposal.md` for motivation. To integrate with the DarkBladeDev documentation portal powered by Astro + Starlight (`github-docs-loader.ts`), plugins must provide a `docs/metadata.yml` descriptor and localized content under `docs/es/` and `docs/en/` with strict 1:1 filename slug parity.

## Goals / Non-Goals

**Goals:**
- Provide `docs/metadata.yml` that passes Zod schema validation in `RemotePluginMetadataSchema`.
- Author 8 comprehensive documentation guides in both Spanish (`docs/es/`) and English (`docs/en/`) with matching slugs.
- Document all active plugin features up to `v1.7.7` (`menus.yml` custom GUI layouts, `/pfadmin force-open [-b]`, `/pfadmin import`, recursive recipe/fuel directory scanning, ExecutableItems provider options, HEX/MiniMessage formats).
- Safely clean up legacy flat markdown files in `docs/` while preserving the repository root `README.md`.

**Non-Goals:**
- Modifying Java code, plugin resources, or runtime behavior.
- Altering existing Git tags or attempting to retroactively inject localized documentation into immutable historical releases.

## Decisions

### Decision 1: Mark historical releases (`1.0.0` - `1.4.0`) with `hasDocs: false`
- **Choice**: In `docs/metadata.yml`, only the active release `1.7.7` (`versions[0]`) will serve documentation directly from the repository. Previous releases (`1.0.0` through `1.4.0`) will be declared in the catalog with `hasDocs: false`.
- **Rationale**: Git tags are immutable. Because tags `1.0.0` through `1.4.0` were cut with the legacy flat file structure, attempting to load documentation tarballs for them would trigger Starlight locale mismatch warnings during CI builds.
- **Alternatives Considered**:
  - Leaving `hasDocs: true` without tag changes: Triggers loader warnings and broken navigation in the portal.
  - Retagging Git history: Violates repository immutability and disrupts downstream clones.

### Decision 2: 1:1 Kebab-Case Filename Slugs
- **Choice**: Standardize on 8 lowercase kebab-case filenames present in both `docs/es/` and `docs/en/`:
  - `index.md`
  - `configuration.md`
  - `gui-menus.md`
  - `commands-and-permissions.md`
  - `custom-recipes.md`
  - `custom-fuels.md`
  - `external-item-providers.md`
  - `database-and-storage.md`
- **Rationale**: Astro Starlight maps filesystem paths directly to clean URLs (`/[lang]/docs/playerfurnaces/[slug]/`). Symmetrical filenames allow seamless language switching in the portal UI.
- **Alternatives Considered**: Keeping PascalCase (`Commands-and-Permissions.md`) - would result in messy and inconsistent URL routes.

### Decision 3: Standardize on GitHub-style Callouts (`> [!NOTE]`)
- **Choice**: Use standard GitHub-style markdown alert blocks (`> [!NOTE]`, `> [!TIP]`, `> [!WARNING]`).
- **Rationale**: The DarkBladeDev portal pipeline incorporates `remark-gfm-asides` to automatically transform GitHub alerts into native Starlight `<Aside>` components.

## Risks / Trade-offs

- **[Risk] Broken relative links from external bookmarks or old repo clones** → *Mitigation*: Update the root `README.md` to reference the portal documentation structure and clear table of contents.
- **[Risk] Translation drift or discrepancies between English and Spanish docs** → *Mitigation*: Author both language sets in lockstep, verifying all YAML configuration examples, commands, and permission tables against the actual `v1.7.7` codebase.

## Migration Plan

1. Create `docs/metadata.yml` with valid metadata, dependencies, links, and versions.
2. Create `docs/en/` and write the 8 English documentation guides.
3. Create `docs/es/` and write the 8 Spanish documentation guides.
4. Verify 1:1 filename parity and frontmatter formatting.
5. Remove obsolete flat `.md` files in `docs/`.
6. Update root `README.md` links to align with the new structure.
