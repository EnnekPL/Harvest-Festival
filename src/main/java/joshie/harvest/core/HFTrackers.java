package joshie.harvest.core;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import joshie.harvest.animals.tracker.AnimalTrackerServer;
import joshie.harvest.calendar.data.Calendar;
import joshie.harvest.calendar.data.CalendarClient;
import joshie.harvest.calendar.data.CalendarSavedData;
import joshie.harvest.calendar.data.CalendarServer;
import joshie.harvest.core.handlers.DailyTickHandler;
import joshie.harvest.core.handlers.ServerHandler;
import joshie.harvest.core.helpers.EntityHelper;
import joshie.harvest.core.lib.HFModInfo;
import joshie.harvest.mining.gen.MineManager;
import joshie.harvest.player.PlayerLoader;
import joshie.harvest.player.PlayerTracker;
import joshie.harvest.player.PlayerTrackerClient;
import joshie.harvest.player.PlayerTrackerServer;
import joshie.harvest.town.data.TownSavedData;
import joshie.harvest.town.tracker.TownTracker;
import joshie.harvest.town.tracker.TownTrackerClient;
import joshie.harvest.town.tracker.TownTrackerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class HFTrackers {
    /*####################World Based Trackers##########################*/
    private static final TIntObjectMap<ServerHandler> SERVER_WORLDS = new TIntObjectHashMap<>();

    @SideOnly(Side.CLIENT)
    public static void resetClient() {
        CLIENT_PLAYER = new PlayerTrackerClient();
        CLIENT_CALENDAR = new CalendarClient();
        CLIENT_TOWNS = new TownTrackerClient();
    }

    public static void resetServer() {
        SERVER_WORLDS.clear();
        SERVER_CALENDAR = null;
        SERVER_TOWNS = null;
        MINE_MANAGER = null;
    }

    private static ServerHandler getServer(World world) {
        ServerHandler handler = SERVER_WORLDS.get(world.provider.getDimension());
        if (handler == null) {
            handler = new ServerHandler(world); //Create a new handler
            SERVER_WORLDS.put(world.provider.getDimension(), handler);
        }

        return handler;
    }

    @SuppressWarnings("unchecked")
    public static AnimalTrackerServer getAnimalTracker(World world) {
        return getServer(world).getAnimalTracker();
    }

    public static DailyTickHandler getTickables(World world) {
        return getServer(world).getTickables();
    }

    /*####################Town Trackers##########################*/
    private static final String TOWN_NAME = HFModInfo.CAPNAME + "-Towns";
    @SideOnly(Side.CLIENT)
    private static TownTracker CLIENT_TOWNS;
    private static TownTrackerServer SERVER_TOWNS;

    @SuppressWarnings("unchecked")
    public static <C extends TownTracker> C getTowns(World world) {
        return (world.isRemote) ? (C) CLIENT_TOWNS : (C) getServerTowns(world);
    }

    private static TownTrackerServer getServerTowns(World overworld) {
        if (SERVER_TOWNS == null) {
            TownSavedData data = (TownSavedData) overworld.getPerWorldStorage().getOrLoadData(TownSavedData.class, TOWN_NAME);
            if (data == null) {
                data = new TownSavedData(TOWN_NAME);
                overworld.getPerWorldStorage().setData(TOWN_NAME, data);
            }

            SERVER_TOWNS = data.getData();
            SERVER_TOWNS.setWorld(data, overworld);
        }

        return SERVER_TOWNS;
    }

    public static void markTownsDirty() {
        SERVER_TOWNS.markDirty();
    }

    /*####################Calendar Trackers##########################*/
    private static final String CALENDAR_NAME = HFModInfo.CAPNAME + "-Calendar";
    @SideOnly(Side.CLIENT)
    private static Calendar CLIENT_CALENDAR;
    private static CalendarServer SERVER_CALENDAR;

    @SuppressWarnings("unchecked")
    public static <C extends Calendar> C getCalendar(World world) {
        if (world.isRemote && CLIENT_CALENDAR == null) {
            resetClient();
        }
        return (world.isRemote) ? (C) CLIENT_CALENDAR : (C) getServerCalendar(world);
    }

    private static CalendarServer getServerCalendar(World overworld) {
        if (SERVER_CALENDAR == null) {
            CalendarSavedData data = (CalendarSavedData) overworld.getPerWorldStorage().getOrLoadData(CalendarSavedData.class, CALENDAR_NAME);
            if (data == null) {
                data = new CalendarSavedData(CALENDAR_NAME);
                overworld.getPerWorldStorage().setData(CALENDAR_NAME, data);
            }

            SERVER_CALENDAR = data.getCalendar();
            SERVER_CALENDAR.setWorld(data, overworld);
            SERVER_CALENDAR.recalculate(overworld);
        }

        return SERVER_CALENDAR;
    }

    public static void markCalendarDirty() {
        SERVER_CALENDAR.markDirty();
    }

    /*####################Mining Trackers##########################*/
    private static final String MINING_NAME = "HF-Mine-Manager";
    private static MineManager MINE_MANAGER;

    public static MineManager getMineManager(World world) {
        if (MINE_MANAGER == null) {
            MINE_MANAGER = (MineManager) world.getPerWorldStorage().getOrLoadData(MineManager.class, MINING_NAME);
            if (MINE_MANAGER == null) {
                MINE_MANAGER = new MineManager(MINING_NAME);
                world.getPerWorldStorage().setData(MINING_NAME, MINE_MANAGER);
            }
        }

        return MINE_MANAGER;
    }

    /*####################Player Trackers#############################*/
    @SideOnly(Side.CLIENT)
    private static PlayerTracker CLIENT_PLAYER;
    private static final HashMap<UUID, PlayerTrackerServer> SERVER_PLAYERS = new HashMap<>();

    public static Collection<PlayerTrackerServer> getPlayerTrackers() {
        return SERVER_PLAYERS.values();
    }
    
    @SideOnly(Side.CLIENT)
    public static PlayerTrackerClient getClientPlayerTracker() {
        return (PlayerTrackerClient) CLIENT_PLAYER;
    }

    public static <P extends PlayerTracker> P getPlayerTrackerFromPlayer(EntityPlayer player) {
        return getPlayerTracker(player.world, EntityHelper.getPlayerUUID(player));
    }

    @SuppressWarnings("unchecked")
    public static <P extends PlayerTracker> P getPlayerTracker(World world, UUID uuid) {
        if (world.isRemote) return (P) CLIENT_PLAYER;
        else {
            P tracker = (P) SERVER_PLAYERS.get(uuid);
            return tracker != null ? tracker: (P) PlayerLoader.getDataFromUUID(null, uuid);
        }
    }

    public static void setPlayerData(UUID uuid, PlayerTrackerServer data) {
        SERVER_PLAYERS.put(uuid, data);
    }
}
