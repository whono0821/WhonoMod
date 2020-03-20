package WhonoMod.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerHandler extends IPowerProvider, IPowerReceiver{

    @Override
    int receivePower(ForgeDirection from, int maxReceive, boolean simulate);

    @Override
    int extractPower(ForgeDirection from, int maxExtract, boolean simulate);

    @Override
    int getPowerStored(ForgeDirection from);

    @Override
    int getMaxPowerStored(ForgeDirection from);
}
