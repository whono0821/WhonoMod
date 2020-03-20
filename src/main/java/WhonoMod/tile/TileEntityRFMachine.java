package WhonoMod.tile;

import WhonoMod.block.BlockRFMachine;
import WhonoMod.recipe.SampleMachineRecipe;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityRFMachine extends TileEntity implements ISidedInventory, IEnergyReceiver {
    /**
     * The ItemStacks that hold the items currently being used in the RFMachine
     */
    private ItemStack[] RFMachineItemStacks = new ItemStack[2];
    /**
     * The number of ticks that the current item has been processing for
     */
    public int RFMachineProcessTime;
    private String containerName;

    private EnergyStorage energyStorage = new EnergyStorage(10000, 100, 0);

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return RFMachineItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1) {
        return RFMachineItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (RFMachineItemStacks[par1] != null) {
            ItemStack itemstack;

            if (RFMachineItemStacks[par1].stackSize <= par2) {
                itemstack = RFMachineItemStacks[par1];
                RFMachineItemStacks[par1] = null;
                return itemstack;
            } else {
                itemstack = RFMachineItemStacks[par1].splitStack(par2);

                if (RFMachineItemStacks[par1].stackSize == 0)
                    RFMachineItemStacks[par1] = null;

                return itemstack;
            }
        } else
            return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (RFMachineItemStacks[par1] != null) {
            ItemStack itemstack = RFMachineItemStacks[par1];
            RFMachineItemStacks[par1] = null;
            return itemstack;
        } else
            return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        RFMachineItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
            par2ItemStack.stackSize = getInventoryStackLimit();
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? containerName : "container.whonomod.RFMachine";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return containerName != null && containerName.length() > 0;
    }

    public void func_145951_a(String par1) {
        containerName = par1;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        energyStorage.readFromNBT(par1);
        NBTTagList nbttaglist = par1.getTagList("Items", 10);
        RFMachineItemStacks = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < RFMachineItemStacks.length)
                RFMachineItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        }

        RFMachineProcessTime = par1.getShort("ProcessTime");

        if (par1.hasKey("CustomName", 8))
            containerName = par1.getString("CustomName");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        energyStorage.writeToNBT(par1);
        par1.setShort("ProcessTime", (short) RFMachineProcessTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < RFMachineItemStacks.length; ++i)
            if (RFMachineItemStacks[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                RFMachineItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }

        par1.setTag("Items", nbttaglist);

        if (hasCustomInventoryName())
            par1.setString("CustomName", containerName);
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    @SideOnly(Side.CLIENT)
    public int getProcessProgressScaled(int par1) {
        return RFMachineProcessTime * par1 / 200;
    }
    

    private boolean isEnergyStoring() {
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    public void updateEntity() {
        boolean flag = isEnergyStoring() && canProcess();
        boolean flag1 = false;

        if (!worldObj.isRemote) {

            if (isEnergyStoring() && canProcess()) {
                energyStorage.setEnergyStored(energyStorage.getEnergyStored() - 1);
                ++RFMachineProcessTime;

                if (RFMachineProcessTime == 200) {
                    RFMachineProcessTime = 0;
                    processItem();
                    flag1 = true;
                }
            } else
                RFMachineProcessTime = 0;

            if (flag != isEnergyStoring() && canProcess()) {
                flag1 = true;
                BlockRFMachine.updateRFMachineBlockState(isEnergyStoring() && canProcess(), worldObj, xCoord, yCoord, zCoord);
            }
        }

        if (flag1)
            markDirty();
    }

    /**
     * Returns true if the RFMachine can process an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canProcess() {
        if (RFMachineItemStacks[0] == null)
            return false;
        else {
            ItemStack itemstack = SampleMachineRecipe.instance().getSampleDoingResult(RFMachineItemStacks[0]);
            if (itemstack == null) return false;
            if (RFMachineItemStacks[1] == null) return true;
            if (!RFMachineItemStacks[1].isItemEqual(itemstack)) return false;
            int result = RFMachineItemStacks[1].stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= RFMachineItemStacks[1].getMaxStackSize();
        }
    }

    /**
     * Turn one item from the RFMachine source stack into the appropriate processed item in the RFMachine result stack
     */
    private void processItem() {
        if (canProcess()) {
            ItemStack itemstack = SampleMachineRecipe.instance().getSampleDoingResult(RFMachineItemStacks[0]);

            if (RFMachineItemStacks[1] == null)
                RFMachineItemStacks[1] = itemstack.copy();
            else if (RFMachineItemStacks[1].getItem() == itemstack.getItem())
                RFMachineItemStacks[1].stackSize += itemstack.stackSize;

            --RFMachineItemStacks[0].stackSize;

            if (RFMachineItemStacks[0].stackSize <= 0)
                RFMachineItemStacks[0] = null;
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : par1EntityPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
        return par1 == 0;
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int par1) {
        int[] ret = new int[getSizeInventory()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = i;
        }

            return ret;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
        return isItemValidForSlot(par1, par2ItemStack);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
        return par1 != 0;
    }


    /* IEnergyReceiver */
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {

        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {

        return energyStorage.getMaxEnergyStored();
    }

    // IEnergyConnection
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
