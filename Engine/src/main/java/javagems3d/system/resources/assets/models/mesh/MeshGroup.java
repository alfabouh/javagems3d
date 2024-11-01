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

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;
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

    private final List<Animation> animationList;

    public MeshGroup() {
        this.animationList = new ArrayList<>();
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

    public void createRenderAABB() {
        this.createRenderAABB(DefaultPointers.POSITIONS.getIndex());
    }

    public void createRenderAABB(int positionAttributeIndex) {
        JGemsHelper.UTILS.createMeshRenderAABBData(this, positionAttributeIndex);
    }

    public void putNode(Node meshNode) {
        this.meshNodeList.add(meshNode);
    }

    public MeshGroup loadAnimations(List<Animation> animations) {
        this.getAnimationList().clear();
        this.getAnimationList().addAll(animations);
        return this;
    }

    public int getAnimationsNum() {
        return this.getAnimationList().size();
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

    public List<Animation> getAnimationList() {
        return this.animationList;
    }

    public List<Node> getModelNodeList() {
        return this.meshNodeList;
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
