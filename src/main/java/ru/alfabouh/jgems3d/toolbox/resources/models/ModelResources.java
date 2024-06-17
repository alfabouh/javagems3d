package ru.alfabouh.jgems3d.toolbox.resources.models;

import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;

public class ModelResources {
    public MeshDataGroup xyz;
    public MeshDataGroup cubic;

    public void init() {
        this.cubic = ResourceManager.createModel("/assets/toolbox/models/cubic/cubic.obj");
        this.xyz = ResourceManager.createModel("/assets/toolbox/models/xyz/xyz.obj");
    }
}
