package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.inventories.NoItemGUI;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a clickable GUI.
 * Base for all other classes in this package.
 */
@NoArgsConstructor
@Getter
public abstract class GUI implements InventoryHolder {

    private NoItemGUI noItemGUI;
    private Inventory previousInventory;

    public GUI(Inventory previousInventory){
        if (previousInventory != null && previousInventory.getHolder() != null && previousInventory.getHolder() instanceof GUI) {
            this.previousInventory = previousInventory;
        }
    }

    /**
     * The default constructor.
     *
     * @param noItemGUI         The NoItemGUI of this GUI
     * @param previousInventory The previous Inventory
     */
    public GUI(@NotNull NoItemGUI noItemGUI, Inventory previousInventory) {
        this.noItemGUI = noItemGUI;
        if (previousInventory != null && previousInventory.getHolder() != null && previousInventory.getHolder() instanceof GUI) {
            this.previousInventory = previousInventory;
        }
    }

    /**
     * The default constructor.
     *
     * @param noItemGUI The NoItemGUI of this GUI
     */
    public GUI(@NotNull NoItemGUI noItemGUI) {
        this.noItemGUI = noItemGUI;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));

        Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.getInstance(), () -> addContent(inventory));

        return inventory;
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    public abstract void onInventoryClick(InventoryClickEvent event);

    /**
     * Called when updating the Inventories contents
     */
    public abstract void addContent(Inventory inventory);

    protected void preFillBackground(Inventory inventory, Background background) {
        InventoryUtils.fillInventory(inventory, background);

        Item item = IridiumSkyblock.getInstance().getInventories().footerLineItem;
        if (hasFooterLine() && item != null) {
            int size = inventory.getSize();
            int startIndex = size - 9;

            ItemStack bukkitItem = ItemStackUtils.makeItem(item);
            for (int slot = startIndex; slot < size; slot++) {
                inventory.setItem(slot, bukkitItem);
            }
        }
    }

    protected boolean hasFooterLine() {
        return noItemGUI.hasFooterLine;
    }

}
