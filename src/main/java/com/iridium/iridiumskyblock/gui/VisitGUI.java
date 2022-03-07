package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * GUI which shows a list of all Islands a user can visit.
 */
public class VisitGUI extends GUI {

    private final int page;
    private final User viewer;

    /**
     * The default constructor.
     *
     * @param page   The current page of this GUI
     * @param viewer The viewer of this GUI
     */
    public VisitGUI(int page, User viewer) {
        super(IridiumSkyblock.getInstance().getInventories().visitGUI);
        this.page = page;
        this.viewer = viewer;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().visitGUI.background);

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        inventory.setItem(inventory.getSize() + nextPage.slot, ItemStackUtils.makeItem(nextPage));

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        inventory.setItem(inventory.getSize() + previousPage.slot, ItemStackUtils.makeItem(previousPage));

        int elementsPerPage = inventory.getSize() - 9;
        List<Island> islands = IridiumSkyblock.getInstance().getDatabaseManager().getIslandTableManager().getEntries().stream()
                .filter(island -> viewer.isBypassing() || island.isVisitable())
                .skip((long) (page - 1) * elementsPerPage)
                .limit(elementsPerPage)
                .collect(Collectors.toList());

        AtomicInteger slot = new AtomicInteger(0);
        for (Island island : islands) {
            inventory.setItem(slot.getAndIncrement(), ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().visitGUI.item, new PlaceholderBuilder().applyIslandPlaceholders(island).build()));
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
        List<Island> islands = IridiumSkyblock.getInstance().getDatabaseManager().getIslandTableManager().getEntries().stream()
                .filter(island -> viewer.isBypassing() || island.isVisitable())
                .collect(Collectors.toList());

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        if (event.getSlot() == getInventory().getSize() + previousPage.slot && page > 1) {
            event.getWhoClicked().openInventory(new VisitGUI(page - 1, viewer).getInventory());
            return;
        }

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        if (event.getSlot() == getInventory().getSize() + nextPage.slot && (event.getInventory().getSize() - 9) * page < islands.size()) {
            event.getWhoClicked().openInventory(new VisitGUI(page + 1, viewer).getInventory());
            return;
        }

        if (event.getSlot() + 1 <= islands.size()) {
            int index = ((event.getInventory().getSize() - 9) * (page - 1)) + event.getSlot();
            if (islands.size() > index) {
                Island island = islands.get(index);
                IridiumSkyblock.getInstance().getCommands().visitCommand.execute(event.getWhoClicked(), new String[]{"", island.getOwner().getName()});
                event.getWhoClicked().closeInventory();
            }
        }
    }

}
