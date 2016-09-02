package joshie.harvest.cooking.item;

import joshie.harvest.cooking.item.ItemUtensil.Utensil;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.base.item.ItemHFEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

import static joshie.harvest.cooking.item.ItemUtensil.Utensil.KNIFE;

public class ItemUtensil extends ItemHFEnum<ItemUtensil, Utensil> {
    public enum Utensil implements IStringSerializable {
        BLADE, KNIFE;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.US);
        }
    }

    public ItemUtensil() {
        super(HFTab.COOKING, Utensil.class);
    }

    @Override
    public boolean shouldDisplayInCreative(Utensil ingredient) {
        return ingredient == KNIFE;
    }
}
