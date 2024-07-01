package ru.alfabouh.jgems3d.engine.physics.objects.base;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionObject;

public enum BodyGroup {
    ENTITY_DYNAMIC(100, btBroadphaseProxy.DefaultFilter, btBroadphaseProxy.AllFilter, false),
    ENTITY_STATIC(101, btBroadphaseProxy.DefaultFilter, btBroadphaseProxy.AllFilter, true),
    PLAYER(102, BodyGroup.PlayerFilter, btBroadphaseProxy.AllFilter, false),
    GHOST(103, BodyGroup.GhostFilter, BodyGroup.PlayerFilter, true),
    PARTICLE(104, BodyGroup.GhostFilter, btBroadphaseProxy.AllFilter, false),
    LIQUID(105, BodyGroup.LiquidFilter, BodyGroup.PlayerFilter, true);

    public static final short
            DefaultByPassNavChecks = 1 << 7,
            PlayerFilter = 1 << 10,
            GhostFilter = 1 << 8,
            LiquidFilter = 1 << 9;

    private final int index;
    private final int group;
    private final int mask;
    private final boolean isStatic;

    BodyGroup(int index, int group, int mask, boolean isStatic) {
        this.index = index;
        this.group = group;
        this.mask = mask;
        this.isStatic = isStatic;
    }

    public static boolean equals(btCollisionObject btCollisionObject, BodyGroup bodyGroup) {
        return btCollisionObject.getUserIndex() == bodyGroup.getIndex();
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public int getMask() {
        return this.mask;
    }

    public int getGroup() {
        return this.group;
    }

    public int getIndex() {
        return this.index;
    }
}
