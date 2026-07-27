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

package javagems3d.graphics.rendering.ui.snapshots.helper;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.snapshots.SnapshotsTrace;
import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class UITrackingHelper implements AutoCloseable {
    public static DirtyMarker dirtyMarker = null;

    public static final Map<String, UITrackingHelper> cache = new HashMap<>();
    private SnapshotsContainer snapshotsContainer;
    private final SnapshotsTrace snapshotsTrace;
    private String id;

    protected UITrackingHelper(SnapshotsTrace snapshotsTrace) {
        this.snapshotsContainer = null;
        this.snapshotsTrace = snapshotsTrace;
        this.id = null;
    }

    public static void startTrackingRowOfUITrackers() {
        UITrackingHelper.dirtyMarker = new DirtyMarker();
    }

    public static boolean stopTrackingRowOfUITrackersAndGetResult() {
        if (UITrackingHelper.dirtyMarker != null) {
            boolean f = UITrackingHelper.dirtyMarker.marked;
            UITrackingHelper.dirtyMarker = null;
            return f;
        }
        return false;
    }

    public static UITrackingHelper create(String id, Supplier<UITrackingHelper> trackingHelperSupplier) {
        if (UITrackingHelper.cache.containsKey(id)) {
            return UITrackingHelper.cache.get(id);
        }
        UITrackingHelper uiTrackingHelper = trackingHelperSupplier.get();
        uiTrackingHelper.id = id;
        return uiTrackingHelper;
    }

    public abstract void takeSnapshot();

    public boolean saveSnapshot() {
        if (ImGui.isItemActive() || ImGui.isItemEdited()) {
            if (!UITrackingHelper.cache.containsKey(this.id)) {
                this.snapshotsContainer = this.snapshotsTrace.takeSnapshot();
                UITrackingHelper.cache.put(this.id, this);
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        if (ImGui.isItemDeactivatedAfterEdit()) {
            if (this.snapshotsContainer != null) {
                this.snapshotsTrace.pushSnapshot(this.snapshotsContainer);
                if (UITrackingHelper.dirtyMarker != null) {
                    UITrackingHelper.dirtyMarker.marked = true;
                }
                this.snapshotsContainer = null;
            }
            UITrackingHelper.cache.remove(this.id);
        }
    }

    public static class DirtyMarker {
        public boolean marked;
    }
}
