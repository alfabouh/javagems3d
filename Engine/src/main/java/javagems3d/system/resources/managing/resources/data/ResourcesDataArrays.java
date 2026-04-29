package javagems3d.system.resources.managing.resources.data;

import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class ResourcesDataArrays {
    private final MeshBuffersDataArray meshBuffersDataArray;
    private final BindlessTexturesDataArray bindlessTexturesDataArray;
    private final Set<MeshStructure3D<?>> meshesWithAnimation;

    public ResourcesDataArrays(@NotNull MeshBuffersDataArray meshBuffersDataArray, @NotNull BindlessTexturesDataArray bindlessTexturesDataArray) {
        this.meshBuffersDataArray = meshBuffersDataArray;
        this.bindlessTexturesDataArray = bindlessTexturesDataArray;
        this.meshesWithAnimation = new HashSet<>();
    }

    public Set<MeshStructure3D<?>> getMeshesWithAnimation() {
        return this.meshesWithAnimation;
    }

    public void clearAll() {
        this.getBindlessTexturesArray().clear();
        this.getMeshBuffersDataArray().clear();
        this.getMeshesWithAnimation().clear();
    }

    public MeshBuffersDataArray getMeshBuffersDataArray() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesDataArray getBindlessTexturesArray() {
        return this.bindlessTexturesDataArray;
    }
}
