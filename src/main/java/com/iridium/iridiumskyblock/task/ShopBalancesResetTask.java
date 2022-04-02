package com.iridium.iridiumskyblock.task;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.ShopLimits;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.managers.tablemanagers.IslandTableManager;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Objects;

public final class ShopBalancesResetTask implements Runnable {

    private final IridiumSkyblock plugin;
    private final DataCacheFile dataCacheFile;
    private final IslandTableManager islandTableManager;
    private long cachedEpochDay;
    private BukkitTask currentTask;

    public ShopBalancesResetTask(@NotNull IridiumSkyblock plugin) {
        this.plugin = plugin;
        this.dataCacheFile = new DataCacheFile(plugin, ".current-day-cache");
        this.islandTableManager = plugin.getDatabaseManager().getIslandTableManager();
    }

    public void start() {
        stop();

        if(!plugin.getShop().shopLimitsConfig.resetLimitsEveryDay)
            return;

        this.cachedEpochDay = dataCacheFile.readAsLong(0).orElse(0);
        this.currentTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 100L, 200L);
    }

    public void stop() {
        if(currentTask == null)
            return;

        this.currentTask.cancel();
        this.currentTask = null;
    }

    public void restart() {
        stop();
        start();
    }

    @Override
    public synchronized void run() {
        long currentEpochDay = LocalDate.now().getLong(ChronoField.EPOCH_DAY);
        if(currentEpochDay > cachedEpochDay) {
            cachedEpochDay = currentEpochDay;
            if(dataCacheFile.write(currentEpochDay)) {
                onNextDay();
            } else {
                stop();
            }
        }
    }

    private void onNextDay() {
        islandTableManager.getEntries().forEach(this::processIsland);
    }

    private void processIsland(@NotNull Island island) {
        ShopLimits balance = island.getShopLimits();
        if(plugin.getShop().shopLimitsConfig.isDefaultAmounts(island, balance))
            return;

        island.resetShopLimits();
        islandTableManager.save(island);

        island.getMembers().stream()
                .map(User::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(StringUtils.color(plugin.getMessages().shopLimitsResetAlert.replace("%prefix%", plugin.getConfiguration().prefix))));
    }

}
