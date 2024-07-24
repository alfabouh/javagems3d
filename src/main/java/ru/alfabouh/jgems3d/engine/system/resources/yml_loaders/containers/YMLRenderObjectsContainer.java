package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers;

import java.util.HashMap;
import java.util.Map;

public class YMLRenderObjectsContainer {
    private final Map<String, YMLObjInfo> map;

    public YMLRenderObjectsContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(String id, String renderObjectData, String meshDataGroupName, String type) {
        this.getMap().put(id, new YMLObjInfo(renderObjectData, type, meshDataGroupName));
    }

    public Map<String, YMLObjInfo> getMap() {
        return this.map;
    }

    public static class YMLObjInfo {
        private final String renderObjectData;
        private final String type;
        private final String meshDataGroupName;

        public YMLObjInfo(String renderObjectData, String type, String meshDataGroupName) {
            this.renderObjectData = renderObjectData;
            this.type = type;
            this.meshDataGroupName = meshDataGroupName;
        }

        public String getType() {
            return this.type;
        }

        public String getMeshDataGroupName() {
            return this.meshDataGroupName;
        }

        public String getRenderObjectData() {
            return this.renderObjectData;
        }
    }
}
