package dev.darkblade.playerfurnaces.gui;

import dev.darkblade.playerfurnaces.PlayerFurnacesPlugin;
import dev.darkblade.playerfurnaces.engine.FurnaceEngine;
import dev.darkblade.playerfurnaces.model.MenuLayout;
import dev.darkblade.playerfurnaces.model.VirtualFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GuiListener implements Listener {

    private final PlayerFurnacesPlugin plugin;

    public GuiListener(PlayerFurnacesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();

        if (inv.getHolder() instanceof FurnaceHubGui hubGui) {
            event.setCancelled(true);
            int rawSlot = event.getRawSlot();

            if (rawSlot >= 0 && rawSlot < hubGui.getInventory().getSize()) {
                MenuLayout layout = hubGui.getLayout();
                if (layout != null) {
                    Integer furnaceId = null;
                    for (Map.Entry<Integer, Integer> entry : layout.getDynamicSlotMap().entrySet()) {
                        if (entry.getValue() == rawSlot) {
                            furnaceId = entry.getKey();
                            break;
                        }
                    }

                    if (furnaceId != null) {
                        Player target = hubGui.getTargetOwner();

                        if (!plugin.getFurnaceManager().hasPermissionForFurnace(target, furnaceId)) {
                            plugin.getMessageManager().sendMessage(player, "no-furnace-permission", "{id}", String.valueOf(furnaceId));
                            return;
                        }

                        VirtualFurnace furnace = plugin.getFurnaceManager().getOrCreateFurnace(target.getUniqueId(), furnaceId);
                        FurnaceEngine.updateFurnaceState(furnace);
                        FurnaceViewGui viewGui = new FurnaceViewGui(plugin, player, furnace);
                        player.openInventory(viewGui.getInventory());
                    }
                }
            }
            return;
        }

        if (inv.getHolder() instanceof FurnaceViewGui viewGui) {
            int rawSlot = event.getRawSlot();
            VirtualFurnace furnace = viewGui.getFurnace();

            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }

            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                return;
            }

            if (rawSlot >= 0 && rawSlot < viewGui.getInventory().getSize()) {
                if (event.getClick() == ClickType.NUMBER_KEY) {
                    event.setCancelled(true);
                    return;
                }
                if (rawSlot == viewGui.getCollectSlot()) {
                    event.setCancelled(true);
                    collectOutput(player, furnace);
                    viewGui.refresh();
                    return;
                }

                if (rawSlot == viewGui.getBackSlot()) {
                    event.setCancelled(true);
                    syncFurnaceFromInventory(inv, furnace, viewGui);
                    FurnaceHubGui hubGui = new FurnaceHubGui(plugin, player, player);
                    player.openInventory(hubGui.getInventory());
                    return;
                }

                if (rawSlot != viewGui.getInputSlot() && rawSlot != viewGui.getFuelSlot() && rawSlot != viewGui.getOutputSlot()) {
                    event.setCancelled(true);
                    return;
                }

                if (rawSlot == viewGui.getOutputSlot()) {
                    ItemStack cursor = event.getCursor();
                    if (cursor != null && !cursor.getType().isAir()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                syncFurnaceFromInventory(inv, furnace, viewGui);
                viewGui.refresh();
            });
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof FurnaceHubGui) {
            event.setCancelled(true);
            return;
        }

        if (event.getInventory().getHolder() instanceof FurnaceViewGui viewGui) {
            for (int slot : event.getRawSlots()) {
                if (slot < viewGui.getInventory().getSize() && slot != viewGui.getInputSlot() && slot != viewGui.getFuelSlot()) {
                    event.setCancelled(true);
                    return;
                }
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                syncFurnaceFromInventory(event.getInventory(), viewGui.getFurnace(), viewGui);
                viewGui.refresh();
            });
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof FurnaceViewGui viewGui) {
            syncFurnaceFromInventory(event.getInventory(), viewGui.getFurnace(), viewGui);
            plugin.getDatabaseManager().saveFurnace(viewGui.getFurnace());
        }
    }

    private void syncFurnaceFromInventory(Inventory inv, VirtualFurnace furnace, FurnaceViewGui viewGui) {
        if (viewGui.getInputSlot() != -1) furnace.setInputItem(inv.getItem(viewGui.getInputSlot()));
        if (viewGui.getFuelSlot() != -1) furnace.setFuelItem(inv.getItem(viewGui.getFuelSlot()));
        if (viewGui.getOutputSlot() != -1) furnace.setOutputItem(inv.getItem(viewGui.getOutputSlot()));
        FurnaceEngine.updateFurnaceState(furnace);
    }

    private void collectOutput(Player player, VirtualFurnace furnace) {
        ItemStack output = furnace.getOutputItem();
        if (output != null && output.getAmount() > 0) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(output);
            if (leftover.isEmpty()) {
                furnace.setOutputItem(null);
                plugin.getMessageManager().sendMessage(player, "collection.success");
            } else {
                furnace.setOutputItem(leftover.get(0));
                plugin.getMessageManager().sendMessage(player, "collection.partial");
            }
        }
    }
}
