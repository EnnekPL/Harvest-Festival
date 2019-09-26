package joshie.harvest.api.core;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum Size implements IStringSerializable {
    SMALL(0), MEDIUM(9000), LARGE(18000), NONE(0);

    private final int relationship;

    Size(int relationship) {
        this.relationship = relationship;
    }

    public int getRelationshipRequirement() {
        return relationship;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public static Size _valueOf(String name)
    {
        Size size = valueOf(name);
        return size == null ? NONE : size;
    }
}