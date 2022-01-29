package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XBiome;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.managers.CooldownProvider;
import org.apache.commons.lang.WordUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class IslandBiomeGUI extends IslandGUI {

    private final List<XBiome> biomes;
    private final int page;
    private final World.Environment environment;
    private final CooldownProvider<CommandSender> cooldownProvider;

    public IslandBiomeGUI(int page, Island island, World.Environment environment, CooldownProvider<CommandSender> cooldownProvider, Inventory previousInventory) {
        super(IridiumSkyblock.getInstance().getInventories().biomeGUI, previousInventory, island);
        this.biomes = Arrays.stream(XBiome.VALUES)
                .filter(biome -> biome.getEnvironment() == environment)
                .filter(biome -> biome.getBiome() != null)
                .filter(biome -> biome != XBiome.THE_VOID)
                .filter(biome -> biome != XBiome.CUSTOM)
                .filter(biome -> !biome.getBiome().name().startsWith("old_"))
                .collect(Collectors.toList());
        this.environment = environment;
        this.page = page;
        this.cooldownProvider = cooldownProvider;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        preFillBackground(inventory, IridiumSkyblock.getInstance().getInventories().biomeGUI.background);

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        inventory.setItem(inventory.getSize() + nextPage.slot, ItemStackUtils.makeItem(nextPage));

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        inventory.setItem(inventory.getSize() + previousPage.slot, ItemStackUtils.makeItem(previousPage));

        AtomicInteger index = new AtomicInteger(0);

        final long elementsPerPage = IridiumSkyblock.getInstance().getInventories().biomeGUI.size - 9;
        biomes.stream()
                .skip((page - 1) * elementsPerPage)
                .limit(elementsPerPage)
                .forEach(xBiome ->
                        inventory.setItem(index.getAndIncrement(), ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().biomeGUI.item, Collections.singletonList(new Placeholder("biome", WordUtils.capitalizeFully(xBiome.name().toLowerCase().replace("_", " "))))))
                );

        if (IridiumSkyblock.getInstance().getConfiguration().backButtons && getPreviousInventory() != null) {
            Item backButton = IridiumSkyblock.getInstance().getInventories().backButton;
            inventory.setItem(inventory.getSize() + backButton.slot, ItemStackUtils.makeItem(backButton));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        final int size = IridiumSkyblock.getInstance().getInventories().biomeGUI.size;
        Player player = (Player) event.getWhoClicked();

        Item previousPage = IridiumSkyblock.getInstance().getInventories().previousPage;
        if (event.getSlot() == size + previousPage.slot && page > 1) {
            player.openInventory(new IslandBiomeGUI(page - 1, getIsland(), environment, cooldownProvider, getPreviousInventory()).getInventory());
            return;
        }

        Item nextPage = IridiumSkyblock.getInstance().getInventories().nextPage;
        if (event.getSlot() == size + nextPage.slot && (size - 9) * page < biomes.size()) {
            player.openInventory(new IslandBiomeGUI(page + 1, getIsland(), environment, cooldownProvider, getPreviousInventory()).getInventory());
            return;
        }

        if (event.getSlot() + 1 <= biomes.size()) {
            int index = ((size - 9) * (page - 1)) + event.getSlot();
            if (biomes.size() > index) {
                IridiumSkyblock.getInstance().getCommands().biomeCommand.execute(player, new String[]{"", biomes.get(index).toString()});
                player.closeInventory();
                cooldownProvider.applyCooldown(player);
            }
        }
    }

}
