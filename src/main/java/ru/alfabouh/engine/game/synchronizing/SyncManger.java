package ru.alfabouh.engine.game.synchronizing;

public class SyncManger {
    public static final Syncer SyncPhysicsAndRender = new Syncer();

    public static void freeAll() {
        SyncManger.SyncPhysicsAndRender.free();
    }
}
