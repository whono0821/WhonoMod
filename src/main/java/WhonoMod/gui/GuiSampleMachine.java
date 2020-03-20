package WhonoMod.gui;

import WhonoMod.container.ContainerSampleMachine;
import WhonoMod.tile.TileEntitySampleMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiSampleMachine extends GuiContainer {
    private static final ResourceLocation sampleMachineGuiTexture = new ResourceLocation("whonomod:textures/gui/container/sampleMachine.png");
    private TileEntitySampleMachine tileSampleMachine;

    public GuiSampleMachine(InventoryPlayer par1InventoryPlayer, TileEntitySampleMachine par2TileEntitySampleMachine)
    {
        super(new ContainerSampleMachine(par1InventoryPlayer, par2TileEntitySampleMachine));
        tileSampleMachine = par2TileEntitySampleMachine;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = tileSampleMachine.hasCustomInventoryName() ? tileSampleMachine.getInventoryName() : I18n.format(tileSampleMachine.getInventoryName(), new Object[0]);
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0xFFFFFF);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(sampleMachineGuiTexture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int i1;

        if (tileSampleMachine.isTransmuting())
        {
            i1 = tileSampleMachine.getBurnTimeRemainingScaled(12);
            drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
        }

        i1 = tileSampleMachine.getProcessProgressScaled(24);
        drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
    }
}
