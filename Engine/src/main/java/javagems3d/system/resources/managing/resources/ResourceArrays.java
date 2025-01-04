package javagems3d.system.resources.managing.resources;

import javagems3d.system.resources.managing.arrays.BindlessTexturesArray;
import javagems3d.system.resources.managing.arrays.MeshBuffersDataArray;
import org.jetbrains.annotations.NotNull;

public final class ResourceArrays {
    private final MeshBuffersDataArray meshBuffersDataArray;
    private final BindlessTexturesArray bindlessTexturesArray;

    public ResourceArrays(@NotNull MeshBuffersDataArray meshBuffersDataArray, @NotNull BindlessTexturesArray bindlessTexturesArray) {
        this.meshBuffersDataArray = meshBuffersDataArray;
        this.bindlessTexturesArray = bindlessTexturesArray;
    }

    public void clearAll() {
        this.getBindlessTexturesArray().clear();
        this.getMeshBuffersDataArray().clear();
    }

    public MeshBuffersDataArray getMeshBuffersDataArray() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesArray getBindlessTexturesArray() {
        return this.bindlessTexturesArray;
    }
}
