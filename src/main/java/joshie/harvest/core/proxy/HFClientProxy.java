package joshie.harvest.core.proxy;

import net.minecraft.client.renderer.block.statemap.StateMap;
import static net.minecraft.block.BlockLiquid.LEVEL;

public class HFClientProxy extends HFCommonProxy {
    //public static final HashMap<Item, EntityItemRenderer> RENDER_MAP = new HashMap<>();
    public static final StateMap NO_WATER = new StateMap.Builder().ignore(LEVEL).build();

    @Override
    public boolean isClient() {
        return true;
    }
}
