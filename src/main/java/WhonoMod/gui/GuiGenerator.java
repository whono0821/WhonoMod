package WhonoMod.gui;

import WhonoMod.container.ContainerGenerator;
import WhonoMod.tile.TileEntityGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiGenerator extends GuiContainer {
    
    private static ResourceLocation generatorGuiTexture = new ResourceLocation("whonomod:textures/gui/container/generator.png");
    private TileEntityGenerator tileEntityGenerator;

    public GuiGenerator(InventoryPlayer inventory, TileEntityGenerator tileGenerator) {
        super(new ContainerGenerator(inventory, tileGenerator));
        
        this.tileEntityGenerator = tileGenerator;
    }

    @Override
    public void drawScreen(int x, int y, float f) {

        super.drawScreen(x, y, f);
        this.drawToolTipsSunIntensity(x, y);
    }

    public void drawToolTipsSunIntensity(int mouseX, int mouseY) {

        int boxX = (this.width - this.xSize) / 2 + 16;
        int boxY = (this.height - this.ySize) / 2 + 16;

        int defaultX = 16;
        int defaultY = 64;

        if(mouseX > boxX && mouseX < boxX + defaultX && mouseY > boxY && mouseY < boxY + defaultY) {

            List list = new ArrayList();
            int storedEnergy = tileEntityGenerator.getPowerStored(null);
            int maxEnergy = tileEntityGenerator.getMaxPowerStored(null);
            list.add("\u00a7eEnergy:");
            list.add(storedEnergy + "/" + maxEnergy);

            this.drawHoveringText(list, (int)mouseX, (int)mouseY, fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {

        String s = tileEntityGenerator.hasCustomInventoryName() ? tileEntityGenerator.getInventoryName() : I18n.format(tileEntityGenerator.getInventoryName(), new Object[0]);
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0xFFFFFF);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(generatorGuiTexture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        int i1;

        i1 = tileEntityGenerator.getProcessProgressScaled(24);
        drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);

        int k1 = tileEntityGenerator.getScaledEnergyStored(64);
        drawTexturedModalRect(k + 16, l + 80 - k1, 176, 94 - k1, 16, k1 + 1);
    }
}
