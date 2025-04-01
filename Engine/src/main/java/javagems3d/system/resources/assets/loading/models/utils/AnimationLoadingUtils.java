package javagems3d.system.resources.assets.loading.models.utils;

import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.components.Bone;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.animation.components.VertexWeight;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AnimationLoadingUtils {

    public static List<Animation> readAnimation(AIScene aiScene, List<Bone> bonesList, Animation.Node root, Matrix4f globalInverseTransformation) {
        List<Animation> animations = new ArrayList<>();

        int totalAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < totalAnimations; i++) {
            if (aiAnimations != null) {
                AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
                int maxFrames = AnimationLoadingUtils.calcAnimationMaxFrames(aiAnimation);

                List<AnimationFrame> frameList = new ArrayList<>();
                Animation animation = new Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), aiAnimation.mTicksPerSecond(), frameList);
                animations.add(animation);

                for (int j = 0; j < maxFrames; j++) {
                    Matrix4f[] boneMatrices = new Matrix4f[JGemsConfig.SYSTEM.ANIM_MAX_BONES];
                    Arrays.fill(boneMatrices, new Matrix4f().identity());
                    AnimationFrame animationFrame = new AnimationFrame(boneMatrices);
                    AnimationLoadingUtils.createFrameMatrices(aiAnimation, bonesList, animationFrame, j, root, root.getTransformation(), globalInverseTransformation);
                    frameList.add(animationFrame);
                }
            }
        }

        return animations;
    }

    public static SkeletonData readSkeleton(AIMesh aiMesh, List<Bone> bonesList) {
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        Map<Integer, List<VertexWeight>> weightMap = new HashMap<>();
        int totalBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < totalBones; i++) {
            if (aiBones != null) {
                AIBone aiBone = AIBone.create(aiBones.get(i));
                int id = bonesList.size();
                Bone bone = new Bone(id, aiBone.mName().dataString(), AnimationLoadingUtils.toJOMLMatrix(aiBone.mOffsetMatrix()));
                bonesList.add(bone);
                int totalWeights = aiBone.mNumWeights();
                AIVertexWeight.Buffer aiVertexWeights = aiBone.mWeights();
                for (int j = 0; j < totalWeights; j++) {
                    AIVertexWeight aiVertexWeight = aiVertexWeights.get(j);
                    VertexWeight vertexWeight = new VertexWeight(bone.getBoneId(), aiVertexWeight.mVertexId(), aiVertexWeight.mWeight());
                    List<VertexWeight> vertexWeightList = weightMap.computeIfAbsent(vertexWeight.getVertexId(), k -> new ArrayList<>());
                    vertexWeightList.add(vertexWeight);
                }
            } else {
                return null;
            }
        }

        int totalVertices = aiMesh.mNumVertices();
        for (int i = 0; i < totalVertices; i++) {
            List<VertexWeight> vertexWeightList = weightMap.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vertexWeight = vertexWeightList.get(j);
                    weights.add(vertexWeight.getWeight());
                    boneIds.add(vertexWeight.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }

        return new SkeletonData(weights, boneIds);
    }

    public static Animation.Node createNodesTree(AINode aiNode, Animation.Node parent) {
        String name = aiNode.mName().dataString();
        Animation.Node node = new Animation.Node(name, parent, AnimationLoadingUtils.toJOMLMatrix(aiNode.mTransformation()));

        int totalLeaves = aiNode.mNumChildren();
        PointerBuffer pointerBuffer = aiNode.mChildren();
        for (int i = 0; i < totalLeaves; i++) {
            if (pointerBuffer != null) {
                AINode aiLeafNode = AINode.create(pointerBuffer.get(i));
                Animation.Node leaf = AnimationLoadingUtils.createNodesTree(aiLeafNode, node);
                node.addLeaf(leaf);
            } else {
                return null;
            }
        }
        return node;
    }

    public static Matrix4f toJOMLMatrix(AIMatrix4x4 aiMatrix4x4) {
        return new Matrix4f(
                aiMatrix4x4.a1(), aiMatrix4x4.b1(), aiMatrix4x4.c1(), aiMatrix4x4.d1(),
                aiMatrix4x4.a2(), aiMatrix4x4.b2(), aiMatrix4x4.c2(), aiMatrix4x4.d2(),
                aiMatrix4x4.a3(), aiMatrix4x4.b3(), aiMatrix4x4.c3(), aiMatrix4x4.d3(),
                aiMatrix4x4.a4(), aiMatrix4x4.b4(), aiMatrix4x4.c4(), aiMatrix4x4.d4()
        );
    }

    private static void createFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, AnimationFrame animationFrame, int frame, Animation.Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
        String nodeName = node.getName();
        AINodeAnim aiNodeAnim = AnimationLoadingUtils.findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransformation = node.getTransformation();
        if (aiNodeAnim != null) {
            nodeTransformation = AnimationLoadingUtils.createNodeTransformationMatrix(aiNodeAnim, frame);
        }

        Matrix4f nodeGlobalTransformation = new Matrix4f(parentTransformation).mul(nodeTransformation);

        List<Bone> affectedBones = boneList.stream().filter(e -> e.getBoneName().equals(nodeName)).collect(Collectors.toList());
        for (Bone bone : affectedBones) {
            Matrix4f boneTransformation = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransformation).mul(bone.getOffset());
            animationFrame.getBoneMatrices()[bone.getBoneId()] = boneTransformation;
        }

        for (Animation.Node leaf : node.getLeaves()) {
            AnimationLoadingUtils.createFrameMatrices(aiAnimation, boneList, animationFrame, frame, leaf, nodeGlobalTransformation, globalInverseTransform);
        }
    }

    private static Matrix4f createNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        Matrix4f nodeTransformation = new Matrix4f();
        int totalPositions = aiNodeAnim.mNumPositionKeys();
        if (totalPositions > 0) {
            if (positionKeys != null) {
                AIVectorKey aiVectorKey = positionKeys.get(Math.min(totalPositions - 1, frame));
                AIVector3D vector3D = aiVectorKey.mValue();
                nodeTransformation.translate(vector3D.x(), vector3D.y(), vector3D.z());
            } else {
                //return null;
            }
        }
        int totalRotations = aiNodeAnim.mNumRotationKeys();
        if (totalRotations > 0) {
            if (rotationKeys != null) {
                AIQuatKey aiVectorKey = rotationKeys.get(Math.min(totalRotations - 1, frame));
                AIQuaternion aiQuaternion = aiVectorKey.mValue();
                nodeTransformation.rotate(new Quaternionf(aiQuaternion.x(), aiQuaternion.y(), aiQuaternion.z(), aiQuaternion.w()));
            } else {
                //return null;
            }
        }
        int totalScalingKeys = aiNodeAnim.mNumScalingKeys();
        if (totalScalingKeys > 0) {
            if (scalingKeys != null) {
                AIVectorKey aiVectorKey = scalingKeys.get(Math.min(totalScalingKeys - 1, frame));
                AIVector3D vector3D = aiVectorKey.mValue();
                nodeTransformation.scale(vector3D.x(), vector3D.y(), vector3D.z());
            } else {
                //return null;
            }
        }
        return nodeTransformation;
    }

    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
        AINodeAnim result = null;
        int totalAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < totalAnimNodes; i++) {
            if (aiChannels != null) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
                if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
                    result = aiNodeAnim;
                    break;
                }
            }
        }
        return result;
    }

    private static int calcAnimationMaxFrames(AIAnimation animation) {
        int maxFrames = 0;
        int totalNodeAnimations = animation.mNumChannels();
        PointerBuffer aiChannels = animation.mChannels();
        for (int i = 0; i < totalNodeAnimations; i++) {
            if (aiChannels != null) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
                int totalFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()), aiNodeAnim.mNumRotationKeys());
                maxFrames = Math.max(maxFrames, totalFrames);
            }
        }
        return maxFrames;
    }
}