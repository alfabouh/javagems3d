package ru.alfabouh.jgems3d.engine.render.opengl.scene.debug.bullet;

import org.bytedeco.bullet.LinearMath.btIDebugDraw;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class JBDebugDraw extends btIDebugDraw {
    private int debugMode;
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

    public void drawLine(btVector3 from, btVector3 to, btVector3 color) {
        JGemsShaderManager debugShaders = ResourceManager.shaderAssets.debug;
        debugShaders.bind();
        debugShaders.getUtils().performPerspectiveMatrix();
        debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
        debugShaders.performUniform("colour", new Vector4d(MathHelper.convert(color), 1.0d));
        this.drawLine(from, to);
        debugShaders.unBind();
    }

    private void drawLine(btVector3 v1, btVector3 v2) {
        float[] vertices = {
                (float) v1.x(), (float) v1.y(), (float) v1.z(),
                (float) v2.x(), (float) v2.y(), (float) v2.z()
        };

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(this.vao);
        GL11.glDrawArrays(GL11.GL_LINES, 0, 2);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL30.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
    }

    @Override
    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public int getDebugMode() {
        return this.debugMode;
    }
}