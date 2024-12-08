package javagems3d.graphics.rendering.programs.indirect;

import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Map;

public class IndirectBufferCommandsBuilder {
    private final IndirectRenderBuffer indirectRenderBuffer;

    private int staticDrawCount;
    private int staticRenderBufferHandle;

    public IndirectBufferCommandsBuilder(IndirectRenderBuffer indirectRenderBuffer) {
        this.indirectRenderBuffer = indirectRenderBuffer;
    }

    public void createBuffer() {
        this.staticRenderBufferHandle = GL46.glGenBuffers();
    }

    public void destroyBuffer() {
        GL46.glDeleteBuffers(this.getRenderBufferHandle());
    }

    public void buildCommands(Map<MeshBuffer, Integer> meshBufferCountMap) {
        final int COM_SIZE = 5 * Float.BYTES;

        int allMeshes = 0;
        for (MeshBuffer meshBuffer : this.getIndirectRenderBuffer().getAllStaticMeshBuffers()) {
            allMeshes += meshBuffer.getPassData().size();
        }

        int firstIdx = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(allMeshes * COM_SIZE);
        for (MeshBuffer meshBuffer : this.getIndirectRenderBuffer().getAllStaticMeshBuffers()) {
            int entitiesCount = meshBufferCountMap.getOrDefault(meshBuffer, 0);
            for (MeshBuffer.PassData data : meshBuffer.getPassData()) {
                commandBuffer.putInt(data.getVertices());
                commandBuffer.putInt(entitiesCount);
                commandBuffer.putInt(firstIdx);
                commandBuffer.putInt(data.getOffset());
                commandBuffer.putInt(baseInstance);

                firstIdx += data.getVertices();
                baseInstance += entitiesCount;
            }
        }
        commandBuffer.flip();

        this.staticDrawCount = commandBuffer.remaining() / COM_SIZE;
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, this.staticRenderBufferHandle);
        GL46.glBufferData(GL46.GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL46.GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    protected IndirectRenderBuffer getIndirectRenderBuffer() {
        return this.indirectRenderBuffer;
    }

    public int getRenderBufferHandle() {
        return this.staticRenderBufferHandle;
    }

    public int getDrawCount() {
        return this.staticDrawCount;
    }
}
