package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.inventories.InventoryConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class InventoryConfigGUI extends GUI {

    private final InventoryConfig inventoryConfig;

    public InventoryConfigGUI(InventoryConfig inventoryConfig, Inventory previousInventory) {
        super(inventoryConfig, previousInventory);
        this.inventoryConfig = inventoryConfig;
    }

    @Override
    public void addContent(Inventory inventory) {
        Player viewingPlayer = getViewingPlayer(inventory);
        if(viewingPlayer == null)
            return;

        inventory.clear();

        preFillBackground(inventory, inventoryConfig.background);

        List<Placeholder> placeholders = Collections.singletonList(new Placeholder("player_name", viewingPlayer.getName()));
        inventoryConfig.items.values().forEach(item -> inventory.setItem(item.slot, ItemStackUtils.makeItem(item, placeholders)));

        if (IridiumSkyblock.getInstance().getConfiguration().backButtons && getPreviousInventory() != null) {
            Item backButton = IridiumSkyblock.getInstance().getInventories().backButton;
            inventory.setItem(inventory.getSize() + backButton.slot, ItemStackUtils.makeItem(backButton));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        for (String command : inventoryConfig.items.keySet()) {
            if (inventoryConfig.items.get(command).slot == event.getSlot()) {
                if(command.equalsIgnoreCase("is about"))
                    event.getWhoClicked().closeInventory();

                Bukkit.getServer().dispatchCommand(event.getWhoClicked(), command);
            }
        }
    }

    private @Nullable Player getViewingPlayer(@NotNull Inventory inventory) {
        List<HumanEntity> viewers = inventory.getViewers();
        if (viewers.isEmpty())
            return null;

        HumanEntity entity = viewers.get(0);
        if (entity instanceof Player)
            return (Player) entity;

        return null;
    }

}
