package WhonoMod.tile;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.IPowerContainerItem;
import WhonoMod.api.IPowerReceiver;
import WhonoMod.api.PowerStorage;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import WhonoMod.handler.EventHandler;
import WhonoMod.util.InventoryStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityMachineBase extends TileEntity implements ISidedInventory, IPowerReceiver {

    protected static final int[] POWER_CAPACITY_LEVEL = {2000, 4000, 8000, 16000};
    protected static final int[] POWER_VOLTAGE_LEVEL = {32, 128, 512, 2048, -1};

    protected static final float PROCESS_TIME = 0.7f;
    protected static final float POWER_CONSUME = 1.6f;

    protected byte[] outputSide = {-1, -1, -1, -1, -1, -1};
    protected byte[] inputSide = {-1, -1, -1, -1, -1, -1};

    protected boolean autoOutput = false;
    protected boolean autoInput = false;

    private short face;

    protected PowerStorage power;
    protected InventoryStorage inventory;

    protected int processMax;
    protected int processRem;
    protected boolean isActive;
    protected boolean wasActive;

    protected int processTimeLevel = 1;
    protected int powerConsumeLevel = 1;
    protected int powerCapacityLevel = 1;
    protected int powerVoltageLevel = 1;


    TileEntityMachineBase() {

        power = new PowerStorage(POWER_CAPACITY_LEVEL[powerCapacityLevel], POWER_VOLTAGE_LEVEL[powerVoltageLevel], 0);
    }

    @Override
    public void onChunkUnload() {

        super.onChunkUnload();

        invalidate();
    }

    @Override
    public Packet getDescriptionPacket() {

        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

        super.onDataPacket(net, pkt);

        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void markDirty() {

        super.markDirty();

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void invalidate() {

        super.invalidate();

        MinecraftForge.EVENT_BUS.post(new PowerNetworkUnloadEvent(this));
    }

    @Override
    public void updateEntity() {

        super.updateEntity();

        if (!PowerNetwork.isContained(this) && !isInvalid()) {

            MinecraftForge.EVENT_BUS.post(new PowerNetworkLoadEvent(this));
        }

        if (!worldObj.isRemote) {

            wasActive = isActive;

            if (isActive) {

                if (canProcess()) {

                    int power = calcPower();
                    int consumePower = (int)Math.floor(power * Math.pow(POWER_CONSUME, powerConsumeLevel));
                    int processTime = (int)Math.floor(power / Math.pow(PROCESS_TIME, processTimeLevel));
                    this.power.modifyPowerStored(consumePower);
                    processRem -= processTime;
                }
                if (canFinish()) {

                    processFinish();
                    isActive = false;
                }

            }
            if (!isActive) {

                if (canProcess()) {

                    processStart();
                    isActive = true;
                }
            }
            chargePower();
        }

        if (checkTime()) {

            transferInput();
            transferOutput();
        }

        if (isActive != wasActive) {

            markDirty();
        }
    }

    protected boolean canFinish() {

        return processRem > 0 ? false :hasValidInput();
    }

    protected abstract int calcPower();
    protected abstract boolean canProcess();
    protected abstract boolean hasValidInput();
    protected abstract void processStart();
    protected abstract void processFinish();
    protected abstract void transferInput();
    protected abstract void transferOutput();

    protected boolean checkTime() {

        return EventHandler.getCycleTime() % 10 == 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);

        power.readFromNBT(nbt);
        inventory.readFromNBT(nbt);

        autoInput = nbt.getBoolean("AutoInput");
        autoOutput = nbt.getBoolean("AutoOutput");

        face = nbt.getShort("Face");

        processMax = nbt.getInteger("ProcessMax");
        processRem = nbt.getInteger("ProcessRem");
        isActive = nbt.getBoolean("IsActive");
        wasActive = nbt.getBoolean("WasActive");

        processTimeLevel = nbt.getInteger("ProcessTimeLevel");
        powerConsumeLevel = nbt.getInteger("PowerConsumeLevel");
        powerCapacityLevel = nbt.getInteger("PowerCapacityLevel");
        powerVoltageLevel = nbt.getInteger("PowerVoltageLevel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);

        power.writeToNBT(nbt);
        inventory.writeToNBT(nbt);

        nbt.setBoolean("AutoInput", autoInput);
        nbt.setBoolean("AutoOutput", autoOutput);

        nbt.setShort("Face", face);

        nbt.setInteger("ProcessMax", processMax);
        nbt.setInteger("ProcessRem", processRem);
        nbt.setBoolean("IsActive", isActive);
        nbt.setBoolean("WasActive", wasActive);

        nbt.setInteger("ProcessTimeLevel", processTimeLevel);
        nbt.setInteger("PowerConsumeLevel", powerConsumeLevel);
        nbt.setInteger("PowerCapacityLevel", powerCapacityLevel);
        nbt.setInteger("PowerVoltageLevel", powerVoltageLevel);
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
    public int getScaledPowerStored(int scale) {

        double stored = power.getPowerStored();
        double max = power.getMaxPowerStored();
        double v = ((stored / max) * scale);
        return (int) v;
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

    public int getChargeSlot() {

        return inventory.getSizeInventory() - 1;
    }

    public boolean hasChargeSlot() {

        return true;
    }

    protected boolean hasPower(int power) {

        return this.power.getPowerStored() >= power;
    }

    protected boolean drainPower(int power) {

        return hasPower(power) &&  this.power.extractPower(power, false) == power;
    }

    public final void setPowerStored(int quantity) {

        power.setPowerStored(quantity);
    }

    protected void chargePower() {

        int chargeSlot = getChargeSlot();
        ItemStack itemStack = getStackInSlot(chargeSlot);

        if (hasChargeSlot() && itemStack.getItem() instanceof IPowerContainerItem) {

            IPowerContainerItem powerContainerItem = (IPowerContainerItem)itemStack.getItem();
            int powerRequest = Math.min(power.getMaxReceive(), power.getMaxPowerStored() - power.getPowerStored());
            power.receivePower(powerContainerItem.extractPower(itemStack, powerRequest, false), false);

            if (itemStack.stackSize <= 0) {

                inventory.setInventorySlotContents(chargeSlot, null);
            }
        }
    }

    //ISideInventory
    @Override
    public int[] getAccessibleSlotsFromSide(int slot) {

        int[] ret = new int[getSizeInventory()];
        for (int i = 0; i < getSizeInventory() - 1; ++i) {

            ret[i] = i;
        }

        return ret;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {

        int[] input = inventory.getInputSlot();
        for (int index : input) {

            if (index == slot) return true;
        }

        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {

        int[] output = inventory.getOutputSlot();
        for (int index : output) {

            if (index == slot) return true;
        }

        return false;
    }

    @Override
    public int getSizeInventory() {

        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {

        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int stackSize) {

        return inventory.decrStackSize(slot, stackSize);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {

        return inventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {

        inventory.setInventorySlotContents(slot, itemStack);
    }

    @Override
    public String getInventoryName() {

        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {

        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {

        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {

        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {

        inventory.openInventory();
    }

    @Override
    public void closeInventory() {

        inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {

        return inventory.isItemValidForSlot(slot, itemStack);
    }

    //IPowerReceiver
    @Override
    public int receivePower(ForgeDirection from, int maxReceive, boolean simulate) {

        if (!simulate) {

            if (maxReceive > POWER_VOLTAGE_LEVEL[powerVoltageLevel] && POWER_VOLTAGE_LEVEL[powerVoltageLevel] > 0) {

                worldObj.createExplosion(null, xCoord, yCoord, zCoord, 3.0f, true);
            }
        }
        return power.receivePower(maxReceive, simulate);
    }

    @Override
    public int getPowerStored(ForgeDirection from) {

        return power.getPowerStored();
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {

        return power.getMaxPowerStored();
    }

    @Override
    public boolean canConnectPower(ForgeDirection from) {

        return true;
    }
}
