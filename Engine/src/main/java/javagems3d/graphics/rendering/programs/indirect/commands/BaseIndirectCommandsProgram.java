package javagems3d.graphics.rendering.programs.indirect.commands;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class BaseIndirectCommandsProgram extends IndirectCommandsProgram {
    public BaseIndirectCommandsProgram(IndirectBufferProgram indirectBufferProgram) {
        super(indirectBufferProgram);
    }

    public void buildCommands(IntBuffer indexes, IntBuffer materialIds, Collection<SceneObject> sceneObjects, IndirectObjectsRenderer.Mode mode) {
        Map<SceneObject, Integer> idMap = new HashMap<>();
        Map<MeshBuffer, Set<SceneObject>> objectsMap = new HashMap<>();

        int i = 0;
        int drawCount = 0;

        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = sceneObject.getModel().getMeshBufferForIndirectRendering();
            if (meshBuffer == null) {
                throw new JGemsNullException("Model should have MeshBuffer, to implement indirect rendering");
            }
            if (mode.equals(IndirectObjectsRenderer.Mode.ALL)) {
                drawCount += meshBuffer.getSolidPassData().size() + meshBuffer.getTransparentPassData().size();
            } else {
                final List<MeshBuffer.PassData> passData = this.chooseCollection(mode, meshBuffer);
                drawCount += passData.size();
            }
            int id = i++;
            idMap.put(sceneObject, id);
            JGemsHelper.UTILS.putObjectInMapOrUpdate(objectsMap, meshBuffer, new HashSet<SceneObject>() {{
                add(sceneObject);
            }}, (ex, nw) ->
            {
                ex.add(nw);
                return ex;
            }, sceneObject);
}

        int baseInstance = 0;
        ByteBuffer commandsBuffer = this.initCommandsByteBuffer(drawCount);

        for (Map.Entry<MeshBuffer, Set<SceneObject>> entry : objectsMap.entrySet()) {
            int entitiesCount = entry.getValue().size();

            if (mode.equals(IndirectObjectsRenderer.Mode.ALL)) {
                List<MeshBuffer.PassData> solidPassData = entry.getKey().getSolidPassData();
                int firstIdxSolid = 0;
                for (MeshBuffer.PassData data : solidPassData) {
                    commandsBuffer.putInt(data.numVertexIndexes());
                    commandsBuffer.putInt(entitiesCount);
                    commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdxSolid);
                    commandsBuffer.putInt(data.getOffset());
                    commandsBuffer.putInt(baseInstance);

                    firstIdxSolid += data.numVertexIndexes();
                    for (SceneObject modeled : entry.getValue()) {
                        if (materialIds != null) {
                            materialIds.put(data.getMaterialId());
                        }
                        indexes.put(idMap.get(modeled));
                    }
                    baseInstance += entitiesCount;
                }
                List<MeshBuffer.PassData> transparentPassData = entry.getKey().getTransparentPassData();
                int firstIdxTransparent = 0;
                for (MeshBuffer.PassData data : transparentPassData) {
                    commandsBuffer.putInt(data.numVertexIndexes());
                    commandsBuffer.putInt(entitiesCount);
                    commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdxTransparent);
                    commandsBuffer.putInt(data.getOffset());
                    commandsBuffer.putInt(baseInstance);

                    firstIdxTransparent += data.numVertexIndexes();
                    for (SceneObject modeled : entry.getValue()) {
                        if (materialIds != null) {
                            materialIds.put(data.getMaterialId());
                        }
                        indexes.put(idMap.get(modeled));
                    }
                    baseInstance += entitiesCount;
                }
            } else {
                final List<MeshBuffer.PassData> passData = this.chooseCollection(mode, entry.getKey());
                int firstIdx = 0;
                for (MeshBuffer.PassData data : passData) {
                    commandsBuffer.putInt(data.numVertexIndexes());
                    commandsBuffer.putInt(entitiesCount);
                    commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdx);
                    commandsBuffer.putInt(data.getOffset());
                    commandsBuffer.putInt(baseInstance);

                    firstIdx += data.numVertexIndexes();
                    baseInstance += entitiesCount;

                    for (SceneObject modeled : entry.getValue()) {
                        if (materialIds != null) {
                            materialIds.put(data.getMaterialId());
                        }
                        indexes.put(idMap.get(modeled));
                    }
                }
            }
        }
        this.passCommandsByteBuffer(commandsBuffer);
    }

    @SuppressWarnings("all")
    private List<MeshBuffer.PassData> chooseCollection(IndirectObjectsRenderer.Mode mode, MeshBuffer key) {
        //TODO: SWITCH DOESNT WORK
        List<MeshBuffer.PassData> list = key.getSolidPassData();
        if (mode.equals(IndirectObjectsRenderer.Mode.ALL)) {
            list = key.getAllPassData();
        } else if (mode.equals(IndirectObjectsRenderer.Mode.ONLY_TRANSPARENT)) {
            list = key.getTransparentPassData();
        } else if (mode.equals(IndirectObjectsRenderer.Mode.ONLY_SOLID)) {
            list = key.getSolidPassData();
        }
        return list;
    }
}
