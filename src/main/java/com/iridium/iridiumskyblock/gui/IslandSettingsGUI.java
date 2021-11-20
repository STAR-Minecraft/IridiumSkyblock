package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.Setting;
import com.iridium.iridiumskyblock.SettingType;
import com.iridium.iridiumskyblock.api.IslandSettingChangeEvent;
import com.iridium.iridiumskyblock.configs.IslandSettings;
import com.iridium.iridiumskyblock.configs.inventories.InventoryConfig;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandSetting;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.managers.CooldownProvider;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * GUI which allows users to alter the Island's permissions.
 */
public class IslandSettingsGUI extends IslandGUI {

    private final CooldownProvider<CommandSender> cooldownProvider;

    /**
     * The default constructor.
     *
     * @param island The Island this GUI belongs to
     */
    public IslandSettingsGUI(@NotNull Island island, Inventory previousInventory, CooldownProvider<CommandSender> cooldownProvider) {
        super(IridiumSkyblock.getInstance().getInventories().islandSettingsGUI, previousInventory, island);
        this.cooldownProvider = cooldownProvider;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();

        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().islandSettingsGUI.background);

        IslandSettings islandSettings = IridiumSkyblock.getInstance().getIslandSettings();
        IslandSettings.ValueAliases valueAliases = islandSettings.valueAliases;

        for (Map.Entry<String, Setting> setting : IridiumSkyblock.getInstance().getSettingsList().entrySet()) {
            IslandSetting islandSetting = IridiumSkyblock.getInstance().getIslandManager().getIslandSetting(getIsland(), setting.getKey(), setting.getValue().getDefaultValue());
            SettingType settingType = SettingType.getByName(setting.getKey());

            // StarMC fork: make setting values customizable in the config file
            String rawValue = islandSetting.getValue();
            String value;
            switch (settingType) {
                case TIME:
                    value = valueAliases.findTimeAlias(rawValue);
                    break;
                case WEATHER:
                    value = valueAliases.findWeatherTypeAlias(rawValue);
                    break;
                default:
                    value = valueAliases.findBooleanAlias(rawValue);
                    break;
            }

            inventory.setItem(setting.getValue().getItem().slot, ItemStackUtils.makeItem(setting.getValue().getItem(), Collections.singletonList(new Placeholder("value", value))));
        }

        for (Item featureItem : islandSettings.features.getActiveFeatures()) {
            inventory.setItem(featureItem.slot, ItemStackUtils.makeItem(featureItem));
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
        Player player = (Player) event.getWhoClicked();
        User user = IridiumSkyblock.getInstance().getUserManager().getUser(player);
        IslandManager islandManager = IridiumSkyblock.getInstance().getIslandManager();

        IslandSettings.Features features = IridiumSkyblock.getInstance().getIslandSettings().features;

        Item ranksItem = features.ranksItem;
        if (ranksItem != null && event.getSlot() == ranksItem.slot) {
            player.openInventory(new IslandRanksGUI(getIsland(), player.getOpenInventory().getTopInventory()).getInventory());
            return;
        }

        Item trustedItem = features.trustedItem;
        if (trustedItem != null && event.getSlot() == trustedItem.slot) {
            player.openInventory(new IslandTrustedGUI(getIsland(), player.getOpenInventory().getTopInventory()).getInventory());
            return;
        }

        Item borderItem = features.borderItem;
        if (borderItem != null && event.getSlot() == borderItem.slot) {
            InventoryConfig islandBorderConfig = IridiumSkyblock.getInstance().getInventories().islandBorder;
            player.openInventory(new InventoryConfigGUI(islandBorderConfig, player.getOpenInventory().getTopInventory()).getInventory());
            return;
        }

        Item biomeItem = features.biomeItem;
        if (biomeItem != null && event.getSlot() == biomeItem.slot) {
            World.Environment environment = player.getWorld().getEnvironment();
            player.openInventory(new IslandBiomeGUI(1, getIsland(), environment, cooldownProvider, player.getOpenInventory().getTopInventory()).getInventory());
            return;
        }

        if (!islandManager.getIslandPermission(getIsland(), user, PermissionType.ISLAND_SETTINGS)) {
            event.getWhoClicked().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cannotChangeSettings.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }

        for (Map.Entry<String, Setting> setting : IridiumSkyblock.getInstance().getSettingsList().entrySet()) {
            if (event.getSlot() != setting.getValue().getItem().slot) continue;

            SettingType settingType = SettingType.getByName(setting.getKey());
            IslandSetting islandSetting = islandManager.getIslandSetting(getIsland(), settingType);
            String newValue = (event.getClick() == ClickType.RIGHT ? settingType.getNext() : settingType.getPrevious()).getNew(islandSetting.getValue());

            IslandSettingChangeEvent islandSettingChangeEvent = new IslandSettingChangeEvent(player, getIsland(), settingType, newValue);
            Bukkit.getPluginManager().callEvent(islandSettingChangeEvent);
            if (islandSettingChangeEvent.isCancelled()) return;

            newValue = islandSettingChangeEvent.getNewValue();
            islandSetting.setValue(newValue);
            settingType.getOnChange().run(getIsland(), newValue);
            addContent(event.getInventory());
            return;
        }
    }

}
