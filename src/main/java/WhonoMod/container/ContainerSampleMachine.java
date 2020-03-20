package WhonoMod.container;

import WhonoMod.container.slot.SlotSampleOutput;
import WhonoMod.recipe.SampleMachineRecipe;
import WhonoMod.tile.TileEntitySampleMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSampleMachine extends Container {
    private TileEntitySampleMachine tileSampleMachine;
    private int lastProcessTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerSampleMachine(InventoryPlayer par1InventoryPlayer, TileEntitySampleMachine par2TileEntitySampleMachine)
    {
        tileSampleMachine = par2TileEntitySampleMachine;
        addSlotToContainer(new Slot(par2TileEntitySampleMachine, 0, 56, 17));
        addSlotToContainer(new Slot(par2TileEntitySampleMachine, 1, 56, 53));
        addSlotToContainer(new SlotSampleOutput(par1InventoryPlayer.player, par2TileEntitySampleMachine, 2, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, tileSampleMachine.sampleMachineProcessTime);
        par1ICrafting.sendProgressBarUpdate(this, 1, tileSampleMachine.sampleMachineBurnTime);
        par1ICrafting.sendProgressBarUpdate(this, 2, tileSampleMachine.currentItemBurnTime);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)crafters.get(i);

            if (lastProcessTime != tileSampleMachine.sampleMachineProcessTime)
                icrafting.sendProgressBarUpdate(this, 0, tileSampleMachine.sampleMachineProcessTime);

            if (lastBurnTime != tileSampleMachine.sampleMachineBurnTime)
                icrafting.sendProgressBarUpdate(this, 1, tileSampleMachine.sampleMachineBurnTime);

            if (lastItemBurnTime != tileSampleMachine.currentItemBurnTime)
                icrafting.sendProgressBarUpdate(this, 2, tileSampleMachine.currentItemBurnTime);
        }

        lastProcessTime = tileSampleMachine.sampleMachineProcessTime;
        lastBurnTime = tileSampleMachine.sampleMachineBurnTime;
        lastItemBurnTime = tileSampleMachine.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
            tileSampleMachine.sampleMachineProcessTime = par2;

        if (par1 == 1)
            tileSampleMachine.sampleMachineBurnTime = par2;

        if (par1 == 2)
            tileSampleMachine.currentItemBurnTime = par2;

        if (par1 == 3)
            tileSampleMachine.sampleMachineProcessTime = par2;
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return tileSampleMachine.isUseableByPlayer(par1EntityPlayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 2)
            {
                if (!mergeItemStack(itemstack1, 3, 39, true))
                    return null;

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 != 1 && par2 != 0)
            {
                if (SampleMachineRecipe.instance().getSampleDoingResult(itemstack1) != null)
                {
                    if (!mergeItemStack(itemstack1, 0, 1, false))
                        return null;
                }
                else if (TileEntitySampleMachine.isItemFuel(itemstack1))
                {
                    if (!mergeItemStack(itemstack1, 1, 2, false))
                        return null;
                }
                else if (par2 >= 3 && par2 < 30)
                {
                    if (!mergeItemStack(itemstack1, 30, 39, false))
                        return null;
                }
                else if (par2 >= 30 && par2 < 39 && !mergeItemStack(itemstack1, 3, 30, false))
                    return null;
            }
            else if (!mergeItemStack(itemstack1, 3, 39, false))
                return null;

            if (itemstack1.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
                slot.onSlotChanged();

            if (itemstack1.stackSize == itemstack.stackSize)
                return null;

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
}
