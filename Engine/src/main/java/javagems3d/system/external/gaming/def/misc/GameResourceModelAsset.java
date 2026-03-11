package javagems3d.system.external.gaming.def.misc;

import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.external.gaming.def.IAsset;

public record GameResourceModelAsset(String name, String relativePath, MeshGroup meshGroup) implements IAsset {
}
