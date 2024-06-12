package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public class TBoxModelObject implements ITBoxScene3DObject {
    private final Model<Format3D> model;
    private final TBoxObjectRenderData renderData;
    private final String id;

    public TBoxModelObject(@NotNull String id, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        this.model = model;
        this.renderData = renderData;
        this.id = id;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    @Override
    public TBoxObjectRenderData getRenderData() {
        return this.renderData;
    }

    @Override
    public String getStringID() {
        return this.id;
    }
}
