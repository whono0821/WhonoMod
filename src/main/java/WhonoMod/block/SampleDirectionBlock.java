package WhonoMod.block;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;

public class SampleDirectionBlock extends Block {

    private IIcon[] iicon = new IIcon[6];

    SampleDirectionBlock(String blockName, Material material) {
        super(material);

        setBlockName(blockName);
        setBlockTextureName(WhonoMod.MODID + ":" + blockName);
        setCreativeTab(WhonoMod.tabWM);
        setHardness(5.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(10.0F);
        setLightLevel(5.0F);
        setStepSound(Block.soundTypeMetal);

        GameRegistry.registerBlock(this, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < 6; i++) {
            this.iicon[i] = register.registerIcon(this.getTextureName() + "_" + Facing.facings[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iicon[side];
    }
}
