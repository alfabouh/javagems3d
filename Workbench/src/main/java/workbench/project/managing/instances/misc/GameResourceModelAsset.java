package workbench.project.managing.instances.misc;

import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import workbench.project.managing.instances.IAsset;

public class GameResourceModelAsset implements IAsset {
    private final String relativePath;
    private final transient String name;
    private final transient MeshGroup meshGroup;

    public GameResourceModelAsset(String name, String relativePath, MeshGroup meshGroup) {
        this.name = name;
        this.relativePath = relativePath;
        this.meshGroup = meshGroup;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String getName() {
        return this.name;
    }

    public MeshGroup getMeshGroup() {
        return this.meshGroup;
    }
}
