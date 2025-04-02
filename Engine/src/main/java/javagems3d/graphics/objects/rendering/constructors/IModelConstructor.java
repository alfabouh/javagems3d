package javagems3d.graphics.objects.rendering.constructors;

import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

@FunctionalInterface
public interface IModelConstructor<T> {
    MeshGroup constructMeshDataGroup(T t);
}
