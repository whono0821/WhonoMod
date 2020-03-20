package WhonoMod.integration.waila;

import WhonoMod.api.IPowerHandler;
import WhonoMod.api.IPowerProvider;
import WhonoMod.api.IPowerReceiver;
import WhonoMod.tile.TileEntityCable;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaCompatModule {

    public static void preInit() {

    }

    public static void init() {

        FMLInterModComms.sendMessage("Waila", "register", "WhonoMod.integration.waila.WailaCompatModule.callbackRegister");
    }

    public static void postInit() {


    }

    public static void callbackRegister(IWailaRegistrar registrar) {

        registrar.registerBodyProvider(new HUDHandlerIPowerHandler(), IPowerReceiver.class);
        registrar.registerNBTProvider(new HUDHandlerIPowerHandler(), IPowerReceiver.class);
        registrar.registerBodyProvider(new HUDHandlerIPowerHandler(), IPowerProvider.class);
        registrar.registerNBTProvider(new HUDHandlerIPowerHandler(), IPowerProvider.class);
    }
}
