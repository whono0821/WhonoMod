package WhonoMod.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerProvider extends IPowerConnection{

    int extractPower(ForgeDirection from, int maxExtract, boolean simulate);

    int getPowerStored(ForgeDirection from);

    int getMaxPowerStored(ForgeDirection from);
}
