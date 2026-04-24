package javagems3d.system.resources.assets.loading.models.gltf.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.*;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Animations;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Skin;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public abstract class GLTF2Parser {
    public static final String DEFAULT_IDENTIFIER = "unknown";

    public static GLTF2RawData parse(@NotNull JGemsPathSource pathToMainFile) {
        try (InputStream jsonInput = JGems3D.getInputStream(pathToMainFile)) {
            JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
            JsonElement root = jsonFileManaging.read(jsonInput);
            return GLTF2Parser.readStructure(pathToMainFile, root);
        } catch (JGemsException e) {
            Log.get().error("Failed to load: " + pathToMainFile);
            throw e;
        } catch (Exception e) {
            Log.get().error("Failed to load: " + pathToMainFile);
            throw new JGemsIOException(e);
        }
    }

    private static GLTF2RawData readStructure(@NotNull JGemsPathSource pathToMainFile, JsonElement root) {
        JsonObject rootObject = root.getAsJsonObject();

        List<ByteBuffer> buffersList = new ArrayList<>();
        JsonArray buffers = rootObject.getAsJsonArray("buffers");
        for (int i = 0; i < buffers.size(); i++) {
            JsonObject bufferObj = buffers.get(i).getAsJsonObject();
            String bufferUri = bufferObj.get("uri").getAsString();
            if (bufferUri.startsWith("data:application")) {
                String base64Data = bufferUri.substring(bufferUri.indexOf(",") + 1);
                byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
                ByteBuffer buffer = MemoryUtil.memAlloc(decodedBytes.length);
                buffer.put(decodedBytes).flip();
                buffersList.add(buffer);
            } else {
                JGemsPath pathToBin = new JGemsPath(pathToMainFile.getPath().getAbsolutePathDirectory(), bufferUri);
                try (InputStream binInput = JGems3D.getInputStream(new JGemsPathSource(pathToBin, pathToMainFile.getSource()))) {
                    ByteBuffer buffer = JGemsHelper.files().toByteBufferSized(binInput, bufferObj.get("byteLength").getAsInt());
                    if (!buffer.isDirect()) {
                        ByteBuffer nativeBuffer = MemoryUtil.memAlloc(buffer.remaining());
                        nativeBuffer.put(buffer).flip();
                        buffer = nativeBuffer;
                    }
                    buffersList.add(buffer);
                } catch (IOException e) {
                    throw new JGemsIOException(e);
                }
            }
        }

        final int defaultScene = rootObject.get("scene").getAsInt();
        JsonArray scenes = rootObject.getAsJsonArray("scenes");
        JsonObject sceneJson = scenes.get(defaultScene).getAsJsonObject();
        JsonArray sceneNodes = sceneJson.getAsJsonArray("nodes");
        JsonArray nodesArray = rootObject.getAsJsonArray("nodes");
        JsonObject asset = rootObject.getAsJsonObject("asset");
        final String sceneName = sceneJson.has("name") ? sceneJson.get("name").getAsString() : GLTF2Parser.DEFAULT_IDENTIFIER;

        final String generator = (asset != null && asset.has("generator")) ? asset.getAsJsonPrimitive("generator").getAsString() : GLTF2Parser.DEFAULT_IDENTIFIER;
        final String version = (asset != null && asset.has("version")) ? asset.getAsJsonPrimitive("version").getAsString() : GLTF2Parser.DEFAULT_IDENTIFIER;

        final List<GLTF2Material> materialList = new ArrayList<>();
        if (rootObject.has("materials")) {
            JsonArray materials = rootObject.getAsJsonArray("materials");

            for (int j = 0; j < materials.size(); j++) {
                JsonArray textures = rootObject.getAsJsonArray("textures");
                JsonArray images = rootObject.getAsJsonArray("images");

                JsonObject material = materials.get(j).getAsJsonObject();
                materialList.add(GLTF2Parser.loadMaterial(material, textures, images));
            }
        }

        Vector3f modelOffsetCenter = new Vector3f();
        final List<GLTF2Node> nodes = GLTF2Parser.readNodes(rootObject, nodesArray, buffersList, modelOffsetCenter);
        List<GLTF2Skin> skins = GLTF2Parser.readSkin(nodes, buffersList, rootObject, modelOffsetCenter);
        List<GLTF2Animations> animations = GLTF2Parser.readAnimations(buffersList, rootObject);

        if ((skins == null && animations != null) || (skins != null && animations == null)) {
            throw new JGemsIOException("Model has invalid animations data");
        }

        GLTF2Scene gltf2Scene = new GLTF2Scene(sceneName, materialList, skins, animations, nodes);
        buffersList.forEach(MemoryUtil::memFree);
        buffersList.clear();

        return new GLTF2RawData(new GLTF2Asset(generator, version), gltf2Scene);
    }

    private static List<GLTF2Node> readNodes(JsonObject rootObject, JsonArray nodesArray, List<ByteBuffer> buffersList, Vector3f modelCenterOffset) {
        final List<GLTF2Node> nodes = new ArrayList<>();
        final Map<Integer, List<Integer>> tempChildrenMap = new HashMap<>();

        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(-Float.MAX_VALUE);
        for (int i = 0; i < nodesArray.size(); i++) {
            JsonObject node = nodesArray.get(i).getAsJsonObject();
            final String nodeName = node.has("name") ? node.get("name").getAsString() : (GLTF2Parser.DEFAULT_IDENTIFIER + "_" + i);
            final int meshIndex = node.has("mesh") ? node.get("mesh").getAsInt() : -1;
            final int skin = node.has("skin") ? node.get("skin").getAsInt() : -1;
            final Matrix4f localTransform = GLTF2Parser.pullMatrixFromNode(node);
            final JsonArray children = node.getAsJsonArray("children");

            GLTF2Mesh gltf2Mesh = meshIndex != -1 ? GLTF2Parser.readMesh(buffersList, rootObject, meshIndex) : null;
            GLTF2Node gltf2Node = new GLTF2Node(nodeName, gltf2Mesh);

            /*
            // NORMALIZE
            {
                List<Float> positions = gltf2Mesh.objects();
                Vector3f min = new Vector3f(Float.MAX_VALUE);
                Vector3f max = new Vector3f(-Float.MAX_VALUE);
                for (int i = 0; i < positions.size(); i += 3) {
                    float x = positions.get(i);
                    float y = positions.get(i + 1);
                    float z = positions.get(i + 2);
                    if (x < min.x) {
                        min.x = x;
                    }
                    if (y < min.y) {
                        min.y = y;
                    }
                    if (z < min.z) {
                        min.z = z;
                    }
                    if (x > max.x) {
                        max.x = x;
                    }
                    if (y > max.y) {
                        max.y = y;
                    }
                    if (z > max.z) {
                        max.z = z;
                    }
                }
                Vector3f center = new Vector3f((min.x + max.x) * 0.5f, (min.y + max.y) * 0.5f, (min.z + max.z) * 0.5f);
                for (int i = 0; i < positions.size(); i += 3) {
                    positions.set(i, positions.get(i) - center.x);
                    positions.set(i + 1, positions.get(i + 1) - center.y);
                    positions.set(i + 2, positions.get(i + 2) - center.z);
                }
            }
             */

            gltf2Node.setLocalTransform(new Matrix4f(localTransform));
            gltf2Node.setSkin(skin);
            nodes.add(gltf2Node);

            if (node.has("children")) {
                for (int j = 0; j < children.size(); j++) {
                    int childIndex = children.get(j).getAsInt();
                    if (!tempChildrenMap.containsKey(i)) {
                        tempChildrenMap.put(i, new ArrayList<>());
                    }
                    tempChildrenMap.get(i).add(childIndex);
                }
            }
        }

        for (Map.Entry<Integer, List<Integer>> listEntry : tempChildrenMap.entrySet()) {
            final int nodeId = listEntry.getKey();
            final List<Integer> children = listEntry.getValue();

            GLTF2Node parent = nodes.get(nodeId);
            for (Integer childId : children) {
                GLTF2Node child = nodes.get(childId);
                parent.getChildren().add(child);
                child.setParent(parent);
            }
        }

        for (GLTF2Node gltf2Node : nodes) {
            GLTF2Mesh gltf2Mesh = gltf2Node.getMesh();
            Matrix4f worldTransform = gltf2Node.computeMeshTransform();

            if (gltf2Mesh != null) {
                for (GLTF2Primitive primitive : gltf2Mesh.getPrimitives()) {
                    List<Float> positions = primitive.getPOSITION().objects();

                    for (int i = 0; i < positions.size(); i += 3) {
                        Vector4f pos = new Vector4f(positions.get(i), positions.get(i + 1), positions.get(i + 2), 1.0f);
                        worldTransform.transform(pos);
                        positions.set(i, pos.x);
                        positions.set(i + 1, pos.y);
                        positions.set(i + 2, pos.z);
                        if (pos.x < min.x) {
                            min.x = pos.x;
                        }
                        if (pos.y < min.y) {
                            min.y = pos.y;
                        }
                        if (pos.z < min.z) {
                            min.z = pos.z;
                        }
                        if (pos.x > max.x) {
                            max.x = pos.x;
                        }
                        if (pos.y > max.y) {
                            max.y = pos.y;
                        }
                        if (pos.z > max.z) {
                            max.z = pos.z;
                        }
                    }

                    Matrix3f normalMatrix = new Matrix3f();
                    worldTransform.normal(normalMatrix);

                    if (primitive.getNORMAL() != null) {
                        List<Float> normals = primitive.getNORMAL().objects();
                        for (int i = 0; i < normals.size(); i += 3) {
                            Vector3f n = new Vector3f(normals.get(i), normals.get(i + 1), normals.get(i + 2));
                            normalMatrix.transform(n);
                            n.normalize();
                            normals.set(i, n.x);
                            normals.set(i + 1, n.y);
                            normals.set(i + 2, n.z);
                        }
                    }

                    if (primitive.getTANGENT() != null) {
                        List<Float> tangents = primitive.getTANGENT().objects();
                        for (int i = 0; i < tangents.size(); i += 3) {
                            Vector3f tangentVec3 = new Vector3f(tangents.get(i), tangents.get(i + 1), tangents.get(i + 2));
                            normalMatrix.transform(tangentVec3);
                            tangentVec3.normalize();
                            tangents.set(i, tangentVec3.x);
                            tangents.set(i + 1, tangentVec3.y);
                            tangents.set(i + 2, tangentVec3.z);
                        }
                    }
                }
            }
        }

        modelCenterOffset.set(new Vector3f((min.x + max.x) * 0.5f, (min.y + max.y) * 0.5f, (min.z + max.z) * 0.5f));

        for (GLTF2Node gltf2Node : nodes) {
            if (gltf2Node.getMesh() != null) {
                for (GLTF2Primitive primitive : gltf2Node.getMesh().getPrimitives()) {
                    List<Float> positions = primitive.getPOSITION().objects();
                    for (int i = 0; i < positions.size(); i += 3) {
                        positions.set(i, positions.get(i) - modelCenterOffset.x);
                        positions.set(i + 1, positions.get(i + 1) - modelCenterOffset.y);
                        positions.set(i + 2, positions.get(i + 2) - modelCenterOffset.z);
                    }
                }
            }
        }

        return nodes;
    }

    private static List<GLTF2Skin> readSkin(List<GLTF2Node> nodes, List<ByteBuffer> buffersList, JsonObject rootObject, Vector3f modelCenterOffset) {
        if (!rootObject.has("skins")) {
            return null;
        }

        JsonArray skinsArray = rootObject.getAsJsonArray("skins");
        if (skinsArray.isEmpty()) {
            return null;
        }

        final List<GLTF2Skin> skinList = new ArrayList<>();
        for (int k = 0; k < skinsArray.size(); k++) {
            JsonObject skinObj = skinsArray.get(k).getAsJsonObject();
            JsonArray jointsArray = skinObj.getAsJsonArray("joints");
            String name = skinObj.has("name") ? skinObj.get("name").getAsString() : (GLTF2Parser.DEFAULT_IDENTIFIER + "_" + k);
            GLTF2Skin gltf2Skin = new GLTF2Skin(name);

            List<Integer> jointIndices = new ArrayList<>();
            for (int i = 0; i < jointsArray.size(); i++) {
                final int joint = jointsArray.get(i).getAsInt();
                jointIndices.add(joint);
                gltf2Skin.getTargetIdJointId().put(joint, i);
                gltf2Skin.getJoints().add(joint);
            }

            int skeletonRootIndex = skinObj.has("skeleton") ? skinObj.get("skeleton").getAsInt() : -1;
            if (skeletonRootIndex >= 0) {
                gltf2Skin.getSkeletonRoots().add(nodes.get(skeletonRootIndex));
            } else {
                for (int jointIndex : jointIndices) {
                    if (nodes.get(jointIndex).getParent() == null) {
                        gltf2Skin.getSkeletonRoots().add(nodes.get(jointIndex));
                    }
                }
            }

            if (gltf2Skin.getSkeletonRoots().isEmpty()) {
                throw new JGemsIOException("Couldn't find root skeleton nodes in the model");
            }

            //final int inverseBindingMatrixId = skinObj.get("inverseBindMatrices").getAsInt();
            //JsonArray accessors = rootObject.getAsJsonArray("accessors");
            //JsonArray bufferViews = rootObject.getAsJsonArray("bufferViews");
            //GLTF2AccessorData AD = GLTF2Parser.readAccessorData(accessors.get(82).getAsJsonObject());
            //GLTF2BufferView BV = GLTF2Parser.readBufferView(bufferViews.get(AD.getBufferView()).getAsJsonObject());
            //GLTF2Accessor<Float> inverseBindingMatrices = GLTF2Parser.readAccessor(BV, AD, buffersList);
            //List<Float> matrixData = inverseBindingMatrices.getObjects();
            //for (int i = 0; i < matrixData.size(); i += 16) {
            //    Matrix4f mat = new Matrix4f(
            //            matrixData.get(i + 0), matrixData.get(i + 4), matrixData.get(i + 8), matrixData.get(i + 12),
            //            matrixData.get(i + 1), matrixData.get(i + 5), matrixData.get(i + 9), matrixData.get(i + 13),
            //            matrixData.get(i + 2), matrixData.get(i + 6), matrixData.get(i + 10), matrixData.get(i + 14),
            //            matrixData.get(i + 3), matrixData.get(i + 7), matrixData.get(i + 11), matrixData.get(i + 15)
            //    );
            //    gltf2Skin.getInverseBindingMatrices().add(mat);
            //}

            for (int jointId : jointIndices) {
                GLTF2Node node = nodes.get(jointId);
                final Matrix4f ibm = node.computeMeshTransform().invert();
                Matrix4f translate = new Matrix4f().translate(modelCenterOffset);
                gltf2Skin.getInverseBindingMatrices().add(ibm.mul(translate));
            }

            skinList.add(gltf2Skin);
        }

        return skinList;
    }

    private static List<GLTF2Animations> readAnimations(List<ByteBuffer> buffersList, JsonObject rootObject) {
        if (!rootObject.has("animations")) {
            return null;
        }

        JsonArray animations = rootObject.getAsJsonArray("animations");
        if (animations.isEmpty()) {
            return null;
        }

        JsonArray accessors = rootObject.getAsJsonArray("accessors");
        JsonArray bufferViews = rootObject.getAsJsonArray("bufferViews");
        final List<GLTF2Animations> animationsList = new ArrayList<>();

        for (int i = 0; i < animations.size(); i++) {
            JsonObject animation = animations.get(i).getAsJsonObject();
            final String name = animation.has("name") ? animation.get("name").getAsString() : (GLTF2Parser.DEFAULT_IDENTIFIER + "_" + i);

            JsonArray channels = animation.getAsJsonArray("channels");
            JsonArray samplers = animation.getAsJsonArray("samplers");

            final List<GLTF2Animations.Sampler> samplerList = new ArrayList<>();
            final List<GLTF2Animations.Channel> channelList = new ArrayList<>();
            for (int j = 0; j < samplers.size(); j++) {
                JsonObject sampler = samplers.get(j).getAsJsonObject();
                final int inputId = sampler.get("input").getAsInt();
                final int outputId = sampler.get("output").getAsInt();

                GLTF2AccessorData inputAD = GLTF2Parser.readAccessorData(accessors.get(inputId).getAsJsonObject());
                GLTF2BufferView inputBV = GLTF2Parser.readBufferView(bufferViews.get(inputAD.bufferView()).getAsJsonObject());
                GLTF2AccessorData outputAD = GLTF2Parser.readAccessorData(accessors.get(outputId).getAsJsonObject());
                GLTF2BufferView outputBV = GLTF2Parser.readBufferView(bufferViews.get(outputAD.bufferView()).getAsJsonObject());

                GLTF2Accessor<Float> timeStamps = GLTF2Parser.readAccessor(inputBV, inputAD, buffersList);
                GLTF2Accessor<Float> dataOnTime = GLTF2Parser.readAccessor(outputBV, outputAD, buffersList);
                GLTF2Animations.Sampler.Interpolation interpolation = GLTF2Animations.Sampler.Interpolation.choose(sampler.get("interpolation").getAsString());

                samplerList.add(new GLTF2Animations.Sampler(timeStamps, dataOnTime, interpolation));
            }

            for (int j = 0; j < channels.size(); j++) {
                JsonObject channel = channels.get(j).getAsJsonObject();
                JsonObject target = channel.getAsJsonObject("target");
                final int samplerId = channel.get("sampler").getAsInt();

                GLTF2Animations.Sampler sampler = samplerList.get(samplerId);
                final int node = target.get("node").getAsInt();
                final GLTF2Animations.Channel.Path path = GLTF2Animations.Channel.Path.choose(target.get("path").getAsString());

                channelList.add(new GLTF2Animations.Channel(sampler, node, path));
            }

            animationsList.add(new GLTF2Animations(name, channelList, samplerList));
        }

        return animationsList;
    }

    private static GLTF2Mesh readMesh(List<ByteBuffer> buffersList, JsonObject rootObject, int meshIndex) {
        JsonArray meshes = rootObject.getAsJsonArray("meshes");
        JsonObject mesh = meshes.get(meshIndex).getAsJsonObject();

        final String meshName = mesh.has("name") ? mesh.get("name").getAsString() : GLTF2Parser.DEFAULT_IDENTIFIER;
        JsonArray primitives = mesh.getAsJsonArray("primitives");
        GLTF2Mesh gltf2Mesh = new GLTF2Mesh(meshName);
        for (int j = 0; j < primitives.size(); j++) {
            JsonObject primitive = primitives.get(j).getAsJsonObject();
            JsonObject primitiveAttributes = primitive.getAsJsonObject("attributes");

            if (!primitiveAttributes.has("POSITION")) {
                throw new JGemsIOException("Couldn't find attribute: POSITION");
            }

            if (!primitive.has("indices")) {
                throw new JGemsIOException("Couldn't find attribute: indices");
            }

            final boolean hasNormals = primitiveAttributes.has("NORMAL");
            if (!hasNormals) {
                Log.get().warn("Couldn't find attribute: NORMAL");
            }

            int POSITION_ACCESSOR_ID = primitiveAttributes.get("POSITION").getAsInt();
            int NORMAL_ACCESSOR_ID = hasNormals ? primitiveAttributes.get("NORMAL").getAsInt() : -1;
            int indices_ACCESSOR_ID = primitive.get("indices").getAsInt();

            int TEXCOORD_0_ACCESSOR_ID = primitiveAttributes.has("TEXCOORD_0") ? primitiveAttributes.get("TEXCOORD_0").getAsInt() : -1;
            int TANGENT_ACCESSOR_ID = primitiveAttributes.has("TANGENT") ? primitiveAttributes.get("TANGENT").getAsInt() : -1;
            int JOINTS_0_ACCESSOR_ID = primitiveAttributes.has("JOINTS_0") ? primitiveAttributes.get("JOINTS_0").getAsInt() : -1;
            int WEIGHTS_0_ACCESSOR_ID = primitiveAttributes.has("WEIGHTS_0") ? primitiveAttributes.get("WEIGHTS_0").getAsInt() : -1;

            @NotNull GLTF2Accessor<Float> POSITION;
            @NotNull GLTF2Accessor<Float> NORMAL;
            @NotNull GLTF2Accessor<Integer> indices;
            @Nullable GLTF2Accessor<Float> TEXCOORD_0 = null;
            @Nullable GLTF2Accessor<Float> TANGENT = null;
            @Nullable GLTF2Accessor<Float> BiTANGENT = null;
            @Nullable GLTF2Accessor<Integer> JOINTS_0 = null;
            @Nullable GLTF2Accessor<Float> WEIGHTS_0 = null;

            // "accessors" & "bufferViews"
            {
                JsonArray accessors = rootObject.getAsJsonArray("accessors");
                JsonArray bufferViews = rootObject.getAsJsonArray("bufferViews");

                @NotNull JsonObject POSITION_ACC_DATA_JS = accessors.get(POSITION_ACCESSOR_ID).getAsJsonObject();
                @NotNull JsonObject indices_ACC_DATA_JS = accessors.get(indices_ACCESSOR_ID).getAsJsonObject();
                @NotNull GLTF2AccessorData POSITION_ACC_DATA = GLTF2Parser.readAccessorData(POSITION_ACC_DATA_JS);
                @NotNull GLTF2AccessorData indices_ACC_DATA = GLTF2Parser.readAccessorData(indices_ACC_DATA_JS);

                @Nullable GLTF2AccessorData NORMAL_ACC_DATA = null;
                @Nullable GLTF2AccessorData TEXCOORD_0_ACC_DATA = null;
                @Nullable GLTF2AccessorData TANGENT_ACC_DATA = null;
                @Nullable GLTF2AccessorData JOINTS_0_ACC_DATA = null;
                @Nullable GLTF2AccessorData WEIGHTS_0_ACC_DATA = null;

                @NotNull GLTF2BufferView POSITION_BV = GLTF2Parser.readBufferView(bufferViews.get(POSITION_ACC_DATA.bufferView()).getAsJsonObject());
                @NotNull GLTF2BufferView indices_BV = GLTF2Parser.readBufferView(bufferViews.get(indices_ACC_DATA.bufferView()).getAsJsonObject());
                @Nullable GLTF2BufferView NORMAL_BV = null;
                @Nullable GLTF2BufferView TEXCOORD_0_BV = null;
                @Nullable GLTF2BufferView TANGENT_BV = null;
                @Nullable GLTF2BufferView JOINTS_0_BV = null;
                @Nullable GLTF2BufferView WEIGHTS_0_BV = null;

                POSITION = GLTF2Parser.readAccessor(POSITION_BV, POSITION_ACC_DATA, buffersList);
                indices = GLTF2Parser.readAccessor(indices_BV, indices_ACC_DATA, buffersList);

                if (NORMAL_ACCESSOR_ID >= 0) {
                    JsonObject NORMAL_ACC_DATA_JS = accessors.get(NORMAL_ACCESSOR_ID).getAsJsonObject();
                    NORMAL_ACC_DATA = GLTF2Parser.readAccessorData(NORMAL_ACC_DATA_JS);
                    NORMAL_BV = GLTF2Parser.readBufferView(bufferViews.get(NORMAL_ACC_DATA.bufferView()).getAsJsonObject());
                    NORMAL = GLTF2Parser.readAccessor(NORMAL_BV, NORMAL_ACC_DATA, buffersList);
                } else {
                    NORMAL = new GLTF2Accessor<>(GLTF2Parser.calculateNormals(POSITION.objects(), indices.objects()), null);
                }

                if (TEXCOORD_0_ACCESSOR_ID >= 0) {
                    JsonObject TEXCOORD_0_ACC_DATA_JS = accessors.get(TEXCOORD_0_ACCESSOR_ID).getAsJsonObject();
                    TEXCOORD_0_ACC_DATA = GLTF2Parser.readAccessorData(TEXCOORD_0_ACC_DATA_JS);
                    TEXCOORD_0_BV = GLTF2Parser.readBufferView(bufferViews.get(TEXCOORD_0_ACC_DATA.bufferView()).getAsJsonObject());
                    TEXCOORD_0 = GLTF2Parser.readAccessor(TEXCOORD_0_BV, TEXCOORD_0_ACC_DATA, buffersList);
                }

                if (TANGENT_ACCESSOR_ID >= 0) {
                    JsonObject TANGENT_ACC_DATA_JS = accessors.get(TANGENT_ACCESSOR_ID).getAsJsonObject();
                    TANGENT_ACC_DATA = GLTF2Parser.readAccessorData(TANGENT_ACC_DATA_JS);
                    TANGENT_BV = GLTF2Parser.readBufferView(bufferViews.get(TANGENT_ACC_DATA.bufferView()).getAsJsonObject());
                    TANGENT = GLTF2Parser.readAccessor(TANGENT_BV, TANGENT_ACC_DATA, buffersList);
                    BiTANGENT = new GLTF2Accessor<>(GLTF2Parser.calculateBiTangents(TANGENT.objects(), NORMAL.objects()), null);
                } else {
                    if (TEXCOORD_0 != null) {
                        Pair<List<Float>, List<Float>> tangents = GLTF2Parser.calculateTangentsAndBiTangents(indices.objects(), POSITION.objects(), TEXCOORD_0.objects(), POSITION.objects().size() / 3);
                        TANGENT = new GLTF2Accessor<>(tangents.first(), null);
                        BiTANGENT = new GLTF2Accessor<>(tangents.second(), null);
                    } else {
                        Log.get().warn("The model doesn't have UV to calculate tangents and biTangents");
                        List<Float> uv = new ArrayList<>();
                        List<Float> bi_tangents = new ArrayList<>();

                        for (int i = 0; i < POSITION.objects().size() / 3; i++) {
                            bi_tangents.add(0.0f);
                            bi_tangents.add(0.0f);
                            bi_tangents.add(0.0f);
                            uv.add(0.0f);
                            uv.add(0.0f);
                        }

                        TANGENT = new GLTF2Accessor<>(bi_tangents, null);
                        BiTANGENT = new GLTF2Accessor<>(bi_tangents, null);
                        TEXCOORD_0 = new GLTF2Accessor<>(uv, null);
                    }
                }

                if (JOINTS_0_ACCESSOR_ID >= 0) {
                    JsonObject JOINTS_0_ACC_DATA_JS = accessors.get(JOINTS_0_ACCESSOR_ID).getAsJsonObject();
                    JOINTS_0_ACC_DATA = GLTF2Parser.readAccessorData(JOINTS_0_ACC_DATA_JS);
                    JOINTS_0_BV = GLTF2Parser.readBufferView(bufferViews.get(JOINTS_0_ACC_DATA.bufferView()).getAsJsonObject());
                    JOINTS_0 = GLTF2Parser.readAccessor(JOINTS_0_BV, JOINTS_0_ACC_DATA, buffersList);
                }

                if (WEIGHTS_0_ACCESSOR_ID >= 0) {
                    JsonObject WEIGHTS_0_ACC_DATA_JS = accessors.get(WEIGHTS_0_ACCESSOR_ID).getAsJsonObject();
                    WEIGHTS_0_ACC_DATA = GLTF2Parser.readAccessorData(WEIGHTS_0_ACC_DATA_JS);
                    WEIGHTS_0_BV = GLTF2Parser.readBufferView(bufferViews.get(WEIGHTS_0_ACC_DATA.bufferView()).getAsJsonObject());
                    WEIGHTS_0 = GLTF2Parser.readAccessor(WEIGHTS_0_BV, WEIGHTS_0_ACC_DATA, buffersList);
                }
            }

            final int primitiveMaterialId = primitive.has("material") ? primitive.getAsJsonPrimitive("material").getAsInt() : -1;
            GLTF2Primitive gltf2Primitive = new GLTF2Primitive(POSITION, NORMAL, TEXCOORD_0, TANGENT, BiTANGENT, JOINTS_0, WEIGHTS_0, primitiveMaterialId, indices);
            gltf2Mesh.getPrimitives().add(gltf2Primitive);
        }

        return gltf2Mesh;
    }

    private static GLTF2ImageTexture getTextureFromIndex(int texIndex, JsonArray textures, JsonArray images) {
        JsonObject textureObj = textures.get(texIndex).getAsJsonObject();
        int sourceIndex = textureObj.get("source").getAsInt();
        JsonObject imageObj = images.get(sourceIndex).getAsJsonObject();

        String name = imageObj.has("name") ? imageObj.get("name").getAsString() : "unnamed";
        String uri = imageObj.get("uri").getAsString();

        return new GLTF2ImageTexture(name, uri);
    }

    public static GLTF2Material loadMaterial(JsonObject materialJson, JsonArray textures, JsonArray images) {
        String name = materialJson.has("name") ? materialJson.get("name").getAsString() : GLTF2Parser.DEFAULT_IDENTIFIER;
        GLTF2Material material = new GLTF2Material(name);

        JsonObject pbr = materialJson.has("pbrMetallicRoughness") ? materialJson.getAsJsonObject("pbrMetallicRoughness") : null;
        if (pbr != null) {
            if (pbr.has("baseColorFactor")) {
                JsonArray arr = pbr.getAsJsonArray("baseColorFactor");
                if (arr.size() >= 4) {
                    Vector4f baseColor = new Vector4f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat(), arr.get(3).getAsFloat());
                    material.setDiffusionColor(baseColor);
                }
            }

            if (pbr.has("baseColorTexture")) {
                int index = pbr.getAsJsonObject("baseColorTexture").get("index").getAsInt();
                GLTF2ImageTexture tex = GLTF2Parser.getTextureFromIndex(index, textures, images);
                material.setDiffusionTextureIndex(tex);
            }

            if (pbr.has("metallicFactor")) {
                material.setMetallicFactor(pbr.get("metallicFactor").getAsFloat());
            }

            if (pbr.has("roughnessFactor")) {
                material.setRoughnessFactor(pbr.get("roughnessFactor").getAsFloat());
            }

            if (pbr.has("metallicRoughnessTexture")) {
                int index = pbr.getAsJsonObject("metallicRoughnessTexture").get("index").getAsInt();
                GLTF2ImageTexture tex = GLTF2Parser.getTextureFromIndex(index, textures, images);
                material.setMetallicRoughnessTexture(tex);
            }
        }

        if (materialJson.has("normalTexture")) {
            int index = materialJson.getAsJsonObject("normalTexture").get("index").getAsInt();
            GLTF2ImageTexture tex = GLTF2Parser.getTextureFromIndex(index, textures, images);
            material.setNormalTextureIndex(tex);
        }

        if (materialJson.has("emissiveFactor")) {
            JsonArray arr = materialJson.getAsJsonArray("emissiveFactor");
            if (arr.size() >= 3) {
                Vector3f emissiveColor = new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
                material.setEmissionColor(emissiveColor);
            }
        }

        if (materialJson.has("emissiveTexture")) {
            int index = materialJson.getAsJsonObject("emissiveTexture").get("index").getAsInt();
            GLTF2ImageTexture tex = GLTF2Parser.getTextureFromIndex(index, textures, images);
            material.setEmissionTexture(tex);
        }

        if (pbr != null && pbr.has("baseColorFactor") && pbr.getAsJsonArray("baseColorFactor").size() >= 4) {
            float opacity = pbr.getAsJsonArray("baseColorFactor").get(3).getAsFloat();
            material.setOpacity(opacity);
        }

        return material;
    }

    private static GLTF2AccessorData readAccessorData(JsonObject jsonObject) {
        final int bufferView = jsonObject.get("bufferView").getAsInt();
        final int componentType = jsonObject.get("componentType").getAsInt();
        final int count = jsonObject.get("count").getAsInt();
        final String typeStr = jsonObject.get("type").getAsString();
        final int byteOffset = jsonObject.has("byteOffset") ? jsonObject.get("byteOffset").getAsInt() : 0;

        return new GLTF2AccessorData(bufferView, count, byteOffset, componentType, typeStr);
    }

    private static GLTF2BufferView readBufferView(JsonObject jsonObject) {
        final int bufferId = jsonObject.get("buffer").getAsInt();
        final int byteLength = jsonObject.get("byteLength").getAsInt();
        final int byteOffset = jsonObject.has("byteOffset") ? jsonObject.get("byteOffset").getAsInt() : 0;
        final int byteStride = jsonObject.has("byteStride") ? jsonObject.get("byteStride").getAsInt() : 0;
        final int target = jsonObject.has("target") ? jsonObject.get("target").getAsInt() : -1;

        return new GLTF2BufferView(bufferId, byteLength, byteOffset, byteStride, target);
    }

    @SuppressWarnings("unchecked")
    private static <T> GLTF2Accessor<T> readAccessor(GLTF2BufferView gltf2BufferView, GLTF2AccessorData gltf2AccessorData, List<ByteBuffer> bufferList) {
        final int bytesOfType = GLTF2Parser.getBytesOfType(gltf2AccessorData.componentType());
        final int typeSize = GLTF2Accessor.ValueType.getTypeSize(gltf2AccessorData.typeStr());
        final int elementByteSize = typeSize * bytesOfType;
        ByteBuffer buffer = bufferList.get(gltf2BufferView.id());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int stride = gltf2BufferView.byteStride() == 0 ? elementByteSize : gltf2BufferView.byteStride();

        final List<T> readObjects = new ArrayList<>(gltf2AccessorData.count() * typeSize);

        for (int k = 0; k < gltf2AccessorData.count(); k++) {
            int basePosition = gltf2BufferView.byteOffset() + gltf2AccessorData.byteOffset() + k * stride;
            buffer.position(basePosition);
            for (int i = 0; i < typeSize; i++) {
                T component = (T) GLTF2Parser.readComponent(buffer, gltf2AccessorData.componentType());
                readObjects.add(component);
            }
        }

        return new GLTF2Accessor<>(readObjects, gltf2AccessorData);
    }


    private static Matrix4f pullMatrixFromNode(JsonObject node) {
        Matrix4f localTransform = new Matrix4f().identity();

        if (node.has("matrix")) {
            JsonArray matrix = node.getAsJsonArray("matrix");
            localTransform.set(
                    matrix.get(0).getAsFloat(), matrix.get(4).getAsFloat(), matrix.get(8).getAsFloat(), matrix.get(12).getAsFloat(),
                    matrix.get(1).getAsFloat(), matrix.get(5).getAsFloat(), matrix.get(9).getAsFloat(), matrix.get(13).getAsFloat(),
                    matrix.get(2).getAsFloat(), matrix.get(6).getAsFloat(), matrix.get(10).getAsFloat(), matrix.get(14).getAsFloat(),
                    matrix.get(3).getAsFloat(), matrix.get(7).getAsFloat(), matrix.get(11).getAsFloat(), matrix.get(15).getAsFloat()
            );
        } else {
            Matrix4f s = new Matrix4f().identity();
            Matrix4f r = new Matrix4f().identity();
            Matrix4f t = new Matrix4f().identity();

            if (node.has("scale")) {
                JsonArray scale = node.getAsJsonArray("scale");
                s.scale(scale.get(0).getAsFloat(), scale.get(1).getAsFloat(), scale.get(2).getAsFloat());
            }

            if (node.has("rotation")) {
                JsonArray rotation = node.getAsJsonArray("rotation");
                Quaternionf q = new Quaternionf(rotation.get(0).getAsFloat(), rotation.get(1).getAsFloat(), rotation.get(2).getAsFloat(), rotation.get(3).getAsFloat());
                r.rotate(q);
            }

            if (node.has("translation")) {
                JsonArray translation = node.getAsJsonArray("translation");
                t.translate(translation.get(0).getAsFloat(), translation.get(1).getAsFloat(), translation.get(2).getAsFloat());
            }

            localTransform = new Matrix4f(t).mul(r).mul(s);
        }

        return localTransform;
    }

    private static List<Float> calculateNormals(List<Float> positions, List<Integer> indices) {
        int vertexCount = positions.size() / 3;
        float[] normals = new float[positions.size()];

        for (int i = 0; i < indices.size(); i += 3) {
            int i0 = indices.get(i);
            int i1 = indices.get(i + 1);
            int i2 = indices.get(i + 2);

            float x0 = positions.get(i0 * 3), y0 = positions.get(i0 * 3 + 1), z0 = positions.get(i0 * 3 + 2);
            float x1 = positions.get(i1 * 3), y1 = positions.get(i1 * 3 + 1), z1 = positions.get(i1 * 3 + 2);
            float x2 = positions.get(i2 * 3), y2 = positions.get(i2 * 3 + 1), z2 = positions.get(i2 * 3 + 2);

            float ex1 = x1 - x0, ey1 = y1 - y0, ez1 = z1 - z0;
            float ex2 = x2 - x0, ey2 = y2 - y0, ez2 = z2 - z0;

            float nx = ey1 * ez2 - ez1 * ey2;
            float ny = ez1 * ex2 - ex1 * ez2;
            float nz = ex1 * ey2 - ey1 * ex2;

            normals[i0 * 3] += nx;
            normals[i0 * 3 + 1] += ny;
            normals[i0 * 3 + 2] += nz;

            normals[i1 * 3] += nx;
            normals[i1 * 3 + 1] += ny;
            normals[i1 * 3 + 2] += nz;

            normals[i2 * 3] += nx;
            normals[i2 * 3 + 1] += ny;
            normals[i2 * 3 + 2] += nz;
        }

        List<Float> result = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            float nx = normals[i * 3];
            float ny = normals[i * 3 + 1];
            float nz = normals[i * 3 + 2];
            float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (length == 0) {
                length = 1.0f;
            }
            result.add(nx / length);
            result.add(ny / length);
            result.add(nz / length);
        }

        return result;
    }

    private static Pair<List<Float>, List<Float>> calculateTangentsAndBiTangents(List<Integer> indexes, List<Float> positions, List<Float> uv, int totalVertices) {
        List<Float> tangents = new ArrayList<>(totalVertices * 3);
        List<Float> biTangents = new ArrayList<>(totalVertices * 3);

        Vector3f[] tan1 = new Vector3f[totalVertices];
        Vector3f[] tan2 = new Vector3f[totalVertices];

        for (int i = 0; i < totalVertices; i++) {
            tan1[i] = new Vector3f();
            tan2[i] = new Vector3f();
        }

        for (int i = 0; i < indexes.size(); i += 3) {
            int i0 = indexes.get(i);
            int i1 = indexes.get(i + 1);
            int i2 = indexes.get(i + 2);

            Vector3f v0 = new Vector3f(positions.get(i0 * 3), positions.get(i0 * 3 + 1), positions.get(i0 * 3 + 2));
            Vector3f v1 = new Vector3f(positions.get(i1 * 3), positions.get(i1 * 3 + 1), positions.get(i1 * 3 + 2));
            Vector3f v2 = new Vector3f(positions.get(i2 * 3), positions.get(i2 * 3 + 1), positions.get(i2 * 3 + 2));

            Vector2f uv0 = new Vector2f(uv.get(i0 * 2), uv.get(i0 * 2 + 1));
            Vector2f uv1 = new Vector2f(uv.get(i1 * 2), uv.get(i1 * 2 + 1));
            Vector2f uv2 = new Vector2f(uv.get(i2 * 2), uv.get(i2 * 2 + 1));

            Vector3f deltaPos1 = v1.sub(v0, new Vector3f());
            Vector3f deltaPos2 = v2.sub(v0, new Vector3f());

            float deltaUV1x = uv1.x - uv0.x;
            float deltaUV1y = uv1.y - uv0.y;
            float deltaUV2x = uv2.x - uv0.x;
            float deltaUV2y = uv2.y - uv0.y;

            float r = 1.0f / (deltaUV1x * deltaUV2y - deltaUV1y * deltaUV2x);

            Vector3f tangent = new Vector3f((deltaPos1.x * deltaUV2y - deltaPos2.x * deltaUV1y) * r, (deltaPos1.y * deltaUV2y - deltaPos2.y * deltaUV1y) * r, (deltaPos1.z * deltaUV2y - deltaPos2.z * deltaUV1y) * r);
            Vector3f biTangent = new Vector3f((deltaPos2.x * deltaUV1x - deltaPos1.x * deltaUV2x) * r, (deltaPos2.y * deltaUV1x - deltaPos1.y * deltaUV2x) * r, (deltaPos2.z * deltaUV1x - deltaPos1.z * deltaUV2x) * r);

            tan1[i0].add(tangent);
            tan1[i1].add(tangent);
            tan1[i2].add(tangent);

            tan2[i0].add(biTangent);
            tan2[i1].add(biTangent);
            tan2[i2].add(biTangent);
        }

        for (int i = 0; i < totalVertices; i++) {
            Vector3f t = tan1[i];
            t.normalize();

            Vector3f b = tan2[i];
            b.normalize();

            tangents.add(t.x);
            tangents.add(t.y);
            tangents.add(t.z);
            biTangents.add(b.x);
            biTangents.add(b.y);
            biTangents.add(b.z);
        }

        return new Pair<>(tangents, biTangents);
    }

    private static List<Float> calculateBiTangents(List<Float> tangents, List<Float> normals) {
        if (tangents.size() % 4 != 0 || normals.size() % 3 != 0 || tangents.size() / 4 != normals.size() / 3) {
            throw new JGemsIOException("Tangent and normal arrays have incompatible sizes.");
        }

        List<Float> newTangetsList = new ArrayList<>();
        List<Float> biTangents = new ArrayList<>(normals.size());

        for (int i = 0; i < normals.size() / 3; i++) {
            float nx = normals.get(i * 3);
            float ny = normals.get(i * 3 + 1);
            float nz = normals.get(i * 3 + 2);

            float tx = tangents.get(i * 4);
            float ty = tangents.get(i * 4 + 1);
            float tz = tangents.get(i * 4 + 2);
            float tw = tangents.get(i * 4 + 3);

            newTangetsList.add(tx);
            newTangetsList.add(ty);
            newTangetsList.add(tz);

            float bx = ny * tz - nz * ty;
            float by = nz * tx - nx * tz;
            float bz = nx * ty - ny * tx;

            bx *= tw;
            by *= tw;
            bz *= tw;

            biTangents.add(bx);
            biTangents.add(by);
            biTangents.add(bz);
        }

        tangents.clear();
        tangents.addAll(newTangetsList);

        return biTangents;
    }

    private static Object readComponent(ByteBuffer buffer, int componentType) {
        switch (componentType) {
            case 5120: return buffer.get(); // BYTE
            case 5121: return buffer.get() & 0xFF; // UNSIGNED_BYTE
            case 5122: return buffer.getShort(); // SHORT
            case 5123: return buffer.getShort() & 0xFFFF; // UNSIGNED_SHORT
            case 5125: return buffer.getInt(); // UNSIGNED_INT
            case 5126: return buffer.getFloat(); // FLOAT
            default: throw new JGemsIOException("Unsupported component type: " + componentType);
        }
    }

    private static int getBytesOfType(int type) {
        switch (type) {
            case 5121:  // UNSIGNED_BYTE
            case 5120: {  // BYTE
                return 1;
            }
            case 5122:  // SHORT
            case 5123: { // UNSIGNED_SHORT
                return 2;
            }
            case 5125: // UNSIGNED_INT
            case 5126: { // FLOAT
                return 4;
            }
            default: {
                throw new JGemsIOException("Couldn't find type: " + type);
            }
        }
    }
}
