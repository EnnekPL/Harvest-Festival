package joshie.harvest.npc.gui;

import joshie.harvest.api.HFApi;
import joshie.harvest.core.lib.HFModInfo;
import joshie.harvest.core.util.ChatFontRenderer;
import joshie.harvest.core.util.GuiBase;
import joshie.harvest.npc.entity.EntityNPC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiNPCBase extends GuiBase {
    private static final ResourceLocation chatbox = new ResourceLocation(HFModInfo.MODPATH, "textures/gui/chatbox.png");
    protected EntityNPC npc;
    protected EntityPlayer player;
    
    public GuiNPCBase(EntityNPC eNpc, EntityPlayer ePlayer) {
        super(new ContainerNPC(eNpc, ePlayer.inventory), "chat", 0);
        
        hasInventory = false;
        npc = eNpc;
        player = ePlayer;
        xSize = 256;
        ySize = 256;
    }

    @Override
    public void drawBackground(int x, int y) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        mc.renderEngine.bindTexture(chatbox);
        drawTexturedModalRect(x, y + 150, 0, 150, 256, 46);
        ChatFontRenderer.colorise(npc.getNPC().getInsideColor());
        drawTexturedModalRect(x, y + 150, 0, 100, 256, 46);
        ChatFontRenderer.colorise(npc.getNPC().getOutsideColor());
        drawTexturedModalRect(x, y + 150, 0, 50, 256, 46);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        
        ChatFontRenderer.render(this, x, y, npc.getCommandSenderName(), npc.getNPC().getInsideColor(), npc.getNPC().getOutsideColor());
    }
    
    private void drawHeart(int value) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int xPos = (int) ((((double) value / (Short.MAX_VALUE * 2))) * 7);
        drawTexturedModalRect(240, 130, 0, 0, 25, 25);
        drawTexturedModalRect(240, 130, 25 + (25 * xPos), 0, 25, 25);
    }
    
    @Override
    public void drawForeground(int x, int y) {
        mc.renderEngine.bindTexture(HFModInfo.elements);
        if (npc.getNPC().isMarriageCandidate()) {
            drawHeart(HFApi.RELATIONS.getAdjustedRelationshipValue(player, npc.getRelatable()));
        }
    }
}