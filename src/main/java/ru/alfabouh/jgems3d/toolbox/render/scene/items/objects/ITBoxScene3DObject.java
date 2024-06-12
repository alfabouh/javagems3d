package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public interface ITBoxScene3DObject {
    Model<Format3D> getModel();
    TBoxObjectRenderData getRenderData();
    String getStringID();
}
