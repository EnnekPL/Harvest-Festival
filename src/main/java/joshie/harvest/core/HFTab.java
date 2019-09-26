package joshie.harvest.core;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.core.ITiered.ToolTier;
import joshie.harvest.buildings.HFBuildings;
import joshie.harvest.cooking.HFCooking;
import joshie.harvest.cooking.item.ItemMeal.Meal;
import joshie.harvest.core.lib.CreativeSort;
import joshie.harvest.core.lib.HFModInfo;
import joshie.harvest.core.util.annotations.HFLoader;
import joshie.harvest.core.util.interfaces.ICreativeSorted;
import joshie.harvest.crops.HFCrops;
import joshie.harvest.fishing.HFFishing;
import joshie.harvest.fishing.item.ItemFish.Fish;
import joshie.harvest.mining.HFMining;
import joshie.harvest.tools.HFTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Comparator;

import static joshie.harvest.mining.item.ItemMaterial.Material.ADAMANTITE;

@HFLoader(priority = 0)
@SuppressWarnings("unused")
public abstract class HFTab extends CreativeTabs {
    public static final HFTab TOWN = new HFTab("town") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(HFBuildings.STRUCTURES, 1, 0);
		}};
    public static final HFTab MINING = new HFTab("mining") {
		@Override
		public ItemStack createIcon() {
			return HFMining.MATERIALS.getStackFromEnum(ADAMANTITE);
		}};
    public static final HFTab FARMING = new HFTab("farming") {
		@Override
		public ItemStack createIcon() {
			return HFCrops.STRAWBERRY.getCropStack(1);
		}};
    public static final HFTab COOKING = new HFTab("cooking") {
		@Override
		public ItemStack createIcon() {
			return HFCooking.MEAL.getStackFromEnum(Meal.SALAD);
		}};
    public static final HFTab GATHERING = new HFTab("gathering") {
		@Override
		public ItemStack createIcon() {
			return HFTools.AXES.get(ToolTier.BASIC).getStack();
		}};
    public static final HFTab FISHING = new HFTab("fishing") {
		@Override
		public ItemStack createIcon() {
			return HFFishing.FISH.getStackFromEnum(Fish.PUPFISH);
		}};

    public HFTab(String label) {
        super(label);
        setBackgroundImageName("hf.png");
        setNoTitle();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public String getTranslationKey() {
        return HFModInfo.MODID + ".tab." + this.getTabLabel();
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Override
    public int getSearchbarWidth() {
        return 69;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> list) {
        super.displayAllRelevantItems(list);
        list.sort(new Alphabetical());
    }

    private static class Alphabetical implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            ItemStack stack1 = ((ItemStack) o1);
            ItemStack stack2 = ((ItemStack) o2);

            Item item1 = stack1.getItem();
            Item item2 = stack2.getItem();

            int value1 = 500;
            int value2 = 500;


            if (item1 instanceof ICreativeSorted) {
                value1 = ((ICreativeSorted) item1).getSortValue(stack1);
            } else if (HFApi.crops.getCropFromStack(stack1) != null) {
                value1 = CreativeSort.CROPS;
            }

            if (item2 instanceof ICreativeSorted) {
                value2 = ((ICreativeSorted) item2).getSortValue(stack2);
            } else if (HFApi.crops.getCropFromStack(stack2) != null) {
                value2 = CreativeSort.CROPS;
            }

            return value1 == value2 ? stack1.getDisplayName().compareTo(stack2.getDisplayName()) : value1 > value2 ? 1 : -1;
        }
    }
}