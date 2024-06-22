package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.collision.LocalCollision;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public abstract class TBoxScene3DObject {
    private static int globalObjectID;
    private final String name;
    private final int id;
    private boolean selected;
    private final Model<Format3D> model;
    private final TBoxObjectRenderData renderData;
    private final LocalCollision localCollision;
    private AttributeContainer attributeContainer;

    public TBoxScene3DObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        this.name = name;
        this.id = TBoxScene3DObject.globalObjectID++;
        this.selected = false;
        this.model = model;
        this.renderData = renderData;
        this.localCollision = new LocalCollision(model);
        this.attributeContainer = new AttributeContainer();
    }

    public abstract boolean canEditPosition();
    public abstract boolean canEditScaling();
    public abstract boolean canEditRotation();

    public boolean hasAttributes() {
        return this.getAttributeContainer() != null && this.getAttributeContainer().hasAttributes();
    }

    public void setAttributeContainer(AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public AttributeContainer getAttributeContainer() {
        return this.attributeContainer;
    }

    public void reCalcCollision() {
        this.getLocalCollision().calcAABB(this.getModel().getFormat());
    }

    public LocalCollision getLocalCollision() {
        return this.localCollision;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public TBoxObjectRenderData getRenderData() {
        return this.renderData;
    }

    public String objectId() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public String toString() {
        return this.objectId() + "(" + this.getId() + ")";
    }

    @Override
    public int hashCode() {
        return this.objectId().hashCode() + this.getId() * 31;
    }
}
