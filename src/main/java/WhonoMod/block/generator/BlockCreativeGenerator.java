package WhonoMod.block.generator;

import WhonoMod.WhonoMod;
import WhonoMod.block.itemBlock.generator.ItemBlockCreativeGenerator;
import WhonoMod.tile.generator.TileEntityCreativeGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class BlockCreativeGenerator extends BlockContainer {

    private static final int AMOUNT = 6;
    private static final String[] VOLTAGE_NAME = {"ULV", "LV", "MV", "HV", "EV", "IV"};
    private final IIcon[] iconBlock = new IIcon[AMOUNT];

    public BlockCreativeGenerator() {
        super(Material.iron);

        setBlockName("creative_generator");
        setBlockTextureName(WhonoMod.MODID + ":creative_generator");
        setCreativeTab(WhonoMod.tabWM);
        setHardness(3.5F);
        setStepSound(Block.soundTypeMetal);

        GameRegistry.registerBlock(this, ItemBlockCreativeGenerator.class, "creative_generator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {

        for (int i = 0; i < AMOUNT; ++i) {

            iconBlock[i] = register.registerIcon(getTextureName() + VOLTAGE_NAME[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {

        return iconBlock[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {

        for (int i = 0; i < AMOUNT; ++i) {

            list.add(new ItemStack(item, 1, i));
        }
    }

    public String getVoltageName(ItemStack itemStack) {

        return VOLTAGE_NAME[itemStack.getItemDamage()];
    }

    //ITileEntityProvider
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileEntityCreativeGenerator(meta);
    }
}
