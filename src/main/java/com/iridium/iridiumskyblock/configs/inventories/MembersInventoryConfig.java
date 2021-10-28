package com.iridium.iridiumskyblock.configs.inventories;

import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MembersInventoryConfig extends NoItemGUI {

    public Item item;
    public int[] displaySlots;

    public MembersInventoryConfig(int size, String title, Background background, Item item, int[] displaySlots) {
        super(size, title, background);
        this.item = item;
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
