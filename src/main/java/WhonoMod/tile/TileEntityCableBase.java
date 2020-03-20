package WhonoMod.tile;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.IPowerHandler;
import WhonoMod.api.PowerStorage;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileEntityCableBase extends TileEntity implements IPowerHandler {

    private static final int[] POWER_CAPACITY_LEVEL = {20, 40, 80, 160};
    private static final int[] POWER_VOLTAGE_LEVEL = {32, 128, 512, 2048, -1};

    private short type = 0;
    private short color = 0;

    private TileEntityCableBase master;
    private boolean isMaster;
    private boolean firstRun = true;

    private PowerStorage power;


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

            List<TileEntityCableBase> list = new ArrayList<TileEntityCableBase>();
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

                TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
                if (tile instanceof TileEntityCableBase) {

                    TileEntityCableBase cable = (TileEntityCableBase)tile;
                    if (this.canConnectNetwork(cable)) {

                        list.add(cable);
                    }
                }
            }
            if (!list.isEmpty()) {

                if (!isMaster) {
                    power = master.getEnergy();
                }
                power.setPowerStored(power.getPowerStored() / list.size());
                for (TileEntityCableBase cable : list) {

                    cable.copyPowerStorage(power);
                    cable.master = null;
                    cable.initMultiBlock();
                }
            }
            if (power != null) {

                power = null;
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
        nbt.setShort("Type", type);
        nbt.setShort("Color", color);

        if (isMaster) {

            power.writeToNBT(nbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);

        isMaster = nbt.getBoolean("isMaster");
        type = nbt.getShort("Type");
        color = nbt.getShort("Color");

        if (isMaster) {

            power.readFromNBT(nbt);
        }
    }

    public boolean canConnectNetwork(TileEntityCableBase cable) {

        return this.type == cable.getType() && (this.color == 0 || this.color == cable.getColor());
    }

    public short getColor() {

        return color;
    }

    public short getType() {

        return type;
    }

    private void setMaster(TileEntityCableBase master, int cables) {

        boolean wasMaster = isMaster;
        isMaster = master == this;
        this.master = master;

        if (isMaster) {

            if (power == null) {

                power = new PowerStorage(POWER_CAPACITY_LEVEL[type], POWER_VOLTAGE_LEVEL[type]);
            }
            power.setCapacity(POWER_CAPACITY_LEVEL[type] * cables);
        }
        else if (wasMaster) {

            master.receivePower(ForgeDirection.UNKNOWN, power.getPowerStored(), false);
            power = null;
        }
    }

    private void initMultiBlock() {

        if(this.master == null || this.master.isInvalid()) {

            type = (short)(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) / 2);

            Set<TileEntityCableBase> connectedCables = new HashSet<TileEntityCableBase>();
            Stack<TileEntityCableBase> traversingCables = new Stack<TileEntityCableBase>();
            TileEntityCableBase master = this;
            traversingCables.add(this);

            while(!traversingCables.isEmpty()) {

                TileEntityCableBase cable = traversingCables.pop();
                if (cable.isMaster()) {

                    master = cable;
                }
                connectedCables.add(cable);

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

                    TileEntity tile = worldObj.getTileEntity(cable.xCoord + dir.offsetX, cable.yCoord + dir.offsetY, cable.zCoord + dir.offsetZ);
                    if (tile instanceof TileEntityCableBase) {

                        TileEntityCableBase bufCable = (TileEntityCableBase)tile;
                        if (cable.canConnectNetwork(bufCable)) {

                            traversingCables.add((TileEntityCableBase) tile);
                        }
                    }
                }
            }
            for (TileEntityCableBase cable : connectedCables) {

                cable.setMaster(master, connectedCables.size());
            }
        }
    }

    public boolean isMaster() {

        return isMaster;
    }

    public TileEntityCableBase getMaster() {

        initMultiBlock();
        return master;
    }

    public void copyPowerStorage(PowerStorage energy) {

        this.power = energy.copy();
    }

    public PowerStorage getEnergy() {

        initMultiBlock();
        return power;
    }

    //IPowerHandler
    @Override
    public int extractPower(ForgeDirection from, int maxExtract, boolean simulate) {

        return isMaster ? power.extractPower(maxExtract, simulate) : getMaster().extractPower(from, maxExtract, simulate);
    }

    @Override
    public int receivePower(ForgeDirection from, int maxReceive, boolean simulate) {

        return isMaster ? power.receivePower(maxReceive, simulate) : getMaster().receivePower(from, maxReceive, simulate);
    }

    @Override
    public int getPowerStored(ForgeDirection from) {

        return isMaster ? power.getPowerStored() : getMaster().getPowerStored(from);
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {

        return isMaster ? power.getMaxPowerStored() : getMaster().getMaxPowerStored(from);
    }

    @Override
    public boolean canConnectPower(ForgeDirection from) {

        TileEntity tile = worldObj.getTileEntity(xCoord + from.offsetX, yCoord + from.offsetY, zCoord + from.offsetZ);
        if (tile instanceof TileEntityCableBase) {

            TileEntityCableBase cable = (TileEntityCableBase)tile;
            return cable.getColor() == this.color || cable.getColor() == 0;
        }
        else {

            return true;
        }
    }
}
