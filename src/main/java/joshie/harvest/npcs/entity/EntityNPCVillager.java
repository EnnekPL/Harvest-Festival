package joshie.harvest.npcs.entity;

import joshie.harvest.api.npc.NPC;
import joshie.harvest.npcs.HFNPCs;
import net.minecraft.world.World;

public class EntityNPCVillager extends EntityNPCHuman<EntityNPCVillager> {
    public EntityNPCVillager(World world) {
        super(world, HFNPCs.GODDESS);
    }

    public EntityNPCVillager(World world, NPC npc) {
        super(world, npc);
    }

    private EntityNPCVillager(EntityNPCVillager entity) {
        super(entity);
    }

    @Override
    protected EntityNPCVillager getNewEntity(EntityNPCVillager entity) {
        return new EntityNPCVillager(entity);
    }
}