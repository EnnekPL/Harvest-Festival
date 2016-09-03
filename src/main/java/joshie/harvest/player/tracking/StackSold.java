package joshie.harvest.player.tracking;

import joshie.harvest.core.util.holder.AbstractDataHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class StackSold extends AbstractDataHolder<StackSold> {
    private final Item item;
    private final int meta;
    private int amount; //Amount of this item sold
    private long sell; //Amount of money made from this item

    private StackSold(Item item, int meta, int amount, long sell) {
        this.item = item;
        this.meta = meta;
        this.amount = amount;
        this.sell = sell;
    }

    public static StackSold of(ItemStack stack, long sell) {
        return new StackSold(stack.getItem(), stack.getItemDamage(), stack.stackSize, sell);
    }

    public long getSellValue() {
        return sell;
    }

    @Override
    public void merge(StackSold stack) {
        amount += stack.amount;
        sell += stack.sell;
    }

    public static StackSold readFromNBT(NBTTagCompound tag) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(tag.getString("ItemName")));
        int meta = tag.getInteger("ItemMeta");
        int amount = tag.getInteger("SellAmount");
        long sell = tag.getLong("SellValue");
        return new StackSold(item, meta, amount, sell);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString("ItemName", Item.REGISTRY.getNameForObject(item).toString());
        tag.setInteger("ItemMeta", meta);
        tag.setInteger("SellAmount", amount);
        tag.setLong("SellValue", sell);
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackSold that = (StackSold) o;
        if (meta != that.meta) return false;
        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + meta;
        return result;
    }
}