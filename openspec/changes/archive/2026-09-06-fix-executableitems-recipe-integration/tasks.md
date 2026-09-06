## 1. Provider Lifecycle & Declarations

- [x] 1.1 Add `softdepend: [ExecutableItems, SCore, Craftorithm]` to `src/main/resources/plugin.yml`.
- [x] 1.2 Register `executableitem` singular alias in `PlayerFurnacesPlugin.java`.

## 2. ExecutableItems Item Matching and Building Overhaul

- [x] 2.1 Implement lazy self-healing initialization in `ExecutableItemsItemProvider.java`.
- [x] 2.2 Update `ExecutableItemsItemProvider.isSimilar()` to inspect `ItemStack` instances using `manager.getExecutableItem(ItemStack)` and extract canonical IDs via reflection.
- [x] 2.3 Update `ExecutableItemsItemProvider.getItem()` to dynamically detect and invoke `buildItem` supporting 3-arg, 2-arg, and 1-arg signatures on `ExecutableItemInterface`.

## 3. Output Slot Merging & Smelting Engine Support

- [x] 3.1 Update `FurnaceEngine.java` output item validation to allow custom namespaced items to stack using `itemResolverRegistry.matches(...)` when standard `isSimilar()` fails due to dynamic runtime UUIDs.

## 4. Verification & Testing

- [x] 4.1 Create unit tests for multi-signature reflection handling and item resolution fallbacks.
- [x] 4.2 Run existing test suite (`./gradlew test`) to verify regressions are prevented.
- [x] 4.3 Build shaded plugin JAR (`./gradlew shadowJar`) and confirm build succeeds cleanly.
