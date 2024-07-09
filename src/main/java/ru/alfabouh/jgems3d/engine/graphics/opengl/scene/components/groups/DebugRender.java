package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.sysgraph.Graph;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.debug.constants.GlobalRenderDebugConstants;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class DebugRender extends SceneRenderBase {
    private final JGemsShaderManager debugShaders;

    public DebugRender(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(50, sceneRenderConveyor, new RenderGroup("DEBUG"));
        this.debugShaders = JGemsResourceManager.globalShaderAssets.debug;
    }

    public void onRender(float partialTicks) {
        if (GlobalRenderDebugConstants.SHOW_DEBUG_LINES) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.renderNavMesh(this);
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.unBind();

            if (!JGems.get().getPhysicsWorld().getDynamicsWorld().isNull()) {
                JGems.get().getPhysicsWorld().getDynamicsWorld().debugDrawWorld();
            }
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void renderNavMesh(SceneRenderBase sceneRenderBase) {
        if (sceneRenderBase.getSceneWorld().getWorld().getGraph() == null) {
            return;
        }
        for (Graph.GVertex vertex : sceneRenderBase.getSceneWorld().getWorld().getGraph().getGraphContainer().keySet()) {
            if (JGems.get().getScreen().getCamera().getCamPosition().distance(new Vector3f(vertex.getX(), vertex.getY() + 0.1f, vertex.getZ())) > 5.0f) {
                continue;
            }
            Model<Format3D> model0 = MeshHelper.generateVector3fModel(new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ()), new Vector3f(vertex.getX(), (float) (vertex.getY() + 1.0d), vertex.getZ()));
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
            this.debugShaders.performUniform("colour", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            JGemsSceneUtils.renderModel(model0, GL30.GL_LINES);
            model0.clean();
            for (Graph.GEdge edge : sceneRenderBase.getSceneWorld().getWorld().getGraph().getNeighbors(vertex)) {
                Model<Format3D> model = MeshHelper.generateVector3fModel(new Vector3f(vertex.getX(), vertex.getY() + 0.1f, vertex.getZ()), new Vector3f(edge.getTarget().getX(), edge.getTarget().getY() + 0.1f, edge.getTarget().getZ()));
                this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
                this.debugShaders.performUniform("colour", new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                //if (Map01.entityManiac != null && Map01.entityManiac.getNavigationAI().getPathToVertex() != null && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(vertex) && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(edge.getTarget())) {
                //    this.debugShaders.performUniform("colour", news Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                //}
                JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
                model.clean();
            }
        }
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3fModel(new Vector3f(0.0f), new Vector3f(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunPos()).mul(1000.0f));
        this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}