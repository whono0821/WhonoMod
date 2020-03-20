package WhonoMod.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import WhonoMod.gui.GuiSampleMachine;
import WhonoMod.recipe.SampleMachineRecipe;
import WhonoMod.tile.TileEntitySampleMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import codechicken.nei.ItemList;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class RFMachineRecipeHandler extends TemplateRecipeHandler {

    public class CachedRFMachineRecipe extends CachedRecipe {

        public CachedRFMachineRecipe(ItemStack ingred, ItemStack result) {
            ingred.stackSize = 1;
            this.ingred = new PositionedStack(ingred, 51, 6);
            this.result = new PositionedStack(result, 111, 24);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred));
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        @Override
        public PositionedStack getOtherStack() {
            return afuels.get(cycleticks / 48 % afuels.size()).stack;
        }

        PositionedStack ingred;
        PositionedStack result;
    }

    public static class FuelPair
    {
        public FuelPair(ItemStack ingred, int burnTime) {
            stack = new PositionedStack(ingred, 51, 42, false);
            this.burnTime = burnTime;
        }

        public PositionedStack stack;
        public int burnTime;
    }

    public static ArrayList<FuelPair> afuels;

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(50, 23, 18, 18), "fuel"));
        transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "sampleMachine"));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiSampleMachine.class;
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("container.whonomod.sampleMachine.nei");
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        if (afuels == null)
            findFuels();
        return super.newInstance();
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("sampleMachine") && getClass() == RFMachineRecipeHandler.class) {//don't want subclasses getting a hold of this
            Map<ItemStack, ItemStack> recipes = SampleMachineRecipe.instance().getSampleDoingList();
            for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
                arecipes.add(new CachedRFMachineRecipe(recipe.getKey(), recipe.getValue()));
        } else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        Map<ItemStack, ItemStack> recipes = SampleMachineRecipe.instance().getSampleDoingList();
        for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameType(recipe.getValue(), result))
                arecipes.add(new CachedRFMachineRecipe(recipe.getKey(), recipe.getValue()));
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("fuel") && getClass() == RFMachineRecipeHandler.class)//don't want subclasses getting a hold of this
            loadCraftingRecipes("sampleMachine");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        Map<ItemStack, ItemStack> recipes = SampleMachineRecipe.instance().getSampleDoingList();
        for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
                CachedRFMachineRecipe arecipe = new CachedRFMachineRecipe(recipe.getKey(), recipe.getValue());
                arecipe.setIngredientPermutation(Arrays.asList(arecipe.ingred), ingredient);
                arecipes.add(arecipe);
            }
    }

    @Override
    public String getGuiTexture() {
        return "whonomod:textures/gui/container/sampleMachine.png";
    }

    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(51, 25, 176, 0, 14, 14, 48, 7);
        drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
    }

    private static Set<Item> excludedFuels() {
        Set<Item> efuels = new HashSet<Item>();
        efuels.add(Item.getItemFromBlock(Blocks.brown_mushroom));
        efuels.add(Item.getItemFromBlock(Blocks.red_mushroom));
        efuels.add(Item.getItemFromBlock(Blocks.standing_sign));
        efuels.add(Item.getItemFromBlock(Blocks.wall_sign));
        efuels.add(Item.getItemFromBlock(Blocks.wooden_door));
        efuels.add(Item.getItemFromBlock(Blocks.trapped_chest));
        return efuels;
    }

    private static void findFuels() {
        afuels = new ArrayList<FuelPair>();
        Set<Item> efuels = excludedFuels();
        for (ItemStack item : ItemList.items)
            if (!efuels.contains(item.getItem())) {
                int burnTime = TileEntitySampleMachine.getItemBurnTime(item);
                if (burnTime > 0)
                    afuels.add(new FuelPair(item.copy(), burnTime));
            }
    }

    @Override
    public String getOverlayIdentifier() {
        return "sampleMachine";
    }
}