package WhonoMod.block;

import WhonoMod.WhonoMod;
import WhonoMod.api.IPowerConnection;
import WhonoMod.tile.TileEntityCable;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockCable extends BlockContainer {

    private IIcon itemIcon;
    private boolean isCovered;
    private int colorID = 0;

    public BlockCable(String blockName) {
        super(Material.rock);

        isCovered = true;
        setBlockName(blockName);
        setBlockTextureName(WhonoMod.MODID + ":" + blockName);
        setCreativeTab(WhonoMod.tabWM);
        setHardness(0.8F);
        setStepSound(Block.soundTypeCloth);

        GameRegistry.registerBlock(this, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {

        return this.itemIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getBlockIcon(int meta, int side) {

        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {

        this.itemIcon = register.registerIcon(this.getTextureName() + "Item");
        this.blockIcon = register.registerIcon(this.getTextureName());
    }

    @Override
    public int getRenderType() {

        return WhonoMod.renderCable;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axis, List list, Entity entity) {

        boolean[] connectDire = new boolean[6];
        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            connectDire[dire.ordinal()] = canConnect(world, x, y, z, dire);
        }

        float min = 0.25f;
        float max = 0.75f;

        this.setBlockBounds(min, min, min, max, max, max);
        super.addCollisionBoxesToList(world, x, y, z, axis, list, entity);

        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            int direIndex = dire.ordinal();

            if(connectDire[direIndex]) {

                float[] dim = { min, min, min, max, max, max };

                dim[direIndex / 2] = (direIndex % 2 == 0) ? 0.0F : max;
                dim[direIndex / 2 + 3] = (direIndex % 2 == 0) ? min : 1.0F;

                this.setBlockBounds(dim[2], dim[0], dim[1], dim[5], dim[3], dim[4]);
                super.addCollisionBoxesToList(world, x, y, z, axis, list, entity);
            }
        }
        this.setBlockBounds(min, min, min, max, max, max);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

        boolean[] connectDire = new boolean[6];
        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            connectDire[dire.ordinal()] = canConnect(world, x, y, z, dire);
        }


        float[] dim = { 0.25F, 0.75F, 0.25F, 0.75F, 0.25F, 0.75F };

        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            int direIndex = dire.ordinal();
            if(connectDire[direIndex]) {

                dim[direIndex] = (direIndex % 2 == 0) ? 0.0F : 1.0F;
            }
        }
        this.setBlockBounds(dim[4], dim[0], dim[2], dim[5], dim[1], dim[3]);
    }

    public boolean canConnect(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection dire) {

        TileEntity tile = blockAccess.getTileEntity(x + dire.offsetX, y + dire.offsetY, z + dire.offsetZ);
        if (!(tile instanceof IPowerConnection))    return false;
        IPowerConnection connect = (IPowerConnection)tile;

        return connect.canConnectPower(dire.getOpposite());
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_) {

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

        if (isCovered)  return;

        if (entity instanceof EntityLivingBase) {

            entity.attackEntityFrom(DamageSource.cactus, 1.0F);
        }
    }

    // ITileEntityProvider
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {

        return new TileEntityCable();
    }
}
