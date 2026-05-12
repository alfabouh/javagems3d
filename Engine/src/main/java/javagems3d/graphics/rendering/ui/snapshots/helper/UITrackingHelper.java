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
