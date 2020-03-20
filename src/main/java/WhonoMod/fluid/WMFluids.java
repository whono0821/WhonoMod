package WhonoMod.fluid;


import WhonoMod.item.WMItems;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class WMFluids {

    public static Fluid hydrogen;
    public static Fluid oxygen;

    public static void preInit() {

        hydrogen = new Fluid("hydrogen").setDensity(-500).setViscosity(100).setGaseous(true);
        oxygen = new Fluid("oxygen").setDensity(-400).setViscosity(100).setGaseous(true);

        registerFluid(hydrogen, "hydrogen");
        registerFluid(oxygen, "oxygen");

    }

    public static void init() {

        FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, WMItems.cellWater, WMItems.cellEmpty);
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.LAVA, WMItems.cellLava, WMItems.cellEmpty);
        FluidContainerRegistry.registerFluidContainer(hydrogen, WMItems.cellHydrogen, WMItems.cellEmpty);
        FluidContainerRegistry.registerFluidContainer(oxygen, WMItems.cellOxygen, WMItems.cellEmpty);

    }

    public static void postInit() {

    }

    public static void registerFluid(Fluid fluid, String fluidName) {

        if (!FluidRegistry.isFluidRegistered(fluidName)) {
            FluidRegistry.registerFluid(fluid);
        }
        fluid = FluidRegistry.getFluid(fluidName);
    }
}
