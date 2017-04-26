package joshie.harvest.api.gathering;

import joshie.harvest.api.calendar.Season;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** This is for registering blocks to be generated by the gathering system **/
public interface IGatheringRegistry {
    /** Register a block state to be generated in a specific season
     * @param state the block state to generate
     * @param weight weight for this to generate
     * @param seasons the season this should generate in,
     *                leave this blank if it should generate
     *                in every single season. **/
    void registerGathering(IBlockState state, double weight, Season... seasons);

    /** Returns a random block state for this season
     * @param season the current season
     * @return a block state */
    @Nullable
    IBlockState getRandomStateForSeason(@Nonnull Season season);

    /** Register a block state as being a valid spawn location for gathering blocks
     * @param block the block that is valid */
    void registerValidGatheringSpawn(Block block);
}
