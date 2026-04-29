package javagems3d.graphics.rendering.programs.indirect.commands;

import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public abstract class IndirectCommandsProgram {
    public static final int COM_SIZE = 5 * Float.BYTES;
    protected final IndirectBufferProgram indirectBufferProgram;

    protected int staticDrawCount;
    protected int staticRenderBufferHandle;

    public IndirectCommandsProgram(IndirectBufferProgram indirectBufferProgram) {
        this.indirectBufferProgram = indirectBufferProgram;
    }

    public void buildCommands(@Nullable IntBuffer indexes, @Nullable IntBuffer materialIds, MeshBuffer buffer, int drawCount) {
        if (buffer == null) {
            throw new JGemsNullException("MeshBuffer cannot be null for indirect rendering");
        }

        ByteBuffer commandsBuffer = this.initCommandsByteBuffer(drawCount);
        int baseInstance = 0;
        for (MeshBuffer.PassData data : buffer.getSolidPassData()) {
            commandsBuffer.putInt(data.numVertexIndexes());
            commandsBuffer.putInt(drawCount);
            commandsBuffer.putInt(data.getFirstIndexOffset());
            commandsBuffer.putInt(data.getOffset());
            commandsBuffer.putInt(baseInstance);
            if (materialIds != null) {
                materialIds.put(data.getMaterialId());
            }
            if (indexes != null) {
                indexes.put(baseInstance);
            }
            baseInstance++;
        }
        this.passCommandsByteBuffer(commandsBuffer);
    }

    protected ByteBuffer initCommandsByteBuffer(int drawCount) {
        return MemoryUtil.memAlloc(IndirectCommandsProgram.COM_SIZE * drawCount);
    }

    protected void passCommandsByteBuffer(ByteBuffer commandsBuffer) {
        commandsBuffer.flip();
        this.staticDrawCount = commandsBuffer.remaining() / IndirectCommandsProgram.COM_SIZE;
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, this.staticRenderBufferHandle);
        GL46.glBufferData(GL46.GL_DRAW_INDIRECT_BUFFER, commandsBuffer, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
        MemoryUtil.memFree(commandsBuffer);
    }

    public void createBuffer() {
        this.staticRenderBufferHandle = GL46.glGenBuffers();
    }

    public void destroyBuffer() {
        GL46.glDeleteBuffers(this.getRenderBufferHandle());
    }

    protected IndirectBufferProgram getIndirectRenderBuffer() {
        return this.indirectBufferProgram;
    }

    public int getRenderBufferHandle() {
        return this.staticRenderBufferHandle;
    }

    public int getDrawCount() {
        return this.staticDrawCount;
    }
}
