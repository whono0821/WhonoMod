package WhonoMod.tile.generator;

import WhonoMod.api.IPowerProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCreativeGenerator extends TileEntity implements IPowerProvider {

    private static final int[] VOLTAGE_LEVEL = {8, 32, 128, 512, 2048, Integer.MAX_VALUE};
    private int output;

    public TileEntityCreativeGenerator() {
        super();
    }

    public TileEntityCreativeGenerator(int meta) {
        super();

        output = VOLTAGE_LEVEL[meta];
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        output = nbt.getInteger("output");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("output", output);
    }

    //IPowerReceiver
    @Override
    public int extractPower(ForgeDirection from, int maxExtract, boolean simulate) {
        return output;
    }

    @Override
    public int getPowerStored(ForgeDirection from) {
        return -1;
    }

    @Override
    public int getMaxPowerStored(ForgeDirection from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectPower(ForgeDirection from) {
        return true;
    }
}
