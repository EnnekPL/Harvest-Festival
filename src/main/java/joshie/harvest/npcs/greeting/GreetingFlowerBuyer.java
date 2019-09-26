package joshie.harvest.npcs.greeting;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.npc.IInfoButton;
import joshie.harvest.api.npc.NPC;
import joshie.harvest.api.quests.Quest;
import joshie.harvest.core.helpers.InventoryHelper;
import joshie.harvest.core.helpers.TextHelper;
import joshie.harvest.quests.QuestHelper;
import joshie.harvest.quests.Quests;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static joshie.harvest.core.lib.HFModInfo.ICONS;

public class GreetingFlowerBuyer implements IInfoButton {
    private static final Quest BUY_FLOWER = QuestHelper.getQuest("trader.flowers");

    @Override
    public boolean canDisplay(NPC npc, EntityPlayer player) {
        return HFApi.quests.hasCompleted(Quests.FLOWER_BUYER, player);
    }

    @Override
    public boolean onClicked(NPC inpc, EntityPlayer player) {
        HFApi.quests.completeQuest(BUY_FLOWER, player);
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getLocalizedText(EntityPlayer player, EntityAgeable ageable, NPC npc) {
        EnumHand hand = !player.getHeldItemOffhand().isEmpty() ? EnumHand.OFF_HAND : !player.getHeldItemMainhand().isEmpty() ? EnumHand.MAIN_HAND : null;
        if (hand != null) {
            ItemStack held = player.getHeldItem(hand);
            if (InventoryHelper.startsWith(held, "flower") && held.getCount() >= 1) {
                held.shrink(1); //Reduce the stack size by one
                return TextHelper.getRandomSpeech(npc, "harvestfestival.npc.jade.buy", 32);
            }
        }

        return TextHelper.getRandomSpeech(npc, "harvestfestival.npc.jade.buyno", 32);
    }

    @Override
    public void drawIcon(GuiScreen gui, int x, int y) {
        gui.mc.renderEngine.bindTexture(ICONS);
        gui.drawTexturedModalRect(x, y, 80, 0, 16, 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTooltip() {
        return "harvestfestival.npc.tooltip.flower";
    }
}
