package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldTransparentRender extends SceneRenderBase {
    public static List<IModeledSceneObject> transparentRenderObjects;

    public WorldTransparentRender(SceneData sceneData) {
        super(99, sceneData, new RenderGroup("WORLD_TRANSPARENT"));
        WorldTransparentRender.transparentRenderObjects = new ArrayList<>();
    }

    public void onRender(float partialTicks) {
        WorldTransparentRender.transparentRenderObjects.sort(Comparator.comparing(e -> -e.getModel().getFormat().getPosition().distance(this.getCamera().getCamPosition())));
        GL30.glDepthMask(false);
        this.render(partialTicks, WorldTransparentRender.transparentRenderObjects);
        GL30.glDepthMask(true);
        WorldTransparentRender.transparentRenderObjects.clear();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(float partialTicks, List<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getMeshRenderData().getShaderManager().bind();
                    entityItem.getMeshRenderData().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getMeshRenderData().getShaderManager().unBind();
                }
            }
        }
    }
}