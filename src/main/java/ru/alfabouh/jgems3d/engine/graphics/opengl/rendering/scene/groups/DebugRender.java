package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import com.jme3.bullet.util.DebugShapeFactory;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.math.AABB;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.alfabouh.jgems3d.engine.sysgraph.Graph;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
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
            DynamicsUtils.btDebugDraw.drawLines(JGems.get().getPhysicsWorld().getDynamics());

            this.debugShaders.bind();
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
            this.renderDebugSunDirection(this);
            this.renderNavMesh(this);
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.unBind();
        }
    }

    public void onStartRender() {
        super.onStartRender();
        DynamicsUtils.btDebugDraw.setupBuffers();
    }

    public void onStopRender() {
        super.onStopRender();
        DynamicsUtils.btDebugDraw.cleanup();
    }

    private void renderNavMesh(SceneRenderBase sceneRenderBase) {
        if (sceneRenderBase.getSceneWorld().getWorld().getMapNavGraph() == null) {
            return;
        }
        for (Graph.GVertex vertex : sceneRenderBase.getSceneWorld().getWorld().getMapNavGraph().getGraphContainer().keySet()) {
            if (JGems.get().getScreen().getCamera().getCamPosition().distance(new Vector3f(vertex.getX(), vertex.getY() + 0.1f, vertex.getZ())) > 5.0f) {
                continue;
            }
            Model<Format3D> model0 = MeshHelper.generateVector3fModel(new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ()), new Vector3f(vertex.getX(), (float) (vertex.getY() + 1.0d), vertex.getZ()));
            this.debugShaders.performUniform("colour", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            JGemsSceneUtils.renderModel(model0, GL30.GL_LINES);
            model0.clean();
            for (Graph.GEdge edge : sceneRenderBase.getSceneWorld().getWorld().getMapNavGraph().getNeighbors(vertex)) {
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
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}