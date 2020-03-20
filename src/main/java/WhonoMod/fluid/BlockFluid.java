package WhonoMod.fluid;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluid extends BlockFluidClassic {

    @SideOnly(Side.CLIENT)
    private IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    private IIcon flowIcon;


    public BlockFluid(Fluid fluid, Material material, int alpha) {

        super(fluid, material);
        setBlockName(fluid.getUnlocalizedName());
        setRenderPass(alpha);

        GameRegistry.registerBlock(this, "block_" + fluid.getUnlocalizedName());
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {

        return false;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {

        stillIcon = ir.registerIcon(WhonoMod.MODID + ":" + getUnlocalizedName());
        flowIcon = ir.registerIcon(WhonoMod.MODID + ":" + getUnlocalizedName() + "_flow");

        getFluid().setIcons(stillIcon, flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }

}
