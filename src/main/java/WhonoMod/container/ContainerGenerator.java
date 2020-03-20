package WhonoMod.container;

import WhonoMod.tile.TileEntityGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerGenerator extends Container {

    private TileEntityGenerator tileEntityGenerator;
    private final int sizeInventory;
    private int lastProcessTime;
    private int lastProcessMaxTime;
    private int lastEnergyStored;

    public ContainerGenerator(InventoryPlayer inventoryPlayer, TileEntityGenerator tileEntityGenerator)
    {
        this.tileEntityGenerator = tileEntityGenerator;
        this.sizeInventory = tileEntityGenerator.getSizeInventory();
        addSlotToContainer(new Slot(tileEntityGenerator, 0, 56, 35));
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
        listener.sendProgressBarUpdate(this, 0, tileEntityGenerator.processRem);
        listener.sendProgressBarUpdate(this, 1, tileEntityGenerator.processMax);
        listener.sendProgressBarUpdate(this, 2, tileEntityGenerator.getPowerStored(ForgeDirection.UNKNOWN));
    }
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (lastProcessTime != tileEntityGenerator.processRem)
                icrafting.sendProgressBarUpdate(this, 0, tileEntityGenerator.processRem);
            if (lastProcessMaxTime != tileEntityGenerator.processMax)
                icrafting.sendProgressBarUpdate(this, 1, tileEntityGenerator.processMax);
            if (lastEnergyStored != tileEntityGenerator.getPowerStored(ForgeDirection.UNKNOWN))
                icrafting.sendProgressBarUpdate(this, 2, tileEntityGenerator.getPowerStored(null));

        }
        lastProcessTime = tileEntityGenerator.processRem;
        lastProcessMaxTime = tileEntityGenerator.processMax;
        lastEnergyStored = tileEntityGenerator.getPowerStored(ForgeDirection.UNKNOWN);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
            tileEntityGenerator.processRem = data;
        if (id == 1)
            tileEntityGenerator.processMax = data;
        if (id == 2)
            tileEntityGenerator.setEnergyStored(data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {

        return tileEntityGenerator.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

        ItemStack itemStack = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {

            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (slotIndex == 0) {

                if (!mergeItemStack(itemStack1, sizeInventory, sizeInventory + 36, true)) {

                    return null;
                }
                slot.onSlotChange(itemStack1, itemStack);
            }
            else {

                if (TileEntityFurnace.getItemBurnTime(itemStack1) > 0) {

                    if (!mergeItemStack(itemStack1, 0, 1, false)) {

                        return null;
                    }
                }
                else if (slotIndex >= sizeInventory && slotIndex < sizeInventory + 27) {

                    if (!mergeItemStack(itemStack1, sizeInventory + 27, sizeInventory + 36, false)) {

                        return null;
                    }
                }
                else if (slotIndex >= sizeInventory + 27 && slotIndex < sizeInventory + 36) {

                    if (!mergeItemStack(itemStack1, sizeInventory, sizeInventory + 27, false)) {

                        return null;
                    }
                }
            }

            if (itemStack1.stackSize == 0) {

                slot.putStack((ItemStack)null);
            }
            else {

                slot.onSlotChanged();
            }

            if (itemStack1.stackSize == itemStack.stackSize)
                return null;

            slot.onPickupFromSlot(player, itemStack1);
        }

        return itemStack;
    }
}
