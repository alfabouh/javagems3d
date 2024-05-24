package ru.alfabouh.engine.render.imgui;

import imgui.ImDrawData;
import org.lwjgl.opengl.GL30;

public class IMGUIMesh {
    private final int indicesVbo;
    private final int vaoId;
    private final int verticesVbo;

    public IMGUIMesh() {
        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vaoId);

        this.verticesVbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.verticesVbo);

        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);

        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 8);

        GL30.glEnableVertexAttribArray(2);
        GL30.glVertexAttribPointer(2, 4, GL30.GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);

        this.indicesVbo = GL30.glGenBuffers();

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        GL30.glDeleteBuffers(this.indicesVbo);
        GL30.glDeleteBuffers(this.verticesVbo);
        GL30.glDeleteVertexArrays(this.vaoId);
    }

    public int getIndicesVbo() {
        return this.indicesVbo;
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public int getVerticesVbo() {
        return this.verticesVbo;
    }
}
