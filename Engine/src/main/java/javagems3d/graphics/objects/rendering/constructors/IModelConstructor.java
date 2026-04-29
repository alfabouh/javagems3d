package javagems3d.graphics.objects.rendering.constructors;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

@FunctionalInterface
public interface IModelConstructor<T, E extends IMesh> {
    MeshStructure3D<E> constructMeshDataGroup(T t);
}