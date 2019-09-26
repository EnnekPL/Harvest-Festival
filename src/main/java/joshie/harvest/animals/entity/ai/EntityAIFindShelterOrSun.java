package joshie.harvest.animals.entity.ai;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.calendar.CalendarDate;
import joshie.harvest.api.calendar.Season;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityAIFindShelterOrSun extends EntityAIBase {
    private final EntityAnimal theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private final double movementSpeed;
    private final World theWorld;

    public EntityAIFindShelterOrSun(EntityAnimal theCreatureIn) {
        this.theCreature = theCreatureIn;
        this.movementSpeed = 1D;
        this.theWorld = theCreatureIn.world;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        boolean outside = theWorld.canSeeSky(new BlockPos(theCreature.posX, theCreature.getEntityBoundingBox().minY, theCreature.posZ));
        CalendarDate date = HFApi.calendar.getDate(theWorld);
        if (outside && (!theWorld.isDaytime() || theWorld.isRaining() || date.getSeason() == Season.WINTER)) {
            Vec3d vec3d = findLocation(false);
            if (vec3d == null) {
                return false;
            } else {
                shelterX = vec3d.x;
                shelterY = vec3d.y;
                shelterZ = vec3d.z;
                return true;
            }
        }
        else if (!outside && (theWorld.isDaytime() && !theWorld.isRaining() && date.getSeason() != Season.WINTER)) {
            Vec3d vec3d = findLocation(true);
            if (vec3d == null) {
                return false;
            } else {
                shelterX = vec3d.x;
                shelterY = vec3d.y;
                shelterZ = vec3d.z;
                return true;
            }
        } else return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !theCreature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        theCreature.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, movementSpeed);
    }

    @Nullable
    private Vec3d findLocation(boolean outside)  {
        Random random = theCreature.getRNG();
        BlockPos blockpos = new BlockPos(theCreature.posX, theCreature.getEntityBoundingBox().minY, theCreature.posZ);

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (theWorld.canSeeSky(blockpos1) == outside && theCreature.getBlockPathWeight(blockpos1) < 0.0F) {
                return new Vec3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }
}