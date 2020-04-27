package WhonoMod.block.itemBlock.generator;

import WhonoMod.block.generator.BlockCreativeGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockCreativeGenerator extends ItemBlockWithMetadata {

    BlockCreativeGenerator blockCreativeGenerator;

    public ItemBlockCreativeGenerator(Block block) {
        super(block, block);

        blockCreativeGenerator = (BlockCreativeGenerator) block;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {

        return getUnlocalizedName() + blockCreativeGenerator.getVoltageName(itemStack);
    }
}
