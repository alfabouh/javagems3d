package ru.BouH.engine.render.scene.scene_render.groups;

import org.joml.Vector3d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.map.Map01;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;
import ru.BouH.engine.render.transformation.TransformationManager;

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
        for (Graph.GVertex vertex : sceneRenderBase.getSceneWorld().getWorld().getGraph().getGraphContainer().keySet()) {
            if (Game.getGame().getScreen().getCamera().getCamPosition().distance(new Vector3d(vertex.getX(), vertex.getY() + 0.1d, vertex.getZ())) > 10.0f) {
                continue;
            }
            Model<Format3D> model0 = MeshHelper.generateVector3DModel(new Vector3d(vertex.getX(), vertex.getY(), vertex.getZ()), new Vector3d(vertex.getX(), vertex.getY() + 1.0d, vertex.getZ()));
            this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
            this.debugShaders.performUniform("colour", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            Scene.renderModel(model0, GL30.GL_LINES);
            model0.clean();
            for (Graph.GEdge edge : sceneRenderBase.getSceneWorld().getWorld().getGraph().getNeighbors(vertex)) {
                Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3d(vertex.getX(), vertex.getY() + 0.1d, vertex.getZ()), new Vector3d(edge.getTarget().getX(), edge.getTarget().getY() + 0.1d, edge.getTarget().getZ()));
                this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
                this.debugShaders.performUniform("colour", new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                if (Map01.entityManiac.getNavigationAI().getPathToVertex() != null && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(vertex) && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(edge.getTarget())) {
                    this.debugShaders.performUniform("colour", new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                }
                Scene.renderModel(model, GL30.GL_LINES);
                model.clean();
            }
        }
        Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3d(0.0d), new Vector3d(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunAngle()).mul(1000.0f));
        this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        Scene.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}