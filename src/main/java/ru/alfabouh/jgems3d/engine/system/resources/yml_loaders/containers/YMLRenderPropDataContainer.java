package ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;

import java.util.HashMap;
import java.util.Map;

public class YMLRenderPropDataContainer {
    private final Map<String, YMLObjInfo> map;

    public YMLRenderPropDataContainer() {
        this.map = new HashMap<>();
    }

    public void addObject(String id, ModelRenderParams modelRenderParams, IRenderFabric renderFabric) {
        this.getMap().put(id, new YMLObjInfo(modelRenderParams, renderFabric));
    }

    public Map<String, YMLObjInfo> getMap() {
        return this.map;
    }

    public static class YMLObjInfo {
        private final ModelRenderParams modelRenderParams;
        private final IRenderFabric renderFabric;

        public YMLObjInfo(ModelRenderParams modelRenderParams, IRenderFabric renderFabric) {
            this.modelRenderParams = modelRenderParams;
            this.renderFabric = renderFabric;
        }

        public IRenderFabric getRenderFabric() {
            return this.renderFabric;
        }

        public ModelRenderParams getModelRenderParams() {
            return this.modelRenderParams;
        }
    }
}
