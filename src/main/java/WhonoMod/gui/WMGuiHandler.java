package WhonoMod.gui;

import WhonoMod.container.*;
import WhonoMod.tile.*;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WMGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z))
            return null;
        TileEntity tileentity = world.getTileEntity(x, y, z);
        switch (id) {
            case 0:
                return new ContainerSampleMachine(player.inventory, (TileEntitySampleMachine) tileentity);
            case 1:
                return new ContainerSampleChest(player, (TileEntitySampleChest) tileentity);
            case 2:
                return new ContainerAssembler(player.inventory, (TileEntityAssembler)tileentity);
            case 3:
                return new ContainerRFMachine(player.inventory, (TileEntityRFMachine)tileentity);
            case 4:
                return new ContainerGenerator(player.inventory, (TileEntityGenerator) tileentity);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z))
            return null;
        TileEntity tileentity = world.getTileEntity(x, y, z);
        switch (id) {
            case 0:
                return new GuiSampleMachine(player.inventory, (TileEntitySampleMachine) tileentity);
            case 1:
                return new GuiSampleChest(player, (TileEntitySampleChest) tileentity);
            case 2:
                return new GuiAssembler(player.inventory, (TileEntityAssembler) tileentity);
            case 3:
                return new GuiRFMachine(player.inventory, (TileEntityRFMachine)tileentity);
            case 4:
                return new GuiGenerator(player.inventory, (TileEntityGenerator)tileentity);
        }
        return null;
    }
}
