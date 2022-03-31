package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI which shows the Islands with the highest Island value.
 *
 * @see Island#getValue()
 */
public class IslandTopGUI extends GUI {

    private final Map<Integer, Island> islandSlots = new HashMap<>();

    /**
     * The default constructor.
     */
    public IslandTopGUI(Inventory previousInventory) {
        super(IridiumSkyblock.getInstance().getInventories().islandTopGUI, previousInventory);
    }

    @Override
    public void addContent(Inventory inventory) {
        List<Island> islands = IridiumSkyblock.getInstance().getIslandManager().getIslands(IslandManager.SortType.VALUE);

        islandSlots.clear();
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().islandTopGUI.background);

        for (int rank : IridiumSkyblock.getInstance().getConfiguration().islandTopSlots.keySet()) {
            int slot = IridiumSkyblock.getInstance().getConfiguration().islandTopSlots.get(rank);
            if (islands.size() >= rank) {
                Island island = islands.get(rank - 1);
                islandSlots.put(slot, island);

                List<Placeholder> placeholderList = new PlaceholderBuilder()
                        .applyIslandPlaceholders(island)
                        .build();

                inventory.setItem(slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().islandTopGUI.item, placeholderList));
            } else {
                inventory.setItem(slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().islandTopGUI.filler));
            }
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
        if (!islandSlots.containsKey(event.getSlot())) return;

        Island island = islandSlots.get(event.getSlot());
        IridiumSkyblock.getInstance().getCommands().visitCommand.execute(event.getWhoClicked(), new String[]{"", island.getOwner().getName()});
    }

}
