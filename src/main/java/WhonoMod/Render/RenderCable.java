package WhonoMod.Render;

import WhonoMod.WhonoMod;
import WhonoMod.block.BlockCable;
import WhonoMod.block.BlockMetaCable;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderCable implements ISimpleBlockRenderingHandler {

    private static final float[] faceColors = { 0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F };

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

        if (modelId != this.getRenderId()) return false;

        if (!(block instanceof BlockMetaCable))  return false;

        BlockMetaCable cable = (BlockMetaCable)block;
        IIcon[] icon = new IIcon[6];

        for (ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            icon[dire.ordinal()] = cable.getIcon(world, x, y, z, dire.ordinal());
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        boolean[] connectDire = new boolean[6];
        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            connectDire[dire.ordinal()] = cable.canConnect(world, x, y, z, dire);
        }

        float min = cable.getCableSize(world, x, y, z);
        float max = 1 - min;

        renderer.setRenderBounds(min, min, min, max, max, max);
        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            if(!connectDire[dire.ordinal()]) {
                renderFace(tessellator, renderer, block, x, y, z, icon, dire);
            }
        }

        for(ForgeDirection dire : ForgeDirection.VALID_DIRECTIONS) {

            int direIndex = dire.ordinal();

            if(connectDire[direIndex]) {

                float[] dim = { min, min, min, max, max, max };

                dim[direIndex / 2] = (direIndex % 2 == 0) ? 0.0F : max;
                dim[direIndex / 2 + 3] = (direIndex % 2 == 0) ? min : 1.0F;

                renderer.setRenderBounds(dim[2], dim[0], dim[1], dim[5], dim[3], dim[4]);

                for(ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {

                    if (face.getOpposite() == dire) continue;
                    renderFace(tessellator, renderer, block, x, y, z, icon, face);
                }
            }
        }
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {

        return false;
    }

    @Override
    public int getRenderId() {

        return WhonoMod.renderCable;
    }

    private static void renderFace(Tessellator tessellator, RenderBlocks renderer, Block block, int x, int y, int z, IIcon[] icon, ForgeDirection face) {

        int direIndex = face.ordinal();
        tessellator.setColorOpaque_F(faceColors[direIndex], faceColors[direIndex], faceColors[direIndex]);

        switch (face) {
            case DOWN:renderer.renderFaceYNeg(block, x, y, z, icon[direIndex]); break;
            case UP:renderer.renderFaceYPos(block, x, y, z, icon[direIndex]); break;
            case NORTH:renderer.renderFaceZNeg(block, x, y, z, icon[direIndex]); break;
            case SOUTH:renderer.renderFaceZPos(block, x, y, z, icon[direIndex]); break;
            case WEST:renderer.renderFaceXNeg(block, x, y, z, icon[direIndex]); break;
            case EAST:renderer.renderFaceXPos(block, x, y, z, icon[direIndex]); break;
        }
    }
}
