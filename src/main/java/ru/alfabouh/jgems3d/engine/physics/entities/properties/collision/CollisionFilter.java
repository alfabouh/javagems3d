package ru.alfabouh.jgems3d.engine.physics.entities.properties.collision;

public enum CollisionFilter {
    PLAYER(1 << 2),
    ST_BODY(1 << 3),
    DN_BODY(1 << 4),
    GHOST(1 << 5),
    LIQUID(1 << 6),
    NOTHING(0x0),
    ALL(0xffff);

    public boolean matchMask(int mask) {
        return (mask & this.getMask()) != 0;
    }
    
    private final int mask;
    CollisionFilter(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return this.mask;
    }
}
