package javagems3d.graphics.rendering.ui.snapshots.instances;

public interface ISnapshotCompatible <A extends ISnapshotCompatible.SnapshotData> {
    A takeSnapshot();
    void fixSnapshot(A a);

    interface SnapshotData {
    }
}
