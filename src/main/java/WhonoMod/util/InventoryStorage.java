package WhonoMod.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryStorage implements IInventory {

    private final ItemStack[] contents;
    private final String name;
    private final int stackLimit;
    private int[] outputSlot;
    private int[] inputSlot;

    public InventoryStorage(String name, int size, int stackLimit) {

        this.name = name;
        this.stackLimit = stackLimit;
        this.contents = new ItemStack[size];
        this.outputSlot = null;
        this.inputSlot = null;
    }

    public void setOutputSlot(int slot, int size) {

        outputSlot = new int[size];
        for (int i = 0; i < size; ++i) {

            outputSlot[i] = slot + i;
        }
    }

    public int[] getOutputSlot() {

        return outputSlot;
    }

    public void setInputSlot(int slot, int size) {

        inputSlot = new int[size];
        for (int i = 0; i < size; ++i) {

            inputSlot[i] = slot + i;
        }
    }

    public int[] getInputSlot() {

        return inputSlot;
    }

    public void readFromNBT(NBTTagCompound nbt) {

        NBTTagList nbtTagList = nbt.getTagList("Items", 10);

        for (int i = 0; i < nbtTagList.tagCount(); i++) {

            NBTTagCompound tag = nbtTagList.getCompoundTagAt(i);
            byte b0 = tag.getByte("Slot");
            if (0 <= b0 && b0 < contents.length) {

                contents[b0] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {

        NBTTagList nbtTagList = new NBTTagList();

        for (int i = 0; i < contents.length; ++i) {

            if (contents[i] != null) {

                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                contents[i].writeToNBT(tag);
                nbtTagList.appendTag(tag);
            }
        }
        nbt.setTag("Items", nbtTagList);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {

        return contents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int stackSize) {

        if (contents[slot] != null) {

            ItemStack itemStack;

            if (contents[slot].stackSize <= stackSize) {

                itemStack = contents[slot];
                contents[slot] = null;

                return itemStack;
            }
            else {

                itemStack = contents[slot].splitStack(stackSize);

                if (contents[slot].stackSize == 0) {
                    contents[slot] = null;
                }
                return itemStack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {

        if (contents[slot] != null) {

            ItemStack itemStack = contents[slot];
            contents[slot] = null;

            return itemStack;
        }
        else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {

        contents[slot] = itemStack;

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {

            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {

        return name;
    }

    @Override
    public boolean hasCustomInventoryName() {

        return true;
    }

    @Override
    public int getInventoryStackLimit() {

        return stackLimit;
    }

    @Deprecated
    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {

        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {

        if (outputSlot == null) return true;

        for (int index : outputSlot) {

            if (index == slot) return false;
        }
        return true;
    }

    @Override
    public int getSizeInventory() {

        return contents.length;
    }
}
