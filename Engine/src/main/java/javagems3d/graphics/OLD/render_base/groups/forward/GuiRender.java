/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.OLD.render_base.groups.forward;

import javagems3d.graphics.rendering.ui.jgems_imgui.ImmediateUI;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.OLD.JGemsOpenGLRendererOLD;
import javagems3d.graphics.OLD.render_base.RenderGroup;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;

public class GuiRender extends SceneRenderBase {
    private final ImmediateUI immediateUI;

    public GuiRender(ImmediateUI immediateUI, JGemsOpenGLRendererOLD sceneRender) {
        super(-1, sceneRender, new RenderGroup("GUI_FORWARD"));
        this.immediateUI = immediateUI;
    }

    public void onRender(FrameTicking frameTicking) {
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        this.immediateUI.renderFrame(frameTicking.getFrameDeltaTime());
        GL46.glDisable(GL46.GL_BLEND);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}
