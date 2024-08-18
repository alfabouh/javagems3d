package ru.jgems3d.engine.system.resources.assets.models.helper.constructor;

import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshDataGroup constructMeshDataGroup(T t);
}
