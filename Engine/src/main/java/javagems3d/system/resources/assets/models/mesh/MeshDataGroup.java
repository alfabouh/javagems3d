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
import java.util.List;

/**
 * This is a container for meshes
 */
public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;
    private IMeshUserData meshUserData;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
        this.meshUserData = null;
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

    public List<ModelNode> getModelNodeList() {
        return this.modelNodeList;
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getMeshUserData(Class<T> tClass) {
        if (this.getMeshUserData() == null) {
            return null;
        }
        if (this.getMeshUserData().getClass().isAssignableFrom(tClass)) {
            return (T) this.getMeshUserData();
        }
        return null;
    }

    public IMeshUserData getMeshUserData() {
        return this.meshUserData;
    }

    /**
     * This is additional user information, if necessary
     *
     * @param meshUserData
     */
    public void setMeshUserData(IMeshUserData meshUserData) {
        this.meshUserData = meshUserData;
    }

    public void clean() {
        this.getModelNodeList().forEach(ModelNode::cleanMesh);
        this.getModelNodeList().clear();
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clean();
    }
}
