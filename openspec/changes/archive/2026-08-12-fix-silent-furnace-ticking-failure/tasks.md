# Tasks: Fix Silent Virtual Furnace Ticking Failure

## 1. Exception Isolation & Scheduler Protection
- [x] 1.1 Wrap per-furnace update calls in `FurnaceManager.startTickTask()` with `try-catch(Throwable t)` and detailed logging.
- [x] 1.2 Wrap GUI refresh iteration in `FurnaceManager.startTickTask()` with `try-catch(Throwable t)` and non-null inventory checks.

## 2. Recipe Search & Ticking Fixes
- [x] 2.1 Refactor `SmeltingManager.getSmeltingRecipe()` with exception handling and safe fallback iteration.
- [x] 2.2 Add `stepTicks <= 0` guard in `FurnaceEngine.updateFurnaceState()` to prevent zero-step infinite loops.

## 3. Verification & Testing
- [x] 3.1 Run `./gradlew test` to ensure all existing unit tests compile and pass cleanly.
- [x] 3.2 Add or run unit test cases verifying error isolation in furnace ticking.
