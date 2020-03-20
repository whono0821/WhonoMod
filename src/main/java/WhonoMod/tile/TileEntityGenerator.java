package WhonoMod.tile;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.IPowerProvider;
import WhonoMod.api.PowerStorage;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGenerator extends TileEntity implements ISidedInventory, IPowerProvider {

    private ItemStack[] inventory = new ItemStack[1];
    private String containerName;

    private static final int TICK_PER_ENERGY = 1;
    private PowerStorage energy = new PowerStorage(60000, 0, 200);

    private short face;
    private boolean isActive;
    public int processMax;
    public int processRem;

    @Override
    public int getSizeInventory() {

        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return inventory[index];
    }

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

        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {

        if (inventory[index] != null) {

            ItemStack itemstack = inventory[index];
            inventory[index] = null;
            return itemstack;

        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack itemStack) {

        inventory[index] = itemStack;

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {

            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {

        return hasCustomInventoryName() ? containerName : "container.whonomod.generator";
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
        energy.readFromNBT(nbt);

        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {

            NBTTagCompound nbtTagCompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound1.getByte("Slot");

            if (b0 >= 0 && b0 < inventory.length) {

                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
            }
        }

        face = nbt.getShort("Face");
        isActive = nbt.getBoolean("isActive");
        processMax = nbt.getInteger("ProcessMax");
        processRem = nbt.getInteger("ProcessRem");

        if (nbt.hasKey("CustomName", 8)) {

            containerName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);
        energy.writeToNBT(nbt);

        nbt.setShort("Face", face);
        nbt.setBoolean("isActive", isActive);
        nbt.setInteger("ProcessMax", processMax);
        nbt.setInteger("ProcessRem", processRem);

        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; ++i) {

            if (inventory[i] != null) {

                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(nbtTagCompound1);
                nbttaglist.appendTag(nbtTagCompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);

        if (hasCustomInventoryName()) {

            nbt.setString("CustomName", containerName);
        }
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

        double stored = energy.getPowerStored();
        double max =energy.getMaxPowerStored();
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

        boolean wasActive = isActive;

        if (!worldObj.isRemote) {

            if (processRem > 0) {

                --processRem;
                energy.modifyPowerStored(TICK_PER_ENERGY);
            }
            if (processRem <= 0) {

                isActive = false;
                if (inventory[0] != null) {

                    int bufProcess = TileEntityFurnace.getItemBurnTime(inventory[0]);
                    if (bufProcess > 0) {

                        processMax = processRem = bufProcess;
                        inventory[0].stackSize--;
                        if (inventory[0].stackSize <= 0) {

                            inventory[0] = null;
                        }
                        isActive = true;
                    }
                }
            }
        }
        if (wasActive != isActive) {
            markDirty();
        }
    }

    @Override
    public void markDirty() {

        super.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

        return true;
    }

    @Override
    public Packet getDescriptionPacket() {

        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {

        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        readFromNBT(packet.func_148857_g());
    }

    private boolean hasEnergy(int energy) {

        return this.energy.getPowerStored() >= energy;
    }

    private boolean drainEnergy(int energy) {

        return hasEnergy(energy) && this.energy.extractPower(energy, false) == energy;
    }

    public void setEnergyStored(int energy) {

        this.energy.setPowerStored(energy);
    }

    public void setFace(short face) {

        this.face = face;
    }

    public short getFace() {

        return face;
    }

    public boolean isActive() {

        return isActive;
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

        return slot == 0;
    }

    /* IPowerReceiver */
    @Override
    public int extractPower(ForgeDirection from, int maxReceive, boolean simulate) {

        return energy.extractPower(maxReceive, simulate);
    }

    @Override
    public int getPowerStored(ForgeDirection from) {

        return energy.getPowerStored();
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {

        return energy.getMaxPowerStored();
    }

    // IPowerConnection
    @Override
    public boolean canConnectPower(ForgeDirection from) {

        return true;
    }
}
