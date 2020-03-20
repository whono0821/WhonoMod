package WhonoMod.item;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemFluidContainer extends Item {

    public ItemFluidContainer(String itemName) {
        super();
        setUnlocalizedName(itemName);
        setTextureName(WhonoMod.MODID + ":" + itemName);
        setCreativeTab(WhonoMod.tabWM);

        GameRegistry.registerItem(this, itemName);
        }

}
