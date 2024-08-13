package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;

public class GuiRender extends SceneRenderBase {
    public GuiRender(JGemsOpenGLRenderer sceneRender) {
        super(-1, sceneRender, new RenderGroup("GUI_FORWARD"));
    }

    public void onRender(FrameTicking frameTicking) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        JGems3D.get().getUI().renderFrame(frameTicking.getFrameDeltaTime());
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