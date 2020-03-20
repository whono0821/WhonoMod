package WhonoMod.PowerSystem;

import WhonoMod.api.IPowerConnection;
import WhonoMod.api.IPowerProvider;
import WhonoMod.api.IPowerReceiver;
import WhonoMod.tile.TileEntityCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PowerNetwork {

    public static PowerNetwork instance = new PowerNetwork();

    private static Map<World, ArrayList<TileEntity>> tileMaps = new HashMap<World, ArrayList<TileEntity>>();

    private PowerNetwork(){}

    public static void addNetwork(TileEntity tileEntity, World world) {

        ArrayList<TileEntity> list;
        if (!tileMaps.containsKey(world)) {

            tileMaps.put(world, new ArrayList<TileEntity>());
        }
        list = tileMaps.get(world);

        if (!list.contains(tileEntity)) {

            list.add(tileEntity);
        }
    }

    public static void removeNetwork(TileEntity tileEntity, World world) {

        if (!tileMaps.containsKey(world)) {

            return;
        }
        ArrayList<TileEntity> list = tileMaps.get(world);

        if (list.contains(tileEntity)) {

            tileMaps.get(world).remove(tileEntity);
        }
    }

    public static boolean isContained(TileEntity tileEntity) {

        if (!tileMaps.containsKey(tileEntity.getWorldObj())) {

            return false;
        }

        ArrayList<TileEntity> list = tileMaps.get(tileEntity.getWorldObj());

        return list.contains(tileEntity);
    }

    public static void onTickEnd(World world) {

        if (!tileMaps.containsKey(world)) return;

        ArrayList<TileEntity> list = tileMaps.get(world);

        if (list.isEmpty()) return;

        for (TileEntity tile : list) {

            if (tile instanceof IPowerConnection) {

                if (tile instanceof IPowerReceiver) {

                    IPowerReceiver receiver = (IPowerReceiver)tile;
                    for (ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

                        if (receiver.canConnectPower(dire)) {

                            TileEntity bufTile = world.getTileEntity(tile.xCoord + dire.offsetX, tile.yCoord + dire.offsetY, tile.zCoord + dire.offsetZ);

                            if (tile instanceof TileEntityCable && bufTile instanceof TileEntityCable) {

                                continue;
                            }

                            if (bufTile instanceof IPowerProvider) {

                                IPowerProvider provider = (IPowerProvider)bufTile;
                                if(provider.canConnectPower(dire.getOpposite())) {

                                    //int energy = provider.extractPower(dire.getOpposite(), Integer.MAX_VALUE, false);
                                    //receiver.receivePower(dire, energy, false);

                                    int providePower = provider.extractPower(dire.getOpposite(), Integer.MAX_VALUE, true);
                                    if (providePower > 0) {

                                        int receivePower = receiver.receivePower(dire, providePower, true);
                                        if (receivePower > 0) {

                                            provider.extractPower(dire.getOpposite(), receivePower, false);
                                            receiver.receivePower(dire, receivePower, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
