package javagems3d.system.resources.assets.models.helper.constructor;

import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshGroup constructMeshDataGroup(T t);
}
