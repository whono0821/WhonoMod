package WhonoMod.container.slot;

import WhonoMod.recipe.SampleMachineRecipe;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;

public class SlotAssemblerOutput extends Slot {
    private EntityPlayer thePlayer;
    private int stackSize;

    public SlotAssemblerOutput(EntityPlayer player, IInventory inventory, int slot, int x, int y) {
        super(inventory, slot, x, y);
        thePlayer = player;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (getHasStack())
            stackSize += Math.min(amount, getStack().stackSize);

        return super.decrStackSize(amount);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
        this.onCrafting(itemStack);
        super.onPickupFromSlot(player, itemStack);
    }

    @Override
    protected void onCrafting(ItemStack itemStack, int amount) {
        stackSize += amount;
        this.onCrafting(itemStack);
    }

    @Override
    protected void onCrafting(ItemStack itemStack) {
        itemStack.onCrafting(thePlayer.worldObj, thePlayer, stackSize);

        if (!thePlayer.worldObj.isRemote)
        {
            int i = stackSize;
            float f = SampleMachineRecipe.instance().getExperience(itemStack);
            int j;

            if (f == 0.0F)
                i = 0;
            else if (f < 1.0F)
            {
                j = MathHelper.floor_float(i * f);

                if (j < MathHelper.ceiling_float_int(i * f) && (float)Math.random() < i * f - j)
                    ++j;

                i = j;
            }

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, j));
            }
        }

        stackSize = 0;
    }
}
