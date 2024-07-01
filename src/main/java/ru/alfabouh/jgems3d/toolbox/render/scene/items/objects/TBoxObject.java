package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes.AttributeContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public final class TBoxObject extends TBoxScene3DObject {
    public TBoxObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        super(name, renderData, model);
    }

    @Override
    public TBoxObject copy() {
        TBoxObject tBoxObject = new TBoxObject(this.objectId(), this.getRenderData(), new Model<>(this.getModel()));
        tBoxObject.setAttributeContainer(new AttributeContainer(this.getAttributeContainer()));
        tBoxObject.setPositionWithAttribute(new Vector3f(tBoxObject.getModel().getFormat().getPosition()).add(0.0f, 2.5f, 0.0f));
        return tBoxObject;
    }
}