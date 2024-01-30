package ru.BouH.engine.physics.entities;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionObject;

public enum BodyGroup {
    RIGID_BODY(100, btBroadphaseProxy.DefaultFilter, btBroadphaseProxy.AllFilter),
    PLAYER(101, btBroadphaseProxy.DefaultFilter, btBroadphaseProxy.AllFilter),
    BRUSH(102, btBroadphaseProxy.DefaultFilter, btBroadphaseProxy.AllFilter),
    GHOST(103, BodyGroup.GhostFilter, btBroadphaseProxy.AllFilter);


    public static final short GhostFilter = 1 << 8;
    private final int index;
    private final int group;
    private final int mask;

    BodyGroup(int index, int group, int mask) {
        this.index = index;
        this.group = group;
        this.mask = mask;
    }

    public static boolean equals(btCollisionObject btCollisionObject, BodyGroup bodyGroup) {
        return btCollisionObject.getUserIndex() == bodyGroup.getIndex();
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
