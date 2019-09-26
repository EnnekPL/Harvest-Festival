package joshie.harvest.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import joshie.harvest.core.base.block.BlockHFEnum;
import joshie.harvest.core.base.item.ItemBlockHF;
import joshie.harvest.core.base.item.ItemHFEnum;
import joshie.harvest.core.base.item.ItemHFFoodEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import javax.annotation.Nonnull;
import java.util.Random;


public class SetEnum extends LootFunction {
    private final String name;

    public SetEnum(LootCondition[] conditionsIn, String name) {
        super(conditionsIn);
        this.name = name;
    }

    @Override
    @Nonnull
    public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
        if (stack.getItem() instanceof ItemHFFoodEnum) {
            ItemStack ret = ((ItemHFFoodEnum)stack.getItem()).getStackFromEnumString(name);
            ret.setCount(stack.getCount()); ///Update the size
            return ret;
        } else if (stack.getItem() instanceof ItemHFEnum) {
            ItemStack ret = ((ItemHFEnum)stack.getItem()).getStackFromEnumString(name);
            ret.setCount(stack.getCount()); ///Update the size
            return ret;
        } else if (stack.getItem() instanceof ItemBlockHF && ((ItemBlockHF)stack.getItem()).getBlock() instanceof BlockHFEnum) {
            ItemStack ret = ((BlockHFEnum)((ItemBlockHF)stack.getItem()).getBlock()).getStackFromEnumString(name);
            ret.setCount(stack.getCount());
            return ret;
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetEnum> {
        public Serializer() {
            super(new ResourceLocation("hf_set_enum"), SetEnum.class);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull SetEnum functionClazz, @Nonnull JsonSerializationContext serializationContext) {
            object.addProperty("enum", functionClazz.name);
        }

        @Override
        @Nonnull
        public SetEnum deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootCondition[] conditionsIn) {
            return new SetEnum(conditionsIn, object.get("enum").getAsString());
        }
    }
}
