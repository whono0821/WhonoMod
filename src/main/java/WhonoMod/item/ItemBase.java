package WhonoMod.item;

import WhonoMod.WhonoMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBase extends Item {

    public class ItemData {

        public String name;
        public IIcon icon;
        public int rarity = 0;

        public ItemData(String name, int rarity) {
            this.name = name;
            this.rarity = rarity;
        }

        public ItemData(String name) {
            this.name = name;
        }
    }

    private Map<Integer, ItemData> itemMap = new HashMap<Integer, ItemData>();
    private ArrayList<Integer> itemList = new ArrayList<Integer>();

    public ItemBase(String name) {

        setTextureName(WhonoMod.MODID + ":" + name);
        setUnlocalizedName(name);
        setCreativeTab(WhonoMod.tabWM);
        setHasSubtypes(true);

        GameRegistry.registerItem(this, name);
    }

    protected ItemStack addItem(int meta, ItemData itemData) {

        if (itemMap.containsKey(meta)) {
            return null;
        }
        itemMap.put(meta, itemData);
        itemList.add(meta);

        ItemStack itemStack = new ItemStack(this, 1, meta);
        GameRegistry.registerCustomItemStack(itemData.name, itemStack);

        return itemStack;
    }

    public ItemStack addItem(int meta, String name, int rarity) {

        return addItem(meta, new ItemData(name, rarity));
    }

    public ItemStack addItem(int meta, String name) {

        return addItem(meta, new ItemData(name));
    }

    public ItemStack addOreItem(int meta, String name, int rarity) {

        ItemStack itemStack = addItem(meta, new ItemData(name, rarity));
        OreDictionary.registerOre(name, itemStack);

        return itemStack;
    }

    public ItemStack addOreItem(int meta, String name) {

        ItemStack itemStack = addItem(meta, new ItemData(name));
        OreDictionary.registerOre(name, itemStack);

        return itemStack;
    }

    @Override
    public IIcon getIconFromDamage(int i) {

        if (!itemMap.containsKey(i)) {
            return null;
        }
        return itemMap.get(i).icon;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {

        int meta = stack.getItemDamage();
        if (!itemMap.containsKey(meta)) {
            return null;
        }
        return EnumRarity.values()[itemMap.get(meta).rarity];
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {

        for (int meta : itemList) {
            list.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int meta = stack.getItemDamage();
        if (!itemMap.containsKey(meta)) {
            return "item.invaild";
        }
        ItemData item = itemMap.get(meta);

        return getUnlocalizedName() + "." + item.name;
    }

    @Override
    public void registerIcons(IIconRegister ir) {
        for (int meta : itemList) {
            ItemData item = itemMap.get(meta);
            item.icon = ir.registerIcon(this.getIconString() + "." + item.name);
        }
    }

}
