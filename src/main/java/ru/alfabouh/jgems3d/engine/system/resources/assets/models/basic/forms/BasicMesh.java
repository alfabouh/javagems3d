package ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.forms;

import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.IFormat;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

public interface BasicMesh<T extends IFormat> {
    Model<T> generateModel();

    Mesh generateMesh();
}
