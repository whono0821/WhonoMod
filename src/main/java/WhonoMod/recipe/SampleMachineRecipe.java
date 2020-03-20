package WhonoMod.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SampleMachineRecipe {
    private static final SampleMachineRecipe sampleDoingBase = new SampleMachineRecipe();

    private Map<ItemStack, ItemStack> sampleDoingList = new HashMap<ItemStack, ItemStack>();
    private Map<ItemStack, Float> experienceList = new HashMap<ItemStack, Float>();

    public static SampleMachineRecipe instance() {
        return sampleDoingBase;
    }

    private SampleMachineRecipe() {
        doing(Blocks.cobblestone, new ItemStack(Blocks.gravel), 0.0F);
    }

    public void doing(Block input, ItemStack output, float xp) {
        doing(Item.getItemFromBlock(input), output, xp);
    }

    public void doing(Item input, ItemStack output, float xp) {
        doing(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, xp);
    }

    public void doing(ItemStack input, ItemStack output, float xp) {
        sampleDoingList.put(input, output);
        experienceList.put(output, Float.valueOf(xp));
    }

    public ItemStack getSampleDoingResult(ItemStack itemStack) {
        for(Entry<ItemStack, ItemStack> entry : sampleDoingList.entrySet()) {
            if (areStacksEqual(itemStack, entry.getKey()))
                return entry.getValue();
        }

        return null;
    }

    private boolean areStacksEqual(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return par2ItemStack.getItem() == par1ItemStack.getItem() && (par2ItemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || par2ItemStack.getItemDamage() == par1ItemStack.getItemDamage());
    }
    public Map<ItemStack, ItemStack> getSampleDoingList() {
        return sampleDoingList;
    }

    public float getExperience(ItemStack par1ItemStack) {
        float ret = par1ItemStack.getItem().getSmeltingExperience(par1ItemStack);
        if (ret != -1) return ret;

        for (Entry<ItemStack, Float> entry : experienceList.entrySet())
            if (areStacksEqual(par1ItemStack, entry.getKey()))
                return entry.getValue().floatValue();

        return 0.0F;
    }

}
