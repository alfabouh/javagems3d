/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.models.mesh;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.data.IMeshUserData;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshGroup implements ICached {
    public static final String MESH_COLLISION_UD = "mesh_collision";
    public static final String MESH_RENDER_AABB_UD = "mesh_render_aabb";
    
    private final List<Node> meshNodeList;
    private final Map<String, IMeshUserData> meshUserData;

    public MeshGroup() {
        this.meshNodeList = new ArrayList<>();
        this.meshUserData = new HashMap<>();
    }

    public MeshGroup(Node meshNode) {
        this();
        this.putNode(meshNode);
    }

    public MeshGroup(Mesh mesh) {
        this(new Node(mesh));
    }

    public void putNode(Node meshNode) {
        this.meshNodeList.add(meshNode);
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getUnSafeMeshUserData(String key) {
        return this.getMeshUserData(key, null);
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getMeshUserData(String key, Class<T> tClass) {
        if (this.getMeshUserData(key) == null) {
            return null;
        }
        if (tClass == null || this.getMeshUserData(key).getClass().isAssignableFrom(tClass)) {
            return (T) this.getMeshUserData(key);
        }
        return null;
    }

    public List<Node> getModelNodeList() {
        return this.meshNodeList;
    }

    public IMeshUserData getMeshUserData(String key) {
        return this.meshUserData.get(key);
    }

    public void setMeshUserData(String key, IMeshUserData meshUserData) {
        this.meshUserData.put(key, meshUserData);
    }

    public void clean() {
        this.meshUserData.clear();
        this.getModelNodeList().forEach(Node::cleanMesh);
        this.getModelNodeList().clear();
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clean();
    }

    public static class Node {
        private final Mesh mesh;
        private final Material material;

        public Node(Mesh mesh, Material material) {
            this.mesh = mesh;
            this.material = material;
        }

        public Node(Mesh mesh) {
            this.mesh = mesh;
            this.material = null;
        }

        public void cleanMesh() {
            this.getMesh().cleanMesh();
        }

        public Mesh getMesh() {
            return this.mesh;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}
