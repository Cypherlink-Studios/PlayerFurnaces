# virtual-furnaces Specification

## Purpose
TBD - created by archiving change virtual-player-furnaces. Update Purpose after archive.
## Requirements
### Requirement: Virtual Furnace Selector GUI
The system SHALL display an interactive GUI when a player executes `/furnace` or `/horno` showing all virtual furnaces available to that player, utilizing the dynamic layout engine to render the visual structure. Access permissions for virtual furnaces #1 and #2 in `plugin.yml` SHALL default to `op` instead of `true`.

#### Scenario: Opening furnace selector
- **WHEN** player executes `/furnace`
- **THEN** system opens a GUI listing furnaces with visual status indicators (Smelting, Idle, Out of Fuel, Locked) mapped to the dynamic layout slots based on player permissions `playerfurnaces.furnace.<number>`

#### Scenario: Opening locked furnace
- **WHEN** player clicks on a locked furnace icon in the dynamically rendered GUI
- **THEN** system prevents access and sends a message explaining that permission `playerfurnaces.furnace.<number>` is required

### Requirement: Virtual Furnace Smelting & Fuel Mechanics
The system SHALL provide a virtual furnace interface with Input, Fuel, and Output slots that smelts items following standard vanilla recipe times and fuel burn durations, while isolating furnace processing ticks from uncaught exceptions and enforcing loop progress guards to prevent background thread death or infinite execution loops.

#### Scenario: Smelting an item
- **WHEN** player places valid input items (e.g., Raw Iron) and valid fuel (e.g., Coal) into a furnace
- **THEN** furnace consumes fuel, displays progress, and yields smelted output (e.g., Iron Ingot) into the output slot

#### Scenario: Offline smelting calculation
- **WHEN** a player reopens a furnace or accesses it after being offline
- **THEN** system calculates elapsed time since last update and processes all pending smelting cycles accurately

#### Scenario: Exception during furnace tick
- **WHEN** an unexpected exception occurs during furnace state update or GUI refresh for a specific furnace
- **THEN** the system logs the exception with context and continues executing background ticks for all other furnaces without cancelling the background scheduler task.

#### Scenario: Infinite loop prevention
- **WHEN** calculating elapsed smelting steps in `FurnaceEngine`
- **THEN** the step ticks calculated MUST be strictly positive (> 0) or exit the update loop immediately to prevent zero-step infinite while-loops.

#### Scenario: Safe recipe resolution
- **WHEN** looking up standard smelting recipes for item stacks with complex metadata or during concurrent recipe registration
- **THEN** recipe resolution catches iteration exceptions cleanly and returns `null` or cached recipes without crashing the main tick thread.

### Requirement: Anti-Dupe Item Data Persistence
The system SHALL store furnace state, input items, fuel items, and output items in an H2 or SQLite database using binary item serialization.

#### Scenario: Server restart item persistence
- **WHEN** server restarts or reloads while a furnace contains custom items with NBT/PDC data
- **THEN** system restores furnace contents with exact NBT metadata intact without duplication or item loss

### Requirement: Administrator Inspection & Management
The system SHALL provide administrative commands under `/pfadmin` to inspect, manage, force-open, and reload virtual furnaces. Permission verification for force-opening a furnace SHALL check standard furnace permissions `playerfurnaces.furnace.<id>` unless explicitly bypassed with `--bypass-perms`.

#### Scenario: Admin viewing player furnace
- **WHEN** administrator executes `/pfadmin view <player> <id>`
- **THEN** system opens specified player's virtual furnace GUI for inspection or modification regardless of whether the player is online or offline

#### Scenario: Admin forcing player to open default furnace
- **WHEN** administrator executes `/pfadmin force-open <player> 1` for an online player with default permissions
- **THEN** system verifies permission via `playerfurnaces.furnace.1` and forces the target player to open furnace #1 without error

### Requirement: Secure Virtual Furnace GUI Interaction Enforcement
The system SHALL intercept and cancel unsafe inventory click interactions in the virtual furnace view interface, explicitly rejecting all shift-clicks, hotbar number key swaps on top GUI slots, and double-clicks, to prevent item duplication, theft of decorative or functional GUI items, and illegal item insertion into the output slot.

#### Scenario: Cancelling shift-clicks in furnace view GUI
- **WHEN** a player performs a shift-click anywhere while viewing a virtual furnace GUI
- **THEN** the system cancels the event immediately and prevents items from moving between inventories or slots.

#### Scenario: Preventing hotbar swap into output slot
- **WHEN** a player presses a hotbar number key (1-9) while hovering over the output slot in the virtual furnace GUI
- **THEN** the system cancels the event and prevents swapping or placing any item into the output slot.

#### Scenario: Preventing hotbar swap into GUI slots
- **WHEN** a player presses a hotbar number key (1-9) while hovering over any slot in the furnace view top inventory
- **THEN** the system cancels the event and preserves the inventory state.

#### Scenario: Preventing double-click item collection from GUI
- **WHEN** a player performs a double-click anywhere while viewing a virtual furnace GUI
- **THEN** the system cancels the event and prevents Bukkit from aggregating matching items onto the cursor.

#### Scenario: Preserving legitimate output collection
- **WHEN** a player clicks the dedicated COLLECT button in the furnace view GUI with items present in the output slot
- **THEN** the system transfers the smelted output items into the player's personal inventory and updates the furnace state.

#### Scenario: Preserving regular item placement and withdrawal
- **WHEN** a player uses standard cursor clicks to place fuel into the fuel slot, input items into the input slot, or pick up smelted items from the output slot with an empty cursor
- **THEN** the system allows the interaction and synchronizes the virtual furnace state.


