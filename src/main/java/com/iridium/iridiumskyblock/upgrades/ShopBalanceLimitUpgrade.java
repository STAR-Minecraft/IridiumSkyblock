package com.iridium.iridiumskyblock.upgrades;

import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.utils.Placeholder;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class ShopBalanceLimitUpgrade extends UpgradeData{

    public double crystalsLimit;
    public double vaultLimit;

    public ShopBalanceLimitUpgrade(int money, int crystals, double crystalsLimit, double vaultLimit) {
        super(money, crystals);
        this.crystalsLimit = crystalsLimit;
        this.vaultLimit = vaultLimit;
    }

    @JsonIgnore
    @Override
    public List<Placeholder> getPlaceholders() {
        return Arrays.asList(
                new Placeholder("crystals_limit", String.valueOf(crystalsLimit)),
                new Placeholder("vault_limit", String.valueOf(vaultLimit))
        );
    }
}