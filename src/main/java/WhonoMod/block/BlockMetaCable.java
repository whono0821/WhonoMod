package WhonoMod.block;

import WhonoMod.WhonoMod;
import WhonoMod.api.IPowerConnection;
import WhonoMod.block.itemBlock.ItemBlockMetaCable;
import WhonoMod.tile.TileEntityCableBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;
import java.util.List;

public class BlockMetaCable extends BlockContainer {

    private static final int AMOUNT = 9;

    private final IIcon[] iconBlock = new IIcon[AMOUNT];
    private final IIcon[] iconItem = new IIcon[AMOUNT];
    private final IIcon[][] colored = new IIcon[4][16];

    private final float[] size = {0.3125f, 0.3125f, 0.3125f, 0.125f, 0.375f};
    private final String[] type = {"Tin", "Copper", "Gold", "Iron", "GlassFiber"};
    private final String[] covered = {"Covered", "Uncovered"};
    private final String[] color = {
            "White", "Orange", "Magenta", "LightBlue",
            "Yellow", "Lime", "Pink", "Gray",
            "LightGray", "Cyan", "Purple", "Blue",
            "Brown", "Green", "Red", "Black"};

    public BlockMetaCable() {
        super(Material.cloth);

        setBlockName("cable");
        setBlockTextureName(WhonoMod.MODID + ":cable");
        setCreativeTab(WhonoMod.tabWM);
        setHardness(0.8F);
        setStepSound(Block.soundTypeCloth);

        GameRegistry.registerBlock(this, ItemBlockMetaCable.class, "cable");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {

        for (int i = 0; i < AMOUNT; i++) {

            if (i == AMOUNT - 1) {

                iconBlock[i] = register.registerIcon(getTextureName() + type[i / 2]);
                iconItem[i] = register.registerIcon(getTextureName() + type[i / 2] + "Item");
            }

            iconBlock[i] = register.registerIcon(getTextureName() + type[i / 2] + covered[i % 2]);
            iconItem[i] = register.registerIcon(getTextureName() + type[i / 2] + covered[i % 2] + "Item");
        }

        for (int i = 0; i < colored.length; ++i) {

            for (int j = 0; j < colored[i].length; ++j) {

                colored[i][j] = register.registerIcon(getTextureName() + type[i] + color[j]);
            }
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

        TileEntityCableBase cable = (TileEntityCableBase)world.getTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (cable.getColor() == 0) {

            return  iconBlock[meta];
        }

        return colored[meta / 2][cable.getColor()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {

        return iconBlock[meta];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getItemIcon(int meta) {

        return iconItem[meta];
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

    @Override
    public int getRenderType() {

        return WhonoMod.renderCable;
    }

    @Override
    public boolean isOpaqueCube() {

        return false;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axis, List list, Entity entity) {

        boolean[] connectDire = new boolean[6];
        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            connectDire[dire.ordinal()] = canConnect(world, x, y, z, dire);
        }

        int type = world.getBlockMetadata(x, y, z) / 2;

        float min = size[type];
        float max = 1 - size[type];

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

    private void setBlockBounds(AxisAlignedBB bb) {
        setBlockBounds((float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ);
    }

    public static class RayTraceResult {
        public final MovingObjectPosition movingObjectPosition;
        public final AxisAlignedBB boundingBox;
        public final ForgeDirection sideHit;

        RayTraceResult(MovingObjectPosition movingObjectPosition, AxisAlignedBB boundingBox, ForgeDirection side) {

            this.movingObjectPosition = movingObjectPosition;
            this.boundingBox = boundingBox;
            this.sideHit = side;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {

        RayTraceResult rayTraceResult = this.doRayTrace(world, x, y, z, Minecraft.getMinecraft().thePlayer);
        if (rayTraceResult != null && rayTraceResult.boundingBox != null) {
            AxisAlignedBB box = rayTraceResult.boundingBox;

            return box.getOffsetBoundingBox(x, y, z);
        } else {
            return super.getSelectedBoundingBoxFromPool(world, x, y, z).expand(-0.8500000238418579D, -0.8500000238418579D, -0.8500000238418579D);
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {

        RayTraceResult raytraceResult = doRayTrace(world, x, y, z, origin, direction);

        if (raytraceResult == null) {
            return null;
        } else {
            return raytraceResult.movingObjectPosition;
        }
    }

    public RayTraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer player) {

        double reachDistance = 5.0D;
        if (player instanceof EntityPlayerMP) {
            reachDistance = ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
        }

        double eyeHeight = world.isRemote ? (double)(player.getEyeHeight() - player.getDefaultEyeHeight()) : (double)player.getEyeHeight();
        Vec3 lookVec = player.getLookVec();
        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
        Vec3 direction = origin.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
        return this.doRayTrace(world, x, y, z, origin, direction);
    }

    private RayTraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {

        TileEntity tile = world.getTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (!(tile instanceof TileEntityCableBase) || tile.isInvalid()) {

            return null;
        }

        MovingObjectPosition[] hits = new MovingObjectPosition[7];
        AxisAlignedBB[] boxes = new AxisAlignedBB[7];
        ForgeDirection[] sideHit = new ForgeDirection[7];
        Arrays.fill(sideHit, ForgeDirection.UNKNOWN);

        // pipe

        for (ForgeDirection side : ForgeDirection.values()) {
            if (side == ForgeDirection.UNKNOWN || canConnect(world, x, y, z, side)) {
                AxisAlignedBB bb = getCableBoundingBox(side, meta);
                setBlockBounds(bb);
                boxes[side.ordinal()] = bb;
                hits[side.ordinal()] = super.collisionRayTrace(world, x, y, z, origin, direction);
                sideHit[side.ordinal()] = side;
            }
        }

        double minLengthSquared = Double.POSITIVE_INFINITY;
        int minIndex = -1;

        for (int i = 0; i < hits.length; i++) {
            MovingObjectPosition hit = hits[i];
            if (hit == null) {
                continue;
            }

            double lengthSquared = hit.hitVec.squareDistanceTo(origin);

            if (lengthSquared < minLengthSquared) {
                minLengthSquared = lengthSquared;
                minIndex = i;
            }
        }

        setBlockBounds(0, 0, 0, 1, 1, 1);

        if (minIndex == -1) {
            return null;
        } else {
            return new RayTraceResult(hits[minIndex], boxes[minIndex], sideHit[minIndex]);
        }
    }

    private AxisAlignedBB getCableBoundingBox(ForgeDirection side, int meta) {

        int type = meta / 2;
        float min = size[type];
        float max = 1 - size[type];

        float[] dim = {min, min, min, max, max, max};

        if (side != ForgeDirection.UNKNOWN) {

            int sideIndex = side.ordinal();

            dim[sideIndex / 2] = (sideIndex % 2 == 0) ? 0.0F : max;
            dim[sideIndex / 2 + 3] = (sideIndex % 2 == 0) ? min : 1.0F;
        }

        return AxisAlignedBB.getBoundingBox(dim[2], dim[0], dim[1], dim[5], dim[3], dim[4]);
    }

    public boolean canConnect(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection dire) {

        TileEntity tile = blockAccess.getTileEntity(x + dire.offsetX, y + dire.offsetY, z + dire.offsetZ);
        if (!(tile instanceof IPowerConnection))    return false;
        IPowerConnection connect = (IPowerConnection)tile;

        return connect.canConnectPower(dire.getOpposite());
    }

    @Override
    public boolean renderAsNormalBlock() {

        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_) {

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

        if (world.getBlockMetadata(x, y, z) % 2 == 0)  return;

        if (entity instanceof EntityLivingBase) {

            entity.attackEntityFrom(DamageSource.cactus, 1.0F);
        }
    }

    public float getCableSize(IBlockAccess world, int x, int y, int z) {

        return size[world.getBlockMetadata(x, y, z) / 2];
    }

    // ITileEntityProvider
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileEntityCableBase();
    }
}
