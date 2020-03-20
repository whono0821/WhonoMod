package WhonoMod.container;

import WhonoMod.tile.TileEntitySampleChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSampleChest extends Container {

    private TileEntitySampleChest tileEntity;
    private static final int index0  = 0;
    private static final int index1  = 54;
    private static final int index2  = 81;
    private static final int index3  = 90;

    public ContainerSampleChest(EntityPlayer player, TileEntitySampleChest tileEntity) {
        this.tileEntity = tileEntity;
        for (int iy = 0; iy < 6; iy++) {
            for (int ix = 0; ix < 9; ix++) {
                this.addSlotToContainer(new Slot(tileEntity, ix + (iy * 9), 8 + (ix * 18), 18 + (iy * 18)));
            }
        }
        for (int iy = 0; iy < 3; iy++) {
            for (int ix = 0; ix < 9; ix++) {
                this.addSlotToContainer(new Slot(player.inventory, ix + (iy * 9) + 9, 8 + (ix * 18), 140 + (iy * 18)));
            }
        }
        for (int ix = 0; ix < 9; ix++) {
            this.addSlotToContainer(new Slot(player.inventory, ix, 8 + (ix * 18), 198));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemStack = null;
        Slot slot = (Slot) inventorySlots.get(slotNumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if (index0 <= slotNumber && slotNumber < index1) {
                if (!this.mergeItemStack(itemStack1, index1, index3, true)) {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(itemStack1, index0, index1, false)) {
                    return null;
                }
            }

            if (itemStack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
            if (itemStack1.stackSize == itemStack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, itemStack1);
        }
        return itemStack;
    }
}
