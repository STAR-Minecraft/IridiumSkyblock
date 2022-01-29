package com.iridium.iridiumskyblock.utils;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.multiversion.MultiVersion;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utils which perform operations on {@link org.bukkit.Location}'s.
 */
public class LocationUtils {

    private static final List<Material> unsafeBlocks = Stream.of(
            XMaterial.END_PORTAL,
            XMaterial.WATER,
            XMaterial.LAVA
    ).map(XMaterial::parseMaterial).collect(Collectors.toList());

    private static final Logger LOGGER = IridiumSkyblock.getInstance().getLogger();
    private static final String INCORRECT_LOCATION_FORMAT = "Location (%s %s %s) with home offset (%s %s %s) is incorrect!";

    /**
     * Is a location safe to teleport a player to
     *
     * @param location The specified Location
     */
    public static boolean isSafe(@NotNull Location location) {
        if (IridiumSkyblock.getInstance().isTesting()) return true;
        Block block = location.getBlock();
        Block above = location.clone().add(0, 1, 0).getBlock();
        Block below = location.clone().subtract(0, 1, 0).getBlock();
        MultiVersion multiVersion = IridiumSkyblock.getInstance().getMultiVersion();
        return multiVersion.isPassable(block) && multiVersion.isPassable(above) && !multiVersion.isPassable(below) && !unsafeBlocks.contains(below.getType()) && !unsafeBlocks.contains(block.getType()) && !unsafeBlocks.contains(above.getType());
    }

    /**
     * Gets a safe location on the island silently
     * @see #getSafeLocation(Location, Island, boolean)
     *
     * @param location The location we want to teleport
     * @param island   The island we are inside
     * @return A safe Location, if none found return original location
     */
    public static Location getSafeLocation(@NotNull Location location, Island island) {
        return getSafeLocation(location, island, false);
    }

    /**
     * Gets a safe location on the island
     *
     * @param location The location we want to teleport
     * @param island   The island we are inside
     * @param verbose  Additional information printing (about location searching steps)
     * @return A safe Location, if none found return original location
     */
    public static synchronized Location getSafeLocation(@NotNull Location location, Island island, boolean verbose) {
        World world = location.getWorld();
        if (world == null) return location;
        if (island == null) return location;
        if (isSafe(location)) return location;

        if (verbose) {
            double[] homeOffset = island.getHomeOffsetXYZ();
            LOGGER.warning(String.format(INCORRECT_LOCATION_FORMAT,
                    location.getX(), location.getY(), location.getZ(),
                    homeOffset[0], homeOffset[1], homeOffset[2]
            ));
        }

        Location highest = getHighestLocation(location.getBlockX(), location.getBlockZ(), world);
        if (isSafe(highest)) {
            if (verbose) {
                LOGGER.warning(String.format("Using same highest location (%s, %s, %s)!",
                        highest.getX(), highest.getY(), highest.getZ()
                ));
            }
            return highest;
        }

        Location pos1 = island.getPos1(world);
        Location pos2 = island.getPos2(world);
        for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
            for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                Location newLocation = getHighestLocation(x, z, world);
                if (isSafe(newLocation)) {
                    if (verbose) {
                        LOGGER.warning(String.format("Using found safe location in the island region (%s, %s, %s)!",
                                highest.getX(), highest.getY(), highest.getZ()
                        ));
                    }
                    return newLocation;
                }
            }
        }

        if (verbose) {
            LOGGER.severe("No safe location was found!");
        }
        return location;
    }

    /**
     * Gets the highest Location in a world
     * Mojang was dum and changed how this worked
     *
     * @param x     the x coord
     * @param z     the z coord
     * @param world The world
     * @return The highest AIR location
     */
    private static Location getHighestLocation(int x, int z, World world) {
        Block block = world.getHighestBlockAt(x, z);
        while (!IridiumSkyblock.getInstance().getMultiVersion().isPassable(block)) {
            block = block.getLocation().add(0, 1, 0).getBlock();
        }
        return block.getLocation().add(0.5, 0, 0.5);
    }

    /**
     * With the data pack, you can modify the height limits and in the Spigot API.
     * It exists since 1.17 on Spigot and 1.16 at PaperMC.
     *
     * @param world The world
     * @return The lowest AIR location.
     */
    public static int getMinHeight(World world) {
        return XMaterial.getVersion() >= 17 ? world.getMinHeight() : 0;  // World#getMinHeight() -> Available only in 1.17 Spigot and 1.16.5 PaperMC
    }

}
