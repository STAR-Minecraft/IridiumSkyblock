package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.configs.inventories.MembersInventoryConfig;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GUI which displays all members of an Island and allows quick rank management.
 */
public class IslandMembersGUI extends IslandGUI {

    private int page = 1;
    private final Map<Integer, User> members;

    /**
     * The default constructor.
     *
     * @param island The Island this GUI belongs to
     */
    public IslandMembersGUI(@NotNull Island island, Inventory previousInventory) {
        super(IridiumSkyblock.getInstance().getInventories().membersGUI, previousInventory, island);
        this.members = new HashMap<>();
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        members.clear();

        MembersInventoryConfig membersGUI = IridiumSkyblock.getInstance().getInventories().membersGUI;
        preFillBackground(inventory, membersGUI.background);

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        inventory.setItem(inventory.getSize() + nextPage.slot, ItemStackUtils.makeItem(nextPage));

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        inventory.setItem(inventory.getSize() + previousPage.slot, ItemStackUtils.makeItem(previousPage));

        int pageSize = membersGUI.displaySlots != null ? membersGUI.displaySlots.length : 0;
        if(pageSize == 0)
            pageSize = membersGUI.size;

        int startIndex = pageSize * (page - 1);
        AtomicInteger slotIndexer = new AtomicInteger(0);

        for (User user : getIsland().getMembers()) {
            int itemSlotIndex = slotIndexer.getAndIncrement();
            if (itemSlotIndex < startIndex)
                continue;

            int itemSlot = membersGUI.findSlot(itemSlotIndex - startIndex);
            if (itemSlot == -1)
                break;

            inventory.setItem(itemSlot, ItemStackUtils.makeItem(membersGUI.listItem, new PlaceholderBuilder().applyPlayerPlaceholders(user).applyIslandPlaceholders(getIsland()).build()));
            members.put(itemSlot, user);
        }

        Item visitorsItem = membersGUI.visitorsItem;
        if (visitorsItem != null) {
            inventory.setItem(visitorsItem.slot, ItemStackUtils.makeItem(visitorsItem));
        }

        Item bansItem = membersGUI.bansItem;
        if (bansItem != null) {
            inventory.setItem(bansItem.slot, ItemStackUtils.makeItem(bansItem));
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
        if (members.containsKey(event.getSlot())) {
            User user = members.get(event.getSlot());

            switch (event.getClick()) {
                case LEFT:
                    IridiumSkyblock.getInstance().getCommands().demoteCommand.execute(event.getWhoClicked(), new String[]{"", user.getName()});
                    break;
                case RIGHT:
                    IridiumSkyblock.getInstance().getCommands().promoteCommand.execute(event.getWhoClicked(), new String[]{"", user.getName()});
                    break;
            }
            addContent(event.getInventory());
            return;
        }

        MembersInventoryConfig membersGUI = IridiumSkyblock.getInstance().getInventories().membersGUI;

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        if (event.getSlot() == membersGUI.size + previousPage.slot && page > 1) {
            page--;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        if (event.getSlot() == membersGUI.size + nextPage.slot) {
            page++;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Item visitorsItem = membersGUI.visitorsItem;
        if (visitorsItem != null && event.getSlot() == visitorsItem.slot) {
            event.getWhoClicked().openInventory(new IslandVisitorsGUI(1, getIsland(), player.getOpenInventory().getTopInventory()).getInventory());
            return;
        }

        Item bansItem = membersGUI.bansItem;
        if (bansItem != null && event.getSlot() == bansItem.slot) {
            event.getWhoClicked().openInventory(new IslandBansGUI(1, getIsland(), player.getOpenInventory().getTopInventory()).getInventory());
        }
    }

}
