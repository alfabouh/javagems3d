package javagems3d.graphics.rendering.programs.indirect;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class IndirectBufferCommandsBuilder {
    private final IndirectRenderBufferProgram indirectRenderBufferProgram;

    private int staticDrawCount;
    private int staticRenderBufferHandle;

    public IndirectBufferCommandsBuilder(IndirectRenderBufferProgram indirectRenderBufferProgram) {
        this.indirectRenderBufferProgram = indirectRenderBufferProgram;
    }

    public void createBuffer() {
        this.staticRenderBufferHandle = GL46.glGenBuffers();
    }

    public void destroyBuffer() {
        GL46.glDeleteBuffers(this.getRenderBufferHandle());
    }

    public void buildCommands(IntBuffer indexes, IntBuffer materialIds, Collection<SceneObject> sceneObjects) {
        final int COM_SIZE = 5 * Float.BYTES;

        Map<SceneObject, Integer> idMap = new HashMap<>();
        Map<MeshBuffer, Set<SceneObject>> objectsMap = new HashMap<>();

        int i = 0;
        int allMeshes = 0;

        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = sceneObject.getModel().getMeshBufferForIndirectRendering();
            if (meshBuffer == null) {
                throw new JGemsRuntimeException("Model should have MeshBuffer, to implement indirect rendering!");
            }
            allMeshes += meshBuffer.getPassData().size();
            idMap.put(sceneObject, i++);
            JGemsHelper.UTILS.putObjectInMapOrUpdate(objectsMap, meshBuffer, new HashSet<SceneObject>() {{ add(sceneObject); }}, (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, sceneObject);
        }

        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(allMeshes * COM_SIZE);
        for (Map.Entry<MeshBuffer, Set<SceneObject>> meshBuffer : objectsMap.entrySet()) {
            int firstIdx = 0;
            int entitiesCount = meshBuffer.getValue().size();
            for (MeshBuffer.PassData data : meshBuffer.getKey().getPassData()) {
                commandBuffer.putInt(data.getVertices());
                commandBuffer.putInt(entitiesCount);
                commandBuffer.putInt(data.getFirstIndexOffset() + firstIdx);
                commandBuffer.putInt(data.getOffset());
                commandBuffer.putInt(baseInstance);

                firstIdx += data.getVertices();
                baseInstance += entitiesCount;

                for (SceneObject modeled : meshBuffer.getValue()) {
                    if (materialIds != null) {
                        materialIds.put(data.getMaterialId());
                    }
                    indexes.put(idMap.get(modeled));
                }
            }
        }
        commandBuffer.flip();

        this.staticDrawCount = commandBuffer.remaining() / COM_SIZE;

        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, this.staticRenderBufferHandle);
        GL46.glBufferData(GL46.GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);

        MemoryUtil.memFree(commandBuffer);
    }

    protected IndirectRenderBufferProgram getIndirectRenderBuffer() {
        return this.indirectRenderBufferProgram;
    }

    public int getRenderBufferHandle() {
        return this.staticRenderBufferHandle;
    }

    public int getDrawCount() {
        return this.staticDrawCount;
    }
}
