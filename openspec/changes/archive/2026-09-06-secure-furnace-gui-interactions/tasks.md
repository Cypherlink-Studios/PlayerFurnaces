## 1. GUI Security Guard Clauses

- [x] 1.1 In `GuiListener.java`, replace the existing shift-click block in `FurnaceViewGui` with an immediate cancellation guard clause (`event.setCancelled(true); return;`).
- [x] 1.2 In `GuiListener.java`, add a double-click guard clause (`event.getClick() == ClickType.DOUBLE_CLICK`) that cancels the event immediately to prevent auto-collecting GUI items onto the cursor.
- [x] 1.3 In `GuiListener.java`, add a hotbar number key swap guard clause (`event.getClick() == ClickType.NUMBER_KEY`) for all top inventory GUI slots (`rawSlot < viewGui.getInventory().getSize()`) to prevent item swaps into output, input, fuel, or decorative slots.

## 2. Output Slot Invariant Protection

- [x] 2.1 Verify and ensure the output slot handler in `GuiListener.java` strictly permits only cursor pickup with an empty cursor while rejecting any item placement or swap attempts.

## 3. Verification & Testing

- [x] 3.1 Create unit tests in `src/test/java/dev/darkblade/playerfurnaces/GuiSecurityTest.java` verifying that shift-clicks, double-clicks, and number-key swaps on GUI slots are cancelled while legitimate clicks function.
- [x] 3.2 Run the full test suite (`./gradlew test`) and confirm that all unit tests pass without regressions.
- [x] 3.3 Build the shaded plugin JAR (`./gradlew shadowJar`) and confirm build succeeds cleanly.
