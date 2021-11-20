package com.iridium.iridiumskyblock.configs.inventories;

import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MembersInventoryConfig extends NoItemGUI {

    public Item listItem;
    public Item visitorsItem;
    public Item bansItem;
    public int[] displaySlots;

    public MembersInventoryConfig(int size, String title, Background background, Item listItem, Item visitorsItem, Item bansItem, int[] displaySlots) {
        super(size, title, background);
        this.listItem = listItem;
        this.visitorsItem = visitorsItem;
        this.bansItem = bansItem;
        this.displaySlots = displaySlots;
    }

    public int findSlot(int index) {
        if(displaySlots == null)
            return index;

        if(displaySlots.length <= index)
            return -1;

        return displaySlots[index];
    }

}
