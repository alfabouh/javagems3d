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

package ru.jgems3d.engine.system.resources.assets.models.mesh;

import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a container for meshes
 */
public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;
    private IMeshDataContainer meshDataContainer;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
        this.meshDataContainer = null;
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
    public <T extends IMeshDataContainer> T getMeshDataContainer(Class<T> tClass) {
        if (this.getMeshDataContainer() == null) {
            return null;
        }
        if (this.getMeshDataContainer().getClass().isAssignableFrom(tClass)) {
            return (T) this.getMeshDataContainer();
        }
        return null;
    }

    /**
     * This is additional user information, if necessary
     * @param meshDataContainer
     */
    public void setMeshDataContainer(IMeshDataContainer meshDataContainer) {
        this.meshDataContainer = meshDataContainer;
    }

    public IMeshDataContainer getMeshDataContainer() {
        return this.meshDataContainer;
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
