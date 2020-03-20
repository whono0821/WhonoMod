package WhonoMod.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ComparableItemStack {
    public Item item;
    public int meta;
    public int stackSize;
    public int[] oreID = {-1};

    private ComparableItemStack() { }

    public ComparableItemStack(ItemStack itemStack) {
        this(itemStack ,false);
    }

    public ComparableItemStack(ItemStack itemStack, boolean isOre) {
        this.item = itemStack.getItem();
        this.meta = itemStack.getItemDamage();
        this.stackSize = itemStack.stackSize;
        if (isOre)
            this.oreID = OreDictionary.getOreIDs(itemStack);
    }

    public ComparableItemStack(String oreName, int stackSize) {
        this.item = OreDictionary.getOres(oreName).get(0).getItem();
        this.meta = OreDictionary.getOres(oreName).get(0).getItemDamage();
        this.stackSize = stackSize;
        this.oreID[0] = OreDictionary.getOreID(oreName);
    }

    public ItemStack getItemStack() {

        return new ItemStack(this.item, this.stackSize, this.meta);
    }

    public boolean isEqual(ComparableItemStack other) {

        if (other == null) {
            return false;
        }
        if (meta == other.meta) {
            if (item == other.item) {
                return true;
            }
        }
        return false;
    }

    public boolean isItemEqual(ComparableItemStack other) {

        if (other == null)  return false;
        if (isEqual(other)) return true;

        if (this.oreID[0] != -1 && other.oreID[0] != -1) {
            for (int i = 0; i < this.oreID.length; ++i) {
                for (int j = 0; j < other.oreID.length; ++j) {
                    if (this.oreID[i] == other.oreID[j]) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComparableItemStack))
            return false;

        return isItemEqual((ComparableItemStack) o);
    }

    @Override
    public int hashCode() {
        return oreID[0] != -1 ? 31 : (meta & 65535 | Item.getIdFromItem(item) << 16 );
    }
}
