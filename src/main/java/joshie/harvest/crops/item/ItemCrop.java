package joshie.harvest.crops.item;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.crops.Crop;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.base.item.ItemHFFoodEnum;
import joshie.harvest.core.lib.CreativeSort;
import joshie.harvest.crops.item.ItemCrop.Crops;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Locale;

import static joshie.harvest.core.lib.HFModInfo.MODID;

public class ItemCrop extends ItemHFFoodEnum<ItemCrop, Crops> {
    public ItemCrop() {
        super(Crops.class);
        setCreativeTab(HFTab.FARMING);
    }

    @Override
    public int getSortValue(@Nonnull ItemStack stack) {
        return CreativeSort.CROPS;
    }

    @Override
    public int getHealAmount(@Nonnull ItemStack stack) {
        return getEnumFromStack(stack).getHunger();
    }

    @Override
    public float getSaturationModifier(@Nonnull ItemStack stack) {
        return getEnumFromStack(stack).getSaturation();
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Crop crop = HFApi.crops.getCropFromStack(stack);
        return crop != null ? crop.getLocalizedName(true) : super.getItemStackDisplayName(stack);
    }

    @Override
    @Nonnull
	public ItemStack getCreativeStack(Crops crop) {
        return crop.getCrop().getCropStack(1).getItem() == this ? getStackFromEnum(crop) : ItemStack.EMPTY;
    }

    public enum Crops implements IStringSerializable {
        /** Vanilla crops are included here although never used, so that those updating from 0.5 have no issue **/
        TURNIP(2, 0.4F), POTATO(0, 0F), CUCUMBER(2, 0.25F), STRAWBERRY(2, 0.6F), CABBAGE(1, 0.5F),
        ONION(1, 0.4F), TOMATO(2, 0.5F), CORN(1, 0.3F), PUMPKIN(0, 0F), PINEAPPLE(2, 0.6F), WATERMELON(0, 0F),
        EGGPLANT(1, 0.5F), SPINACH(1, 0.6F), CARROT(0, 0F), SWEET_POTATO(1, 0.35F), GREEN_PEPPER(1, 0.5F), BEETROOT(0, 0F),
        GRASS(0, 0F), WHEAT(0, 0F), NETHER_WART(0, 0F), TUTORIAL_TURNIP(1, 0.1F),
        BANANA(4, 0.4F), GRAPE(3, 0.5F), ORANGE(3, 0.3F), PEACH(3, 0.4F);

        private final ResourceLocation cropLocation;
        private Crop crop;
        private final int hunger;
        private final float saturation;

        Crops(int hunger, float saturation) {
            this.cropLocation = new ResourceLocation(MODID, getName());
            this.hunger = hunger;
            this.saturation = saturation;
        }

        public Crop getCrop() {
            if (crop != null) return crop;
            else {
                crop = Crop.REGISTRY.get(cropLocation);
                return crop;
            }
        }

        public int getHunger() {
            return hunger;
        }

        public float getSaturation() {
            return saturation;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}