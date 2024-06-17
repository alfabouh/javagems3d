package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

public class MarkerObject extends ModeledObject {
    private final Vector3d color;

    public MarkerObject(@NotNull Vector3d color, @NotNull MeshDataGroup meshDataGroup) {
        super(meshDataGroup);
        this.color = color;
    }

    public Vector3d markerColor() {
        return this.color;
    }
}
