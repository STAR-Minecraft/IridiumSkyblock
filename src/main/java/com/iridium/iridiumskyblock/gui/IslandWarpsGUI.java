package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandWarp;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IslandWarpsGUI extends IslandGUI {

    /**
     * The default constructor.
     *
     * @param island The Island this GUI belongs to
     */
    public IslandWarpsGUI(@NotNull Island island, Inventory previousInventory) {
        super(IridiumSkyblock.getInstance().getInventories().warpsGUI, previousInventory, island);
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().warpsGUI.background);

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<IslandWarp> islandWarps = IridiumSkyblock.getInstance().getDatabaseManager().getIslandWarpTableManager().getEntries(getIsland());
        Collections.reverse(islandWarps);

        for (IslandWarp islandWarp : islandWarps) {
            int slot = IridiumSkyblock.getInstance().getConfiguration().islandWarpSlots.get(atomicInteger.getAndIncrement());

            List<Placeholder> placeholderList = new PlaceholderBuilder()
                    .add("island_name", getIsland().getName())
                    .add("warp_name", islandWarp.getName())
                    .add("description", islandWarp.getDescription() != null ? islandWarp.getDescription() : "")
                    .build();

            ItemStack itemStack = ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().warpsGUI.item, placeholderList);
            Material material = islandWarp.getIcon().parseMaterial();
            if (material != null) itemStack.setType(material);
            inventory.setItem(slot, itemStack);
        }

        if (IridiumSkyblock.getInstance().getConfiguration().backButtons && getPreviousInventory() != null) {
            Item backButton = IridiumSkyblock.getInstance().getInventories().backButton;
            inventory.setItem(inventory.getSize() + backButton.slot, ItemStackUtils.makeItem(backButton));
        }
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        List<IslandWarp> islandWarps = IridiumSkyblock.getInstance().getDatabaseManager().getIslandWarpTableManager().getEntries(getIsland());
        Collections.reverse(islandWarps);
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (IslandWarp islandWarp : islandWarps) {
            if (IridiumSkyblock.getInstance().getConfiguration().islandWarpSlots.get(atomicInteger.getAndIncrement()) == event.getSlot()) {
                switch (event.getClick()) {
                    case LEFT:
                        IridiumSkyblock.getInstance().getCommands().warpsCommand.execute(event.getWhoClicked(), new String[]{"", islandWarp.getName()});
                        break;
                    case RIGHT:
                        IridiumSkyblock.getInstance().getCommands().deleteWarpCommand.execute(event.getWhoClicked(), new String[]{"", islandWarp.getName()});
                        break;
                }
                addContent(event.getInventory());
                return;
            }
        }
    }

}
