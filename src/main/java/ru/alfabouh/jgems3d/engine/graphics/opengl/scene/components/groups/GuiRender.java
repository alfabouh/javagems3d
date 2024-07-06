package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;

public class GuiRender extends SceneRenderBase {
    public GuiRender(JGemsSceneRender sceneRenderConveyor) {
        super(3, sceneRenderConveyor, new RenderGroup("GUI"));
    }

    public void onRender(float partialTicks) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        JGems.get().getUI().renderFrame(partialTicks);
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}