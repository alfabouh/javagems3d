package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import java.util.List;

public final class GLTF2Scene {
    private final String name;
    private final List<GLTF2Node> nodes;
    private final List<GLTF2Material> materials;

    public GLTF2Scene(String name, List<GLTF2Node> nodes, List<GLTF2Material> materials) {
        this.name = name;
        this.nodes = nodes;
        this.materials = materials;
    }

    public String getName() {
        return this.name;
    }

    public List<GLTF2Node> getNodes() {
        return this.nodes;
    }

    public List<GLTF2Material> getMaterials() {
        return this.materials;
    }
}