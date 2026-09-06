package dev.darkblade.playerfurnaces;

import dev.darkblade.playerfurnaces.gui.FurnaceViewGui;
import dev.darkblade.playerfurnaces.gui.GuiListener;
import dev.darkblade.playerfurnaces.model.VirtualFurnace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class GuiSecurityTest {

    private PlayerFurnacesPlugin plugin;
    private Server server;
    private BukkitScheduler scheduler;
    private GuiListener listener;
    private Player player;
    private FurnaceViewGui viewGui;
    private Inventory inv;
    private VirtualFurnace furnace;

    @BeforeEach
    public void setup() {
        plugin = mock(PlayerFurnacesPlugin.class);
        server = mock(Server.class);
        scheduler = mock(BukkitScheduler.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);

        listener = new GuiListener(plugin);
        player = mock(Player.class);
        viewGui = mock(FurnaceViewGui.class);
        inv = mock(Inventory.class);
        furnace = new VirtualFurnace(UUID.randomUUID(), 1);

        when(inv.getHolder()).thenReturn(viewGui);
        when(viewGui.getFurnace()).thenReturn(furnace);
        when(viewGui.getInventory()).thenReturn(inv);
        when(inv.getSize()).thenReturn(45);

        when(viewGui.getInputSlot()).thenReturn(10);
        when(viewGui.getFuelSlot()).thenReturn(19);
        when(viewGui.getOutputSlot()).thenReturn(16);
        when(viewGui.getCollectSlot()).thenReturn(25);
        when(viewGui.getBackSlot()).thenReturn(36);
    }

    private InventoryClickEvent createMockEvent(int rawSlot, ClickType clickType, boolean isShiftClick, ItemStack cursor) {
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getWhoClicked()).thenReturn(player);
        when(event.getInventory()).thenReturn(inv);
        when(event.getRawSlot()).thenReturn(rawSlot);
        when(event.getClick()).thenReturn(clickType);
        when(event.isShiftClick()).thenReturn(isShiftClick);
        when(event.getCursor()).thenReturn(cursor);
        return event;
    }

    @Test
    public void testShiftClickIsCancelledImmediately() {
        // Shift click on any slot (e.g. filler slot 0)
        InventoryClickEvent event = createMockEvent(0, ClickType.SHIFT_LEFT, true, null);
        listener.onClick(event);
        verify(event).setCancelled(true);

        // Shift click on output slot
        InventoryClickEvent eventOutput = createMockEvent(16, ClickType.SHIFT_RIGHT, true, null);
        listener.onClick(eventOutput);
        verify(eventOutput).setCancelled(true);

        // Shift click in player inventory (slot >= 45)
        InventoryClickEvent eventPlayerInv = createMockEvent(50, ClickType.SHIFT_LEFT, true, null);
        listener.onClick(eventPlayerInv);
        verify(eventPlayerInv).setCancelled(true);
    }

    @Test
    public void testDoubleClickIsCancelledImmediately() {
        InventoryClickEvent event = createMockEvent(10, ClickType.DOUBLE_CLICK, false, null);
        listener.onClick(event);
        verify(event).setCancelled(true);
    }

    @Test
    public void testNumberKeyOnTopGuiSlotsIsCancelled() {
        // Number key swap on output slot
        InventoryClickEvent eventOutput = createMockEvent(16, ClickType.NUMBER_KEY, false, null);
        listener.onClick(eventOutput);
        verify(eventOutput).setCancelled(true);

        // Number key swap on input slot
        InventoryClickEvent eventInput = createMockEvent(10, ClickType.NUMBER_KEY, false, null);
        listener.onClick(eventInput);
        verify(eventInput).setCancelled(true);

        // Number key swap on fuel slot
        InventoryClickEvent eventFuel = createMockEvent(19, ClickType.NUMBER_KEY, false, null);
        listener.onClick(eventFuel);
        verify(eventFuel).setCancelled(true);

        // Number key swap on decorative filler slot
        InventoryClickEvent eventFiller = createMockEvent(0, ClickType.NUMBER_KEY, false, null);
        listener.onClick(eventFiller);
        verify(eventFiller).setCancelled(true);
    }

    @Test
    public void testFillerSlotIsCancelled() {
        InventoryClickEvent event = createMockEvent(0, ClickType.LEFT, false, null);
        listener.onClick(event);
        verify(event).setCancelled(true);
    }

    @Test
    public void testOutputSlotPlacementWithCursorIsCancelled() {
        ItemStack cursorItem = new ItemStack(Material.COBBLESTONE, 1);
        InventoryClickEvent event = createMockEvent(16, ClickType.LEFT, false, cursorItem);
        listener.onClick(event);
        verify(event).setCancelled(true);
    }

    @Test
    public void testOutputSlotPickupWithEmptyCursorAllowed() {
        ItemStack emptyCursor = new ItemStack(Material.AIR);
        InventoryClickEvent event = createMockEvent(16, ClickType.LEFT, false, emptyCursor);
        listener.onClick(event);
        // Should NOT be cancelled
        verify(event, never()).setCancelled(true);
    }
}
