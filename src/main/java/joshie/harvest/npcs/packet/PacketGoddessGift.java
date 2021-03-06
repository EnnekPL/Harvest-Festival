package joshie.harvest.npcs.packet;

import io.netty.buffer.ByteBuf;
import joshie.harvest.HarvestFestival;
import joshie.harvest.core.handlers.GuiHandler;
import joshie.harvest.core.network.Packet;
import joshie.harvest.core.network.Packet.Side;
import joshie.harvest.core.network.PenguinPacket;
import joshie.harvest.npcs.HFNPCs;
import joshie.harvest.npcs.entity.EntityNPC;
import joshie.harvest.npcs.gui.GuiNPCGift;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;

@Packet(Side.CLIENT)
public class PacketGoddessGift extends PenguinPacket {
    private int npcID;
    @Nonnull
    private ItemStack stack;

    public PacketGoddessGift() {}
    public PacketGoddessGift(EntityNPC npc, @Nonnull ItemStack stack) {
        this.npcID = npc.getEntityId();
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf to) {
        to.writeInt(npcID);
        ByteBufUtils.writeItemStack(to, stack);
    }

    @Override
    public void fromBytes(ByteBuf from) {
        npcID = from.readInt();
        stack = ByteBufUtils.readItemStack(from);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handlePacket(EntityPlayer player) {
        EntityNPC npc = (EntityNPC) player.world.getEntityByID(npcID);
        if (npc != null) {
            if (npc.isEntityAlive()) {
                if (npc.getNPC() == HFNPCs.GODDESS) {
                    GuiNPCGift.GODDESS_GIFT = stack;
                    player.openGui(HarvestFestival.instance, GuiHandler.GIFT_GODDESS, player.world, npcID, -1, EnumHand.MAIN_HAND.ordinal());
                }

                npc.setTalking(player);
            }
        }
    }
}