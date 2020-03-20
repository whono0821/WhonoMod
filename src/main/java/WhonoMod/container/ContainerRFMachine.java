package WhonoMod.container;

import WhonoMod.container.slot.SlotSampleOutput;
import WhonoMod.recipe.SampleMachineRecipe;
import WhonoMod.tile.TileEntityRFMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRFMachine extends Container {
    private TileEntityRFMachine tileRFMachine;
    private final int sizeInventory;
    private int lastProcessTime;

    public ContainerRFMachine(InventoryPlayer par1InventoryPlayer, TileEntityRFMachine par2TileEntityRFMachine)
    {
        tileRFMachine = par2TileEntityRFMachine;
        sizeInventory = par2TileEntityRFMachine.getSizeInventory();
        addSlotToContainer(new Slot(par2TileEntityRFMachine, 0, 56, 35));
        addSlotToContainer(new SlotSampleOutput(par1InventoryPlayer.player, par2TileEntityRFMachine, 1, 116, 35));
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
        par1ICrafting.sendProgressBarUpdate(this, 0, tileRFMachine.RFMachineProcessTime);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)crafters.get(i);

            if (lastProcessTime != tileRFMachine.RFMachineProcessTime)
                icrafting.sendProgressBarUpdate(this, 0, tileRFMachine.RFMachineProcessTime);
        }

        lastProcessTime = tileRFMachine.RFMachineProcessTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
            tileRFMachine.RFMachineProcessTime = par2;

        if (par1 == 2)
            tileRFMachine.RFMachineProcessTime = par2;
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return tileRFMachine.isUseableByPlayer(par1EntityPlayer);
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

            if (par2 == 1)
            {
                if (!mergeItemStack(itemstack1, sizeInventory, sizeInventory + 36, true))
                    return null;

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if ( par2 != 0)
            {
                if (SampleMachineRecipe.instance().getSampleDoingResult(itemstack1) != null)
                {
                    if (!mergeItemStack(itemstack1, 0, 1, false))
                        return null;
                }
                else if (par2 >= sizeInventory && par2 < sizeInventory + 27)
                {
                    if (!mergeItemStack(itemstack1, sizeInventory + 27, sizeInventory + 36, false))
                        return null;
                }
                else if (par2 >= sizeInventory + 27 && par2 < sizeInventory + 36 && !mergeItemStack(itemstack1, sizeInventory, sizeInventory + 27, false))
                    return null;
            }
            else if (!mergeItemStack(itemstack1, sizeInventory, sizeInventory + 36, false))
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
