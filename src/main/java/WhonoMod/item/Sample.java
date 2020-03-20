package WhonoMod.item;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class Sample extends Item {

    Sample(String itemName) {
        super();
        setUnlocalizedName(itemName);
        setTextureName(WhonoMod.MODID + ":" + itemName);
        setCreativeTab(WhonoMod.tabWM);

        GameRegistry.registerItem(this, itemName);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean useAdvances) {

        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            list.add("pressed shift!!");
        else
            list.add("nature");
    }
}
