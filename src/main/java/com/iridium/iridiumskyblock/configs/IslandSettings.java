package com.iridium.iridiumskyblock.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumskyblock.IslandTime;
import com.iridium.iridiumskyblock.IslandWeatherType;
import com.iridium.iridiumskyblock.Setting;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Island permission configuration used by IridiumSkyblock (permissions.yml).
 * Is deserialized automatically on plugin startup and reload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IslandSettings {

    // StarMC fork: added user-friendly customizable value aliases
    public ValueAliases valueAliases = new ValueAliases();
    public Settings settings = new Settings();
    public Features features = new Features();

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValueAliases {

        public Map<Boolean, String> booleanAliases = ImmutableMap.<Boolean, String>builder()
                .put(true, "True")
                .put(false, "False")
                .build();

        public Map<IslandTime, String> timeAliases = ImmutableMap.<IslandTime, String>builder()
                .put(IslandTime.DEFAULT, "Default")
                .put(IslandTime.MORNING, "Morning")
                .put(IslandTime.DAY, "Dat")
                .put(IslandTime.EVENING, "Evening")
                .put(IslandTime.NIGHT, "Night")
                .build();

        public Map<IslandWeatherType, String> weatherTypeAliases = ImmutableMap.<IslandWeatherType, String>builder()
                .put(IslandWeatherType.DEFAULT, "Default")
                .put(IslandWeatherType.RAINING, "Raining")
                .put(IslandWeatherType.CLEAR, "Clear")
                .build();

        public String findBooleanAlias(String booleanKey) {
            switch (booleanKey.toLowerCase()) {
                case "true":
                    return booleanAliases.getOrDefault(true, booleanKey);
                case "false":
                    return booleanAliases.getOrDefault(false, booleanKey);
                default:
                    return booleanKey;
            }
        }

        public String findTimeAlias(String timeKey) {
            try {
                IslandTime islandTime = IslandTime.valueOf(timeKey.toUpperCase());
                return timeAliases.getOrDefault(islandTime, timeKey);
            } catch (IllegalArgumentException ignored) {
                return timeKey;
            }
        }

        public String findWeatherTypeAlias(String weatherTypeKey) {
            try {
                IslandWeatherType islandWeatherType = IslandWeatherType.valueOf(weatherTypeKey.toUpperCase());
                return weatherTypeAliases.getOrDefault(islandWeatherType, weatherTypeKey);
            } catch (IllegalArgumentException ignored) {
                return weatherTypeKey;
            }
        }

    }

    public static final class Settings {

        public Setting mobSpawn = new Setting(new Item(XMaterial.ZOMBIE_HEAD, 10, 1, "&b&lMob Spawn", Arrays.asList("&7Allow mobs to spawn on your Island.", "", "&b&lValue", "&7%value%")), "true");
        public Setting leafDecay = new Setting(new Item(XMaterial.OAK_LEAVES, 11, 1, "&b&lLeaf Decay", Arrays.asList("&7Allow leaves to decay on your Island.", "", "&b&lValue", "&7%value%")), "true");
        public Setting weather = new Setting(new Item(XMaterial.SNOWBALL, 12, 1, "&b&lIsland Weather", Arrays.asList("&7Change the weather of your Island.", "", "&b&lValue", "&7%value%")), IslandWeatherType.DEFAULT.name());
        public Setting time = new Setting(new Item(XMaterial.CLOCK, 13, 1, "&b&lIsland Time", Arrays.asList("&7Change your Island time.", "", "&b&lValue", "&7%value%")), IslandTime.DEFAULT.name());
        public Setting endermanGrief = new Setting(new Item(XMaterial.ENDER_PEARL, 14, 1, "&b&lEnderman Grief", Arrays.asList("&7Allow enderman to grief your Island.", "", "&b&lValue", "&7%value%")), "true");
        public Setting liquidFlow = new Setting(new Item(XMaterial.WATER_BUCKET, 15, 1, "&b&lLiquid Flow", Arrays.asList("&7Allow Water and Lava to flow on your Island.", "", "&b&lValue", "&7%value%")), "true");
        public Setting tntDamage = new Setting(new Item(XMaterial.TNT, 16, 1, "&b&lTnT Damage", Arrays.asList("&7Allow TnT to explode on your Island.", "", "&b&lValue", "&7%value%")), "true");
        public Setting fireSpread = new Setting(new Item(XMaterial.FLINT_AND_STEEL, 19, 1, "&b&lFire Spread", Arrays.asList("&7Allow fire to spread on your Island.", "", "&b&lValue", "&7%value%")), "true");

    }

    public static final class Features {

        public Item ranksItem;
        public Item trustedItem;
        public Item borderItem;
        public Item biomeItem;

        @JsonIgnore
        public @NotNull List<Item> getActiveFeatures() {
            List<Item> activeFeatures = new ArrayList<>();
            if(ranksItem != null)
                activeFeatures.add(ranksItem);
            if(trustedItem != null)
                activeFeatures.add(trustedItem);
            if(borderItem != null)
                activeFeatures.add(borderItem);
            if(biomeItem != null)
                activeFeatures.add(biomeItem);
            return activeFeatures;
        }

    }

}
