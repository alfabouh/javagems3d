package javagems3d.system.resources.assets.loading.models.gltf;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.GLTF2Parser;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.GLTF2RawData;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.*;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Animations;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Skin;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.*;
import java.util.function.Consumer;

public class GLTF2ModelLoader implements ILoadingHelper {
    private final JGemsPath path;
    private final SystemResources systemResources;
    public int countVertexes;
    private final JGems3D.GetSource getSource;

    public GLTF2ModelLoader(@NotNull JGems3D.GetSource source, SystemResources systemResources, JGemsPath pathToMainFile) {
        this.path = pathToMainFile;
        this.systemResources = systemResources;
        this.getSource = source;
    }

    public MeshGroup createMeshGroup(@Nullable MeshCollisionData.Fabric meshCollisionDataFabric, boolean attachMeshBuffer, boolean keepNodesInMemory) {
        GLTF2RawData gltf2RawData = GLTF2Parser.parse(this.getSource, this.getPath());
        GLTF2Scene gltf2Scene = gltf2RawData.getGltf2Scene();
        return this.createMeshGroup(gltf2Scene, meshCollisionDataFabric, attachMeshBuffer, keepNodesInMemory);
    }

    public MeshBuffer createMeshBuffer(@Nullable MeshCollisionData.Fabric meshCollisionDataFabric, boolean keepNodesInMemory) {
        GLTF2RawData gltf2RawData = GLTF2Parser.parse(this.getSource, this.getPath());
        GLTF2Scene gltf2Scene = gltf2RawData.getGltf2Scene();
        return this.createMeshBuffer(gltf2Scene, meshCollisionDataFabric, keepNodesInMemory);
    }

    public String getStr(String postfix) {
        return GLTF2ModelLoader.getModelStr(this.getPath(), postfix);
    }

    public static String getModelStr(JGemsPath path, String postfix) {
        return path + postfix;
    }

    private MeshGroup createMeshGroup(GLTF2Scene gltf2Scene, @Nullable MeshCollisionData.Fabric fabric, boolean attachMeshBuffer, boolean keepNodesInMemory) {
        MeshGroup meshGroup = null;
        String grString = this.getStr(MeshGroup.POSTFIX);
        if (this.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = this.getResourceCache().getCachedObjectUnSafeCast(grString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshGroup = this.processMeshGroup(gltf2Scene, this.getSystemResources(), attachMeshBuffer, keepNodesInMemory);
            this.getResourceCache().registerInCache(grString, meshGroup);
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while processing the model");
        }
        JGemsUtils.createMeshAABBData(meshGroup);
        if (DynamicsSystem.VALID) {
            JGemsUtils.createMeshCollisionData(meshGroup, fabric);
        }
        if (!keepNodesInMemory) {
            meshGroup.clearNodesData();
        }
        return meshGroup;
    }

    private MeshBuffer createMeshBuffer(GLTF2Scene gltf2Scene, @Nullable MeshCollisionData.Fabric fabric, boolean keepNodesInMemory) {
        String bffString = this.getStr(MeshBuffer.POSTFIX);
        MeshBuffer meshBuffer = null;
        if (this.isCacheValid() && this.getResourceCache().checkObjectInCache(bffString)) {
            meshBuffer = this.getResourceCache().getCachedObjectUnSafeCast(bffString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshBuffer = this.processMeshBuffer(gltf2Scene, this.getSystemResources(), keepNodesInMemory);
            this.getResourceCache().registerInCache(bffString, meshBuffer);
        }
        if (meshBuffer == null) {
            throw new JGemsNullException("There was an error, while processing the model");
        }
        JGemsUtils.createMeshAABBData(meshBuffer);
        if (DynamicsSystem.VALID) {
            JGemsUtils.createMeshCollisionData(meshBuffer, fabric);
        }
        return meshBuffer;
    }

    private MeshGroup processMeshGroup(GLTF2Scene scene, SystemResources systemResources, boolean attachMeshBuffer, boolean keepNodesInMemory) {
        MeshGroup group = new MeshGroup();
        MeshBuffer buffer = attachMeshBuffer ? new MeshBuffer() : null;

        try {
            List<Animation> animations = this.readAnimations(scene);
            List<Material> materials = readMaterials(scene, systemResources);
            systemResources.processMessage("Building Mesh Group...", 0x00ff00);
            this.processNodes(scene.getNodes(), materials, node -> group.putNode(MeshStructure3D.chooseLayer(node.getMaterial()), node), buffer != null ? node -> buffer.putNode(MeshStructure3D.chooseLayer(node.getMaterial()), node) : null);

            if (animations != null) {
                group.loadAnimations(animations);
                if (buffer != null) {
                    buffer.loadAnimations(animations);
                }
            }

            if (buffer != null) {
                group.setLinkedMeshBuffer(buffer);
                buffer.setKeepNodesInMemory(keepNodesInMemory);
                systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(buffer);
            }

            Log.get().info("Mesh " + this.getPath() + " successfully created");
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }

        return group;
    }

    private MeshBuffer processMeshBuffer(GLTF2Scene scene, SystemResources systemResources, boolean keepNodesInMemory) {
        MeshBuffer buffer = new MeshBuffer();

        try {
            List<Animation> animations = this.readAnimations(scene);
            List<Material> materials = this.readMaterials(scene, systemResources);
            systemResources.processMessage("Building Mesh Buffer...", 0x00ff00);

            this.processNodes(scene.getNodes(), materials, null, node -> buffer.putNode(MeshStructure3D.chooseLayer(node.getMaterial()), node));

            if (animations != null) {
                buffer.loadAnimations(animations);
            }

            buffer.setKeepNodesInMemory(keepNodesInMemory);
            systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(buffer);

            Log.get().info("Mesh " + this.getPath() + " successfully created");
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }

        return buffer;
    }

    private void processNodes(List<GLTF2Node> nodes, List<Material> materials, Consumer<MeshNode3D<RenderMesh>> renderMeshConsumer, Consumer<MeshNode3D<DataMesh>> dataMeshConsumer) {
        for (GLTF2Node node : nodes) {
            if (!node.hasMesh()) {
                continue;
            }
            for (GLTF2Primitive primitive : node.getMesh().getPrimitives()) {
                int matIdx = primitive.getMaterialId();
                Material material = (matIdx >= 0 && matIdx < materials.size()) ? materials.get(matIdx) : new Material();
                SkeletonData skeleton = (primitive.getWEIGHTS_0() != null && primitive.getJOINTS_0() != null) ? new SkeletonData(primitive.getWEIGHTS_0().getObjects(), primitive.getJOINTS_0().getObjects()) : null;

                if (renderMeshConsumer != null) {
                    RenderMesh renderMesh = this.createRenderMesh(primitive, skeleton);
                    renderMeshConsumer.accept(new MeshNode3D<>(renderMesh, material));
                }

                if (dataMeshConsumer != null) {
                    DataMesh dataMesh = this.createDataMesh(primitive, skeleton);
                    dataMeshConsumer.accept(new MeshNode3D<>(dataMesh, material));
                }
            }
        }
    }


    private List<Material> readMaterials(GLTF2Scene gltf2Scene, SystemResources systemResources) {
        List<Material> materials = new ArrayList<>();
        for (GLTF2Material gltfMat : gltf2Scene.getMaterials()) {
            Material mat = this.readMaterial(gltfMat, systemResources, this.getPath().getAbsolutePathDirectory().getFullPath());
            systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(mat);
            materials.add(mat);
        }
        return materials;
    }

    private List<Animation> readAnimations(GLTF2Scene gltf2Scene) {
        if (gltf2Scene.getAnimations() == null || gltf2Scene.getSkins() == null) {
            return null;
        }

        List<Animation> animations = new ArrayList<>();
        for (GLTF2Animations gltf2Animation : gltf2Scene.getAnimations()) {
            Map<Float, Map<Integer, Pair<GLTF2Skin, Set<DataOnTime<?>>>>> timeMap = new TreeMap<>(Comparator.comparingDouble((k) -> k));

            for (GLTF2Animations.Channel channel : gltf2Animation.getChannels()) {
                final int nodeId = channel.getTargetNode();
                GLTF2Animations.Sampler gltf2Sampler = channel.getSampler();

                for (int i = 0; i < gltf2Sampler.getTimeStamps().size(); i++) {
                    float time = gltf2Sampler.getTimeStamps().getObjects().get(i);
                    if (!timeMap.containsKey(time)) {
                        timeMap.put(time, new HashMap<>());
                    }
                    DataOnTime<?> data = null;
                    List<Float> floats = gltf2Sampler.getDataOnTime().getObjects();
                    switch (channel.getPath()) {
                        case TRANSLATE: {
                            data = new TranslationOnTime(new Vector3f(floats.get(i * 3), floats.get(i * 3 + 1), floats.get(i * 3 + 2)), time);
                            break;
                        }
                        case ROTATION: {
                            data = new RotationOnTime(new Quaternionf(floats.get(i * 4), floats.get(i * 4 + 1), floats.get(i * 4 + 2), floats.get(i * 4 + 3)), time);
                            break;
                        }
                        case SCALE: {
                            data = new ScaleOnTime(new Vector3f(floats.get(i * 3), floats.get(i * 3 + 1), floats.get(i * 3 + 2)), time);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    int skinId = gltf2Scene.getNodes().get(nodeId).getSkin();
                    if (skinId < 0) {
                        skinId = 0;
                    }
                    final GLTF2Skin skin = gltf2Scene.getSkins().get(skinId);
                    int nodeNew = gltf2Scene.getSkins().get(skinId).getTargetIdJointId().get(nodeId);
                    if (!timeMap.get(time).containsKey(nodeNew)) {
                        timeMap.get(time).put(nodeNew, new Pair<>(skin, new HashSet<>()));
                    }
                    timeMap.get(time).get(nodeNew).getSecond().add(data);
                }
            }

            List<AnimationFrame> animationFrames = new ArrayList<>();
            for (Map.Entry<Float, Map<Integer, Pair<GLTF2Skin, Set<DataOnTime<?>>>>> entry : timeMap.entrySet()) {
                for (GLTF2Node node : gltf2Scene.getNodes()) {
                    node.setLocalAnimationTransform(null);
                }
                Matrix4f[] matrices = new Matrix4f[JGemsConfig.SYSTEM.ANIM_MAX_BONES];
                for (int i = 0; i < matrices.length; i++) {
                    matrices[i] = new Matrix4f().identity();
                }
                for (Map.Entry<Integer, Pair<GLTF2Skin, Set<DataOnTime<?>>>> entryInner : entry.getValue().entrySet()) {
                    final int boneId = entryInner.getKey();
                    Vector3f translation = new Vector3f(0, 0, 0);
                    Quaternionf rotation = new Quaternionf().identity();
                    Vector3f scale = new Vector3f(1, 1, 1);

                    final Set<DataOnTime<?>> dataOntimeSet = entryInner.getValue().getSecond();
                    for (DataOnTime<?> data : dataOntimeSet) {
                        switch (data.path()) {
                            case TRANSLATE: {
                                translation.set(((TranslationOnTime) data).data());
                                break;
                            }
                            case ROTATION: {
                                rotation.set(((RotationOnTime) data).data());
                                break;
                            }
                            case SCALE: {
                                scale.set(((ScaleOnTime) data).data());
                                break;
                            }
                        }
                    }
                    Matrix4f localAnimationTransformation = new Matrix4f().identity().translate(translation).rotate(rotation).scale(scale);
                    GLTF2Skin skin = entryInner.getValue().getFirst();
                    int nodeIndex = skin.getJoints().get(boneId);
                    GLTF2Node gltf2Node = gltf2Scene.getNodes().get(nodeIndex);
                    gltf2Node.setLocalAnimationTransform(localAnimationTransformation);
                }

                for (Map.Entry<Integer, Pair<GLTF2Skin, Set<DataOnTime<?>>>> entryInner : entry.getValue().entrySet()) {
                    final int boneId = entryInner.getKey();
                    GLTF2Skin skin = entryInner.getValue().getFirst();
                    int nodeIndex = skin.getJoints().get(boneId);
                    GLTF2Node gltf2Node = gltf2Scene.getNodes().get(nodeIndex);
                    @NotNull Matrix4f inverseBindingMatrix = skin.getInverseBindingMatrices().get(boneId);
                    matrices[boneId] = gltf2Node.computeAnimationTransform(inverseBindingMatrix);
                }

                animationFrames.add(new AnimationFrame(matrices));
            }

            @SuppressWarnings("all") final float duration = timeMap.keySet().stream().max(Comparator.comparingDouble((e) -> e)).get();
            Animation animation = new Animation(gltf2Animation.getName(), duration, 24.0f, animationFrames);
            animations.add(animation);

            Log.get().info("Loaded animation (size:" + animations.size() + ") for: " + this.getPath() + ". " + animation.getName());
            systemResources.processMessage("Loaded animation(size:" + animations.size() + "). " + animation.getName(), 0xff00ff);
        }

        return animations;
    }

    private DataMesh createDataMesh(GLTF2Primitive gltf2Primitive, SkeletonData skeletonData) {
        List<Integer> vertices = gltf2Primitive.getIndices().getObjects();
        this.countVertexes += vertices.size();
        if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsIOException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Float> textureCoordinates = gltf2Primitive.getTEXCOORD_0().getObjects();
        List<Float> positions = gltf2Primitive.getPOSITION().getObjects();
        List<Float> normals = gltf2Primitive.getNORMAL().getObjects();
        List<Float> tangents = gltf2Primitive.getTANGENT().getObjects();
        List<Float> biTangents = gltf2Primitive.getBiTANGENT().getObjects();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        DataMesh dataMesh = new DataMesh();
        dataMesh.setSkeletonData(skeletonData);

        dataMesh.putVertexIndexes(vertices);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, positions);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, textureCoordinates);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_NORMALS, normals);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TANGENTS, tangents);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BI_TANGENTS, biTangents);

        if (skeletonData != null) {
            dataMesh.putVertexBufferI(DefaultAttributePointers.ATTR_BONES_INDEXES, skeletonData.getBoneIds());
            dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BONES_WEIGHTS, skeletonData.getWeights());
        }

        return dataMesh;
    }

    private RenderMesh createRenderMesh(GLTF2Primitive gltf2Primitive, SkeletonData skeletonData) {
        List<Integer> vertices = gltf2Primitive.getIndices().getObjects();
        this.countVertexes += vertices.size();
        if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsIOException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Float> textureCoordinates = gltf2Primitive.getTEXCOORD_0().getObjects();
        List<Float> positions = gltf2Primitive.getPOSITION().getObjects();
        List<Float> normals = gltf2Primitive.getNORMAL().getObjects();
        List<Float> tangents = gltf2Primitive.getTANGENT().getObjects();
        List<Float> biTangents = gltf2Primitive.getBiTANGENT().getObjects();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        RenderMesh renderMesh = new RenderMesh();
        renderMesh.setSkeletonData(skeletonData);

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        renderMesh.setVertexIndexes(vertices);
        vaPositions.set(positions);
        vaNormals.set(normals);
        vaTextureCoordinates.set(textureCoordinates);
        vaTangents.set(tangents);
        vaBiTangents.set(biTangents);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);
        renderMesh.putVertexAttribute(vaNormals);
        renderMesh.putVertexAttribute(vaTangents);
        renderMesh.putVertexAttribute(vaBiTangents);

        if (skeletonData != null) {
            IntegerVertexAttribute vaBonesIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONES_INDEXES);
            FloatVertexAttribute vaBonesWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONES_WEIGHTS);
            vaBonesIndexes.put(skeletonData.getBoneIds());
            vaBonesWeights.put(skeletonData.getWeights());

            renderMesh.putVertexAttribute(vaBonesIndexes);
            renderMesh.putVertexAttribute(vaBonesWeights);
        }

        renderMesh.bakeMesh();
        return renderMesh;
    }

    private Material readMaterial(GLTF2Material gltf2Material, SystemResources systemResources, String fullPath) {
        float opacityConstant = 1.0f;
        float metallicFactor = 0.0f;
        float roughnessFactor = 0.5f;
        boolean textureIsImageAndHasAlphaPixels = false;

        Color4Texture diffuseColor = null;
        Color3Texture emissionColor = null;

        ITexture2DProgram diffuseMap = null;
        ITexture2DProgram emissionMap = null;
        ITexture2DProgram normalsMap = null;
        ITexture2DProgram metallicRoughnessMap = null;

        final ImageTexture.Properties imageProperties = new ImageTexture.Properties(true, true);

        Vector4f diffuseColorVec = gltf2Material.getDiffusionColor();
        if (diffuseColorVec != null) {
            diffuseColor = new Color4Texture(new Vector4f(diffuseColorVec.x, diffuseColorVec.y, diffuseColorVec.z, diffuseColorVec.w * opacityConstant));
        } else {
            if (gltf2Material.hasFlag(GLTF2Material.OPACITY)) {
                opacityConstant = gltf2Material.getOpacity();
            }
        }

        Vector3f emissionColorVec = gltf2Material.getEmissionColor();
        if (emissionColorVec != null) {
            emissionColor = new Color3Texture(new Vector3f(emissionColorVec));
        }

        try {
            GLTF2ImageTexture diffuseTexture = gltf2Material.getDiffusionTexture();
            boolean nullColor = (diffuseColor == null);
            if (nullColor) {
                diffuseColor = new Color4Texture(new Vector4f(1.0f));
            }

            if (diffuseTexture != null) {
                diffuseMap = systemResources.createTexture(this.getSource, nullColor ? ResourceManager.DEFAULT_TEXTURE() : null, new JGemsPath(fullPath, diffuseTexture.getUri()), imageProperties);
               // if (computeTransparentPixels != null) {
               //     textureIsImageAndHasAlphaPixels = Material.Transparency.scanForAlphaPixels(computeTransparentPixels, diffuseMap);
               // }
            }

            GLTF2ImageTexture emissionTexture = gltf2Material.getEmissionTexture();
            if (emissionTexture != null) {
                emissionMap = systemResources.createTexture(this.getSource, null, new JGemsPath(fullPath, emissionTexture.getUri()), imageProperties);
                if (emissionColorVec == null) {
                    emissionColor = new Color3Texture(new Vector3f(1.0f));
                }
            }

            GLTF2ImageTexture metallicRoughnessTexture = gltf2Material.getMetallicRoughnessTexture();
            if (metallicRoughnessTexture != null) {
                metallicRoughnessMap = systemResources.createTexture(this.getSource, null, new JGemsPath(fullPath, metallicRoughnessTexture.getUri()), imageProperties);
                metallicFactor = gltf2Material.hasFlag(GLTF2Material.METALLIC_FACTOR) ? gltf2Material.getMetallicFactor() : 0.5f;
            } else {
                metallicFactor = gltf2Material.hasFlag(GLTF2Material.METALLIC_FACTOR) ? gltf2Material.getMetallicFactor() : 0.0f;
            }

            roughnessFactor = gltf2Material.hasFlag(GLTF2Material.ROUGHNESS_FACTOR) ? gltf2Material.getRoughnessFactor() : 0.5f;

            GLTF2ImageTexture normalsTexture = gltf2Material.getNormalTexture();
            if (normalsTexture != null) {
                normalsMap = systemResources.createTexture(this.getSource, null, new JGemsPath(fullPath, normalsTexture.getUri()), imageProperties);
            }
        } catch (JGemsException e) {
            Log.get().exception(e);
        }

        Material material = new Material.Builder()
                .diffuseMap(diffuseMap)
                .diffuseColor(diffuseColor)
                .emissionMap(emissionMap)
                .emissionColor(emissionColor)
                .metallicRoughnessMap(metallicRoughnessMap)
                .normalsMap(normalsMap)
                .metallicFactor(metallicFactor)
                .roughnessFactor(roughnessFactor)
                .build();

        material.getTransparency().setHasTransparentPixels(textureIsImageAndHasAlphaPixels);
        material.getTransparency().setOpacity(opacityConstant);

        return material;
    }

    public JGemsPath getPath() {
        return this.path;
    }

    public SystemResources getSystemResources() {
        return this.systemResources;
    }

    @Override
    public ResourceCache getResourceCache() {
        return this.getSystemResources().getResourceCache();
    }

    public interface DataOnTime<T> {
        float time();
        T data();
        GLTF2Animations.Channel.Path path();
    }

    public static class RotationOnTime implements DataOnTime<Quaternionf> {
        private final Quaternionf rotation;
        private final float time;

        public RotationOnTime(Quaternionf rotation, float time) {
            this.rotation = rotation;
            this.time = time;
        }

        @Override
        public float time() {
            return this.time;
        }

        @Override
        public Quaternionf data() {
            return this.rotation;
        }

        @Override
        public GLTF2Animations.Channel.Path path() {
            return GLTF2Animations.Channel.Path.ROTATION;
        }
    }

    public static class TranslationOnTime implements DataOnTime<Vector3f> {
        private final Vector3f translation;
        private final float time;

        public TranslationOnTime(Vector3f translation, float time) {
            this.translation = translation;
            this.time = time;
        }

        @Override
        public float time() {
            return this.time;
        }

        @Override
        public Vector3f data() {
            return this.translation;
        }

        @Override
        public GLTF2Animations.Channel.Path path() {
            return GLTF2Animations.Channel.Path.TRANSLATE;
        }
    }

    public static class ScaleOnTime implements DataOnTime<Vector3f> {
        private final Vector3f scale;
        private final float time;

        public ScaleOnTime(Vector3f scale, float time) {
            this.scale = scale;
            this.time = time;
        }

        @Override
        public float time() {
            return this.time;
        }

        @Override
        public Vector3f data() {
            return this.scale;
        }

        @Override
        public GLTF2Animations.Channel.Path path() {
            return GLTF2Animations.Channel.Path.SCALE;
        }
    }
}
