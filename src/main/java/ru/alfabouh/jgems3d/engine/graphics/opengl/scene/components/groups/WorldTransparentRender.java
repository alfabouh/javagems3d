package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldTransparentRender extends SceneRenderBase {
    public static List<IModeledSceneObject> transparentRenderObjects;

    public WorldTransparentRender(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(99, sceneRenderConveyor, new RenderGroup("WORLD_TRANSPARENT"));
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
                    entityItem.getModelRenderParams().getShaderManager().bind();
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performPerspectiveMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}