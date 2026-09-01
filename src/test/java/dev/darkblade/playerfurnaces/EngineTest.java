package dev.darkblade.playerfurnaces;

import dev.darkblade.playerfurnaces.model.FurnaceStatus;
import dev.darkblade.playerfurnaces.model.VirtualFurnace;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    @Test
    public void testVirtualFurnaceModelAndStatus() {
        UUID owner = UUID.randomUUID();
        VirtualFurnace furnace = new VirtualFurnace(owner, 1);

        assertEquals(owner, furnace.getOwnerUuid());
        assertEquals(1, furnace.getFurnaceId());
        assertEquals(FurnaceStatus.IDLE, furnace.getStatus());

        furnace.setBurnTime(200);
        furnace.setCookTime(0);
        assertEquals(FurnaceStatus.IDLE, furnace.getStatus());
    }

    @Test
    public void testTimestampDeltaTracking() {
        UUID owner = UUID.randomUUID();
        VirtualFurnace furnace = new VirtualFurnace(owner, 2);

        long initialTime = furnace.getLastUpdatedTimestamp();
        assertTrue(initialTime > 0);

        furnace.setLastUpdatedTimestamp(initialTime - 10000);
        assertEquals(initialTime - 10000, furnace.getLastUpdatedTimestamp());
    }

    @Test
    public void testZeroStepTicksGuard() {
        UUID owner = UUID.randomUUID();
        VirtualFurnace furnace = new VirtualFurnace(owner, 3);
        furnace.setCookTime(200);
        furnace.setTotalCookTime(200);
        furnace.setBurnTime(0);

        long now = System.currentTimeMillis();
        furnace.setLastUpdatedTimestamp(now - 1000);

        assertDoesNotThrow(() -> dev.darkblade.playerfurnaces.engine.FurnaceEngine.updateFurnaceState(furnace, null, null, null));
    }

    @Test
    public void testFutureTimestampGuard() {
        UUID owner = UUID.randomUUID();
        VirtualFurnace furnace = new VirtualFurnace(owner, 4);
        long futureTime = System.currentTimeMillis() + 60000;
        furnace.setLastUpdatedTimestamp(futureTime);

        dev.darkblade.playerfurnaces.engine.FurnaceEngine.updateFurnaceState(furnace, null, null, null);

        assertTrue(furnace.getLastUpdatedTimestamp() <= System.currentTimeMillis());
    }
}
