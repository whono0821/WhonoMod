package WhonoMod.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class WMItems {

    public static ItemBase itemMaterial;
    public static ItemCell itemCell;
    public static ItemCraftingTool itemWrench;

    public static Item sample;
    public static Item sampleMeta;
    public static Item fluidContainer;

    public static ItemStack ingotCopper;
    public static ItemStack ingotIridium;

    public static ItemStack cellEmpty;
    public static ItemStack cellWater;
    public static ItemStack cellLava;
    public static ItemStack cellHydrogen;
    public static ItemStack cellOxygen;

    public static ItemStack wrenchWood;
    public static ItemStack wrenchStone;
    public static ItemStack wrenchIron;
    public static ItemStack wrenchBronze;
    public static ItemStack wrenchSteel;
    public static ItemStack wrenchStainless;
    public static ItemStack wrenchTungsten;
    public static ItemStack wrenchTungstenSteel;


    public static void preInit() {

        itemMaterial = new ItemBase("material");
        itemCell = new ItemCell("cell");
        itemWrench = new ItemCraftingTool("wrench");

        sample = new Sample("sample");
        sampleMeta = new SampleMeta("sample_meta");
        fluidContainer = new ItemFluidContainer("fluid_container");
        
        ingotCopper = itemMaterial.addOreItem(0, "ingotCopper", 0);
        ingotIridium = itemMaterial.addOreItem(1, "ingotIridium", 3);

        cellEmpty = itemCell.addItem(0, "empty");
        cellWater = itemCell.addItem(1, "water");
        cellLava = itemCell.addItem(2, "lava");
        cellHydrogen = itemCell.addItem(3, "hydrogen");
        cellOxygen = itemCell.addItem(4, "oxygen");

        wrenchWood = itemWrench.addItem(2, 0, "wood");
        wrenchStone = itemWrench.addItem(4, 1, "stone");
        wrenchIron = itemWrench.addItem(64, 2, "iron");
        wrenchBronze = itemWrench.addItem(64, 3, "bronze");
        wrenchSteel = itemWrench.addItem(128, 4, "steel");
        wrenchStainless = itemWrench.addItem(256, 5, "stainless");
        wrenchTungsten = itemWrench.addItem(512, 6, "tungsten");
        wrenchTungstenSteel = itemWrench.addItem(1024, 7, "tungstenSteel");

    }

    public static void init() {
        GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                        new ItemStack(Items.diamond, 2),
                        "craftingWrench", Items.diamond
                )
        );
    }

    public static void postInit() {

    }

}
