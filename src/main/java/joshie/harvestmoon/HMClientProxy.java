package joshie.harvestmoon;

import static joshie.harvestmoon.lib.HMModInfo.JAVAPATH;
import joshie.harvestmoon.base.ItemBlockBase;
import joshie.harvestmoon.blocks.BlockHMBaseMeta;
import joshie.harvestmoon.blocks.render.RenderBlockPreview;
import joshie.harvestmoon.blocks.render.SpecialRendererFryingPan;
import joshie.harvestmoon.blocks.tiles.TileFryingPan;
import joshie.harvestmoon.crops.Crop;
import joshie.harvestmoon.handlers.RenderHandler;
import joshie.harvestmoon.handlers.events.RenderEvents;
import joshie.harvestmoon.helpers.ClientHelper;
import joshie.harvestmoon.init.HMBlocks;
import joshie.harvestmoon.lib.RenderIds;
import joshie.harvestmoon.npc.EntityNPC;
import joshie.harvestmoon.npc.EntityNPCBuilder;
import joshie.harvestmoon.npc.EntityNPCMiner;
import joshie.harvestmoon.npc.EntityNPCShopkeeper;
import joshie.harvestmoon.npc.RenderNPC;
import joshie.harvestmoon.util.generic.EntityFakeItem;
import joshie.harvestmoon.util.generic.RenderFakeItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.text.WordUtils;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class HMClientProxy extends HMCommonProxy {
    @Override
    public void preInit() {
        ClientHelper.resetClient();

        for (Crop crop : Crop.crops) {
            try {
                RenderHandler.registerCrop(crop.getUnlocalizedName(), Class.forName(JAVAPATH + "crops.render." + crop.getRenderName()));
            } catch (Exception e) {}
        }

        registerRenders(HMBlocks.cookware);
        registerRenders(HMBlocks.woodmachines);

        RenderIds.ALL = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderHandler());
        FMLCommonHandler.instance().bus().register(new RenderEvents());
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        ClientRegistry.bindTileEntitySpecialRenderer(TileFryingPan.class, new SpecialRendererFryingPan());
        RenderingRegistry.registerEntityRenderingHandler(EntityNPC.class, new RenderNPC());
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCBuilder.class, new RenderNPC());
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCShopkeeper.class, new RenderNPC());
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCMiner.class, new RenderNPC());
        RenderingRegistry.registerEntityRenderingHandler(EntityFakeItem.class, new RenderFakeItem());
        for (int i = 0; i < 8; i++) {
            RenderHandler.register(HMBlocks.preview, i, RenderBlockPreview.class);
        }
    }

    private void registerRenders(Block b) {
        BlockHMBaseMeta block = (BlockHMBaseMeta) b;
        ItemBlockBase item = (ItemBlockBase) Item.getItemFromBlock(block);
        for (int i = 0; i < block.getMetaCount(); i++) {
            try {
                String name = sanitizeGeneral(item.getName(new ItemStack(block, 1, i)));
                RenderHandler.register(block, i, Class.forName(JAVAPATH + "blocks.render.Render" + name));
            } catch (Exception e) {}
        }
    }

    private String sanitizeGeneral(String name) {
        name = name.replace(".", " ");
        name = WordUtils.capitalize(name);
        return name.replace(" ", "");
    }
}