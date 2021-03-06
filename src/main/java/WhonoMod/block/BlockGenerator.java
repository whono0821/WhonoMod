package WhonoMod.block;

import WhonoMod.WhonoMod;
import WhonoMod.item.ItemCraftingTool;
import WhonoMod.item.WMItems;
import WhonoMod.tile.TileEntityGenerator;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockGenerator extends BlockContainer {

    private final Random rand = new Random();
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconActiveFront;

    public BlockGenerator() {
        super(Material.rock);

        this.setHardness(2.5F);
        this.setResistance(12.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName("Generator");
        setCreativeTab(WhonoMod.tabWM);

        GameRegistry.registerBlock(this, "Generator");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {

        return Item.getItemFromBlock(WMBlocks.generator);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {

        super.onBlockAdded(world, x, y, z);
        getDirection(world, x, y, z);
    }

    private void getDirection(World world, int x, int y, int z) {

        if (!world.isRemote){
            Block block = world.getBlock(x, y, z - 1);
            Block block1 = world.getBlock(x, y, z + 1);
            Block block2 = world.getBlock(x - 1, y, z);
            Block block3 = world.getBlock(x + 1, y, z);
            byte b0 = 3;

            if (block.func_149730_j() && !block1.func_149730_j())
                b0 = 3;

            if (block1.func_149730_j() && !block.func_149730_j())
                b0 = 2;

            if (block2.func_149730_j() && !block3.func_149730_j())
                b0 = 5;

            if (block3.func_149730_j() && !block2.func_149730_j())
                b0 = 4;

            world.setBlockMetadataWithNotify(x, y, z, b0, 2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {

        TileEntityGenerator generator = (TileEntityGenerator)iBlockAccess.getTileEntity(x, y, z);
        short face = generator.getFace();
        boolean isActive = generator.isActive();

        if (side == face && isActive) {

            return iconActiveFront;
        }

        return this.getIcon(side, iBlockAccess.getBlockMetadata(x, y, z));

    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {

        return side == 1 ? iconTop : side == 0 ? iconTop : side != meta ? blockIcon : iconFront;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {

        blockIcon = iconRegister.registerIcon(WhonoMod.MODID + ":Generator_side");
        iconFront = iconRegister.registerIcon(WhonoMod.MODID + ":Generator_front_off");
        iconActiveFront = iconRegister.registerIcon(WhonoMod.MODID + ":Generator_front_on");
        iconTop = iconRegister.registerIcon(WhonoMod.MODID + ":Generator_top");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

        if (player.isSneaking()) {

            ItemStack equippedStack = player.getCurrentEquippedItem();
            Item equipped = equippedStack != null ? equippedStack.getItem() : null;
            if (equipped != null) {

                if (equipped == WMItems.itemWrench) {

                    if (!world.isRemote) {

                        NBTTagCompound nbt = getTag(world, x, y, z);
                        dropItem(player, nbt, world, x, y, z, true);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ((ItemCraftingTool)equipped).takeDamage(equippedStack, 2) );
                    }
                    return true;
                }
            }
            return false;
        }

        if(!world.isRemote) {

            FMLNetworkHandler.openGui(player, WhonoMod.instance, 4, world, x, y, z);
        }
        return true;
    }

    public NBTTagCompound getTag(World world, int x, int y, int z) {

        return null;
    }

    public ArrayList<ItemStack> dropItem(EntityPlayer player, NBTTagCompound nbt, World world, int x, int  y, int z , boolean isDrop) {

        int meta = world.getBlockMetadata(x, y, z);

        ItemStack dropStack = new ItemStack(this, 1, meta);

        if (nbt != null && !nbt.hasNoTags()) {
            dropStack.setTagCompound(nbt);
        }
        world.setBlockToAir(x, y, z);

        if (isDrop) {
            float f = 0.3F;
            double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            Entity entityItem = new EntityItem(world, x + x2, y + y2, z + z2, dropStack);
            world.spawnEntityInWorld(entityItem);
        }

        ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
        dropList.add(dropStack);
        return dropList;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileEntityGenerator();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {

        int l = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEntityGenerator generator = (TileEntityGenerator)world.getTileEntity(x, y, z);
        int face = -1;

        switch (l) {
            case 0: face = 2; break;
            case 1: face = 5; break;
            case 2: face = 3; break;
            case 3: face = 4; break;
        }

        world.setBlockMetadataWithNotify(x, y, z, face, 2);
        generator.setFace((short)face);

        if (itemStack.hasDisplayName())
            generator.func_145951_a(itemStack.getDisplayName());
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

        TileEntityGenerator tileentityGenerator = (TileEntityGenerator)world.getTileEntity(x, y, z);

        if (tileentityGenerator != null){
            for (int i = 0; i < tileentityGenerator.getSizeInventory(); ++i){
                ItemStack itemstack = tileentityGenerator.getStackInSlot(i);

                if (itemstack != null){
                    float f = rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0){
                        int j = rand.nextInt(21) + 10;

                        if (j > itemstack.stackSize)
                            j = itemstack.stackSize;

                        itemstack.stackSize -= j;
                        EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());

                        float f3 = 0.05F;
                        entityitem.motionX = (float)rand.nextGaussian() * f3;
                        entityitem.motionY = (float)rand.nextGaussian() * f3 + 0.2F;
                        entityitem.motionZ = (float)rand.nextGaussian() * f3;
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }
            world.func_147453_f(x, y, z, block);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {

        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int size) {

        return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {

        return Item.getItemFromBlock(WMBlocks.generator);
    }

}
