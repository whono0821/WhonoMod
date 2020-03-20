package WhonoMod.container;

import WhonoMod.container.slot.SlotAssemblerOutput;
import WhonoMod.recipe.AssemblerManager;
import WhonoMod.recipe.SampleMachineRecipe;
import WhonoMod.tile.TileEntityAssembler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAssembler extends Container {

    private TileEntityAssembler tileEntityAssembler;
    private final int sizeInventory;
    private int lastProcessTime;
    private int lastProcessMaxTime;
    private int lastEnergyStored;

    public ContainerAssembler(InventoryPlayer inventoryPlayer, TileEntityAssembler tileEntityAssembler)
    {
        this.tileEntityAssembler = tileEntityAssembler;
        this.sizeInventory = tileEntityAssembler.getSizeInventory();
        addSlotToContainer(new Slot(tileEntityAssembler, 0, 56, 35));
        addSlotToContainer(new Slot(tileEntityAssembler, 1, 38, 35));
        addSlotToContainer(new SlotAssemblerOutput(inventoryPlayer.player, tileEntityAssembler, 2, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
    }


    @Override
    public void addCraftingToCrafters(ICrafting listener)
    {
        super.addCraftingToCrafters(listener);
        listener.sendProgressBarUpdate(this, 0, tileEntityAssembler.processRem);
        listener.sendProgressBarUpdate(this, 1, tileEntityAssembler.processMax);
        listener.sendProgressBarUpdate(this, 2, tileEntityAssembler.getPowerStored(null));
    }
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (lastProcessTime != tileEntityAssembler.processRem)
                icrafting.sendProgressBarUpdate(this, 0, tileEntityAssembler.processRem);
            if (lastProcessMaxTime != tileEntityAssembler.processMax)
                icrafting.sendProgressBarUpdate(this, 1, tileEntityAssembler.processMax);
            if (lastEnergyStored != tileEntityAssembler.getPowerStored(null))
                icrafting.sendProgressBarUpdate(this, 2, tileEntityAssembler.getPowerStored(null));

        }
        lastProcessTime = tileEntityAssembler.processRem;
        lastProcessMaxTime = tileEntityAssembler.processMax;
        lastEnergyStored = tileEntityAssembler.getPowerStored(null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
            tileEntityAssembler.processRem = data;
        if (id == 1)
            tileEntityAssembler.processMax = data;
        if (id == 2)
            tileEntityAssembler.setEnergyStored(data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tileEntityAssembler.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == 2)
            {
                if (!mergeItemStack(itemstack1, sizeInventory, sizeInventory + 36, true))
                    return null;

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if ( slotIndex != 0 && slotIndex != 1)
            {
                if (AssemblerManager.isMaterial(itemstack1))
                {
                    if (!mergeItemStack(itemstack1, 0, 2, false))
                        return null;
                }
                else if (slotIndex >= sizeInventory && slotIndex < sizeInventory + 27)
                {
                    if (!mergeItemStack(itemstack1, sizeInventory + 27, sizeInventory + 36, false))
                        return null;
                }
                else if (slotIndex >= sizeInventory + 27 && slotIndex < sizeInventory + 36 && !mergeItemStack(itemstack1, sizeInventory, sizeInventory + 27, false))
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

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
