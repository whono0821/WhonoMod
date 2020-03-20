package WhonoMod.gui;

import WhonoMod.container.ContainerRFMachine;
import WhonoMod.tile.TileEntityRFMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiRFMachine extends GuiContainer {
    private static final ResourceLocation RFMachineGuiTexture = new ResourceLocation("whonomod:textures/gui/container/sampleMachine.png");
    private TileEntityRFMachine tileRFMachine;

    public GuiRFMachine(InventoryPlayer par1InventoryPlayer, TileEntityRFMachine par2TileEntityRFMachine)
    {
        super(new ContainerRFMachine(par1InventoryPlayer, par2TileEntityRFMachine));
        tileRFMachine = par2TileEntityRFMachine;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = tileRFMachine.hasCustomInventoryName() ? tileRFMachine.getInventoryName() : I18n.format(tileRFMachine.getInventoryName(), new Object[0]);
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0xFFFFFF);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(RFMachineGuiTexture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int i1;

        i1 = tileRFMachine.getProcessProgressScaled(24);
        drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
    }
}
