package ru.alfabouh.engine.render.scene.scene_render.groups;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.graph.Graph;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;
import ru.alfabouh.engine.render.transformation.TransformationManager;

public class DebugRender extends SceneRenderBase {
    private final ShaderManager debugShaders;
    private int vao;
    private int vbo;

    public DebugRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(50, sceneRenderConveyor, new RenderGroup("DEBUG"));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(double partialTicks) {
        if (this.getSceneRenderConveyor().getCurrentDebugMode() == 1) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.renderNavMesh(this);
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

    private void renderNavMesh(SceneRenderBase sceneRenderBase) {
        for (Graph.GVertex vertex : sceneRenderBase.getSceneWorld().getWorld().getGraph().getGraphContainer().keySet()) {
            if (Game.getGame().getScreen().getCamera().getCamPosition().distance(new Vector3d(vertex.getX(), vertex.getY() + 0.1d, vertex.getZ())) > 5.0f) {
                continue;
            }
            Model<Format3D> model0 = MeshHelper.generateVector3DModel(new Vector3f((float) vertex.getX(), (float) vertex.getY(), (float) vertex.getZ()), new Vector3f((float) vertex.getX(), (float) (vertex.getY() + 1.0d), (float) vertex.getZ()));
            this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
            this.debugShaders.performUniform("colour", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            Scene.renderModel(model0, GL30.GL_LINES);
            model0.clean();
            for (Graph.GEdge edge : sceneRenderBase.getSceneWorld().getWorld().getGraph().getNeighbors(vertex)) {
                Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3f((float) vertex.getX(), (float) (vertex.getY() + 0.1f), (float) vertex.getZ()), new Vector3f((float) edge.getTarget().getX(), (float) (edge.getTarget().getY() + 0.1f), (float) edge.getTarget().getZ()));
                this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
                this.debugShaders.performUniform("colour", new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                //if (Map01.entityManiac != null && Map01.entityManiac.getNavigationAI().getPathToVertex() != null && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(vertex) && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(edge.getTarget())) {
                //    this.debugShaders.performUniform("colour", new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                //}
                Scene.renderModel(model, GL30.GL_LINES);
                model.clean();
            }
        }
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3f(0.0f), new Vector3f(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunAngle()).mul(1000.0f));
        this.debugShaders.getUtils().performViewMatrix3d(TransformationManager.instance.getMainCameraViewMatrix());
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        Scene.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}