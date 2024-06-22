package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public final class TBoxObject extends TBoxScene3DObject {
    public TBoxObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        super(name, renderData, model);
    }

    @Override
    public boolean canEditPosition() {
        return true;
    }

    @Override
    public boolean canEditScaling() {
        return true;
    }

    @Override
    public boolean canEditRotation() {
        return true;
    }
}