package WhonoMod.handler;

import WhonoMod.PowerSystem.PowerNetwork;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkLoadEvent;
import WhonoMod.api.PowerNetworkEvent.PowerNetworkUnloadEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventHandler {

    private static int cycleTime = 0;


    public EventHandler() {

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static int getCycleTime() {

        return cycleTime;
    }

    @SubscribeEvent
    public void onPowerNetworkLoad(PowerNetworkLoadEvent event) {

        PowerNetwork.addNetwork(event.tileEntity, event.world);
    }

    @SubscribeEvent
    public void onPowerNetworkUnload(PowerNetworkUnloadEvent event) {

        PowerNetwork.removeNetwork(event.tileEntity, event.world);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        World world = event.world;

        if (event.phase == TickEvent.Phase.START) {

            cycleTime++;
            if (cycleTime == 20) cycleTime = 0;

        } else {

            PowerNetwork.onTickEnd(event.world);
        }
    }
}
