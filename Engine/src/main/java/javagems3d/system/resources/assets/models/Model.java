package javagems3d.system.resources.assets.models;

import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.pose.IPose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Model<T extends IPose, R extends MeshStructure<?, ?>> implements AutoCloseable {
    private final T pose;
    private final R meshStructure;

    public Model(@NotNull T pose, @Nullable R meshStructure) {
        this.pose = pose;
        this.meshStructure = meshStructure;
    }

    @SuppressWarnings("all")
    public Model(@NotNull Model<T, R> model) {
        this((T) model.getPose().copy(), model.getMeshStructure());
    }

    public Model(@NotNull Model<T, R> model, @NotNull T pose) {
        this(pose, model.getMeshStructure());
    }

    public boolean isValid() {
        return this.getMeshStructure() != null;
    }

    public T getPose() {
        return this.pose;
    }

    public R getMeshStructure() {
        return this.meshStructure;
    }

    @Override
    public void close() {
        this.clear();
    }

    public void clear() {
        if (this.getMeshStructure() == null) {
            return;
        }
        this.getMeshStructure().clear();
    }
}
