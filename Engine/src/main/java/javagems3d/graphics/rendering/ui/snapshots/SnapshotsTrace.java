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

package javagems3d.graphics.rendering.ui.snapshots;

import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public abstract class SnapshotsTrace {
    private int stepCount;
    private static final int LIMIT = 1024;
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
        this.stepCount++;
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
        this.stepCount++;
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
        this.stepCount++;
    }

    private void trim(Deque<?> stack) {
        if (stack.size() > LIMIT) {
            stack.removeLast();
        }
    }

    public int getStepCount() {
        return this.stepCount;
    }

    public Deque<SnapshotsContainer> getUndoStack() {
        return this.undoStack;
    }

    public Deque<SnapshotsContainer> getRedoStack() {
        return this.redoStack;
    }
}