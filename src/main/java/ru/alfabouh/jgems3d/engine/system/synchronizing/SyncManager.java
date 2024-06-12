package ru.alfabouh.jgems3d.engine.system.synchronizing;

public class SyncManager {
    public static final Syncer SyncPhysics = new Syncer();
    public static final Syncer SyncRender = new Syncer();
    public static final Syncer PhysicsIteration = new Syncer();

    public static void freeAll() {
        SyncManager.SyncRender.free();
        SyncManager.SyncPhysics.free();
        SyncManager.PhysicsIteration.free();
    }
}
