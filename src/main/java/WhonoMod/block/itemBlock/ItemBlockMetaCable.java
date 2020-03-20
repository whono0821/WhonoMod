package WhonoMod.block.itemBlock;

import WhonoMod.block.BlockMetaCable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockMetaCable extends ItemBlockWithMetadata {

    private BlockMetaCable block;

    public ItemBlockMetaCable(Block block) {
        super(block, block);

        this.block = (BlockMetaCable)block;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName() + itemStack.getItemDamage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {

        return this.block.getItemIcon(meta);
    }
}
