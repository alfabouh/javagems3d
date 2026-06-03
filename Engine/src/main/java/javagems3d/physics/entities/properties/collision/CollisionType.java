package javagems3d.physics.entities.properties.collision;

public enum CollisionType {
    PLAYER(1 << 2),
    ST_BODY(1 << 3),
    DN_BODY(1 << 4),
    GHOST(1 << 5),
    LIQUID(1 << 6),
    NOTHING(0x0),
    WORLD(ST_BODY.getMask() | DN_BODY.getMask() | LIQUID.getMask()),
    UNIVERSAL(0xffff);

    private final int mask;

    CollisionType(int mask) {
        this.mask = mask;
    }

    public boolean matchMask(int mask) {
        return (mask & this.getMask()) != 0;
    }

    public int getMask() {
        return this.mask;
    }
}
