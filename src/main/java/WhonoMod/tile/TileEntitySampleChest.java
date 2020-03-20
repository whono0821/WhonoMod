package WhonoMod.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySampleChest extends TileEntity implements IInventory {

    protected ItemStack[] itemStacks = new ItemStack[54];

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] == null)
                continue;
            NBTTagCompound nbt1 = new NBTTagCompound();
            nbt1.setByte("Slot", (byte) i);
            itemStacks[i].writeToNBT(nbt1);
            nbttaglist.appendTag(nbt1);
        }
        nbt.setTag("Items", nbttaglist);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        itemStacks = new ItemStack[54];
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbt1.getByte("Slot");
            if (0 <= b0 && b0 < itemStacks.length) {
                itemStacks[b0] = ItemStack.loadItemStackFromNBT(nbt1);
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return 54;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (itemStacks[slot] == null)
            return null;
        ItemStack itemstack;
        if (itemStacks[slot].stackSize <= amount) {
            itemstack = itemStacks[slot];
            itemStacks[slot] = null;
            return itemstack;
        }
        itemstack = itemStacks[slot].splitStack(amount);
        if (itemStacks[slot].stackSize < 1) {
            itemStacks[slot] = null;
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        itemStacks[slot] = itemStack;
        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public String getInventoryName() {
        return "container.WhonoMod.SampleChest";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return true;
    }

}
