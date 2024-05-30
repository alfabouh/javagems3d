package ru.alfabouh.engine.system.resources.assets.models.basic.constructor;

import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshDataGroup constructMeshDataGroup(T t);
}
