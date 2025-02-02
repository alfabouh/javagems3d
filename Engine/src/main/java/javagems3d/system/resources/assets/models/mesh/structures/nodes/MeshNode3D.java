package javagems3d.system.resources.assets.models.mesh.structures.nodes;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeshNode3D<T extends IMesh> extends MeshNode<T> {
    private Material material;

    public MeshNode3D(@NotNull T meshData) {
        super(meshData);
        this.material = null;
    }

    public MeshNode3D(@NotNull T meshData, @Nullable Material material) {
        super(meshData);
        this.material = material;
    }

    public boolean hasTransparency() {
        return this.getMaterial() != null && this.getMaterial().hasTransparency();
    }

    public void setMaterial(@Nullable Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }
}
