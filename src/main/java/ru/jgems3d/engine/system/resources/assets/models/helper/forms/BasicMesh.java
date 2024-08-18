package ru.jgems3d.engine.system.resources.assets.models.helper.forms;

import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.IFormat;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

public interface BasicMesh<T extends IFormat> {
    Model<T> generateModel();

    Mesh generateMesh();
}
