package com.iridium.iridiumskyblock.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.dependencies.xseries.XSound;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandUpgrade;
import com.iridium.iridiumskyblock.database.ShopBalance;
import com.iridium.iridiumskyblock.exceptions.ShopBalanceInsufficientFundsException;
import com.iridium.iridiumskyblock.exceptions.ShopBalanceLimitExceededException;
import com.iridium.iridiumskyblock.shop.ShopItem;
import com.iridium.iridiumskyblock.shop.ShopItem.BuyCost;
import com.iridium.iridiumskyblock.shop.ShopItem.SellReward;
import com.iridium.iridiumskyblock.upgrades.ShopBalanceLimitUpgrade;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The shop configuration used by IridiumSkyblock (shop.yml).
 * Is deserialized automatically on plugin startup and reload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shop {

    public Map<String, ShopCategoryConfig> categories = ImmutableMap.<String, ShopCategoryConfig>builder()
            .put("Blocks", new ShopCategoryConfig(new Item(XMaterial.GRASS_BLOCK, 12, 1, "&9&lBlocks", Collections.emptyList()), "Blocks", 6))
            .put("Food", new ShopCategoryConfig(new Item(XMaterial.COOKED_CHICKEN, 13, 1, "&9&lFood", Collections.emptyList()), "Food", 4))
            .put("Ores", new ShopCategoryConfig(new Item(XMaterial.GOLD_INGOT, 14, 1, "&9&lOres", Collections.emptyList()), "Ores", 4))
            .put("Farming", new ShopCategoryConfig(new Item(XMaterial.WHEAT, 21, 1, "&9&lFarming", Collections.emptyList()), "Farming", 5))
            .put("Mob Drops", new ShopCategoryConfig(new Item(XMaterial.SPIDER_EYE, 22, 1, "&9&lMob Drops", Collections.emptyList()), "Mob Drops", 5))
            .put("Miscellaneous", new ShopCategoryConfig(new Item(XMaterial.SADDLE, 23, 1, "&9&lMiscellaneous", Collections.emptyList()), "Miscellaneous", 4))
            .build();

    public Map<String, List<ShopItem>> items = ImmutableMap.<String, List<ShopItem>>builder()
            .put("Blocks", Arrays.asList(
                    new ShopItem(
                            "&9&lGrass Block",
                            "",
                            XMaterial.GRASS_BLOCK,
                            1,
                            10,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lDirt Block",
                            "",
                            XMaterial.DIRT,
                            10,
                            11,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lGravel",
                            "",
                            XMaterial.GRAVEL,
                            10,
                            12,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lGranite",
                            "",
                            XMaterial.GRANITE,
                            10,
                            13,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lDiorite",
                            "",
                            XMaterial.DIORITE,
                            10,
                            14,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lAndesite",
                            "",
                            XMaterial.ANDESITE,
                            10,
                            15,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lOak Log",
                            "",
                            XMaterial.OAK_LOG,
                            16,
                            16,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lSpruce Log",
                            "",
                            XMaterial.SPRUCE_LOG,
                            16,
                            19,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lBirch Log",
                            "",
                            XMaterial.BIRCH_LOG,
                            16,
                            20,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lJungle Log",
                            "",
                            XMaterial.JUNGLE_LOG,
                            16,
                            21,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lAcacia Log",
                            "",
                            XMaterial.ACACIA_LOG,
                            16,
                            22,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lDark Oak Log",
                            "",
                            XMaterial.DARK_OAK_LOG,
                            16,
                            23,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lSnow Block",
                            "",
                            XMaterial.SNOW_BLOCK,
                            16,
                            24,
                            new BuyCost(200, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lIce",
                            "",
                            XMaterial.ICE,
                            8,
                            25,
                            new BuyCost(300, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lPacked Ice",
                            "",
                            XMaterial.PACKED_ICE,
                            8,
                            28,
                            new BuyCost(300, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lSponge",
                            "",
                            XMaterial.SPONGE,
                            4,
                            29,
                            new BuyCost(1000, 0),
                            new SellReward(200, 0)
                    ),
                    new ShopItem(
                            "&9&lSand",
                            "",
                            XMaterial.SAND,
                            8,
                            30,
                            new BuyCost(100, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lSandstone",
                            "",
                            XMaterial.SANDSTONE,
                            16,
                            31,
                            new BuyCost(80, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lClay Ball",
                            "",
                            XMaterial.CLAY_BALL,
                            32,
                            32,
                            new BuyCost(70, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lObsidian",
                            "",
                            XMaterial.OBSIDIAN,
                            4,
                            33,
                            new BuyCost(250, 0),
                            new SellReward(25, 0)
                    ),
                    new ShopItem(
                            "&9&lGlowstone",
                            "",
                            XMaterial.GLOWSTONE,
                            8,
                            34,
                            new BuyCost(125, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lEnd Stone",
                            "",
                            XMaterial.END_STONE,
                            4,
                            39,
                            new BuyCost(250, 0),
                            new SellReward(25, 0)
                    ),
                    new ShopItem(
                            "&9&lPrismarine",
                            "",
                            XMaterial.PRISMARINE,
                            16,
                            40,
                            new BuyCost(200, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lWool",
                            "",
                            XMaterial.WHITE_WOOL,
                            8,
                            41,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    )
            ))
            .put("Food", Arrays.asList(
                    new ShopItem(
                            "&9&lApple",
                            "",
                            XMaterial.APPLE,
                            10,
                            11,
                            new BuyCost(50, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lEnchanted Golden Apple",
                            "",
                            XMaterial.ENCHANTED_GOLDEN_APPLE,
                            3,
                            12,
                            new BuyCost(1000, 0),
                            new SellReward(100, 0)
                    ),
                    new ShopItem(
                            "&9&lCarrot",
                            "",
                            XMaterial.CARROT,
                            10,
                            13,
                            new BuyCost(100, 0),
                            new SellReward(25, 0)
                    ),
                    new ShopItem(
                            "&9&lBaked Potato",
                            "",
                            XMaterial.BAKED_POTATO,
                            10,
                            14,
                            new BuyCost(150, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lBread",
                            "",
                            XMaterial.BREAD,
                            10,
                            15,
                            new BuyCost(50, 0),
                            new SellReward(30, 0)
                    ),
                    new ShopItem(
                            "&9&lCookie",
                            "",
                            XMaterial.COOKIE,
                            5,
                            20,
                            new BuyCost(130, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lCooked Porkchop",
                            "",
                            XMaterial.COOKED_PORKCHOP,
                            10,
                            21,
                            new BuyCost(100, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lCooked Beef",
                            "",
                            XMaterial.COOKED_BEEF,
                            10,
                            22,
                            new BuyCost(100, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lCooked Mutton",
                            "",
                            XMaterial.COOKED_MUTTON,
                            10,
                            23,
                            new BuyCost(100, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lCooked Rabbit",
                            "",
                            XMaterial.COOKED_RABBIT,
                            10,
                            24,
                            new BuyCost(100, 0),
                            new SellReward(25, 0)
                    )
                    )
            )
            .put("Ores", Arrays.asList(
                    new ShopItem(
                            "&9&lCoal",
                            "",
                            XMaterial.COAL,
                            16,
                            11,
                            new BuyCost(100, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lRedstone",
                            "",
                            XMaterial.REDSTONE,
                            16,
                            12,
                            new BuyCost(150, 0),
                            new SellReward(7, 0)
                    ),
                    new ShopItem(
                            "&9&lLapis Lazuli",
                            "",
                            XMaterial.LAPIS_LAZULI,
                            16,
                            13,
                            new BuyCost(150, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lIron Ingot",
                            "",
                            XMaterial.IRON_INGOT,
                            8,
                            14,
                            new BuyCost(200, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lGold Ingot",
                            "",
                            XMaterial.GOLD_INGOT,
                            8,
                            15,
                            new BuyCost(200, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lDiamond",
                            "",
                            XMaterial.DIAMOND,
                            8,
                            21,
                            new BuyCost(1000, 0),
                            new SellReward(100, 0)
                    ),
                    new ShopItem(
                            "&9&lEmerald",
                            "",
                            XMaterial.EMERALD,
                            8,
                            22,
                            new BuyCost(200, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lQuartz",
                            "",
                            XMaterial.QUARTZ,
                            64,
                            23,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    )
                    )
            )
            .put("Farming", Arrays.asList(
                    new ShopItem(
                            "&9&lWheat Seeds",
                            "",
                            XMaterial.WHEAT_SEEDS,
                            16,
                            10,
                            new BuyCost(130, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lPumpkin Seeds",
                            "",
                            XMaterial.PUMPKIN_SEEDS,
                            16,
                            11,
                            new BuyCost(150, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lMelon Seeds",
                            "",
                            XMaterial.MELON_SEEDS,
                            16,
                            12,
                            new BuyCost(250, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lNether Wart",
                            "",
                            XMaterial.NETHER_WART,
                            4,
                            13,
                            new BuyCost(100, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lSugar Cane",
                            "",
                            XMaterial.SUGAR_CANE,
                            16,
                            14,
                            new BuyCost(150, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lWheat",
                            "",
                            XMaterial.WHEAT,
                            16,
                            15,
                            new BuyCost(50, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lPumpkin",
                            "",
                            XMaterial.PUMPKIN,
                            16,
                            16,
                            new BuyCost(150, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lMelon Slice",
                            "",
                            XMaterial.MELON_SLICE,
                            16,
                            19,
                            new BuyCost(150, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lCactus",
                            "",
                            XMaterial.CACTUS,
                            8,
                            20,
                            new BuyCost(80, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lOak Sapling",
                            "",
                            XMaterial.OAK_SAPLING,
                            4,
                            21,
                            new BuyCost(20, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lSpruce Sapling",
                            "",
                            XMaterial.SPRUCE_SAPLING,
                            4,
                            22,
                            new BuyCost(20, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lBirch Sapling",
                            "",
                            XMaterial.BIRCH_SAPLING,
                            4,
                            23,
                            new BuyCost(20, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lJungle Sapling",
                            "",
                            XMaterial.JUNGLE_SAPLING,
                            4,
                            24,
                            new BuyCost(150, 0),
                            new SellReward(4, 0)
                    ),
                    new ShopItem(
                            "&9&lAcacia Sapling",
                            "",
                            XMaterial.ACACIA_SAPLING,
                            4,
                            25,
                            new BuyCost(20, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lDark Oak Sapling",
                            "",
                            XMaterial.DARK_OAK_SAPLING,
                            4,
                            30,
                            new BuyCost(150, 0),
                            new SellReward(4, 0)
                    ),
                    new ShopItem(
                            "&9&lBrown Mushroom",
                            "",
                            XMaterial.BROWN_MUSHROOM,
                            8,
                            31,
                            new BuyCost(60, 0),
                            new SellReward(6, 0)
                    ),
                    new ShopItem(
                            "&9&lRed Mushroom",
                            "",
                            XMaterial.RED_MUSHROOM,
                            8,
                            32,
                            new BuyCost(60, 0),
                            new SellReward(6, 0)
                    )
                    )
            )
            .put("Mob Drops", Arrays.asList(
                    new ShopItem(
                            "&9&lRotten Flesh",
                            "",
                            XMaterial.ROTTEN_FLESH,
                            16,
                            10,
                            new BuyCost(20, 0),
                            new SellReward(2, 0)
                    ),
                    new ShopItem(
                            "&9&lBone",
                            "",
                            XMaterial.BONE,
                            16,
                            11,
                            new BuyCost(100, 0),
                            new SellReward(3, 0)
                    ),
                    new ShopItem(
                            "&9&lGunpowder",
                            "",
                            XMaterial.GUNPOWDER,
                            16,
                            12,
                            new BuyCost(30, 0),
                            new SellReward(3, 0)
                    ),
                    new ShopItem(
                            "&9&lString",
                            "",
                            XMaterial.STRING,
                            16,
                            13,
                            new BuyCost(80, 0),
                            new SellReward(3, 0)
                    ),
                    new ShopItem(
                            "&9&lArrow",
                            "",
                            XMaterial.ARROW,
                            16,
                            14,
                            new BuyCost(75, 0),
                            new SellReward(4, 0)
                    ),
                    new ShopItem(
                            "&9&lSpider Eye",
                            "",
                            XMaterial.SPIDER_EYE,
                            16,
                            15,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lEnder Pearl",
                            "",
                            XMaterial.ENDER_PEARL,
                            3,
                            16,
                            new BuyCost(75, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lSlime Ball",
                            "",
                            XMaterial.SLIME_BALL,
                            16,
                            19,
                            new BuyCost(200, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lPrismarine Crystals",
                            "",
                            XMaterial.PRISMARINE_CRYSTALS,
                            16,
                            20,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lPrismarine Shard",
                            "",
                            XMaterial.PRISMARINE_SHARD,
                            16,
                            21,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lBlaze Rod",
                            "",
                            XMaterial.BLAZE_ROD,
                            4,
                            22,
                            new BuyCost(250, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lMagma Cream",
                            "",
                            XMaterial.MAGMA_CREAM,
                            4,
                            23,
                            new BuyCost(150, 0),
                            new SellReward(15, 0)
                    ),
                    new ShopItem(
                            "&9&lGhast Tear",
                            "",
                            XMaterial.GHAST_TEAR,
                            4,
                            24,
                            new BuyCost(200, 0),
                            new SellReward(30, 0)
                    ),
                    new ShopItem(
                            "&9&lLeather",
                            "",
                            XMaterial.LEATHER,
                            8,
                            25,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lRabbit Foot",
                            "",
                            XMaterial.RABBIT_FOOT,
                            4,
                            30,
                            new BuyCost(250, 0),
                            new SellReward(30, 0)
                    ),
                    new ShopItem(
                            "&9&lInk Sack",
                            "",
                            XMaterial.INK_SAC,
                            8,
                            31,
                            new BuyCost(50, 0),
                            new SellReward(5, 0)
                    ),
                    new ShopItem(
                            "&9&lFeather",
                            "",
                            XMaterial.FEATHER,
                            16,
                            32,
                            new BuyCost(30, 0),
                            new SellReward(3, 0)
                    )
                    )
            )
            .put("Miscellaneous", Arrays.asList(
                    new ShopItem(
                            "&9&lBucket",
                            "",
                            XMaterial.BUCKET,
                            1,
                            12,
                            new BuyCost(100, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lWater Bucket",
                            "",
                            XMaterial.WATER_BUCKET,
                            1,
                            13,
                            new BuyCost(200, 0),
                            new SellReward(10, 0)
                    ),
                    new ShopItem(
                            "&9&lLava Bucket",
                            "",
                            XMaterial.LAVA_BUCKET,
                            1,
                            14,
                            new BuyCost(200, 0),
                            new SellReward(20, 0)
                    ),
                    new ShopItem(
                            "&9&lName Tag",
                            "",
                            XMaterial.NAME_TAG,
                            1,
                            21,
                            new BuyCost(200, 0),
                            new SellReward(30, 0)
                    ),
                    new ShopItem(
                            "&9&lSaddle",
                            "",
                            XMaterial.SADDLE,
                            1,
                            22,
                            new BuyCost(300, 0),
                            new SellReward(30, 0)
                    ),
                    new ShopItem(
                            "&9&lEnd Portal Frame",
                            "",
                            XMaterial.END_PORTAL_FRAME,
                            Arrays.asList("&5&lVisit the end!", " "),
                            null,
                            1,
                            23,
                            new BuyCost(5000, 50),
                            new SellReward(0, 0)
                    )
                    )
            )
            .build();

    public String overviewTitle = "&7Island Shop";
    public String categoryTitle = "&7Island Shop | %category_name%";
    public List<String> buyPriceLore = Collections.singletonList("&aBuy Price: $%buy_price_vault%, %buy_price_crystals% Crystals");
    public List<String> sellRewardLore = Collections.singletonList("&cSelling Reward: $%sell_reward_vault%, %sell_reward_crystals% Crystals");
    public List<String> notPurchasableLore = Collections.singletonList("&cThis item cannot be purchased!");
    public List<String> notSellableLore = Collections.singletonList("&cThis item cannot be sold!");
    public List<String> shopItemLore = Arrays.asList(" ", "&b&l[!] &bLeft-Click to Purchase %amount%, Shift for 64", "&b&l[!] &bRight Click to Sell %amount%, Shift for 64");

    public ShopBalanceConfig shopBalanceConfig = new ShopBalanceConfig();

    public boolean abbreviatePrices = true;
    public boolean abbreviateShopBalances = true;
    public boolean dropItemWhenFull = false;
    public boolean addFooterLineInGUI = true;

    public int overviewSize = 4 * 9;

    public XSound failSound = XSound.BLOCK_ANVIL_LAND;
    public XSound successSound = XSound.ENTITY_PLAYER_LEVELUP;

    public XSound shopResetFailSound = XSound.BLOCK_ANVIL_LAND;
    public XSound shopResetSuccessSound = XSound.ENTITY_PLAYER_LEVELUP;

    public Background overviewBackground = new Background(ImmutableMap.<Integer, Item>builder().build());
    public Background categoryBackground = new Background(ImmutableMap.<Integer, Item>builder().build());

    public Item resetButton = new Item(XMaterial.HOPPER, 35, 1, "&b&lReset Shop Balance", Arrays.asList(
            "&7Want to reset your shop balance to default?",
            "&7You can do that with this button.",
            "",
            "&7Reset Cost: &b%crystals% Crystals and $%vault%",
            "",
            "&b&l[!] &bLeft Click to Reset the Shop Balance"
    ));

    /**
     * Represents configurable options of a {@link com.iridium.iridiumskyblock.shop.ShopCategory}.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopCategoryConfig {
        public Item item;
        public String displayName;
        public int inventoryRows;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShopBalanceConfig {
        private static final double DEFAULT_LIMIT = -1D;

        public boolean resetBalancesEveryDay = true;

        public Map<String, Double> resetPrice = ImmutableMap.<String, Double>builder()
                .put("crystals", 100.0)
                .put("vault", 10000.0)
                .build();

        @JsonIgnore
        public double getResetCost(@NotNull String currency) {
            return Math.max(0D, resetPrice.getOrDefault(currency, 0D));
        }

        @JsonIgnore
        public double getBalanceLimit(@NotNull Island island, @NotNull String currency) {
            IslandUpgrade islandUpgrade = IridiumSkyblock.getInstance().getIslandManager().getIslandUpgrade(island, "shop-balance-limit");
            int upgradeLevel = islandUpgrade.getLevel();

            ShopBalanceLimitUpgrade upgrade = IridiumSkyblock.getInstance().getUpgrades().shopBalanceLimitUpgrade.upgrades.get(upgradeLevel);
            if (upgrade == null)
                return 0D;

            double limit;
            switch (currency.toLowerCase()) {
                case "crystals":
                    limit = upgrade.crystalsLimit;
                    break;
                case "vault":
                    limit = upgrade.vaultLimit;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected currency with ID: " + currency + "!");
            }

            return limit >= 0D ? limit : DEFAULT_LIMIT;
        }

        public boolean has(@NotNull ShopBalance shopBalance, double crystals, double vault) {
            return shopBalance.has("crystals", crystals) && shopBalance.has("vault", vault);
        }

        public boolean depositAmount(@NotNull ShopBalance shopBalance, double crystals, double vault) {
            try {
                shopBalance.add("crystals", crystals);
                shopBalance.add("vault", vault);
                return true;
            } catch (ShopBalanceLimitExceededException ignored) {
                return false;
            }
        }

        public boolean withdrawAmount(@NotNull ShopBalance shopBalance, double crystals, double vault) {
            try {
                shopBalance.take("crystals", crystals);
                shopBalance.take("vault", vault);
                return true;
            } catch (ShopBalanceInsufficientFundsException ignored) {
                return false;
            }
        }

        public void fillWithDefaultAmounts(@NotNull Island island, @NotNull ShopBalance shopBalance) {
            shopBalance.clear();

            ShopBalanceLimitUpgrade upgrade = getUpgrade(island);
            shopBalance.set("crystals", upgrade.crystalsLimit);
            shopBalance.set("vault", upgrade.vaultLimit);
        }

        @JsonIgnore
        public boolean isDefaultBalance(@NotNull Island island, @NotNull ShopBalance shopBalance) {
            ShopBalanceLimitUpgrade upgrade = getUpgrade(island);
            double crystalsBalance = shopBalance.getBalanceOf("crystals").orElse(0D);
            double vaultBalance = shopBalance.getBalanceOf("vault").orElse(0D);
            return crystalsBalance == upgrade.crystalsLimit && vaultBalance == upgrade.vaultLimit;
        }

        @JsonIgnore
        public @NotNull List<Placeholder> getResetPricePlaceholders() {
            List<Placeholder> placeholders = new ArrayList<>();
            if(resetPrice != null && !resetPrice.isEmpty()) {
                resetPrice.entrySet().stream()
                        .map(this::makePlaceholderInstance)
                        .forEach(placeholders::add);
            }
            return placeholders;
        }

        @JsonIgnore
        private @NotNull ShopBalanceLimitUpgrade getUpgrade(@NotNull Island island) {
            IslandUpgrade islandUpgrade = IridiumSkyblock.getInstance().getIslandManager().getIslandUpgrade(island, "shop-balance-limit");
            int upgradeLevel = islandUpgrade.getLevel();
            return IridiumSkyblock.getInstance().getUpgrades().shopBalanceLimitUpgrade.upgrades.get(upgradeLevel);
        }

        private @NotNull Placeholder makePlaceholderInstance(@NotNull Map.Entry<String, ?> mapEntry) {
            return makePlaceholderInstance(mapEntry.getKey(), mapEntry.getValue());
        }

        private @NotNull Placeholder makePlaceholderInstance(@NotNull String key, @Nullable Object value) {
            return new Placeholder(key, String.valueOf(value));
        }

    }

}
