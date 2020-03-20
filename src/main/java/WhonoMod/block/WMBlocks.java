package WhonoMod.block;

import WhonoMod.fluid.BlockFluid;
import WhonoMod.fluid.WMFluids;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class WMBlocks {

    public static Block sample;
    public static Block sampleMeta;
    public static Block sampleDirection;
    public static Block sampleChest;
    public static Block sampleMachine, sampleMachine_on;
    public static Block rfMachine, rfMachine_on;
    public static Block assembler, assembler_on;
    public static Block generator;

    public static Block cable;

    public static Block blockFluidHydrogen;
    public static Block blockFluidOxygen;

    public static void preInit() {

        sample = new SampleBlock("sample_block", Material.iron);
        sampleMeta = new SampleMetaBlock("sampleMetaBlock", Material.iron);
        sampleDirection = new SampleDirectionBlock("sampleDirectionBlock", Material.iron);
        sampleChest = new SampleChest("sampleChest", Material.iron);
        sampleMachine = new BlockSampleMachine(false);
        sampleMachine_on = new BlockSampleMachine(true);
        rfMachine = new BlockRFMachine(false);
        rfMachine_on = new BlockRFMachine(true);
        assembler = new BlockAssembler(false);
        assembler_on = new BlockAssembler(true);
        generator = new BlockGenerator();

        cable = new BlockMetaCable();

        blockFluidHydrogen = new BlockFluid(WMFluids.hydrogen, Material.water, 0);
        blockFluidOxygen = new BlockFluid(WMFluids.oxygen, Material.water, 1);

    }

    public static void init() {

    }

    public static void postInit() {

    }

}
