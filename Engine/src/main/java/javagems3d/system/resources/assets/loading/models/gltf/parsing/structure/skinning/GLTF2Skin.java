package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Node;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.*;

public final class GLTF2Skin {
    private final String name;
    private final List<GLTF2Node> skeletonRoots;
    private final List<Integer> joints;
    private final Map<Integer, Integer> targetIdJointId;
    private final List<Matrix4f> inverseBindingMatrices;

    public GLTF2Skin(String name) {
        this.name = name;
        this.inverseBindingMatrices = new ArrayList<>();
        this.targetIdJointId = new HashMap<>();
        this.skeletonRoots = new ArrayList<>();
        this.joints = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<Integer> getJoints() {
        return this.joints;
    }

    public List<GLTF2Node> getSkeletonRoots() {
        return this.skeletonRoots;
    }

    public Map<Integer, Integer> getTargetIdJointId() {
        return this.targetIdJointId;
    }

    public List<Matrix4f> getInverseBindingMatrices() {
        return this.inverseBindingMatrices;
    }
}
