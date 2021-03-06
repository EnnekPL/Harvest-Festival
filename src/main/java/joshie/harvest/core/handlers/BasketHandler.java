package joshie.harvest.core.handlers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import joshie.harvest.api.HFApi;
import joshie.harvest.core.HFCore;
import joshie.harvest.core.block.BlockStorage;
import joshie.harvest.core.block.BlockStorage.Storage;
import joshie.harvest.core.entity.EntityBasket;
import joshie.harvest.core.item.ItemBlockStorage;
import joshie.harvest.core.network.PacketHandler;
import joshie.harvest.core.network.PacketOpenBasket;
import joshie.harvest.core.tile.TileBasket;
import joshie.harvest.core.util.annotations.HFEvents;
import joshie.harvest.crops.block.BlockHFCrops;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@HFEvents
public class BasketHandler {
    public static boolean forbidsDrop(Block block) {
        return block instanceof BlockDoor || block instanceof BlockFenceGate || block instanceof BlockTrapDoor || block instanceof BlockLever || block instanceof BlockButton || block instanceof BlockHFCrops || block instanceof BlockStorage;
    }

    @SubscribeEvent //TODO: Check that picking up partial items
    public void onItemPickup(EntityItemPickupEvent event) {
        ItemStack stack = event.getItem().getItem();
        if (HFApi.shipping.getSellValue(stack) > 0) {
            NonNullList<ItemStack> list = NonNullList.withSize(1, stack);
            if (EntityBasket.findBasketAndShip(event.getEntityPlayer(), list)) {
                event.getItem().setDead();
                event.setCanceled(true);
            }
        }
    }

    public static void setBasket(World world, BlockPos pos, IItemHandler handler) {
        world.setBlockState(pos, HFCore.STORAGE.getStateFromEnum(Storage.BASKET));
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBasket) {
            TileBasket basket = (TileBasket) tile;
            for (int i = 0; i < handler.getSlots(); i++) {
                basket.handler.setStackInSlot(i, handler.getStackInSlot(i));
            }
        }
    }

    private static final Set<Entity> EMPTY = new HashSet<>();
    private final Cache<EntityPlayer, Set<Entity>> droppedClient = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();
    private final Cache<EntityPlayer, Set<Entity>> droppedServer = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

    private Set<Entity> getSetFromPlayer(EntityPlayer player) {
        try {
            return player.world.isRemote ? droppedClient.get(player, HashSet::new) : droppedServer.get(player, HashSet::new);
        } catch (ExecutionException ex) {
            return EMPTY;
        }
    }

    public static EntityBasket getWearingBasket(EntityPlayer player) {
        for (Entity entity : player.getPassengers()) {
            if (entity instanceof EntityBasket)
                return (EntityBasket) entity;
        }

        return null;
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSneaking()) {
            EntityBasket basket = getWearingBasket(player);
            if (basket != null) {
                PacketHandler.sendToServer(new PacketOpenBasket());
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onRightClickGround(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!player.isSneaking() && player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty() && !forbidsDrop(event.getWorld().getBlockState(event.getPos()).getBlock())) {
            Set<Entity> set = getSetFromPlayer(player);
            player.getPassengers().stream().filter(entity -> entity instanceof EntityBasket && !set.contains(entity)).forEach(entity -> {
                ItemStack basket = HFCore.STORAGE.getStackFromEnum(Storage.BASKET);
                TileEntity tile = (((ItemBlockStorage) basket.getItem()).onBasketUsed(basket, player, player.world, event.getPos(), EnumHand.MAIN_HAND, event.getFace(), 0F, 0F, 0F));
                if (tile instanceof TileBasket) {
                    ((TileBasket) tile).setAppearanceAndContents(((EntityBasket) entity).getEntityItem(), ((EntityBasket) entity).handler);
                    set.add(entity);
                    entity.setDead();
                }
            });
        } else if (player.isSneaking() && player.world.isRemote) {
            EntityBasket basket = getWearingBasket(player);
            if (basket != null) {
                PacketHandler.sendToServer(new PacketOpenBasket());
            }
        }
    }
}
