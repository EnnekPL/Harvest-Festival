package joshie.harvest.shops;

import com.google.common.collect.HashMultimap;
import joshie.harvest.api.HFApi;
import joshie.harvest.api.calendar.Weekday;
import joshie.harvest.api.shops.IPurchaseable;
import joshie.harvest.api.shops.IShop;
import joshie.harvest.api.shops.IShopGuiOverlay;
import joshie.harvest.calendar.CalendarHelper;
import joshie.harvest.core.util.Text;
import joshie.harvest.shops.purchaseable.Purchaseable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Shop implements IShop {
    public static final IForgeRegistry<ShopEntry> REGISTRY = new RegistryBuilder<ShopEntry>().setName(new ResourceLocation("harvestfestival", "shop_items")).setType(ShopEntry.class).setIDRange(0, 100000).create();
    private final List<IPurchaseable> contents = new ArrayList<>();
    private final HashMultimap<Weekday, OpeningHours> open = HashMultimap.create();
    public final ResourceLocation resourceLocation;
    public final String unlocalizedName;
    @SideOnly(Side.CLIENT)
    private IShopGuiOverlay overlay;

    public static class ShopEntry extends IForgeRegistryEntry.Impl<ShopEntry> {
        private final IPurchaseable purchaseable;
        public ShopEntry(IPurchaseable purchaseable) {
            this.purchaseable = purchaseable;
        }

        public IPurchaseable getPurchaseable() {
            return purchaseable;
        }
    }

    public Shop(ResourceLocation resource) {
        resourceLocation = resource;
        unlocalizedName = resource.getResourceDomain() + ".shop." + resource.getResourcePath();
    }

    @Override
    public IShop addOpening(Weekday day, int opening, int closing) {
        open.get(day).add(new OpeningHours(opening, closing));
        return this;
    }

    @Override
    public IShop addItem(IPurchaseable item) {
        if (item != null) {
            this.contents.add(item);
            REGISTRY.register(new ShopEntry(item).setRegistryName(Shop.getRegistryName(resourceLocation, item)));
        }

        return this;
    }

    public static ResourceLocation getRegistryName(ResourceLocation resource, IPurchaseable item) {
        return new ResourceLocation(Loader.instance().activeModContainer().getModId().toLowerCase(Locale.ENGLISH) + ":" + resource.getResourcePath() + "_" + item.getPurchaseableID());
    }

    @Override
    public IShop addItem(long cost, ItemStack... items) {
        return addItem(new Purchaseable(cost, items));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IShop setGuiOverlay(IShopGuiOverlay overlay) {
        this.overlay = overlay;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public IShopGuiOverlay getGuiOverlay() {
        return overlay;
    }

    public String getLocalizedName() {
        return Text.localize(unlocalizedName);
    }

    public String getWelcome() {
        return Text.getRandomSpeech(null, resourceLocation, unlocalizedName + ".greeting", 10);
    }

    public List<IPurchaseable> getContents(@Nonnull EntityPlayer player) {
        List<IPurchaseable> contents = new ArrayList<>();
        for (IPurchaseable purchaseable : this.contents) {
            if (purchaseable.canList(player.worldObj, player)) {
                contents.add(purchaseable);
            }
        }

        return contents;
    }

    public boolean isOpen(World world, @Nullable EntityPlayer player) {
        if (HFShops.TWENTY_FOUR_HOUR_SHOPPING) return true;
        Weekday day = HFApi.calendar.getDate(world).getWeekday();
        for (OpeningHours hours: open.get(day)) {
            long daytime = CalendarHelper.getTime(world); //0-23999 by default
            int scaledOpening = CalendarHelper.getScaledTime(hours.open);
            int scaledClosing = CalendarHelper.getScaledTime(hours.close);
            boolean isOpen = daytime >= scaledOpening && daytime <= scaledClosing;
            if (isOpen && (player == null || getContents(player).size() > 0)) return true;
        }

        return false;
    }

    public boolean isPreparingToOpen(World world) {
        if (HFShops.TWENTY_FOUR_HOUR_SHOPPING) return false;
        Weekday day = HFApi.calendar.getDate(world).getWeekday();
        for (OpeningHours hours: open.get(day)) {
            long daytime = CalendarHelper.getTime(world); //0-23999 by default
            int hourHalfBeforeWork = fix(CalendarHelper.getScaledTime(hours.open) - 1500);
            if(daytime >= hourHalfBeforeWork) return true;
        }

        return false;
    }

    private int fix(int i) {
        return Math.min(24000, Math.max(0, i));
    }

    /** The integers in here are as follows
     * 1000 = 1 AM
     * 2500 = 2:30am
     * 18000 = 6PM
     * etc. */
    private static class OpeningHours {
        private final int open;
        private final int close;

        OpeningHours(int open, int close) {
            this.open = open;
            this.close = close;
        }
    }
}