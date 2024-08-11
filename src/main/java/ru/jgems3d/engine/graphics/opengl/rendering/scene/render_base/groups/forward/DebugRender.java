package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.system.graph.GraphEdge;
import ru.jgems3d.engine.system.graph.GraphVertex;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.List;

public class DebugRender extends SceneRenderBase {
    private final JGemsShaderManager debugShaders;

    public DebugRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("DEBUG_FORWARD"));
        this.debugShaders = JGemsResourceManager.globalShaderAssets.debug;
    }

    public void onRender(FrameTicking frameTicking) {
        GL30.glHint(GL30.GL_LINE_SMOOTH_HINT, GL30.GL_NICEST);
        GL30.glEnable(GL30.GL_LINE_SMOOTH);
        if (GlobalRenderDebugConstants.SHOW_DEBUG_LINES) {
            DynamicsUtils.btDebugDraw.drawLines(JGems3D.get().getPhysicsWorld().getDynamics());
            this.debugShaders.bind();
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());

            this.renderDebugSunDirection();
            this.renderNavMesh();

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

    private void renderNavMesh() {
        PhysicsWorld world = JGems3D.get().getPhysicsWorld();
        if (world.getMapNavGraph() == null) {
            return;
        }
        List<GraphVertex> vertices = world.getMapNavGraph().getVerticesInChunk(this.getCamera().getCamPosition());
        if (vertices == null) {
            return;
        }
        for (GraphVertex vertex : vertices) {
            try (Model<Format3D> model0 = MeshHelper.generateVector3fModel(vertex.getPosition(), new Vector3f(vertex.getPosition()).add(0.0f, 1.0f, 0.0f))) {
                this.debugShaders.performUniform(new UniformString("colour"), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
                JGemsSceneUtils.renderModel(model0, GL30.GL_LINES);
                for (GraphEdge edge : world.getMapNavGraph().getNeighbors(vertex)) {
                    try (Model<Format3D> model1 = MeshHelper.generateVector3fModel(new Vector3f(vertex.getPosition()).add(0.0f, 0.1f, 0.0f),new Vector3f(edge.getTarget().getPosition()).add(0.0f, 0.1f, 0.0f))) {
                        this.debugShaders.performUniform(new UniformString("colour"), new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                        JGemsSceneUtils.renderModel(model1, GL30.GL_LINES);
                    }
                }
            }
        }
    }

    private void renderDebugSunDirection() {
        try (Model<Format3D> model = MeshHelper.generateVector3fModel(new Vector3f(0.0f), new Vector3f(this.getSceneWorld().getEnvironment().getSky().getSunPos()).mul(1000.0f))) {
            this.debugShaders.performUniform(new UniformString("colour"), new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
            JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
        }
    }
}