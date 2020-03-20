package WhonoMod.tile;

import WhonoMod.block.BlockSampleMachine;
import WhonoMod.item.WMItems;
import WhonoMod.recipe.SampleMachineRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntitySampleMachine extends TileEntity implements ISidedInventory {
    private static final int[] slotsTop = new int[]{0};
    private static final int[] slotsBottom = new int[]{2, 1};
    private static final int[] slotsSides = new int[]{1};
    /**
     * The ItemStacks that hold the items currently being used in the sampleMachine
     */
    private ItemStack[] sampleMachineItemStacks = new ItemStack[3];
    /**
     * The number of ticks that the sampleMachine will keep burning
     */
    public int sampleMachineBurnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the sampleMachine burning for
     */
    public int currentItemBurnTime;
    /**
     * The number of ticks that the current item has been processing for
     */
    public int sampleMachineProcessTime;
    private String containerName;

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return sampleMachineItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1) {
        return sampleMachineItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (sampleMachineItemStacks[par1] != null) {
            ItemStack itemstack;

            if (sampleMachineItemStacks[par1].stackSize <= par2) {
                itemstack = sampleMachineItemStacks[par1];
                sampleMachineItemStacks[par1] = null;
                return itemstack;
            } else {
                itemstack = sampleMachineItemStacks[par1].splitStack(par2);

                if (sampleMachineItemStacks[par1].stackSize == 0)
                    sampleMachineItemStacks[par1] = null;

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
        if (sampleMachineItemStacks[par1] != null) {
            ItemStack itemstack = sampleMachineItemStacks[par1];
            sampleMachineItemStacks[par1] = null;
            return itemstack;
        } else
            return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        sampleMachineItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
            par2ItemStack.stackSize = getInventoryStackLimit();
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? containerName : "container.whonomod.sampleMachine";
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
        NBTTagList nbttaglist = par1.getTagList("Items", 10);
        sampleMachineItemStacks = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < sampleMachineItemStacks.length)
                sampleMachineItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        }

        sampleMachineBurnTime = par1.getShort("BurnTime");
        sampleMachineProcessTime = par1.getShort("ProcessTime");
        currentItemBurnTime = getItemBurnTime(sampleMachineItemStacks[1]);

        if (par1.hasKey("CustomName", 8))
            containerName = par1.getString("CustomName");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setShort("BurnTime", (short) sampleMachineBurnTime);
        par1.setShort("ProcessTime", (short) sampleMachineProcessTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < sampleMachineItemStacks.length; ++i)
            if (sampleMachineItemStacks[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                sampleMachineItemStacks[i].writeToNBT(nbttagcompound1);
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
        return sampleMachineProcessTime * par1 / 200;
    }

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int par1) {
        if (currentItemBurnTime == 0)
            currentItemBurnTime = 200;

        return sampleMachineBurnTime * par1 / currentItemBurnTime;
    }

    /**
     * Transmuator is transmuting
     */
    public boolean isTransmuting() {
        return sampleMachineBurnTime > 0;
    }

    @Override
    public void updateEntity() {
        boolean flag = sampleMachineBurnTime > 0;
        boolean flag1 = false;

        if (sampleMachineBurnTime > 0)
            --sampleMachineBurnTime;

        if (!worldObj.isRemote) {
            if (sampleMachineBurnTime == 0 && canProcess()) {
                currentItemBurnTime = sampleMachineBurnTime = getItemBurnTime(sampleMachineItemStacks[1]);

                if (sampleMachineBurnTime > 0) {
                    flag1 = true;

                    if (sampleMachineItemStacks[1] != null) {
                        --sampleMachineItemStacks[1].stackSize;

                        if (sampleMachineItemStacks[1].stackSize == 0)
                            sampleMachineItemStacks[1] = sampleMachineItemStacks[1].getItem().getContainerItem(sampleMachineItemStacks[1]);
                    }
                }
            }

            if (isTransmuting() && canProcess()) {
                ++sampleMachineProcessTime;

                if (sampleMachineProcessTime == 200) {
                    sampleMachineProcessTime = 0;
                    processItem();
                    flag1 = true;
                }
            } else
                sampleMachineProcessTime = 0;

            if (flag != sampleMachineBurnTime > 0) {
                flag1 = true;
                BlockSampleMachine.updateSampleMachineBlockState(sampleMachineBurnTime > 0, worldObj, xCoord, yCoord, zCoord);
            }
        }

        if (flag1)
            markDirty();
    }

    /**
     * Returns true if the sampleMachine can process an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canProcess() {
        if (sampleMachineItemStacks[0] == null)
            return false;
        else {
            ItemStack itemstack = SampleMachineRecipe.instance().getSampleDoingResult(sampleMachineItemStacks[0]);
            if (itemstack == null) return false;
            if (sampleMachineItemStacks[2] == null) return true;
            if (!sampleMachineItemStacks[2].isItemEqual(itemstack)) return false;
            int result = sampleMachineItemStacks[2].stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= sampleMachineItemStacks[2].getMaxStackSize();
        }
    }

    /**
     * Turn one item from the sampleMachine source stack into the appropriate processed item in the sampleMachine result stack
     */
    public void processItem() {
        if (canProcess()) {
            ItemStack itemstack = SampleMachineRecipe.instance().getSampleDoingResult(sampleMachineItemStacks[0]);

            if (sampleMachineItemStacks[2] == null)
                sampleMachineItemStacks[2] = itemstack.copy();
            else if (sampleMachineItemStacks[2].getItem() == itemstack.getItem())
                sampleMachineItemStacks[2].stackSize += itemstack.stackSize;

            --sampleMachineItemStacks[0].stackSize;

            if (sampleMachineItemStacks[0].stackSize <= 0)
                sampleMachineItemStacks[0] = null;
        }
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the sampleMachine burning, or 0 if the item isn't
     * fuel
     */
    public static int getItemBurnTime(ItemStack par1ItemStack) {
        if (par1ItemStack == null)
            return 0;
        else {
            Item item = par1ItemStack.getItem();

            if (item == WMItems.sample) return 10000;
            return TileEntityFurnace.getItemBurnTime(par1ItemStack);
        }
    }

    public static boolean isItemFuel(ItemStack par1ItemStack) {
        /**
         * Returns the number of ticks that the supplied fuel item will keep the sampleMachine burning, or 0 if the item isn't
         * fuel
         */
        return getItemBurnTime(par1ItemStack) > 0;
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
        return par1 == 2 ? false : par1 == 1 ? isItemFuel(par2ItemStack) : true;
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int par1) {
        return par1 == 0 ? slotsBottom : par1 == 1 ? slotsTop : slotsSides;
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
        return par3 != 0 || par1 != 1 || par2ItemStack.getItem() == Items.bucket;
    }
}
