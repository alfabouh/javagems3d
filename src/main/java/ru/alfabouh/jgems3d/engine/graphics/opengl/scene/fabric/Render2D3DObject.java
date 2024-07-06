package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.RenderWorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IRenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;

public class Render2D3DObject extends RenderWorldItem {
    public Render2D3DObject() {
    }

    @Override
    public void onRender(float partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractSceneItemObject entityObject = (AbstractSceneItemObject) renderItem;
        if (entityObject.hasRender() && entityObject.hasModel()) {
            entityObject.getModel().getFormat().setOrientedToView(true);
            JGemsSceneUtils.renderSceneObject(entityObject, entityObject.getRenderData().getOverObjectMaterial());
        }
    }
}
