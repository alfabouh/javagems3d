package workbench.graphics.scene.ui.asnapshots.helper;

import javagems3d.graphics.rendering.ui.snapshots.SnapshotsTrace;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import workbench.WBench;

public class WBenchUITrackingHelper extends UITrackingHelper {
    protected WBenchUITrackingHelper(SnapshotsTrace snapshotsTrace) {
        super(snapshotsTrace);
    }

    @Override
    public void takeSnapshot() {
        WBenchUITrackingHelper.instantlyTrackAndPush();
        if (UITrackingHelper.dirtyMarker != null) {
            UITrackingHelper.dirtyMarker.marked = true;
        }
    }

    public static WBenchUITrackingHelper INSTANCE() {
        return new WBenchUITrackingHelper(WBench.get().getMapProjectManager().getSnapshotsTrace());
    }

    public static void instantlyTrackAndPush() {
        WBench.get().getMapProjectManager().takeSnapshot();
    }
}
