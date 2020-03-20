package WhonoMod.api;

import net.minecraft.item.ItemStack;

public interface IPowerContainerItem {

    int receivePower(ItemStack container, int maxReceive, boolean simulate);
    
    int extractPower(ItemStack container, int maxExtract, boolean simulate);

    int getPowerStored(ItemStack container);

    int getMaxPowerStored(ItemStack container);
}
