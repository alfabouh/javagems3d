package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import org.jetbrains.annotations.Nullable;

public final class GLTF2Node {
    private final String name;
    private final GLTF2Mesh mesh;

    public GLTF2Node(String name, @Nullable GLTF2Mesh mesh) {
        this.name = name;
        this.mesh = mesh;
    }

    public String getName() {
        return this.name;
    }

    public GLTF2Mesh getMesh() {
        return this.mesh;
    }
}
