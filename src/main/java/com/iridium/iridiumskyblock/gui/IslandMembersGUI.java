package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.configs.inventories.NoItemGUI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * GUI which displays all members of an Island and allows quick rank management.
 */
public class IslandMembersGUI extends PagedGUI<User> {

    private final Island island;

    public IslandMembersGUI(Island island, Inventory previousInventory) {
        super(1,
                IridiumSkyblock.getInstance().getInventories().membersGUI.size,
                IridiumSkyblock.getInstance().getInventories().membersGUI.background,
                IridiumSkyblock.getInstance().getInventories().previousPage,
                IridiumSkyblock.getInstance().getInventories().nextPage,
                previousInventory,
                IridiumSkyblock.getInstance().getInventories().backButton
        );
        this.island = island;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumSkyblock.getInstance().getInventories().membersGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<User> getPageObjects() {
        return IridiumSkyblock.getInstance().getIslandManager().getIslandMembers(island);
    }

    public void addContent(Inventory inventory) {
        // TODO fix that
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

    @Override
    public ItemStack getItemStack(User user) {
        return ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().membersGUI.item, new PlaceholderBuilder()
                .applyIslandPlaceholders(island)
                .applyPlayerPlaceholders(user)
                .build()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        User user = getItem(event.getSlot());
        if (user == null) return;
        switch (event.getClick()) {
            case LEFT:
                IridiumSkyblock.getInstance().getCommands().demoteCommand.execute(event.getWhoClicked(), new String[]{"", user.getName()});
                break;
            case RIGHT:
                IridiumSkyblock.getInstance().getCommands().promoteCommand.execute(event.getWhoClicked(), new String[]{"", user.getName()});
                break;
        }
        addContent(event.getInventory());
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