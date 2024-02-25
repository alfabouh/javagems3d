package ru.BouH.engine.render.scene.scene_render.groups;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.math.Triple;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.bullet.JBDebugDraw;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;
import ru.BouH.engine.render.transformation.TransformationManager;

import java.util.ArrayList;
import java.util.List;

public class DebugRender extends SceneRenderBase {
    private final ShaderManager debugShaders;
    private int vao;
    private int vbo;

    public DebugRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(50, sceneRenderConveyor, new RenderGroup("DEBUG", true));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(double partialTicks) {
        if (this.getSceneRenderConveyor().getCurrentDebugMode() == 1) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.debugShaders.getUtils().performProjectionMatrix();
            this.debugShaders.unBind();
            if (!Game.getGame().getPhysicsWorld().getDynamicsWorld().isNull()) {
                Game.getGame().getPhysicsWorld().getDynamicsWorld().debugDrawWorld();
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3d(0.0d), new Vector3d(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunAngle()).mul(1000.0f));
        this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        Scene.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}