## ADDED Requirements

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
