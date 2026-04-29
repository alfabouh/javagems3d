package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import java.util.ArrayList;
import java.util.List;

public final class GLTF2Mesh {
    private final String name;
    private final List<GLTF2Primitive> primitives;

    public GLTF2Mesh(String name) {
        this.name = name;
        this.primitives = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<GLTF2Primitive> getPrimitives() {
        return this.primitives;
    }
}
