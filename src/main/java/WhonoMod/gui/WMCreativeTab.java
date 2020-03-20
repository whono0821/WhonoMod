package WhonoMod.gui;

import WhonoMod.item.WMItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class WMCreativeTab extends CreativeTabs {

    public WMCreativeTab(String label) {
        super(label);
    }

    @Override
    public Item getTabIconItem() {
        return WMItems.sample;
    }
}
