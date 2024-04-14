package ru.alfabouh.engine.game.resources.assets.models.basic.constructor;

import ru.alfabouh.engine.game.resources.assets.models.mesh.MeshDataGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshDataGroup constructMeshDataGroup(T t);
}
