package WhonoMod.tile;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.IPowerReceiver;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import WhonoMod.api.PowerStorage;
import WhonoMod.block.BlockAssembler;
import WhonoMod.recipe.AssemblerManager;
import WhonoMod.recipe.AssemblerRecipe;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAssembler extends TileEntity implements ISidedInventory, IPowerReceiver {

    private ItemStack[] inventory = new ItemStack[3];
    private String containerName;

    private PowerStorage energyStorage = new PowerStorage(60000, 200, 0);

    public int processMax;
    public int processRem;
    public boolean isActive;

    @Override
    public int getSizeInventory() { return inventory.length; }

    @Override
    public ItemStack getStackInSlot(int index) { return inventory[index]; }

    @Override
    public ItemStack decrStackSize(int index, int stackSize) {
        if (inventory[index] != null) {
            ItemStack itemstack;

            if (inventory[index].stackSize <= stackSize) {
                itemstack = inventory[index];
                inventory[index] = null;
                return itemstack;
            } else {
                itemstack = inventory[index].splitStack(stackSize);

                if (inventory[index].stackSize == 0)
                    inventory[index] = null;

                return itemstack;
            }
        } else
            return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (inventory[index] != null) {
            ItemStack itemstack = inventory[index];
            inventory[index] = null;
            return itemstack;
        } else
            return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack itemStack) {
        inventory[index] = itemStack;

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
            itemStack.stackSize = getInventoryStackLimit();
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? containerName : "container.whonomod.assembler";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return containerName != null && containerName.length() > 0;
    }

    public void func_145951_a(String name) {
        containerName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energyStorage.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbtTagCompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound1.getByte("Slot");

            if (b0 >= 0 && b0 < inventory.length)
                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
        }

        processMax = nbt.getInteger("ProcessMax");
        processRem = nbt.getInteger("ProcessRem");
        isActive = nbt.getBoolean("Active");

        if (nbt.hasKey("CustomName", 8))
            containerName = nbt.getString("CustomName");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        energyStorage.writeToNBT(nbt);

        nbt.setInteger("ProcessMax", processMax);
        nbt.setInteger("ProcessRem", processRem);
        nbt.setBoolean("Active", isActive);

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < inventory.length; ++i)
            if (inventory[i] != null) {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(nbtTagCompound1);
                nbttaglist.appendTag(nbtTagCompound1);
            }

        nbt.setTag("Items", nbttaglist);

        if (hasCustomInventoryName())
            nbt.setString("CustomName", containerName);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @SideOnly(Side.CLIENT)
    public int getProcessProgressScaled(int scale) {
        if (processRem <= 0)
            return 0;

        double rem = processMax - processRem;
        double max = processMax;
        double v = ((rem / max) * scale);
        return (int) v;
    }

    @SideOnly(Side.CLIENT)
    public int getScaledEnergyStored(int scale) {
        double stored = energyStorage.getPowerStored();
        double max =energyStorage.getMaxPowerStored();
        double v = ((stored / max) * scale);
        return (int) v;
    }

    @Override
    public void invalidate() {

        super.invalidate();

        MinecraftForge.EVENT_BUS.post(new PowerNetworkUnloadEvent(this));
    }

    @Override
    public void onChunkUnload() {

        super.onChunkUnload();
        invalidate();
    }

    @Override
    public void updateEntity() {

        if (!PowerNetwork.isContained(this) && !isInvalid()) {

            MinecraftForge.EVENT_BUS.post(new PowerNetworkLoadEvent(this));
        }

        if (worldObj.isRemote)
            return;

        if(isActive) {
            if (canProcess()) {
                if (processRem > 0) {
                    int energy = calcEnergy();
                    energyStorage.modifyPowerStored(-energy);
                    processRem -= energy;
                }
                if (canFinish()) {
                    processFinish();
                    energyStorage.modifyPowerStored(-processRem);

                    if (canProcess()) {
                        processStart();
                    } else {
                        isActive = false;
                    }
                }
            } else {
                isActive = false;
                processRem = 0;
                return;
            }
        } else {
            if (canProcess()) {
                processStart();
                int energy = calcEnergy();
                energyStorage.modifyPowerStored(-energy);
                processRem -= energy;
                isActive = true;
            }
        }
        if (isActive != canProcess()) {
            BlockAssembler.updateAssemblerState(isActive, worldObj, xCoord, yCoord, zCoord);
        }

    }

    private int calcEnergy() {

        if (!isActive)  return 0;
        if (energyStorage.getPowerStored() > 48000)
            return 80;
        if (energyStorage.getPowerStored() < 9600)
            return 8;
        return energyStorage.getPowerStored() / 960;
    }

    private boolean canFinish() {
        return processRem > 0 ? false : hasValidInput();
    }

    private boolean hasValidInput() {

        AssemblerRecipe recipe = AssemblerManager.getRecipe(inventory[0], inventory[1]);

        if (recipe == null) return false;

        int pSize = recipe.getPrimaryInput().stackSize;
        int sSize = recipe.getSecondaryInput().stackSize;

        if (AssemblerManager.isRecipeReversed(inventory[0], inventory[1])) {
            if (pSize > inventory[1].stackSize || sSize > inventory[0].stackSize)
                return false;
        } else {
            if (pSize > inventory[0].stackSize || sSize > inventory[1].stackSize)
                return false;
        }
        return true;
    }

    private void processFinish() {

        AssemblerRecipe recipe = AssemblerManager.getRecipe(inventory[0], inventory[1]);

        if (recipe == null) {
            isActive = false;
            processRem = 0;
            return;
        }
        ItemStack output = recipe.getOutput();

        if (inventory[2] == null) {
            inventory[2] = output;
        } else if (inventory[2].isItemEqual(output)) {
            if (inventory[2].stackSize + output.stackSize <= output.getMaxStackSize() && inventory[2].stackSize + output.stackSize <= getInventoryStackLimit()) {
                inventory[2].stackSize += output.stackSize;
            }
        }
        if (AssemblerManager.isRecipeReversed(inventory[0], inventory[1])) {
            inventory[1].stackSize -= recipe.getPrimaryInput().stackSize;
            inventory[0].stackSize -= recipe.getSecondaryInput().stackSize;
        } else {
            inventory[0].stackSize -= recipe.getPrimaryInput().stackSize;
            inventory[1].stackSize -= recipe.getSecondaryInput().stackSize;
        }
        if (inventory[0].stackSize <= 0) {
            inventory[0] = null;
        }
        if (inventory[1].stackSize <= 0) {
            inventory[1] = null;
        }
    }

    private boolean canProcess() {

        if (inventory[0] == null || inventory[1] == null) {
            return false;
        }

        AssemblerRecipe recipe = AssemblerManager.getRecipe(inventory[0], inventory[1]);

        if ( recipe == null || energyStorage.getPowerStored() < recipe.getEnergy()) {
            return false;
        }
        if (AssemblerManager.isRecipeReversed(inventory[0], inventory[1])) {
            if (recipe.getPrimaryInput().stackSize > inventory[1].stackSize || recipe.getSecondaryInput().stackSize > inventory[0].stackSize) {
                return false;
            }
        } else {
            if (recipe.getPrimaryInput().stackSize > inventory[0].stackSize || recipe.getSecondaryInput().stackSize > inventory[1].stackSize) {
                return false;
            }
        }
        if (inventory[2] == null) {
            return true;
        }

        ItemStack output = recipe.getOutput();

        if (!inventory[2].isItemEqual(output)) {
            return  false;
        }
        int result = inventory[2].stackSize + output.stackSize;
        return result <= getInventoryStackLimit() && result <= output.getMaxStackSize();
    }

    private void processStart() {
        AssemblerRecipe recipe = AssemblerManager.getRecipe(inventory[0], inventory[1]);
        processMax = recipe.getEnergy();
        processRem = processMax;
    }


    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return slot != 2 ? true : false;
    }

    private boolean hasEnergy(int energy) {
        return energyStorage.getPowerStored() >= energy;
    }

    private boolean drainEnergy(int energy) {
        return hasEnergy(energy) && energyStorage.extractPower(energy, false) == energy;
    }

    public void setEnergyStored(int energy) {
        energyStorage.setPowerStored(energy);
    }

    /* ISidedInventory */
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] ret = new int[getSizeInventory()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = i;
        }

        return ret;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return isItemValidForSlot(slot, itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return slot == 2 ? true : false;
    }

    /* IPowerReceiver */
    @Override
    public int receivePower(ForgeDirection from, int maxReceive, boolean simulate) {

        return energyStorage.receivePower(maxReceive, simulate);
    }

    @Override
    public int getPowerStored(ForgeDirection from) {

        return energyStorage.getPowerStored();
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {

        return energyStorage.getMaxPowerStored();
    }

    // IPowerConnection
    @Override
    public boolean canConnectPower(ForgeDirection from) {
        return true;
    }
}
