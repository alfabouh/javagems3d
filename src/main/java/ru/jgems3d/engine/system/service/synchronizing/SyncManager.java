/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.service.synchronizing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SyncManager {
    public static final Set<Syncer> syncerSet = new HashSet<>();

    public static Syncer SyncPhysics;

    static {
        SyncManager.SyncPhysics = SyncManager.createNewSyncer();
    }

    public static Syncer createNewSyncer() {
        Syncer s = new Syncer();
        SyncManager.syncerSet.add(s);
        return s;
    }

    public static void freeAll() {
        SyncManager.syncerSet.forEach(Syncer::free);
    }

    public static <T> List<T> createSyncronisedList(List<T> list) {
        return Collections.synchronizedList(list);
    }

    public static <T> Set<T> createSyncronisedSet() {
        return ConcurrentHashMap.newKeySet();
    }

    public static <R, T> Map<R, T> createSyncronisedMap() {
        return new ConcurrentHashMap<>();
    }
}
