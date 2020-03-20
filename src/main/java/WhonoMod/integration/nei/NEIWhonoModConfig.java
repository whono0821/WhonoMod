package WhonoMod.integration.nei;

import WhonoMod.WhonoMod;
import WhonoMod.block.WMBlocks;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import net.minecraft.item.ItemStack;

public class NEIWhonoModConfig implements IConfigureNEI {

    @Override
    public void loadConfig(){
        API.registerRecipeHandler(new RFMachineRecipeHandler());
        API.registerUsageHandler(new RFMachineRecipeHandler());

        API.hideItem(new ItemStack(WMBlocks.rfMachine_on));
        API.hideItem(new ItemStack(WMBlocks.sampleMachine_on));
    }

    @Override
    public String getName()
    {
        return "Whono Mod NEI";
    }
    @Override
    public String getVersion()
    {
        return WhonoMod.VERSION;
    }
}
