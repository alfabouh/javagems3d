package javagems3d.graphics.rendering.programs.indirect.commands;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class BaseIndirectCommandsProgram extends IndirectCommandsProgram {
    public BaseIndirectCommandsProgram(IndirectBufferProgram indirectBufferProgram) {
        super(indirectBufferProgram);
    }

    public void buildCommands(IntBuffer indexes, IntBuffer materialIds, Collection<SceneObject> sceneObjects) {
        Map<SceneObject, Integer> idMap = new HashMap<>();
        Map<MeshBuffer, Set<SceneObject>> objectsMap = new HashMap<>();

        int i = 0;
        int drawCount = 0;

        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = sceneObject.getModel().getMeshBufferForIndirectRendering();
            if (meshBuffer == null) {
                throw new JGemsRuntimeException("Model should have MeshBuffer, to implement indirect rendering!");
            }
            drawCount += meshBuffer.getPassData().size();
            int id = i++;
            idMap.put(sceneObject, id);
            JGemsHelper.UTILS.putObjectInMapOrUpdate(objectsMap, meshBuffer, new HashSet<SceneObject>() {{ add(sceneObject); }}, (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, sceneObject);
        }

        int baseInstance = 0;
        ByteBuffer commandsBuffer = this.initCommandsByteBuffer(drawCount);
        for (Map.Entry<MeshBuffer, Set<SceneObject>> meshBuffer : objectsMap.entrySet()) {
            int firstIdx = 0;
            int entitiesCount = meshBuffer.getValue().size();
            for (MeshBuffer.PassData data : meshBuffer.getKey().getPassData()) {
                commandsBuffer.putInt(data.numVertexIndexes());
                commandsBuffer.putInt(entitiesCount);
                commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdx);
                commandsBuffer.putInt(data.getOffset());
                commandsBuffer.putInt(baseInstance);

                firstIdx += data.numVertexIndexes();
                baseInstance += entitiesCount;

                for (SceneObject modeled : meshBuffer.getValue()) {
                    if (materialIds != null) {
                        materialIds.put(data.getMaterialId());
                    }
                    indexes.put(idMap.get(modeled));
                }
            }
        }
        this.passCommandsByteBuffer(commandsBuffer);
    }
}
