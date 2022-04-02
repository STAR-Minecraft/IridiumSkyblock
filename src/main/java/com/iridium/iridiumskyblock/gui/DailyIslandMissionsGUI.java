package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Mission;
import com.iridium.iridiumskyblock.database.Island;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DailyIslandMissionsGUI extends IslandGUI {

    /**
     * The default constructor.
     *
     * @param island The Island this GUI belongs to
     */
    public DailyIslandMissionsGUI(@NotNull Island island, Inventory previousInventory) {
        super(IridiumSkyblock.getInstance().getInventories().dailyMissionGUI, previousInventory, island);
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().missionsGUI.background);

        AtomicInteger indexer = new AtomicInteger(0);
        for (Map.Entry<String, Mission> entry : IridiumSkyblock.getInstance().getIslandManager().getDailyIslandMissions(getIsland()).entrySet()) {
            List<Placeholder> placeholders = IntStream.range(0, entry.getValue().getMissions().size())
                    .boxed()
                    .map(integer -> IridiumSkyblock.getInstance().getIslandManager().getIslandMission(getIsland(), entry.getValue(), entry.getKey(), integer))
                    .map(islandMission -> new Placeholder("progress_"+(islandMission.getMissionIndex()+1), String.valueOf(islandMission.getProgress())))
                    .collect(Collectors.toList());

            if (IridiumSkyblock.getInstance().getMissions().dailySlots.size() >= indexer.get()) {
                Integer slot = IridiumSkyblock.getInstance().getMissions().dailySlots.get(indexer.getAndIncrement());
                inventory.setItem(slot, ItemStackUtils.makeItem(entry.getValue().getItem(), placeholders));
            }
        }

        if (IridiumSkyblock.getInstance().getConfiguration().backButtons && getPreviousInventory() != null) {
            inventory.setItem(inventory.getSize() + IridiumSkyblock.getInstance().getInventories().backButton.slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().backButton));
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
        // Do nothing here
    }

}
