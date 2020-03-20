package WhonoMod.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.WorldEvent;

public class PowerNetworkEvent extends WorldEvent {

    public final TileEntity tileEntity;

    public PowerNetworkEvent(TileEntity tileEntity) {
        super(tileEntity.getWorldObj());

        this.tileEntity = tileEntity;
    }

    public static class PowerNetworkLoadEvent extends PowerNetworkEvent {

        public PowerNetworkLoadEvent(TileEntity tileEntity) {
            super(tileEntity);
        }
    }

    public static class PowerNetworkUnloadEvent extends PowerNetworkEvent {

        public PowerNetworkUnloadEvent(TileEntity tileEntity) {
            super(tileEntity);
        }
    }
}
