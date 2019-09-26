package joshie.harvest.gathering.block;

import static net.minecraftforge.common.EnumPlantType.Plains;

import java.util.Locale;

import javax.annotation.Nonnull;

import joshie.harvest.core.HFTab;
import joshie.harvest.core.base.block.BlockHFEnum;
import joshie.harvest.core.block.BlockFlower.FlowerType;
import joshie.harvest.core.entity.EntityBasket;
import joshie.harvest.core.lib.CreativeSort;
import joshie.harvest.core.lib.HFModInfo;
import joshie.harvest.core.util.interfaces.ISellable;
import joshie.harvest.gathering.block.BlockNature.NaturalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockNature extends BlockHFEnum<BlockNature, NaturalBlock> implements IPlantable
{
    protected static final AxisAlignedBB GRASS_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);

    public BlockNature()
    {
        super(Material.PLANTS, NaturalBlock.class, HFTab.GATHERING);
        setSoundType(SoundType.PLANT);
    }

    public enum NaturalBlock implements IStringSerializable, ISellable
    {
        MATSUTAKE(350L), BAMBOO(50L), MINT(20L), CHAMOMILE(30L), LAVENDER(40L);

        private final long sell;

        NaturalBlock(long sell)
        {
            this.sell = sell;
        }

        @Override
        public long getSellValue()
        {
            return sell;
        }

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, fortune);
        EntityPlayer player = harvesters.get();
        if (player != null)
        {
            EntityBasket.findBasketAndShip(player, drops);
        }
    }

    @Override
    public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos)
    {
        IBlockState soil = world.getBlockState(pos.down());
        return super.canPlaceBlockAt(world, pos) && canBlockStay(world, pos.down(), soil);
    }

    private boolean canSustainBush(IBlockState state)
    {
        return state.getMaterial() == Material.GROUND;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        checkAndDropBlock(world, pos, state);
    }

    private void checkAndDropBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!canBlockStay(world, pos, state))
        {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        if (state.getBlock() == this)
        {
            IBlockState soil = world.getBlockState(pos.down());
            return soil.getBlock().canSustainPlant(soil, world, pos.down(), net.minecraft.util.EnumFacing.UP, this);
        }

        return this.canSustainBush(world.getBlockState(pos.down()));
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return GRASS_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return Plains;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return getDefaultState();
    }

    @Override
    public int getEntityLifeSpan(@Nonnull ItemStack stack, World world)
    {
        return stack.getItemDamage() == FlowerType.GODDESS.ordinal() ? 50 : 6000;
    }

    @Override
    public int getSortValue(@Nonnull ItemStack stack)
    {
        return CreativeSort.TOOLS - 100;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(Item item, String name)
    {
        for (int i = 0; i < values.length; i++)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(HFModInfo.MODID, property.getName() + "_" + getEnumFromMeta(i).getName()), "inventory"));
        }
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
