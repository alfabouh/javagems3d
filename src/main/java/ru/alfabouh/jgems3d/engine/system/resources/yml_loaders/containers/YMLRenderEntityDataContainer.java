package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;

import java.util.HashMap;
import java.util.Map;

public class YMLRenderEntityDataContainer {
    private final Map<String, RenderObjectData> map;

    public YMLRenderEntityDataContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(String id, RenderObjectData renderObjectData) {
        this.getMap().put(id, renderObjectData);
    }

    public Map<String, RenderObjectData> getMap() {
        return this.map;
    }
}
