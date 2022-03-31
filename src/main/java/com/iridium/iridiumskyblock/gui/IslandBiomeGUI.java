package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.dependencies.xseries.XBiome;
import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.configs.inventories.NoItemGUI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.managers.CooldownProvider;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IslandBiomeGUI extends PagedGUI<XBiome> {

    private static final List<XBiome> BIOMES;

    private final List<XBiome> biomes;
    private final CooldownProvider<CommandSender> cooldownProvider;

    static {
        BIOMES = Arrays.stream(XBiome.VALUES)
                .filter(biome -> biome.getBiome() != null)
                .filter(biome -> biome != XBiome.THE_VOID)
                .filter(biome -> biome != XBiome.CUSTOM)
                .filter(biome -> !biome.getBiome().name().startsWith("old_"))
                .collect(Collectors.toList());
    }

    public IslandBiomeGUI(Island island, World.Environment environment, CooldownProvider<CommandSender> cooldownProvider, Inventory previousInventory) {
        super(1,
                IridiumSkyblock.getInstance().getInventories().biomeGUI.size,
                IridiumSkyblock.getInstance().getInventories().biomeGUI.background,
                IridiumSkyblock.getInstance().getInventories().previousPage,
                IridiumSkyblock.getInstance().getInventories().nextPage,
                previousInventory,
                IridiumSkyblock.getInstance().getInventories().backButton
        );

        this.biomes = BIOMES.stream()
                .filter(biome -> biome.getEnvironment() == environment)
                .collect(Collectors.toList());

        this.cooldownProvider = cooldownProvider;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumSkyblock.getInstance().getInventories().biomeGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<XBiome> getPageObjects() {
        return biomes;
    }

    @Override
    public ItemStack getItemStack(XBiome biome) {
        List<Placeholder> placeholderList = new PlaceholderBuilder()
                .add("biome", WordUtils.capitalizeFully(biome.name().toLowerCase().replace("_", " ")))
                .build();

        return ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().biomeGUI.item, placeholderList);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        XBiome biome = getItem(event.getSlot());
        if (biome == null) return;
        IridiumSkyblock.getInstance().getCommands().biomeCommand.execute(event.getWhoClicked(), new String[]{"", biome.toString()});
        event.getWhoClicked().closeInventory();
        cooldownProvider.applyCooldown(event.getWhoClicked());
    }

}
