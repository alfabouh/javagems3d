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

import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshDataGroup implements ICached {
    public static final String MESH_COLLISION_UD = "mesh_collision";
    public static final String MESH_RENDER_AABB_UD = "mesh_render_aabb";
    
    private final List<ModelNode> modelNodeList;
    private final Map<String, IMeshUserData> meshUserData;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
        this.meshUserData = new HashMap<>();
    }

    public MeshDataGroup(ModelNode modelNode) {
        this();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
    }

    public void putNode(ModelNode modelNode) {
        this.modelNodeList.add(modelNode);
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

    public List<ModelNode> getModelNodeList() {
        return this.modelNodeList;
    }

    public IMeshUserData getMeshUserData(String key) {
        return this.meshUserData.get(key);
    }

    public void setMeshUserData(String key, IMeshUserData meshUserData) {
        this.meshUserData.put(key, meshUserData);
    }

    public void clean() {
        this.meshUserData.clear();
        this.getModelNodeList().forEach(ModelNode::cleanMesh);
        this.getModelNodeList().clear();
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clean();
    }
}
