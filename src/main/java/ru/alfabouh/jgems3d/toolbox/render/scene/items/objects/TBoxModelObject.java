package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.collision.LocalCollision;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public class TBoxModelObject extends TBoxScene3DObject {
    private final Model<Format3D> model;
    private final TBoxObjectRenderData renderData;
    private final LocalCollision localCollision;

    public TBoxModelObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        super(name);
        this.model = model;
        this.renderData = renderData;
        this.localCollision = new LocalCollision(model);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    @Override
    public TBoxObjectRenderData getRenderData() {
        return this.renderData;
    }

    public void reCalcCollision() {
        this.getLocalCollision().calcAABB(this.getModel().getFormat());
    }

    @Override
    public LocalCollision getLocalCollision() {
        return this.localCollision;
    }
}
