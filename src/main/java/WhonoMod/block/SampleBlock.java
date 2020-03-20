package WhonoMod.block;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class SampleBlock extends Block {

    SampleBlock(String blockName, Material material) {
        super(material);
        setBlockName(blockName);
        setBlockTextureName(WhonoMod.MODID + ":" + blockName);
        setCreativeTab(WhonoMod.tabWM);
        setHardness(5.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(10.0F);
        setLightLevel(5.0F);
        setStepSound(Block.soundTypeMetal);

        GameRegistry.registerBlock(this, blockName);
    }


}
