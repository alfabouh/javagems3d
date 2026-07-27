/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.programs.indirect.commands;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.IndirectSceneObjectsRenderer;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.service.exceptions.JGemsNullException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class DefaultIndirectCommandsProgram extends IndirectCommandsProgram {
    public DefaultIndirectCommandsProgram(IndirectBufferProgram indirectBufferProgram) {
        super(indirectBufferProgram);
    }

    public void buildCommands(@NotNull IntBuffer indexes, @Nullable IntBuffer materialIds, Collection<SceneObject> sceneObjects, IndirectSceneObjectsRenderer.Mode mode) {
        Map<SceneObject, Integer> idMap = new HashMap<>();
        Map<MeshBuffer, Set<SceneObject>> objectsMap = new HashMap<>();

        int i = 0;
        int drawCount = 0;

        for (SceneObject sceneObject : sceneObjects) {
            MeshBuffer meshBuffer = (MeshBuffer) sceneObject.getModel().getMeshStructure();
            if (meshBuffer == null) {
                throw new JGemsNullException("Model should have MeshBuffer, to implement indirect rendering");
            }
            if (mode.equals(IndirectSceneObjectsRenderer.Mode.ALL)) {
                drawCount += meshBuffer.getSolidPassData().size() + meshBuffer.getTransparentPassData().size();
            } else {
                final List<MeshBuffer.PassData> passData = this.chooseCollection(mode, meshBuffer);
                drawCount += passData.size();
            }
            int id = i++;
            idMap.put(sceneObject, id);
            JGemsHelper.Files.putObjectInMapOrUpdate(objectsMap, meshBuffer, new HashSet<>() {{
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

            if (mode.equals(IndirectSceneObjectsRenderer.Mode.ALL)) {
                int firstIdxSolid = 0;
                for (Map.Entry<Integer, List<MeshBuffer.PassData>> entry1 : entry.getKey().getMeshPassDataMap().entrySet()) {
                    firstIdxSolid = 0;
                    for (MeshBuffer.PassData data : entry1.getValue()) {
                       // if (data.getLocalAABB() == null || data.getLocalAABB().getAabbMin().y > -15) {
                            commandsBuffer.putInt(data.numVertexIndexes());
                            commandsBuffer.putInt(entitiesCount);
                            commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdxSolid);
                            commandsBuffer.putInt(data.getOffset());
                            commandsBuffer.putInt(baseInstance);
                            baseInstance += entitiesCount;
                            for (SceneObject modeled : entry.getValue()) {
                                if (materialIds != null) {
                                    materialIds.put(data.getMaterialId());
                                }
                                indexes.put(idMap.get(modeled));
                            }
                       // }
                        firstIdxSolid += data.numVertexIndexes();
                    }
                }
            } else {
                final List<MeshBuffer.PassData> passData = this.chooseCollection(mode, entry.getKey());
                int firstIdx = 0;
                for (MeshBuffer.PassData data : passData) {
                    //if (firstIdx <= -100) {
                        commandsBuffer.putInt(data.numVertexIndexes());
                        commandsBuffer.putInt(entitiesCount);
                        commandsBuffer.putInt(data.getFirstIndexOffset() + firstIdx);
                        commandsBuffer.putInt(data.getOffset());
                        commandsBuffer.putInt(baseInstance);
                        baseInstance += entitiesCount;
                        for (SceneObject modeled : entry.getValue()) {
                            if (materialIds != null) {
                                materialIds.put(data.getMaterialId());
                            }
                            indexes.put(idMap.get(modeled));
                        }
                    //}
                    firstIdx += data.numVertexIndexes();
                }
            }
        }
        this.passCommandsByteBuffer(commandsBuffer);
    }

    @SuppressWarnings("all")
    private List<MeshBuffer.PassData> chooseCollection(IndirectSceneObjectsRenderer.Mode mode, MeshBuffer key) {
        List<MeshBuffer.PassData> list = key.getSolidPassData();
        if (mode.equals(IndirectSceneObjectsRenderer.Mode.ALL)) {
            list = key.getAllPassData();
        } else if (mode.equals(IndirectSceneObjectsRenderer.Mode.ONLY_TRANSPARENT)) {
            list = key.getTransparentPassData();
        } else if (mode.equals(IndirectSceneObjectsRenderer.Mode.ONLY_SOLID)) {
            list = key.getSolidPassData();
        }
        return list;
    }
}
