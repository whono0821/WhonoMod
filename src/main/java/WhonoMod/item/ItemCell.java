package WhonoMod.item;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.ArrayList;

public class ItemCell extends ItemBase {

    private ArrayList<String> fluidName = new ArrayList<String>();

    public ItemCell(String name) {

        super(name);
    }

    private static int getMatchingSlot(EntityPlayer player, ItemStack stack) {

        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            ItemStack slotStack = player.inventory.getStackInSlot(slot);

            if (slotStack == null) {
                return slot;
            }

            if (!slotStack.isItemEqual(stack)) {
                continue;
            }

            int space = slotStack.getMaxStackSize() - slotStack.stackSize;
            if (space >= stack.stackSize) {
                return slot;
            }
        }

        return -1;
    }

    @Override
    public ItemStack addItem(int meta, ItemData itemData) {

        fluidName.add(itemData.name);
        return super.addItem(meta, itemData);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {

        if (world.isRemote) {
            return itemStack;
        }

        if (itemStack.getItemDamage() != 0) {
            return itemStack;
        }

        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;
            Block target = world.getBlock(x, y, z);

            FluidStack fluid = null;

            if (target instanceof IFluidBlock) {
                fluid = ((IFluidBlock) target).drain(world, x, y, z, false);
            } else if (target == Blocks.water || target == Blocks.flowing_water) {
                fluid = new FluidStack(FluidRegistry.WATER, 1000);
            } else if (target == Blocks.lava || target == Blocks.flowing_lava) {
                fluid = new FluidStack(FluidRegistry.LAVA, 1000);
            } else {
                return itemStack;
            }


            if (!fluidName.contains(fluid.getFluid().getName())) {
                return itemStack;
            }

            ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluid, itemStack);
            if (filledContainer == null) {
                return itemStack;
            }

            int slot = getMatchingSlot(player, filledContainer);
            if (slot < 0) {
                return itemStack;
            }

            if (player.inventory.getStackInSlot(slot) == null) {
                player.inventory.setInventorySlotContents(slot, filledContainer.copy());
            } else {
                player.inventory.getStackInSlot(slot).stackSize++;
            }

            if (target instanceof IFluidBlock) {
                ((IFluidBlock) target).drain(world, x, y, z, true);
            } else {
                world.setBlockToAir(x, y, z);
            }

            itemStack.stackSize--;

            return itemStack;
        }

        return itemStack;
    }


}
