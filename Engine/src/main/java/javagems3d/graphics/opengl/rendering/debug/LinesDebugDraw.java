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

package javagems3d.graphics.opengl.rendering.debug;


import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Vector3f;
import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.graph.Graph;
import javagems3d.system.graph.GraphEdge;
import javagems3d.system.graph.GraphVertex;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class LinesDebugDraw {
    private FloatBuffer navMeshFloatBuffer;
    private int navMeshPointsSize;

    private int vao;
    private int vbo;

    public void setupBuffers() {
        this.vao = GL46.glGenVertexArrays();
        this.vbo = GL46.glGenBuffers();

        GL46.glBindVertexArray(this.vao);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vbo);

        GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);

        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
    }

    public void drawAABBLinesRN(JGemsShaderManager debugShaders, List<ICulled> culleds) {

    }

    public void drawAABBLinesBT(JGemsShaderManager debugShaders, DynamicsSystem dynamicsSystem) {
        for (PhysicsCollisionObject physicsCollisionObject : dynamicsSystem.getPhysicsSpace().getPcoList()) {
            debugShaders.performUniform(new UniformString("colour"), DefaultUniformActions.VEC4F(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)));

            BoundingBox boundingBox = new BoundingBox();
            physicsCollisionObject.boundingBox(boundingBox);
            Vector3f min = new Vector3f();
            boundingBox.getMin(min);
            Vector3f max = new Vector3f();
            boundingBox.getMax(max);
            this.drawAABB(min, max);
        }
    }

    public void destroyNavMeshFloatBuffer() {
        this.navMeshFloatBuffer.clear();
    }

    public void constructNavMeshFloatBuffer(Graph graph) {
        List<Float> points = new ArrayList<>();

        for (GraphVertex vertex : graph.getGraph().keySet()) {
            for (GraphEdge edge : graph.getNeighbors(vertex)) {
                points.add(vertex.getPosition().x);
                points.add(vertex.getPosition().y + 0.1f);
                points.add(vertex.getPosition().z);

                points.add(edge.getTarget().getPosition().x);
                points.add(edge.getTarget().getPosition().y + 0.1f);
                points.add(edge.getTarget().getPosition().z);
            }
        }

        float[] aPoints = JGemsHelper.UTILS.convertFloatsArray(points);

        this.navMeshFloatBuffer = MemoryUtil.memAllocFloat(aPoints.length);
        this.navMeshFloatBuffer.put(aPoints).flip();
        this.navMeshPointsSize = aPoints.length / 3;
    }

    public void drawNavMeshLines(JGemsShaderManager debugShaders) {
        if (this.navMeshFloatBuffer == null) {
            return;
        }
        debugShaders.performUniform(new UniformString("colour"), DefaultUniformActions.VEC4F(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f)));
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vbo);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, this.navMeshFloatBuffer, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);

        GL46.glBindVertexArray(this.vao);
        GL46.glEnableVertexAttribArray(0);
        GL46.glDrawArrays(GL46.GL_LINES, 0, this.navMeshPointsSize);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
    }

    private void drawAABB(Vector3f min, Vector3f max) {
        Vector3f[] vertices = {
                new Vector3f(min.x, min.y, min.z),
                new Vector3f(max.x, min.y, min.z),
                new Vector3f(min.x, max.y, min.z),
                new Vector3f(max.x, max.y, min.z),
                new Vector3f(min.x, min.y, max.z),
                new Vector3f(max.x, min.y, max.z),
                new Vector3f(min.x, max.y, max.z),
                new Vector3f(max.x, max.y, max.z),
        };

        int[] indices = {
                0, 1, 0, 2, 0, 4,
                1, 3, 1, 5,
                2, 3, 2, 6,
                3, 7,
                4, 5, 4, 6,
                5, 7,
                6, 7
        };

        float[] vertexArray = new float[indices.length * 3];
        for (int i = 0; i < indices.length; i++) {
            vertexArray[i * 3] = vertices[indices[i]].x;
            vertexArray[i * 3 + 1] = vertices[indices[i]].y;
            vertexArray[i * 3 + 2] = vertices[indices[i]].z;
        }

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vbo);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertexArray, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);

        GL46.glBindVertexArray(this.vao);
        GL46.glEnableVertexAttribArray(0);
        GL46.glDrawArrays(GL46.GL_LINES, 0, vertexArray.length / 3);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
    }

    public void cleanUp() {
        MemoryUtil.memFree(this.navMeshFloatBuffer);
        GL46.glDeleteBuffers(this.vbo);
        GL46.glDeleteVertexArrays(this.vao);
    }
}