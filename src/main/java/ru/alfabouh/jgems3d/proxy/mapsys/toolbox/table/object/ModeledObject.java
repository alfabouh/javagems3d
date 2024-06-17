package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;

public class ModeledObject implements IMapObject {
    private final MeshDataGroup meshDataGroup;

    public ModeledObject(@NotNull MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public MeshDataGroup meshDataGroup() {
        return this.meshDataGroup;
    }
}
