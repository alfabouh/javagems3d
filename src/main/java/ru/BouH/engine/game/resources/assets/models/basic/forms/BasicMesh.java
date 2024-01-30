package ru.BouH.engine.game.resources.assets.models.basic.forms;

import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.IFormat;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;

public interface BasicMesh<T extends IFormat> {
    Model<T> generateModel();

    Mesh generateMesh();
}
