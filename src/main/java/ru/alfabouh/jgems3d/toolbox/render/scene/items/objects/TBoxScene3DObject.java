package ru.alfabouh.jgems3d.toolbox.render.scene.items.objects;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.collision.LocalCollision;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.properties.MapObjectProperties;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public abstract class TBoxScene3DObject {
    private static int globalObjectID;
    private MapObjectProperties mapObjectProperties;
    private final String name;
    private final int id;
    private boolean selected;

    public TBoxScene3DObject(@NotNull String name) {
        this.name = name;
        this.id = TBoxScene3DObject.globalObjectID++;
        this.selected = false;
        this.mapObjectProperties = new MapObjectProperties();
    }

    public abstract Model<Format3D> getModel();
    public abstract TBoxObjectRenderData getRenderData();
    public abstract LocalCollision getLocalCollision();

    public void setMapObjectProperties(MapObjectProperties mapObjectProperties) {
        this.mapObjectProperties = mapObjectProperties;
    }

    public MapObjectProperties getMapObjectProperties() {
        return this.mapObjectProperties;
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
