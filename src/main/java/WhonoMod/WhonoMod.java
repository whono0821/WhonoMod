package WhonoMod;


import WhonoMod.Render.RenderCable;
import WhonoMod.block.WMBlocks;
import WhonoMod.fluid.WMFluids;
import WhonoMod.gui.WMCreativeTab;
import WhonoMod.gui.WMGuiHandler;
import WhonoMod.handler.HandlerManager;
import WhonoMod.integration.IntegrationManager;
import WhonoMod.item.WMItems;
import WhonoMod.recipe.AssemblerManager;
import WhonoMod.tile.WMTileEntities;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@Mod(modid = WhonoMod.MODID, name = WhonoMod.NAME, version = WhonoMod.VERSION)
public class WhonoMod {

    public static final String MODID = "whonomod";
    public static final String NAME = "Whono Mod";
    public static final String VERSION = "1.0.0";

    public static int renderCable;

    @Instance
    public static WhonoMod instance = new WhonoMod();

    public static CreativeTabs tabWM;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        HandlerManager.preInit();

        tabWM = new WMCreativeTab(NAME);

        WMFluids.preInit();
        WMBlocks.preInit();
        WMTileEntities.preInit();
        WMItems.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(this.instance, new WMGuiHandler());

        AssemblerManager.addRecipe(new ItemStack(Blocks.gold_block, 2), "ingotIron", 2, "oreIron", 4, 400);
        AssemblerManager.addRecipe(new ItemStack(Blocks.gold_block, 3), new ItemStack(Blocks.gold_ore), new ItemStack(Blocks.sand), 400);

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        WMFluids.init();
        WMItems.init();

        IntegrationManager.init();

        renderCable = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderCable());
    }

}
