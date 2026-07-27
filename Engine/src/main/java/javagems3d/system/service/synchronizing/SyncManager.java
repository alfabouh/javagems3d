/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.service.synchronizing;

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

    public static <T> Set<T> createSyncronisedSet(Set<T> set) {
        return Collections.synchronizedSet(set);
    }

    public static <T> Set<T> createSyncronisedSet() {
        return ConcurrentHashMap.newKeySet();
    }

    public static <R, T> Map<R, T> createSyncronisedMap() {
        return new ConcurrentHashMap<>();
    }
}
