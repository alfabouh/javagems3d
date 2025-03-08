package javagems3d.system.resources.assets.models.mesh.structures.nodes;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeshNode3D<T extends IMesh> extends MeshNode<T> {
    private Material material;

    public MeshNode3D(@NotNull T meshData) {
        this(meshData, new Material(null));
    }

    public MeshNode3D(@NotNull T meshData, @NotNull Material material) {
        super(meshData);
        this.material = material;
    }

    @Override
    public void clear() {
        super.clear();
        this.material = null;
    }

    public boolean hasTransparency() {
        return this.getMaterial().hasTransparency();
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }
}
