package javagems3d.graphics.rendering.ui.snapshots.helper;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.snapshots.SnapshotsTrace;
import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class UITrackingHelper implements AutoCloseable {
    public static final Map<String, UITrackingHelper> cache = new HashMap<>();
    private SnapshotsContainer snapshotsContainer;
    private final SnapshotsTrace snapshotsTrace;
    private String id;

    protected UITrackingHelper(SnapshotsTrace snapshotsTrace) {
        this.snapshotsContainer = null;
        this.snapshotsTrace = snapshotsTrace;
        this.id = null;
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

    public void saveSnapshot() {
        if (ImGui.isItemActive() || ImGui.isItemEdited()) {
            if (!UITrackingHelper.cache.containsKey(this.id)) {
                this.snapshotsContainer = this.snapshotsTrace.takeSnapshot();
                UITrackingHelper.cache.put(this.id, this);
            }
        }
    }

    @Override
    public void close() {
        if (ImGui.isItemDeactivatedAfterEdit()) {
            if (this.snapshotsContainer != null) {
                this.snapshotsTrace.pushSnapshot(this.snapshotsContainer);
                this.snapshotsContainer = null;
            }
            UITrackingHelper.cache.remove(this.id);
        }
    }
}
