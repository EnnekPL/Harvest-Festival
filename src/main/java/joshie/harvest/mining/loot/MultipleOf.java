package joshie.harvest.mining.loot;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;

import javax.annotation.Nonnull;

import static joshie.harvest.core.lib.HFModInfo.MODID;

public class MultipleOf extends FloorCondition {
    private final int multiple;
    private final boolean reverse;

    public MultipleOf(int multiple, boolean reverse) {
        this.multiple = multiple;
        this.reverse = reverse;
    }

    @Override
    public boolean testFloor(int floor) {
        if (reverse) return floor % multiple != 0;
        return floor % multiple == 0;
    }

    public static class Serializer extends LootCondition.Serializer<MultipleOf> {
        public Serializer() {
            super(new ResourceLocation(MODID, "multiple"), MultipleOf.class);
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull MultipleOf value, @Nonnull JsonSerializationContext context) {
            json.addProperty("of", value.multiple);
            if (value.reverse) {
                json.addProperty("reverse", true);
            }
        }

        @Override
        @Nonnull
        public MultipleOf deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
            return new MultipleOf(JsonUtils.getInt(json, "of", 0), JsonUtils.getBoolean(json, "reverse", false));
        }
    }
}
