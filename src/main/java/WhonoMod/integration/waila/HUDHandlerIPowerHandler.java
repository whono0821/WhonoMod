package WhonoMod.integration.waila;

import WhonoMod.api.IPowerHandler;
import WhonoMod.api.IPowerProvider;
import WhonoMod.api.IPowerReceiver;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class HUDHandlerIPowerHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return tip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

        if (!accessor.getNBTData().hasKey("powerStore")) return tip;

        int powerStore = accessor.getNBTInteger(accessor.getNBTData(), "powerStore");
        int maxPowerStore = accessor.getNBTInteger(accessor.getNBTData(), "maxPowerStore");

        if ((((ITaggedList)tip).getEntries("PowerStorage").size() == 0)) {

            ((ITaggedList) tip).add(String.format("%d / %d Power", powerStore, maxPowerStore), "PowerStorage");
        }

        return tip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

        return tip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {

        int powerStore = -1;
        int maxPowerStore = -1;

        if (tile instanceof IPowerHandler) {

            IPowerHandler powerHandler = (IPowerHandler)tile;
            powerStore = powerHandler.getPowerStored(ForgeDirection.UNKNOWN);
            maxPowerStore = powerHandler.getMaxPowerStored(ForgeDirection.UNKNOWN);
        }
        else if (tile instanceof IPowerProvider) {

            IPowerProvider powerProvider = (IPowerProvider)tile;
            powerStore = powerProvider.getPowerStored(ForgeDirection.UNKNOWN);
            maxPowerStore = powerProvider.getMaxPowerStored(ForgeDirection.UNKNOWN);
        }
        else if(tile instanceof IPowerReceiver) {

            IPowerReceiver powerReceiver = (IPowerReceiver)tile;
            powerStore = powerReceiver.getPowerStored(ForgeDirection.UNKNOWN);
            maxPowerStore = powerReceiver.getMaxPowerStored(ForgeDirection.UNKNOWN);
        }

        tag.setInteger("powerStore", powerStore);
        tag.setInteger("maxPowerStore", maxPowerStore);

        return tag;
    }
}
