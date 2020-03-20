package WhonoMod.block;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class SampleMetaBlock extends Block {

    private int AMOUNT = 16;
    private IIcon[] iicon = new IIcon[AMOUNT];

    SampleMetaBlock(String blockName, Material material) {
        super(material);

        setBlockName(blockName);
        setBlockTextureName(WhonoMod.MODID + ":" + blockName);
        setCreativeTab(WhonoMod.tabWM);
        setHardness(5.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(10.0F);
        setLightLevel(5.0F);
        setStepSound(Block.soundTypeMetal);

        GameRegistry.registerBlock(this, ItemSampleMetaBlock.class, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < AMOUNT; i++) {
            iicon[i] = register.registerIcon(getTextureName() + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iicon[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < AMOUNT; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

}
