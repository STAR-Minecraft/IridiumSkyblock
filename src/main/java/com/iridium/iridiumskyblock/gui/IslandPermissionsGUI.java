package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.IslandRank;
import com.iridium.iridiumskyblock.Permission;
import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * GUI which allows users to alter the Island's permissions.
 */
public class IslandPermissionsGUI extends IslandGUI {

    private int page;
    private final IslandRank islandRank;

    /**
     * The default constructor.
     *
     * @param island     The Island this GUI belongs to
     * @param islandRank The rank which is being configured
     */
    public IslandPermissionsGUI(@NotNull Island island, @NotNull IslandRank islandRank, Inventory previousInventory, int page) {
        super(IridiumSkyblock.getInstance().getInventories().islandPermissionsGUI, previousInventory, island);
        this.islandRank = islandRank;
        this.page = page;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().islandPermissionsGUI.background);

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        inventory.setItem(inventory.getSize() + nextPage.slot, ItemStackUtils.makeItem(nextPage));

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        inventory.setItem(inventory.getSize() + previousPage.slot, ItemStackUtils.makeItem(previousPage));

        for (Map.Entry<String, Permission> permission : IridiumSkyblock.getInstance().getPermissionList().entrySet()) {
            if (permission.getValue().getPage() != page) continue;
            boolean allowed = IridiumSkyblock.getInstance().getIslandManager().getIslandPermission(getIsland(), islandRank, permission.getValue(), permission.getKey());
            inventory.setItem(permission.getValue().getItem().slot, ItemStackUtils.makeItem(permission.getValue().getItem(), Collections.singletonList(new Placeholder("permission", allowed ? IridiumSkyblock.getInstance().getPermissions().allowed : IridiumSkyblock.getInstance().getPermissions().denied))));
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
        for (Map.Entry<String, Permission> permission : IridiumSkyblock.getInstance().getPermissionList().entrySet()) {
            if (permission.getValue().getItem().slot != event.getSlot()) continue;
            if (permission.getValue().getPage() != page) continue;

            User user = IridiumSkyblock.getInstance().getUserManager().getUser((Player) event.getWhoClicked());
            if (user.getIslandRank().getLevel() <= islandRank.getLevel() || !IridiumSkyblock.getInstance().getIslandManager().getIslandPermission(getIsland(), user, PermissionType.CHANGE_PERMISSIONS)) {
                event.getWhoClicked().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cannotChangePermissions.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                boolean allowed = IridiumSkyblock.getInstance().getIslandManager().getIslandPermission(getIsland(), islandRank, permission.getValue(), permission.getKey());
                IridiumSkyblock.getInstance().getIslandManager().setIslandPermission(getIsland(), islandRank, permission.getKey(), !allowed);
                event.getWhoClicked().openInventory(getInventory());
            }
            return;
        }

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        if (event.getSlot() == getNoItemGUI().size + previousPage.slot && hasPage(page - 1)) {
            page--;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        if (event.getSlot() == getNoItemGUI().size + nextPage.slot && hasPage(page + 1)) {
            page++;
            event.getWhoClicked().openInventory(getInventory());
        }
    }

    private boolean hasPage(int page) {
        return IridiumSkyblock.getInstance().getPermissionList().entrySet().stream()
                .anyMatch(entry -> entry.getValue().getPage() == page);
    }

}
