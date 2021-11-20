package com.iridium.iridiumskyblock.shop;

import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.ShopPurchaseEvent;
import com.iridium.iridiumskyblock.api.ShopSellEvent;
import com.iridium.iridiumskyblock.configs.Shop;
import com.iridium.iridiumskyblock.configs.Shop.ShopCategoryConfig;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.ShopBalance;
import com.iridium.iridiumskyblock.shop.ShopItem.BuyCost;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the shop.
 */
public class ShopManager {

    private final List<ShopCategory> categories = new ArrayList<>();

    public void reloadCategories() {
        categories.clear();

        for (String categoryName : IridiumSkyblock.getInstance().getShop().items.keySet()) {
            ShopCategoryConfig shopCategoryConfig = IridiumSkyblock.getInstance().getShop().categories.get(categoryName);
            if (shopCategoryConfig == null) {
                IridiumSkyblock.getInstance().getLogger().warning("Shop category " + categoryName + " is not configured, skipping...");
                continue;
            }

            String displayName = shopCategoryConfig.displayName;
            if (displayName == null)
                displayName = categoryName;

            List<ShopItem> categoryItems = IridiumSkyblock.getInstance().getShop().items.get(categoryName);
            categories.add(
                    new ShopCategory(
                            categoryName,
                            displayName,
                            shopCategoryConfig.item,
                            categoryItems,
                            shopCategoryConfig.inventoryRows * 9
                    )
            );
        }
    }

    /**
     * Returns a list of all loaded categories.
     *
     * @return All loaded categories of the shop
     */
    public List<ShopCategory> getCategories() {
        return categories;
    }

    /**
     * Returns the category with the provided name, null if there is none.
     *
     * @param name The name of the category
     * @return The category with the name
     */
    public Optional<ShopCategory> getCategoryByName(String name) {
        return categories.stream()
                .filter(category -> name.equals(category.name))
                .findAny();
    }

    /**
     * Returns the category with the provided name containing colors, null if there is none.
     *
     * @param slot The slot of the category
     * @return The category with the name
     */
    public Optional<ShopCategory> getCategoryBySlot(int slot) {
        return categories.stream()
                .filter(category -> category.item.slot == slot)
                .findAny();
    }

    /**
     * Buys an item for the Player in the shop.
     * He might not have enough money to do so.
     *
     * @param player   The player which wants to buy the item
     * @param shopItem The item which is requested
     * @param amount   The amount of the item which is requested
     */
    public void buy(Player player, ShopItem shopItem, int amount) {
        BuyCost buyCost = shopItem.buyCost;
        double vaultCost = calculateCost(amount, shopItem.defaultAmount, buyCost.vault);
        int crystalCost = (int) calculateCost(amount, shopItem.defaultAmount, buyCost.crystals);
        final Optional<Island> island = IridiumSkyblockAPI.getInstance().getUser(player).getIsland();
        if (!island.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }

        Shop shopConfig = IridiumSkyblock.getInstance().getShop();

        ShopBalance balance = island.get().getShopBalance();
        if(!shopConfig.shopBalanceConfig.has(balance, crystalCost, vaultCost)) {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cannotAfford.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            shopConfig.failSound.play(player);
            return;
        }

        ShopPurchaseEvent shopPurchaseEvent = new ShopPurchaseEvent(player, shopItem, amount);
        Bukkit.getPluginManager().callEvent(shopPurchaseEvent);
        if (shopPurchaseEvent.isCancelled()) return;

        if (shopItem.command == null) {
            // Add item to the player Inventory
            if (!shopConfig.dropItemWhenFull && !InventoryUtils.hasEmptySlot(player.getInventory())) {
                player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().inventoryFull.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                return;
            }

            ItemStack itemStack = shopItem.type.parseItem();
            itemStack.setAmount(amount);
            if (shopItem.displayName != null && !shopItem.displayName.isEmpty()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(StringUtils.color(shopItem.displayName));
                itemStack.setItemMeta(itemMeta);
            }

            for (ItemStack dropItem : player.getInventory().addItem(itemStack).values()) {
                player.getWorld().dropItem(player.getEyeLocation(), dropItem);
            }
        } else {
            // Run the command
            String command = shopItem.command
                    .replace("%player%", player.getName())
                    .replace("%amount%", String.valueOf(amount));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // Only run the withdrawing function when the user can buy it.
        shopConfig.shopBalanceConfig.withdrawAmount(balance, crystalCost, vaultCost);
        IridiumSkyblock.getInstance().getDatabaseManager().getIslandTableManager().save(island.get());

        shopConfig.successSound.play(player);

        player.sendMessage(
                StringUtils.color(
                        IridiumSkyblock.getInstance().getMessages().successfullyBought
                                .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                                .replace("%amount%", String.valueOf(amount))
                                .replace("%item%", StringUtils.color(shopItem.name))
                                .replace("%vault_cost%", String.valueOf(vaultCost))
                                .replace("%crystal_cost%", String.valueOf(crystalCost))
                )
        );
    }

    /**
     * Sells an item for the Player in the shop.
     * He might not meet all requirements to do so.
     *
     * @param player   The player which wants to sell the item
     * @param shopItem The item which is to be sold
     * @param amount   The amount of the item which is to be sold
     */
    public void sell(Player player, ShopItem shopItem, int amount) {
        int inventoryAmount = InventoryUtils.getAmount(player.getInventory(), shopItem.type);
        if (inventoryAmount == 0) {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noSuchItem.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            IridiumSkyblock.getInstance().getShop().failSound.play(player);
            return;
        }

        int soldAmount = Math.min(inventoryAmount, amount);
        ShopSellEvent shopSellEvent = new ShopSellEvent(player, shopItem, soldAmount);
        Bukkit.getPluginManager().callEvent(shopSellEvent);
        if (shopSellEvent.isCancelled()) return;

        double vaultReward = calculateCost(soldAmount, shopItem.defaultAmount, shopItem.sellReward.vault);
        int crystalReward = (int) calculateCost(soldAmount, shopItem.defaultAmount, shopItem.sellReward.crystals);

        Island island = IridiumSkyblockAPI.getInstance().getUser(player).getIsland().get();
        ShopBalance balance = island.getShopBalance();
        if (!IridiumSkyblock.getInstance().getShop().shopBalanceConfig.depositAmount(balance, crystalReward, vaultReward)) {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().shopBalanceLimitExceeded.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }

        IridiumSkyblock.getInstance().getDatabaseManager().getIslandTableManager().save(island);
        InventoryUtils.removeAmount(player.getInventory(), shopItem.type, soldAmount);

        player.sendMessage(
                StringUtils.color(
                        IridiumSkyblock.getInstance().getMessages().successfullySold
                                .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                                .replace("%amount%", String.valueOf(amount))
                                .replace("%item%", StringUtils.color(shopItem.name))
                                .replace("%vault_reward%", String.valueOf(vaultReward))
                                .replace("%crystal_reward%", String.valueOf(crystalReward))
                )
        );

        IridiumSkyblock.getInstance().getShop().successSound.play(player);
    }

    /**
     * Calculates the cost of an item with the provided amount given the default price and amount.
     *
     * @param amount        The amount which should be calculated
     * @param defaultAmount The default amount of the item
     * @param defaultPrice  The default price of the item
     * @return The price of the item in the given quantity
     */
    private double calculateCost(int amount, int defaultAmount, double defaultPrice) {
        double costPerItem = defaultPrice / defaultAmount;
        return round(costPerItem * amount, 2);
    }

    /**
     * Rounds a double with the specified amount of decimal places.
     *
     * @param value  The value of the double that should be rounded
     * @param places The amount of decimal places
     * @return The rounded double
     */
    private double round(double value, int places) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

}
