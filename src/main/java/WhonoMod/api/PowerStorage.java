package WhonoMod.api;

import net.minecraft.nbt.NBTTagCompound;

public class PowerStorage implements IPowerStorage{

    protected int power;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public PowerStorage(int capacity) {

        this(capacity, capacity, capacity);
    }

    public PowerStorage(int capacity, int maxTransfer) {

        this(capacity, maxTransfer, maxTransfer);
    }

    public PowerStorage(int capacity, int maxReceive, int maxExtract) {

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public PowerStorage readFromNBT(NBTTagCompound nbt) {

        this.power = nbt.getInteger("Power");

        if (power > capacity) {
            power = capacity;
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        if (power < 0) {
            power = 0;
        }
        nbt.setInteger("Power", power);
        return nbt;
    }

    public void setCapacity(int capacity) {

        this.capacity = capacity;

        if (power > capacity) {
            power = capacity;
        }
    }

    public void setMaxTransfer(int maxTransfer) {

        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
    }

    public void setMaxReceive(int maxReceive) {

        this.maxReceive = maxReceive;
    }

    public void setMaxExtract(int maxExtract) {

        this.maxExtract = maxExtract;
    }

    public int getMaxReceive() {

        return maxReceive;
    }

    public int getMaxExtract() {

        return maxExtract;
    }

    public void setPowerStored(int power) {

        this.power = power;

        if (this.power > capacity) {
            this.power = capacity;
        } else if (this.power < 0) {
            this.power = 0;
        }
    }

    public void modifyPowerStored(int power) {

        this.power += power;

        if (this.power > capacity) {
            this.power = capacity;
        } else if (this.power < 0) {
            this.power = 0;
        }
    }

    public PowerStorage copy() {

        PowerStorage copy = new PowerStorage(capacity, maxReceive, maxExtract);

        if (power > 0) {

            copy.setPowerStored(power);
        }
        return copy;
    }

    /* IPowerStorage */
    @Override
    public int receivePower(int maxReceive, boolean simulate) {

        int powerReceived = Math.min(capacity - power, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            power += powerReceived;
        }
        return powerReceived;
    }

    @Override
    public int extractPower(int maxExtract, boolean simulate) {

        int powerExtracted = Math.min(power, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            power -= powerExtracted;
        }
        return powerExtracted;
    }

    @Override
    public int getPowerStored() {

        return power;
    }

    @Override
    public int getMaxPowerStored() {

        return capacity;
    }
}
