/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.toolbox.render.scene.items.objects.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.render.scene.items.collision.LocalCollision;
import ru.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public abstract class TBoxAbstractObject {
    private static int globalObjectID;
    private final String name;
    private final int id;
    private final Model<Format3D> model;
    private final TBoxObjectRenderData renderData;
    private final LocalCollision localCollision;
    private boolean selected;
    private AttributesContainer attributesContainer;

    public TBoxAbstractObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model<Format3D> model) {
        this.name = name;
        this.id = TBoxAbstractObject.globalObjectID++;
        this.selected = false;
        this.model = model;
        this.renderData = renderData;
        this.localCollision = new LocalCollision(model);
        this.attributesContainer = new AttributesContainer();
    }

    public abstract TBoxAbstractObject copy();

    public void setPositionWithAttribute(Vector3f vector3f) {
        this.getModel().getFormat().setPosition(vector3f);
        Vector3f vector3f1 = this.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
        if (vector3f != null) {
            vector3f.set(vector3f);
        }
        this.reCalcCollision();
    }

    public void setRotationWithAttribute(Vector3f vector3f) {
        this.getModel().getFormat().setRotation(vector3f);
        Vector3f vector3f1 = this.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
        if (vector3f != null) {
            vector3f.set(vector3f);
        }
        this.reCalcCollision();
    }

    public void setScalingWithAttribute(Vector3f vector3f) {
        this.getModel().getFormat().setScaling(vector3f);
        Vector3f vector3f1 = this.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);
        if (vector3f != null) {
            vector3f.set(vector3f);
        }
        this.reCalcCollision();
    }

    public boolean hasAttributes() {
        return this.getAttributeContainer() != null && this.getAttributeContainer().hasAttributes();
    }

    public AttributesContainer getAttributeContainer() {
        return this.attributesContainer;
    }

    public TBoxAbstractObject setAttributeContainer(AttributesContainer attributesContainer) {
        this.attributesContainer = attributesContainer;
        return this;
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

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String toString() {
        return this.objectId() + "(" + this.getId() + ")";
    }

    @Override
    public int hashCode() {
        return this.objectId().hashCode() + this.getId() * 31;
    }
}
