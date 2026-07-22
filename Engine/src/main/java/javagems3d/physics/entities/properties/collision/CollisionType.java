package javagems3d.physics.entities.properties.collision;

public record CollisionType(int mask) {
    public static final CollisionType PLAYER = new CollisionType(1 << 2);
    public static final CollisionType ST_BODY = new CollisionType(1 << 3);
    public static final CollisionType DN_BODY = new CollisionType(1 << 4);
    public static final CollisionType GHOST = new CollisionType(1 << 5);
    public static final CollisionType LIQUID = new CollisionType(1 << 6);
    public static final CollisionType NOTHING = new CollisionType(0x0);
    public static final CollisionType WORLD = new CollisionType(ST_BODY.mask() | DN_BODY.mask() | LIQUID.mask() | PLAYER.mask());
    public static final CollisionType UNIVERSAL = new CollisionType(0xffff);

    public boolean matchMask(int mask) {
        return (mask & this.mask()) != 0;
    }
}
