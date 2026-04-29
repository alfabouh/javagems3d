package javagems3d.system.resources.assets.loading.models.gltf.parsing;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Asset;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Node;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Scene;

import java.util.List;

public final class GLTF2RawData {
    private final GLTF2Asset gltf2Asset;
    private final GLTF2Scene scene;

    public GLTF2RawData(GLTF2Asset gltf2Asset, GLTF2Scene scene) {
        this.gltf2Asset = gltf2Asset;
        this.scene = scene;
    }

    public GLTF2Asset getGltf2Asset() {
        return this.gltf2Asset;
    }

    public GLTF2Scene getGltf2Scene() {
        return this.scene;
    }
}
