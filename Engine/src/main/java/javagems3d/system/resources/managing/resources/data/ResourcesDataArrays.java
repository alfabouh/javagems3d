package javagems3d.system.resources.managing.resources.data;

import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import org.jetbrains.annotations.NotNull;

public final class ResourcesDataArrays {
    private final MeshBuffersDataArray meshBuffersDataArray;
    private final BindlessTexturesDataArray bindlessTexturesDataArray;

    public ResourcesDataArrays(@NotNull MeshBuffersDataArray meshBuffersDataArray, @NotNull BindlessTexturesDataArray bindlessTexturesDataArray) {
        this.meshBuffersDataArray = meshBuffersDataArray;
        this.bindlessTexturesDataArray = bindlessTexturesDataArray;
    }

    public void clearAll() {
        this.getBindlessTexturesArray().clear();
        this.getMeshBuffersDataArray().clear();
    }

    public MeshBuffersDataArray getMeshBuffersDataArray() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesDataArray getBindlessTexturesArray() {
        return this.bindlessTexturesDataArray;
    }
}
