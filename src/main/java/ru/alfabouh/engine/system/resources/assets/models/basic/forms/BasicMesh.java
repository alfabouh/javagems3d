package ru.alfabouh.engine.system.resources.assets.models.basic.forms;

import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.formats.IFormat;
import ru.alfabouh.engine.system.resources.assets.models.mesh.Mesh;

public interface BasicMesh<T extends IFormat> {
    Model<T> generateModel();

    Mesh generateMesh();
}
