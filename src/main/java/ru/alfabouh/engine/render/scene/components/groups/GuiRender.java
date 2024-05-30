package ru.alfabouh.engine.render.scene.components.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.components.RenderGroup;

public class GuiRender extends SceneRenderBase {
    public GuiRender(SceneRender sceneRenderConveyor) {
        super(3, sceneRenderConveyor, new RenderGroup("GUI"));
    }

    public void onRender(double partialTicks) {
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
