package WhonoMod.recipe;

import WhonoMod.util.ComparableItemStack;
import net.minecraft.item.ItemStack;

public class AssemblerRecipe {

    private final ItemStack output;
    private final ComparableItemStack primaryInput;
    private final ComparableItemStack secondaryInput;
    private final int energy;

    public AssemblerRecipe(ItemStack output, ComparableItemStack primaryInput, ComparableItemStack secondaryInput, int energy) {
        this.output = output;
        this.primaryInput = primaryInput;
        this.secondaryInput = secondaryInput;
        this.energy = energy;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public ItemStack getPrimaryInput() {
        return primaryInput.getItemStack();
    }

    public  ItemStack getSecondaryInput() {
        return secondaryInput.getItemStack();
    }

    public int getEnergy() {
        return energy;
    }
}
