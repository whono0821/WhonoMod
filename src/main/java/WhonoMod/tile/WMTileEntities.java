package WhonoMod.tile;

import WhonoMod.tile.generator.TileEntityCreativeGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class WMTileEntities {

    public static void preInit() {

        GameRegistry.registerTileEntity(TileEntitySampleChest.class, "tileEntitySampleChest");
        GameRegistry.registerTileEntity(TileEntitySampleMachine.class, "tileEntitySampleMachine");
        GameRegistry.registerTileEntity(TileEntityRFMachine.class, "tileEntityRFMachine");
        GameRegistry.registerTileEntity(TileEntityAssembler.class, "tileEntityAssembler");
        GameRegistry.registerTileEntity(TileEntityCableBase.class, "tileEntityCable");
        GameRegistry.registerTileEntity(TileEntityGenerator.class, "tileEntityGenerator");
        GameRegistry.registerTileEntity(TileEntityCreativeGenerator.class, "tileEntityCreativeGenerator");
    }

    public static void init() {

    }

    public static void postInit() {

    }

}
