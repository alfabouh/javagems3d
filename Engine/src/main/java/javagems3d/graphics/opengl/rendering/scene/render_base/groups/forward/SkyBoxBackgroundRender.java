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

package javagems3d.graphics.opengl.rendering.scene.render_base.groups.forward;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.environment.skybox.SkyBox;
import javagems3d.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class SkyBoxBackgroundRender extends SceneRenderBase {
    private final SkyBox skyBox;

    public SkyBoxBackgroundRender(SkyBox skyBox, JGemsOpenGLRenderer sceneRender) {
        super(0, sceneRender, new RenderGroup("SKY_FORWARD_2"));
        this.skyBox = skyBox;
    }
    public void renderSkyBoxBackground(FrameTicking frameTicking) {
        GL30.glDisable(GL30.GL_BLEND);
        for (IModeledSceneObject iModeledSceneObject : this.getSkyBox().getBackground().getToRenderSet()) {
            iModeledSceneObject.renderFabric().onRender(frameTicking, this, iModeledSceneObject);
        }
    }

    public void onRender(FrameTicking frameTicking) {
        Vector3f camPos = JGemsHelper.CAMERA.getCurrentCamera().getCamPosition();
        if (camPos.x < -JGems3D.MAP_MAX_SIZE || camPos.y < -JGems3D.MAP_MAX_SIZE || camPos.z < -JGems3D.MAP_MAX_SIZE || camPos.x > JGems3D.MAP_MAX_SIZE || camPos.y > JGems3D.MAP_MAX_SIZE || camPos.z > JGems3D.MAP_MAX_SIZE) {
            return;
        }
        this.renderSkyBoxBackground(frameTicking);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}