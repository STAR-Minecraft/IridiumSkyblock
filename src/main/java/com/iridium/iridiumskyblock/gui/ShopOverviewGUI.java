package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.configs.Messages;
import com.iridium.iridiumskyblock.configs.Shop;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.ShopBalance;
import com.iridium.iridiumskyblock.shop.ShopCategory;
import com.iridium.iridiumskyblock.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * GUI which shows all categories of the shop.
 */
public class ShopOverviewGUI extends GUI {

    public ShopOverviewGUI(Inventory previousInventory) {
        super(previousInventory);
    }

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumSkyblock.getInstance().getShop().overviewSize, StringUtils.color(IridiumSkyblock.getInstance().getShop().overviewTitle));

        Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.getInstance(), () -> addContent(inventory));

        return inventory;
    }

    /**
     * Called when updating the Inventories contents
     */
    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getShop().overviewBackground);

        for (ShopCategory category : IridiumSkyblock.getInstance().getShopManager().getCategories()) {
            inventory.setItem(category.item.slot, ItemStackUtils.makeItem(category.item));
        }

        Item resetButton = IridiumSkyblock.getInstance().getShop().resetButton;
        if (resetButton != null) {
            Player player = getViewingPlayer(inventory);
            double crystalsBalance = 0D;
            double vaultBalance = 0D;

            if (player != null) {
                Island island = IridiumSkyblockAPI.getInstance().getUser(player).getIsland().get();
                ShopBalance balance = island.getShopBalance();

                crystalsBalance = balance.getBalanceOf("crystals").orElse(0D);
                vaultBalance = balance.getBalanceOf("vault").orElse(0D);
            }

            List<Placeholder> placeholders = IridiumSkyblock.getInstance().getShop().shopBalanceConfig.getResetPricePlaceholders();
            placeholders.add(new Placeholder("shop_balance_crystals", formatBalance(crystalsBalance)));
            placeholders.add(new Placeholder("shop_balance_vault", formatBalance(vaultBalance)));
            inventory.setItem(resetButton.slot, ItemStackUtils.makeItem(resetButton, placeholders));
        }

        if (IridiumSkyblock.getInstance().getConfiguration().backButtons && getPreviousInventory() != null) {
            Item backButton = IridiumSkyblock.getInstance().getInventories().backButton;
            inventory.setItem(inventory.getSize() + backButton.slot, ItemStackUtils.makeItem(backButton));
        }
    }

    @Override
    protected boolean hasFooterLine() {
        return IridiumSkyblock.getInstance().getShop().addFooterLineInGUI;
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

    /**
     * Called when there is a click in this GUI. Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();

        Item resetButton = IridiumSkyblock.getInstance().getShop().resetButton;
        if (resetButton != null && resetButton.slot == event.getSlot() && whoClicked instanceof Player) {
            resetShopBalance((Player) whoClicked);
            return;
        }

        IridiumSkyblock.getInstance().getShopManager().getCategoryBySlot(event.getSlot()).ifPresent(shopCategory ->
                IridiumSkyblock.getInstance().getCommands().shopCommand.execute(whoClicked, new String[]{"", shopCategory.name})
        );
    }

    private void resetShopBalance(Player player) {
        Shop shopConfig = IridiumSkyblock.getInstance().getShop();
        Shop.ShopBalanceConfig shopBalanceConfig = shopConfig.shopBalanceConfig;
        List<Placeholder> resetPricePlaceholders = shopBalanceConfig.getResetPricePlaceholders();

        double vaultCost = shopBalanceConfig.getResetCost("vault");
        int crystalsCost = (int) shopBalanceConfig.getResetCost("crystals");

        Island island = IridiumSkyblockAPI.getInstance().getUser(player).getIsland().get();
        ShopBalance balance = island.getShopBalance();

        player.closeInventory();

        Messages messages = IridiumSkyblock.getInstance().getMessages();
        if(shopBalanceConfig.isDefaultBalance(island, balance)) {
            shopConfig.shopResetFailSound.play(player);
            player.sendMessage(StringUtils.color(messages.shopBalanceResetNotNeeded.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }

        if(!PlayerUtils.canPurchase(player, island, crystalsCost, vaultCost)) {
            shopConfig.shopResetFailSound.play(player);
            player.sendMessage(
                    StringUtils.processMultiplePlaceholders(
                            messages.shopBalanceResetFail.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix),
                            resetPricePlaceholders
                    )
            );
            return;
        }

        PlayerUtils.pay(player, island, crystalsCost, vaultCost);

        island.resetShopBalance();

        IridiumSkyblock.getInstance().getDatabaseManager().getIslandTableManager().save(island);

        shopConfig.shopResetSuccessSound.play(player);

        player.sendMessage(
                StringUtils.processMultiplePlaceholders(
                        messages.shopBalanceResetSuccess.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix),
                        resetPricePlaceholders
                )
        );
    }

    private String formatBalance(double value) {
        if (IridiumSkyblock.getInstance().getShop().abbreviateShopBalances) {
            return IridiumSkyblock.getInstance().getConfiguration().numberFormatter.format(value);
        } else {
            return String.valueOf(value);
        }
    }

}
