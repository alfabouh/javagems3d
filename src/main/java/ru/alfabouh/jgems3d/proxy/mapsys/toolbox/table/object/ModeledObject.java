package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;

public class ModeledObject implements IMapObject {
    private final String objectId;
    private final MeshDataGroup meshDataGroup;

    public ModeledObject(@NotNull String objectId, @NotNull MeshDataGroup meshDataGroup) {
        this.objectId = objectId;
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public String objectId() {
        return this.objectId;
    }

    @Override
    public MeshDataGroup meshDataGroup() {
        return this.meshDataGroup;
    }

    @Override
    public int hashCode() {
        return this.objectId().hashCode();
    }
}
