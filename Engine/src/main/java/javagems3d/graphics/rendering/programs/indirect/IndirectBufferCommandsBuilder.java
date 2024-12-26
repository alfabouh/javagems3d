package javagems3d.graphics.rendering.programs.indirect;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

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
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);

        MemoryUtil.memFree(commandBuffer);
    }

    public void buildCommands(List<Integer> indexesList, Set<SceneObject> sceneObjects) {
        final int COM_SIZE = 5 * Float.BYTES;

        Map<SceneObject, Integer> idMap = new HashMap<>();

        int i1 = 0;
        int allMeshes = 0;
        Map<MeshBuffer, Set<SceneObject>> objectsMap = new HashMap<>();
        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = sceneObject.getModel().getMeshStructureWithUnSafeCast();
            allMeshes += meshBuffer.getPassData().size();
            idMap.put(sceneObject, i1++);
            JGemsHelper.UTILS.putObjectInMapOrUpdate(objectsMap, meshBuffer, new HashSet<SceneObject>() {{ add(sceneObject); }}, (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, sceneObject);
        }

        int firstIdx = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(allMeshes * COM_SIZE);
        for (MeshBuffer meshBuffer : this.getIndirectRenderBuffer().getAllStaticMeshBuffers()) {
            if (!objectsMap.containsKey(meshBuffer)) {
                continue;
            }
            int entitiesCount = objectsMap.get(meshBuffer).size();
            for (MeshBuffer.PassData data : meshBuffer.getPassData()) {
                commandBuffer.putInt(data.getVertices());
                commandBuffer.putInt(entitiesCount);
                commandBuffer.putInt(firstIdx);
                commandBuffer.putInt(data.getOffset());
                commandBuffer.putInt(baseInstance);

                firstIdx += data.getVertices();
                baseInstance += entitiesCount;

                for (SceneObject modeled : objectsMap.get(meshBuffer)) {
                    indexesList.add(idMap.get(modeled));
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
