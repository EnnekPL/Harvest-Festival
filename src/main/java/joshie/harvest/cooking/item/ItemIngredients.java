package joshie.harvest.cooking.item;

import joshie.harvest.api.core.IShippable;
import joshie.harvest.cooking.item.ItemIngredients.Ingredient;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.base.item.ItemHFFoodEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import java.util.Locale;

public class ItemIngredients extends ItemHFFoodEnum<ItemIngredients, Ingredient> implements IShippable {
    public enum Ingredient implements IStringSerializable {
        BUTTER(false), KETCHUP(false), COOKIES(false), EGG_SCRAMBLED(false), SASHIMI(false),
        FLOUR, OIL, RICEBALL, SALT, CHOCOLATE;

        private final boolean isReal;

        Ingredient() {
            isReal = true;
        }

        Ingredient(boolean isReal) {
            this.isReal = isReal;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public ItemIngredients() {
        super(HFTab.COOKING, Ingredient.class);
    }

    @Override
    public long getSellValue(ItemStack stack) {
        switch (getEnumFromStack(stack)) {
            case FLOUR:
                return 45;
            case OIL:
                return 45;
            case RICEBALL:
                return 85;
            case SALT:
                return 25;
            case CHOCOLATE:
                return 85;
            default: return 0;
        }
    }

    @Override
    public boolean shouldDisplayInCreative(Ingredient ingredient) {
        return ingredient.isReal;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    @Override
    public int getHealAmount(ItemStack stack) {
        switch (getEnumFromStack(stack)) {
            case RICEBALL: return 1;
            case CHOCOLATE: return 3;
            default: return 0;
        }
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        switch (getEnumFromStack(stack)) {
            case RICEBALL: return 0.25F;
            case CHOCOLATE: return 0.5F;
            default: return 0F;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.canEat(false) && getHealAmount(stack) > 0) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
    }
}
