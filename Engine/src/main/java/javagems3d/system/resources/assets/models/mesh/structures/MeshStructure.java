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

package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class MeshStructure <T extends IMesh, R extends MeshNode<T>> implements ICached {
    protected final Map<Integer, List<R>> nodesLayers;
    private final Map<String, IUserData> meshUserData;

    public MeshStructure() {
        this.meshUserData = new HashMap<>();
        this.nodesLayers = new HashMap<>();
        this.initLayers();
    }

    protected void initLayers() {
        for (int i : this.getLayersToInit()) {
            this.nodesLayers.put(i, new ArrayList<>());
        }
        if (this.nodesLayers.isEmpty()) {
            this.nodesLayers.put(0, new ArrayList<>());
        }
    }

    public void setMeshUserData(String key, IUserData meshUserData) {
        this.meshUserData.put(key, meshUserData);
    }

    public static int chooseLayer(@NotNull Material material) {
        if (material.hasTransparency()) {
            return MeshStructure3D.TRANSPARENCY_LAYER;
        }
        return MeshStructure3D.SOLID_LAYER;
    }

    public void putNode(int layer, R r) {
        this.getNodes(layer).add(r);
    }

    public abstract int @NotNull [] getLayersToInit();

    public boolean hasNodes(int layer) {
        return !this.getNodes(layer).isEmpty();
    }

    protected void putMeshNode(int layer, R r) {
        this.nodesLayers.get(layer).add(r);
    }

    @Override
    public void onClearingCache(ResourceCache resourceCache) {
        this.clear();
    }

    public void clearNodesData(boolean keepTrianglesInMemory) {
        this.nodesLayers.values().forEach(e -> e.forEach(s -> s.clearData(keepTrianglesInMemory)));
    }

    public void clear() {
        this.meshUserData.clear();
        this.nodesLayers.values().forEach(e -> e.forEach(MeshNode::clear));
        this.nodesLayers.values().forEach(List::clear);
    }

    public List<R> getNodes(int layer) {
        return this.nodesLayers.get(layer);
    }

    public IUserData getMeshUserData(String key) {
        return this.meshUserData.get(key);
    }

    public boolean hasMeshUserData(String key) {
        return this.getMeshUserData(key) != null;
    }

    public List<R> getAllNodes() {
        int capacity = 0;
        for (List<R> layer : this.nodesLayers.values()) {
            capacity += layer.size();
        }
        List<R> list = new ArrayList<>(capacity);
        for (List<R> layer : this.nodesLayers.values()) {
            list.addAll(layer);
        }
        return list;
    }

    public interface IUserData {
    }
}