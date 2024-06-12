package ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor;

import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshDataGroup constructMeshDataGroup(T t);
}
