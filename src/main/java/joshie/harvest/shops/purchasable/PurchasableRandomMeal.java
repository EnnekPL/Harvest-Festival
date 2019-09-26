package joshie.harvest.shops.purchasable;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.cooking.Recipe;
import joshie.harvest.cooking.CookingHelper;
import joshie.harvest.quests.Quests;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static joshie.harvest.core.lib.HFModInfo.MODID;
import static joshie.harvest.core.registry.ShippingRegistry.SELL_VALUE;

public class PurchasableRandomMeal extends PurchasableMeal {
    private final int seedAdjustment;
    private long adjustableCost;
    @Nonnull
    private ItemStack stack = ItemStack.EMPTY;

    public PurchasableRandomMeal(int seedAdjustment) {
        super(0, new ResourceLocation(MODID, "ice_cream"));
        this.seedAdjustment = seedAdjustment;
        this.setStock(10);
    }

    @Override
    public String getPurchaseableID() {
        return "buy[random_" + seedAdjustment + "]";
    }

    @Override
    public long getCost() {
        return adjustableCost;
    }

    @Override
    public boolean canList(@Nonnull World world, @Nonnull EntityPlayer player) {
        return HFApi.quests.hasCompleted(Quests.SELL_MEALS, player) && canDo(world, player, 1);
    }

    @Override
    public boolean canDo(@Nonnull World world, @Nonnull EntityPlayer player, int amount) {
        Random rand = new Random(HFApi.calendar.getDate(world).hashCode() + seedAdjustment);
        List<Recipe> list = new ArrayList<>(Recipe.REGISTRY.values());
        stack = ItemStack.EMPTY; //Reset the stack
        while (stack.isEmpty() || !stack.hasTagCompound()) {
            stack = CookingHelper.makeRecipe(list.get(rand.nextInt(list.size())));
        }

        if (stack.getTagCompound() != null) {
            adjustableCost = (long) (stack.getTagCompound().getLong(SELL_VALUE) / 1.1);
            adjustableCost = (long) Math.ceil((double) adjustableCost / 50) * 50;
            stack.getTagCompound().setLong(SELL_VALUE, 0L);
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getDisplayStack() {
        if (stack.isEmpty()) {
            stack = CookingHelper.makeRecipe(recipe);
            if (stack.getTagCompound() != null) {
                stack.getTagCompound().setLong(SELL_VALUE, 0L);
            }
        }
        return stack;
    }
}