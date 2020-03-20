package WhonoMod.recipe;


import WhonoMod.util.ComparableItemStack;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;

import java.util.*;


public class AssemblerManager {

    private static Map<List<ComparableItemStack>, AssemblerRecipe> recipeMap = new THashMap<List<ComparableItemStack>, AssemblerRecipe>();
    private static Set<ComparableItemStack> materials = new HashSet<ComparableItemStack>();

    private AssemblerManager() {};

    public static void addRecipe(ItemStack output, ComparableItemStack primaryInput, ComparableItemStack secondaryInput, int energy) {
        AssemblerRecipe recipe = new AssemblerRecipe(output, primaryInput, secondaryInput, energy);
        recipeMap.put(Arrays.asList(primaryInput, secondaryInput), recipe);
        materials.add(primaryInput);
        materials.add(secondaryInput);
    }
    public static void addRecipe(ItemStack output, ItemStack primaryInput, ItemStack secondaryInput, int energy) {
        addRecipe(output, new ComparableItemStack(primaryInput), new ComparableItemStack(secondaryInput), energy);
    }
    public static void addRecipe(ItemStack output, String primaryName, int primarySize, String secondaryName, int secondarySize, int energy) {
        addRecipe(output, new ComparableItemStack(primaryName, primarySize), new ComparableItemStack(secondaryName, secondarySize), energy);
    }
    public static void addRecipe(ItemStack output, String primaryName, int primarySize, ItemStack secondaryInput, int energy) {
        addRecipe(output, new ComparableItemStack(primaryName, primarySize), new ComparableItemStack(secondaryInput), energy);
    }
    public static void addRecipe(ItemStack output, ItemStack primaryInput, String secondaryName, int secondarySize, int energy) {
        addRecipe(output, new ComparableItemStack(primaryInput), new ComparableItemStack(secondaryName, secondarySize), energy);
    }

    public static AssemblerRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

        if (primaryInput == null || secondaryInput == null)
            return null;
        if (!isMaterial(primaryInput))
            return null;
        if (!isMaterial(secondaryInput))
            return null;

        ComparableItemStack orePrimary = new ComparableItemStack(primaryInput, true);
        ComparableItemStack oreSecondary = new ComparableItemStack(secondaryInput, true);
        ComparableItemStack nonOrePrimary = new ComparableItemStack(primaryInput, false);
        ComparableItemStack nonOreSecondary = new ComparableItemStack(secondaryInput, false);

        AssemblerRecipe recipe = recipeMap.get(Arrays.asList(orePrimary, oreSecondary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(orePrimary, nonOreSecondary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(nonOrePrimary, oreSecondary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(nonOrePrimary, nonOreSecondary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(oreSecondary, orePrimary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(nonOreSecondary, orePrimary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(oreSecondary, nonOrePrimary));
        if (recipe == null) recipe = recipeMap.get(Arrays.asList(nonOreSecondary, nonOrePrimary));
        if (recipe == null) {
            return null;
        }
        return recipe;
    }

    public static boolean isMaterial(ItemStack itemStack) {

        ComparableItemStack oreStack = new ComparableItemStack(itemStack, true);
        ComparableItemStack nonOreStack = new ComparableItemStack(itemStack, false);

        if(materials.contains(oreStack) || materials.contains(nonOreStack))
            return true;
        else
            return false;
    }

    public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput){

        if (primaryInput == null || secondaryInput == null)
            return false;

        AssemblerRecipe recipe = getRecipe(primaryInput, secondaryInput);

        return recipe != null ? true : false;
    }
}
