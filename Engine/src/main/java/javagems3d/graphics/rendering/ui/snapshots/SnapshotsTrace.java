package javagems3d.graphics.rendering.ui.snapshots;

import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public abstract class SnapshotsTrace {
    private static final int LIMIT = 256;
    private final Deque<SnapshotsContainer> undoStack = new ArrayDeque<>();
    private final Deque<SnapshotsContainer> redoStack = new ArrayDeque<>();
    private final Supplier<SnapshotsContainer> snapshotAllScenesSupplier;

    public SnapshotsTrace(Supplier<SnapshotsContainer> snapshotFunction) {
        this.snapshotAllScenesSupplier = snapshotFunction;
    }

    public SnapshotsContainer takeSnapshot() {
        return this.snapshotAllScenesSupplier.get();
    }

    public void pushSnapshot(@NotNull SnapshotsContainer snapshot) {
        Log.get().debug("New snapshot!");
        this.undoStack.push(snapshot);
        this.redoStack.clear();
        this.trim(this.undoStack);
    }

    public void undo() {
        if (this.undoStack.isEmpty()) {
            return;
        }
        SnapshotsContainer current = this.takeSnapshot();
        this.redoStack.push(current);
        SnapshotsContainer snapshot = this.undoStack.pop();
        snapshot.fixAll();
        this.trim(this.redoStack);
    }

    public void redo() {
        if (this.redoStack.isEmpty()) {
            return;
        }
        SnapshotsContainer current = this.takeSnapshot();
        this.undoStack.push(current);
        SnapshotsContainer snapshot = this.redoStack.pop();
        snapshot.fixAll();
        this.trim(this.undoStack);
    }

    private void trim(Deque<?> stack) {
        if (stack.size() > LIMIT) {
            stack.removeLast();
        }
    }

    public Deque<SnapshotsContainer> getUndoStack() {
        return this.undoStack;
    }

    public Deque<SnapshotsContainer> getRedoStack() {
        return this.redoStack;
    }
}