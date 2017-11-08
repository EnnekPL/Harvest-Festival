package joshie.harvest.knowledge.gui.stats.relations.page;

import joshie.harvest.api.npc.NPC;
import joshie.harvest.core.base.gui.BookPage;
import joshie.harvest.knowledge.gui.stats.GuiStats;
import joshie.harvest.knowledge.gui.stats.button.ButtonNext;
import joshie.harvest.knowledge.gui.stats.button.ButtonPrevious;
import joshie.harvest.knowledge.gui.stats.relations.button.ButtonRelationsNPC;
import joshie.harvest.npcs.HFNPCs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;

import java.util.ArrayList;
import java.util.List;

public class PageNPC extends PageRelationship {
    public static final BookPage INSTANCE = new PageNPC();

    private PageNPC() {
        super("npc", HFNPCs.SPAWNER_NPC.getStackFromObject(HFNPCs.CARPENTER));
    }

    @Override
    public void initGui(GuiStats gui, List<GuiButton> buttonList, List<GuiLabel> labelList) {
        super.initGui(gui, buttonList, labelList);
        int x = 0;
        int y = 0;
        List<NPC> list = new ArrayList<>(NPC.REGISTRY.values());
        for (int i = 1 + start * 14; i < 15 + start * 14 && i < list.size(); i++) {
            NPC npc = list.get(i);
            buttonList.add(new ButtonRelationsNPC(gui, npc, buttonList.size(), 16 + x * 144, 20 + y * 22));
            y++;

            if (y >= 7) {
                y = 0;
                x++;
            }
        }

        if (start < (NPC.REGISTRY.values().size() - 1) / 14) buttonList.add(new ButtonNext(gui, buttonList.size(), 273, 172));
        if (start != 0) buttonList.add(new ButtonPrevious(gui, buttonList.size(), 20, 172));
    }
}
