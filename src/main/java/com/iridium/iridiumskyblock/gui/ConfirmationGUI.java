package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.managers.CooldownProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * GUI which executes code upon confirmation.
 */
public class ConfirmationGUI extends GUI {

    private final @NotNull Runnable runnable;
    private final @NotNull CooldownProvider<CommandSender> cooldownProvider;

    /**
     * The default constructor.
     *
     * @param runnable         The code that should be run when the user confirms his action
     * @param cooldownProvider The provider for cooldowns that should be started on success
     */
    public  ConfirmationGUI(@NotNull Runnable runnable, @NotNull CooldownProvider<CommandSender> cooldownProvider) {
        super(IridiumSkyblock.getInstance().getInventories().confirmationGUI);
        this.runnable = runnable;
        this.cooldownProvider = cooldownProvider;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, getNoItemGUI().background);

        Item noItem = IridiumSkyblock.getInstance().getInventories().confirmationGUI.no;
        inventory.setItem(noItem.slot, ItemStackUtils.makeItem(noItem));

        Item yesItem = IridiumSkyblock.getInstance().getInventories().confirmationGUI.yes;
        inventory.setItem(yesItem.slot, ItemStackUtils.makeItem(yesItem));
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == IridiumSkyblock.getInstance().getInventories().confirmationGUI.no.slot) {
            player.closeInventory();
        } else if (event.getSlot() == IridiumSkyblock.getInstance().getInventories().confirmationGUI.yes.slot) {
            runnable.run();
            player.closeInventory();
            cooldownProvider.applyCooldown(player);
        }
    }
}
