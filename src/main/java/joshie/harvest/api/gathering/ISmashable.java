package joshie.harvest.api.gathering;

import joshie.harvest.api.core.ITiered.ToolTier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static joshie.harvest.api.core.ITiered.ToolTier.BASIC;
import static net.minecraft.block.Block.spawnAsEntity;

/** Implement this on blocks that can be smashed with a hammer or an axe **/
public interface ISmashable {
    enum ToolType {
        AXE, HAMMER
    }

    /** Return the tool type required **/
    ToolType getToolType();

    /** Return the tooltier required to smash this block
     * @param state     the block state
     * @return the tier required**/
    default ToolTier getRequiredTier(IBlockState state) {
        return BASIC;
    }

    /** Called to smash this block
     * @param player    the player smashing the block
     * @param world     the world
     * @param pos       the location of the block
     * @param state     the blocks state
     * @param tier      the tier of the tool used
     * @return return true if something was smashed*/
    default boolean smashBlock(EntityPlayer player, World world, BlockPos pos, IBlockState state, ToolTier tier) {
        ToolTier required = getRequiredTier(state);
        if (required != null && tier.isGreaterThanOrEqualTo(required)) {
            float luck = tier.ordinal() * 0.25F;
            NonNullList<ItemStack> drops = NonNullList.create();
            getDrops(drops, player, world, pos, state, luck);
            world.setBlockToAir(pos); //Clear out the block
            if (drops.size() > 0) {
                if (!world.isRemote) {
                    for (ItemStack drop: drops) {
                        spawnAsEntity(world, pos, drop);
                    }
                }

                return true;
            }
        }

        return false;
    }

    /** The result of breaking this block
     * @param player    the player
     * @param world     the world object
     * @param pos       the position of the block
     * @param state     the block state
     * @param luck      the luck to apply
     * @return the drops */
    void getDrops(NonNullList<ItemStack> drops, EntityPlayer player, World world, BlockPos pos, IBlockState state, float luck);
}