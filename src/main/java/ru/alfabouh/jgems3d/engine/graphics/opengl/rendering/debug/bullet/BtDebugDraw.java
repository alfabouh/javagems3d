package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.debug.bullet;


import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.math.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

import java.nio.FloatBuffer;

public class BtDebugDraw {
    private int vao;
    private int vbo;

    public void setupBuffers() {
        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 6 * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void drawLines(DynamicsSystem dynamicsSystem) {
        for (PhysicsCollisionObject physicsCollisionObject : dynamicsSystem.getPhysicsSpace().getPcoList()) {
            JGemsShaderManager debugShaders = JGemsResourceManager.globalShaderAssets.debug;
            debugShaders.bind();
            debugShaders.getUtils().performPerspectiveMatrix();
            debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
            debugShaders.performUniform("colour", new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

            BoundingBox boundingBox = new BoundingBox();
            physicsCollisionObject.boundingBox(boundingBox);
            Vector3f min = new Vector3f();
            boundingBox.getMin(min);
            Vector3f max = new Vector3f();
            boundingBox.getMax(max);
            this.drawLine(min, max);
            debugShaders.unBind();
        }
    }

    private void drawLine(Vector3f min, Vector3f max) {
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

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(this.vao);
        GL11.glDrawArrays(GL11.GL_LINES, 0, vertexArray.length / 3);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL30.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
    }
}