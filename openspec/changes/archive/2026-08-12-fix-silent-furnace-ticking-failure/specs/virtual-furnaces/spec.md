# virtual-furnaces Specification Delta

## MODIFIED Requirements

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
