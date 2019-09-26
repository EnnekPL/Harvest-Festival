package joshie.harvest.mining;

import joshie.harvest.HarvestFestival;
import joshie.harvest.animals.render.ModelHarvestChicken;
import joshie.harvest.animals.render.ModelHarvestCow;
import joshie.harvest.animals.render.ModelHarvestSheep;
import joshie.harvest.api.HFApi;
import joshie.harvest.api.mining.MiningContext;
import joshie.harvest.core.helpers.RegistryHelper;
import joshie.harvest.core.lib.EntityIDs;
import joshie.harvest.core.util.annotations.HFLoader;
import joshie.harvest.mining.block.*;
import joshie.harvest.mining.block.BlockOre.Ore;
import joshie.harvest.mining.entity.EntityDarkChick;
import joshie.harvest.mining.entity.EntityDarkChicken;
import joshie.harvest.mining.entity.EntityDarkCow;
import joshie.harvest.mining.entity.EntityDarkSheep;
import joshie.harvest.mining.gen.MiningProvider;
import joshie.harvest.mining.item.ItemDarkDrop;
import joshie.harvest.mining.item.ItemDarkDrop.DarkDrop;
import joshie.harvest.mining.item.ItemDarkSpawner;
import joshie.harvest.mining.item.ItemDarkSpawner.DarkSpawner;
import joshie.harvest.mining.item.ItemMaterial;
import joshie.harvest.mining.item.ItemMaterial.Material;
import joshie.harvest.mining.item.ItemMiningTool;
import joshie.harvest.mining.loot.*;
import joshie.harvest.mining.render.*;
import joshie.harvest.mining.tile.TileElevator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import static joshie.harvest.api.calendar.Season.*;
import static joshie.harvest.core.HFCore.FLOWERS;
import static joshie.harvest.core.HFTab.MINING;
import static joshie.harvest.core.block.BlockFlower.FlowerType.WEED;
import static joshie.harvest.core.helpers.ConfigHelper.getBoolean;
import static joshie.harvest.core.helpers.ConfigHelper.getInteger;
import static joshie.harvest.core.helpers.RegistryHelper.registerOreIfNotExists;
import static joshie.harvest.core.helpers.RegistryHelper.registerTiles;
import static joshie.harvest.core.lib.HFModInfo.MODID;
import static joshie.harvest.mining.MiningHelper.*;
import static net.minecraft.init.Items.DIAMOND;
import static net.minecraft.init.Items.EMERALD;

@HFLoader
@SuppressWarnings("unused")
public class HFMining {
    public static final BlockOre ORE = new BlockOre().register("ore");
    public static final BlockStone STONE = new BlockStone().register("stone");
    public static final BlockDirt DIRT = new BlockDirt().setBlockUnbreakable().setResistance(6000000.0F).register("dirt");
    public static final BlockDirt DIRT_DECORATIVE = new BlockDirt().setHardness(0.5F).register("dirt_decorative");
    public static final BlockLadder LADDER = new BlockLadder().register("ladder");
    public static final BlockPortal PORTAL = new BlockPortal().setBlockUnbreakable().register("portal");
    public static final BlockElevator ELEVATOR = new BlockElevator().setBlockUnbreakable().register("elevator");
    public static final ItemMaterial MATERIALS = new ItemMaterial().register("materials");
    public static final ItemDarkSpawner DARK_SPAWNER = new ItemDarkSpawner().register("dark_spawner");
    public static final ItemDarkDrop DARK_DROP = new ItemDarkDrop().register("dark_drop");
    public static final ItemMiningTool MINING_TOOL = new ItemMiningTool().register("tool_mining");
    public static DimensionType MINE_WORLD;

    public static void preInit() {
        MINE_WORLD = DimensionType.register("The Mine", "_hf_mine", MINING_ID, MiningProvider.class, false);
        DimensionManager.registerDimension(MINING_ID, MINE_WORLD);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "dark_cow"), EntityDarkCow.class, "dark_cow", EntityIDs.DARK_COW, HarvestFestival.instance, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "dark_sheep"), EntityDarkSheep.class, "dark_sheep", EntityIDs.DARK_SHEEP, HarvestFestival.instance, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "dark_chicken"), EntityDarkChicken.class, "dark_chicken", EntityIDs.DARK_CHICKEN, HarvestFestival.instance, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "dark_chick"), EntityDarkChick.class, "dark_chick", EntityIDs.DARK_CHICK, HarvestFestival.instance, 80, 3, true);
        OreDictionary.registerOre("feather", DARK_DROP.getStackFromEnum(DarkDrop.FEATHER));
        OreDictionary.registerOre("leather", DARK_DROP.getStackFromEnum(DarkDrop.LEATHER));
        registerTiles(TileElevator.class);
        LootConditionManager.registerCondition(new From.Serializer());
        LootConditionManager.registerCondition(new Between.Serializer());
        LootConditionManager.registerCondition(new Between100.Serializer());
        LootConditionManager.registerCondition(new EndsIn.Serializer());
        LootConditionManager.registerCondition(new Exact.Serializer());
        LootConditionManager.registerCondition(new MultipleOf.Serializer());
        LootConditionManager.registerCondition(new Obtained.Serializer());
        LootConditionManager.registerCondition(new Seasonal.Serializer());
        registerOreIfNotExists("gemRuby", MATERIALS.getStackFromEnum(Material.RUBY));
        registerOreIfNotExists("gemTopaz", MATERIALS.getStackFromEnum(Material.TOPAZ));
        registerOreIfNotExists("gemAmethyst", MATERIALS.getStackFromEnum(Material.AMETHYST));
        registerSellable(DIAMOND, 100L);
        registerSellable(EMERALD, 80L);
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient() {
        ModelLoader.setCustomStateMapper(DIRT, new BakedDirt.StateMapper());
        ModelLoader.setCustomStateMapper(DIRT_DECORATIVE, new BakedDirt.StateMapper());
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkCow.class, RenderDarkCow:: new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkSheep.class, RenderDarkSheep:: new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkChicken.class, RenderDarkChicken:: new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkChick.class, RenderDarkChick:: new);

        //Register the dark spawner
        RegistryHelper.registerEntityRenderer(DARK_SPAWNER, FakeAnimalsItemRenderer.INSTANCE);
        FakeAnimalsItemRenderer.INSTANCE.register(DarkSpawner.COW, "dark_cow", new ModelHarvestCow.Adult());
        FakeAnimalsItemRenderer.INSTANCE.register(DarkSpawner.SHEEP, "dark_sheep", new ModelHarvestSheep.Wooly());
        FakeAnimalsItemRenderer.INSTANCE.register(DarkSpawner.CHICKEN, "dark_chicken", new ModelHarvestChicken.Adult());
        FakeAnimalsItemRenderer.INSTANCE.register(DarkSpawner.CHICK, "dark_chick", new ModelHarvestChicken.Child());
    }

    public static void init() {
        HFApi.npc.getGifts().addToBlacklist(DARK_SPAWNER, MINING_TOOL, DIRT, DIRT_DECORATIVE, STONE, LADDER, PORTAL, ORE);
        //Spring, Summer, Autumn values
        MiningContext copper = new MiningContext(COPPER_FLOOR);
        MiningContext silver = new MiningContext(SILVER_FLOOR);
        MiningContext gold = new MiningContext(GOLD_FLOOR);
        MiningContext mystril = new MiningContext(MYSTRIL_FLOOR);
        HFApi.mining.registerOre(copper, FLOWERS.getStateFromEnum(WEED), 40D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.ROCK), 100D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.COPPER), 6D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.AMETHYST), 6D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(silver, ORE.getStateFromEnum(Ore.SILVER), 7D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(silver, ORE.getStateFromEnum(Ore.TOPAZ), 5D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(gold, ORE.getStateFromEnum(Ore.GOLD), 8D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(gold, ORE.getStateFromEnum(Ore.RUBY), 6D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(gold, ORE.getStateFromEnum(Ore.JADE), 5D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.EMERALD), 4D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.MYSTRIL), 5D, SPRING, SUMMER, AUTUMN);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.DIAMOND), 3D, SPRING, SUMMER, AUTUMN);

        //Winter values
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.ROCK), 110D, WINTER);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.COPPER), 9D, WINTER);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.GEM), 5D, WINTER);
        HFApi.mining.registerOre(copper, ORE.getStateFromEnum(Ore.AMETHYST), 6D, WINTER);
        HFApi.mining.registerOre(silver, ORE.getStateFromEnum(Ore.SILVER), 10D, WINTER);
        HFApi.mining.registerOre(silver, ORE.getStateFromEnum(Ore.TOPAZ), 6D, WINTER);
        HFApi.mining.registerOre(gold, ORE.getStateFromEnum(Ore.GOLD), 10D, WINTER);
        HFApi.mining.registerOre(gold, ORE.getStateFromEnum(Ore.RUBY), 7D, WINTER);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.JADE), 5D, WINTER);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.EMERALD), 4D, WINTER);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.MYSTRIL), 5D, WINTER);
        HFApi.mining.registerOre(mystril, ORE.getStateFromEnum(Ore.DIAMOND), 3D, WINTER);
    }

    private static void registerSellable(Item item, long value) {
        HFApi.shipping.registerSellable(new ItemStack(item), value);
        item.setCreativeTab(MINING);
    }

    public static int MINING_ID;
    public static boolean ANIMALS_ON_EVERY_FLOOR;

    public static void configure() {
        MINING_ID = getInteger("Mining world ID", 4);
        ANIMALS_ON_EVERY_FLOOR = getBoolean("Spawn dark animals on every floor instead of hordes", false);
    }
}