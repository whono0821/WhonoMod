package WhonoMod.api;

public interface IPowerStorage {

    int receivePower(int maxReceive, boolean simulate);

    int extractPower(int maxExtract, boolean simulate);

    int getPowerStored();

    int getMaxPowerStored();
}
