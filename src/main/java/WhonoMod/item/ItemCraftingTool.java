package WhonoMod.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemCraftingTool extends ItemBase implements ICraftingTool{

    private String toolName;
    private Map<Integer, Integer> maxDurabilityMap = new HashMap<Integer, Integer>() ;

    public ItemCraftingTool(String toolName) {

        super(toolName);

        this.toolName = toolName;
        setMaxStackSize(1);
    }

    public ItemStack addItem(int durability, int meta, String name, int rarity) {

        maxDurabilityMap.put(meta, durability);
        ItemStack itemStack = addItem(meta, new ItemData(name, rarity));
        OreDictionary.registerOre("crafting" + toolName.substring(0, 1).toUpperCase() + toolName.substring(1), itemStack);
        return itemStack;
    }

    public ItemStack addItem(int durability, int meta, String name) {

        maxDurabilityMap.put(meta, durability);
        ItemStack itemStack = addItem(meta, new ItemData(name));
        OreDictionary.registerOre("crafting" + toolName.substring(0, 1).toUpperCase() + toolName.substring(1), itemStack);
        return itemStack;
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Durability", 0);
        itemStack.setTagCompound(nbt);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean adv) {

        int maxDurability = this.maxDurabilityMap.get(itemStack.getItemDamage());
        int currentDurability = getCurrentDurability(itemStack);

        list.add("Durability: " + (maxDurability - currentDurability) + "/" + maxDurability);
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {

        return getCurrentDurability(itemStack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {

        int maxDurability = this.maxDurabilityMap.get(itemStack.getItemDamage());
        int currentDamage = getCurrentDurability(itemStack);

        return (double)currentDamage / (double)maxDurability;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {

        return false;
    }

    @Override
    public boolean hasContainerItem(ItemStack itemStack) {

        int maxDurability = this.maxDurabilityMap.get(itemStack.getItemDamage());
        int currentDurability = getCurrentDurability(itemStack);

        return currentDurability < maxDurability - 1;
    }

    @Override
    public ItemStack getContainerItem (ItemStack itemStack) {

        ItemStack returnStack = itemStack.copy();

        NBTTagCompound nbt = itemStack.getTagCompound();
        int currentDamage = getCurrentDurability(itemStack);
        nbt.setInteger("Durability", currentDamage + 1);
        returnStack.setTagCompound(nbt);

        return returnStack;
    }

    @Override
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {

        return true;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {

        return true;
    }

    private int getCurrentDurability(ItemStack itemStack) {

        NBTTagCompound nbt;
        if (itemStack.hasTagCompound()) {
            nbt = itemStack.getTagCompound();
            return nbt.getInteger("Durability");
        } else {
            nbt = new NBTTagCompound();
            nbt.setInteger("Durability", 0);
            itemStack.setTagCompound(new NBTTagCompound());
            return 0;
        }
    }

    public ItemStack takeDamage(ItemStack itemStack, int damage) {

        if (itemStack.getItem() instanceof ItemCraftingTool) {

            int currentDamage = getCurrentDurability(itemStack);
            int maxDurability = this.maxDurabilityMap.get(itemStack.getItemDamage());

            if (currentDamage + damage < maxDurability) {

                NBTTagCompound nbt = itemStack.getTagCompound();
                nbt.setInteger("Durability", currentDamage + damage);
                ItemStack returnStack = itemStack.copy();
                returnStack.setTagCompound(nbt);

                return returnStack;

            } else {
                return null;
            }
        }

        return itemStack;
    }

    @Deprecated
    @Override
    public ItemStack addItem(int meta, String name, int rarity) {

        return addItem(meta, new ItemData(name, rarity));
    }

    @Deprecated
    @Override
    public ItemStack addItem(int meta, String name) {

        return addItem(meta, new ItemData(name));
    }

    @Deprecated
    @Override
    public ItemStack addOreItem(int meta, String name, int rarity) {

        ItemStack itemStack = addItem(meta, new ItemData(name, rarity));
        OreDictionary.registerOre(name, itemStack);

        return itemStack;
    }

    @Deprecated
    @Override
    public ItemStack addOreItem(int meta, String name) {

        ItemStack itemStack = addItem(meta, new ItemData(name));
        OreDictionary.registerOre(name, itemStack);

        return itemStack;
    }

}

