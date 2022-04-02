package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (event.getClickedInventory() == null || holder == null)
            return;

        //Old Inventories
        if (holder instanceof GUI) {
            event.setCancelled(true);
            if (event.getClickedInventory() == event.getInventory()) {
                GUI gui = (GUI) holder;
                if (IridiumSkyblock.getInstance().getConfiguration().backButtons && gui.getPreviousInventory() != null && event.getSlot() == (event.getInventory().getSize() + IridiumSkyblock.getInstance().getInventories().backButton.slot)) {
                    event.getWhoClicked().openInventory(gui.getPreviousInventory());
                } else {
                    gui.onInventoryClick(event);
                }
            }
            return;
        }

        // New Inventories with IridiumCore
        if (holder instanceof com.iridium.iridiumcore.gui.GUI) {
            event.setCancelled(true);
            if (event.getClickedInventory() == event.getInventory()) {
                ((com.iridium.iridiumcore.gui.GUI) holder).onInventoryClick(event);
            }
        }
    }
}
