package WhonoMod.item;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class SampleMeta extends Item {

    private int AMOUNT = 16;
    private  String[] NAME = {
            "a", "i", "u", "e", "o",
            "ka", "ki", "ku", "ke", "ko",
            "sa", "shi", "su", "se", "so", "end"} ;
    private IIcon[] iicon = new IIcon[AMOUNT];

    public  SampleMeta(String itemName) {
        super();
        setUnlocalizedName(itemName);
        setTextureName(WhonoMod.MODID + ":" + itemName);
        setCreativeTab(WhonoMod.tabWM);
        setMaxDamage(0);
        setHasSubtypes(true);

        GameRegistry.registerItem(this, itemName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iicon) {
        for (int i = 0; i < AMOUNT; i++) {
            this.iicon[i] = iicon.registerIcon(this.getIconString() + "." + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta){
        return iicon[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativetab, List list) {
        for (int i = 0; i < AMOUNT; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return super.getUnlocalizedName() + "." + NAME[itemstack.getItemDamage()];
    }
}
