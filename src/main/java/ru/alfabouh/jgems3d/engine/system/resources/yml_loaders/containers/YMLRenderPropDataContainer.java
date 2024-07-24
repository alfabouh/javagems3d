package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers;

import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.ModelRenderParams;

import java.util.HashMap;
import java.util.Map;

public class YMLRenderPropDataContainer {
    private final Map<String, YMLObjInfo> map;

    public YMLRenderPropDataContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(String id, ModelRenderParams modelRenderParams, IRenderObjectFabric renderFabric) {
        this.getMap().put(id, new YMLObjInfo(modelRenderParams, renderFabric));
    }

    public Map<String, YMLObjInfo> getMap() {
        return this.map;
    }

    public static class YMLObjInfo {
        private final ModelRenderParams modelRenderParams;
        private final IRenderObjectFabric renderFabric;

        public YMLObjInfo(ModelRenderParams modelRenderParams, IRenderObjectFabric renderFabric) {
            this.modelRenderParams = modelRenderParams;
            this.renderFabric = renderFabric;
        }

        public IRenderObjectFabric getRenderFabric() {
            return this.renderFabric;
        }

        public ModelRenderParams getModelRenderParams() {
            return this.modelRenderParams;
        }
    }
}
