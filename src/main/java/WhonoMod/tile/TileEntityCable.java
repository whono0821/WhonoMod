package WhonoMod.tile;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.IPowerHandler;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import WhonoMod.api.PowerStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityCable extends TileEntity implements IPowerHandler {

    private static final int CAPACITY_PER_BLOCK = 200;
    private static final int MAX_TRANSFER = 128;

    private TileEntityCable master;
    private boolean isMaster;
    private boolean firstRun = true;

    private PowerStorage energy = new PowerStorage(CAPACITY_PER_BLOCK, MAX_TRANSFER);


    @Override
    public void updateEntity() {

        if (!PowerNetwork.isContained(this) && !isInvalid()) {

            MinecraftForge.EVENT_BUS.post(new PowerNetworkLoadEvent(this));
        }

        if (!worldObj.isRemote) {

            if (firstRun) {

                initMultiBlock();
                firstRun = false;
            }
        }
    }

    @Override
    public void invalidate() {

        super.invalidate();

        MinecraftForge.EVENT_BUS.post(new PowerNetworkUnloadEvent(this));

        if (!worldObj.isRemote) {

            List<TileEntityCable> list = new ArrayList<TileEntityCable>();
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

                TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
                if (tile instanceof TileEntityCable) {

                    list.add((TileEntityCable)tile);
                }
            }
            if (!list.isEmpty()) {

                if (!isMaster) {
                    energy = master.getEnergy();
                }
                energy.setPowerStored(energy.getPowerStored() / list.size());
                for (TileEntityCable cable : list) {

                    cable.copyPowerStorage(energy);
                    cable.master = null;
                    cable.initMultiBlock();
                }
            }
            if (energy != null) {

                energy = null;
            }
        }

    }

    @Override
    public void onChunkUnload() {

        invalidate();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);

        nbt.setBoolean("isMaster", isMaster);

        if (isMaster) {

            energy.writeToNBT(nbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);

        isMaster = nbt.getBoolean("isMaster");

        if (isMaster) {

            energy.readFromNBT(nbt);
        }
    }

    private void setMaster(TileEntityCable master, int cables) {

        boolean wasMaster = isMaster;
        isMaster = master == this;
        this.master = master;

        if (isMaster) {

            if (energy == null) {

                energy = new PowerStorage(CAPACITY_PER_BLOCK, MAX_TRANSFER);
            }
            energy.setCapacity(CAPACITY_PER_BLOCK * cables);
        }
        else if (wasMaster) {

            master.receivePower(ForgeDirection.UNKNOWN, energy.getPowerStored(), false);
            energy = null;
        }
    }

    private void initMultiBlock() {

        if(this.master == null || this.master.isInvalid()) {

            Set<TileEntityCable> connectedCables = new HashSet<TileEntityCable>();
            Stack<TileEntityCable> traversingCables = new Stack<TileEntityCable>();
            TileEntityCable master = this;
            traversingCables.add(this);

            while(!traversingCables.isEmpty()) {

                TileEntityCable cable = traversingCables.pop();
                if (cable.isMaster()) {

                    master = cable;
                }
                connectedCables.add(cable);

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

                    TileEntity tile = worldObj.getTileEntity(cable.xCoord + dir.offsetX, cable.yCoord + dir.offsetY, cable.zCoord + dir.offsetZ);
                    if (tile instanceof TileEntityCable && !connectedCables.contains(tile)) {

                        traversingCables.add((TileEntityCable)tile);
                    }
                }
            }
            for (TileEntityCable cable : connectedCables) {

                cable.setMaster(master, connectedCables.size());
            }
        }
    }

    public boolean isMaster() {
        return isMaster;
    }

    public TileEntityCable getMaster() {

        initMultiBlock();
        return master;
    }

    public void copyPowerStorage(PowerStorage energy) {

        this.energy = energy.copy();
    }

    public PowerStorage getEnergy() {

        initMultiBlock();
        return energy;
    }

    //  IPowerHandler
    @Override
    public boolean canConnectPower(ForgeDirection from) {

        return true;
    }

    @Override
    public int extractPower(ForgeDirection from, int maxExtract, boolean simulate) {

        return isMaster ? energy.extractPower(maxExtract, simulate) : getMaster().extractPower(from, maxExtract, simulate);
    }

    @Override
    public int receivePower(ForgeDirection from, int maxReceive, boolean simulate) {

        return isMaster ? energy.receivePower(maxReceive, simulate) : getMaster().receivePower(from, maxReceive, simulate);
    }

    @Override
    public int getPowerStored(ForgeDirection from) {

        return isMaster ? energy.getPowerStored() : getMaster().getPowerStored(from);
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {

        return isMaster ? energy.getMaxPowerStored() : getMaster().getMaxPowerStored(from);
    }
}
