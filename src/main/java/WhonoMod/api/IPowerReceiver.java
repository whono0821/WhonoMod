package WhonoMod.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerReceiver extends IPowerConnection{

    int receivePower(ForgeDirection from, int maxReceive, boolean simulate);

    int getPowerStored(ForgeDirection from);

    int getMaxPowerStored(ForgeDirection from);
}
