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

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

import java.util.ArrayList;
import java.util.List;

public class DebugRender extends SceneRenderBase {
    private final JGemsShaderManager debugShaders;

    public DebugRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("DEBUG_FORWARD"));
        this.debugShaders = JGemsResourceManager.globalShaderAssets.debug;
    }

    public void onRender(FrameTicking frameTicking) {
        GL46.glHint(GL46.GL_LINE_SMOOTH_HINT, GL46.GL_NICEST);
        GL46.glEnable(GL46.GL_LINE_SMOOTH);
        if (JGemsDebugGlobalConstants.SHOW_DEBUG_LINES) {
            this.debugShaders.beginShading();
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());

            List<ICulled> culleds = new ArrayList<>();
            culleds.addAll(this.getSceneWorld().getLiquids());
            culleds.addAll(this.getSceneWorld().getModeledSceneEntities());
            //culleds.addAll(this.getSceneWorld().getParticlesEmitter().getParticlesSet());

            JGemsDebugGlobalConstants.linesDebugDraw.drawAABBLinesRN(this.debugShaders, culleds);
            JGemsDebugGlobalConstants.linesDebugDraw.drawAABBLinesBT(this.debugShaders, JGemsHelper.getPhysicsWorld().getDynamics());
            JGemsDebugGlobalConstants.linesDebugDraw.drawNavMeshLines(this.debugShaders);

            this.renderDebugSunDirection();

            this.debugShaders.endShading();
        }
    }

    public void onStartRender() {
        super.onStartRender();
        JGemsDebugGlobalConstants.linesDebugDraw.setupBuffers();
    }

    public void onStopRender() {
        super.onStopRender();
        JGemsDebugGlobalConstants.linesDebugDraw.cleanUp();
    }

    private void renderDebugSunDirection() {
        try (Model<Format3D> model = MeshHelper.generateVector3DModel3f(new Vector3f(0.0f), new Vector3f(this.getSceneWorld().getEnvironment().getSkyBox().getSun().getSunPosition()).mul(1000.0f))) {
            this.debugShaders.performUniform(new UniformString("colour"),  DefaultUniformActions.VEC4F(new Vector4f(1.0f, 1.0f, 0.0f, 1.0f)));
            JGemsSceneUtils.renderModel(model, GL46.GL_LINES);
        }
    }
}