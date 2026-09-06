## MODIFIED Requirements

### Requirement: Namespace Based Item Identifier Resolution
The system SHALL resolve item references formatted as `namespace:item_id` by delegating to the registered provider for that namespace, supporting `craftorithm`, `executableitems` (with `executableitem` and `ei` aliases), and vanilla `minecraft` namespaces.

#### Scenario: Resolving Craftorithm items
- **WHEN** a recipe specifies an item ID with `crafthorim:ruby_ingot`
- **THEN** the system delegates to the `CraftorithmItemProvider` to construct or match the corresponding `ItemStack`.

#### Scenario: Resolving ExecutableItems items by ID or alias
- **WHEN** a recipe or fuel specifies an item ID with `executableitems:<id>`, `executableitem:<id>`, or `ei:<id>`
- **THEN** the system delegates to the `ExecutableItemsItemProvider` to construct or match the corresponding `ItemStack`.

#### Scenario: Resolving local or vanilla items
- **WHEN** a recipe specifies a vanilla material or local custom item definition without a third-party namespace
- **THEN** the system matches using Bukkit material properties, MiniMessage display names, and PDC tags.

### Requirement: Soft Dependency Auto-Registration for ExecutableItems
The system SHALL detect the presence of the ExecutableItems plugin during startup and automatically register `ExecutableItemsItemProvider` into `ItemResolverRegistry` under `executableitems`, `executableitem`, and `ei` namespaces, while gracefully re-attempting hook initialization on demand if ExecutableItems is enabled after PlayerFurnaces.

#### Scenario: ExecutableItems plugin enabled
- **WHEN** the ExecutableItems plugin is enabled on the server
- **THEN** `ExecutableItemsItemProvider` is registered and available for recipe item matching and resolution.

#### Scenario: ExecutableItems reloaded or late-enabled
- **WHEN** ExecutableItems is reloaded or enabled after initial startup
- **THEN** `ExecutableItemsItemProvider` dynamically verifies its hook status and reconnects to `ExecutableItemsAPI` without requiring a server reboot.

## ADDED Requirements

### Requirement: ExecutableItems Stack Inspection and Matching
The system SHALL identify whether an in-game `ItemStack` matches a specified ExecutableItem identifier by inspecting the stack through `ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(ItemStack)` rather than strict `ItemStack.isSimilar()` comparisons against newly minted instances.

#### Scenario: Matching an ExecutableItem input stack with runtime metadata
- **WHEN** a player places an ExecutableItem with unique UUID, timestamps, or usage counters into the furnace input or fuel slot
- **THEN** the system interrogates ExecutableItems to obtain the canonical item ID and matches it against the configured recipe ID regardless of instance-specific metadata differences.

#### Scenario: Constructing ExecutableItems outputs with variable parameter signatures
- **WHEN** a furnace recipe produces an ExecutableItem result
- **THEN** the system invokes `buildItem` dynamically accommodating 3-argument (`int amount, Optional<Integer> usage, Optional<Player> creator`), 2-argument, or 1-argument method signatures.

### Requirement: Custom Provider Output Merging and Stacking
The system SHALL allow multiple items produced by a custom recipe to stack in the furnace output slot if the existing item in the output slot matches the recipe result's custom provider identifier, even if instance-specific runtime UUIDs or NBT tags would prevent Bukkit's standard `ItemStack.isSimilar()` from returning true.

#### Scenario: Stacking consecutive ExecutableItems recipe outputs
- **WHEN** an ExecutableItem recipe produces consecutive items into an output slot that already contains an item matching that ExecutableItem ID
- **THEN** the system increases the stack amount up to the item's max stack size instead of halting the smelting process.
