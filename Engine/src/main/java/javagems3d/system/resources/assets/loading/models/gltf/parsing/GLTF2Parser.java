package javagems3d.system.resources.assets.loading.models.gltf.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.*;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.JGemsLogging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public abstract class GLTF2Parser {
    public static GLTF2RawData parse(JGemsPath pathToMainFile) {
        try (InputStream jsonInput = JGems3D.loadFileFromJar(pathToMainFile)) {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            JsonElement root = jsonFileManaging.read(jsonInput);
            return GLTF2Parser.readStructure(pathToMainFile, root);
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private static GLTF2RawData readStructure(JGemsPath pathToMainFile, JsonElement root) {
        JsonObject rootObject = root.getAsJsonObject();

        List<ByteBuffer> buffersList = new ArrayList<>();
        JsonArray buffers = rootObject.getAsJsonArray("buffers");
        for (int i = 0; i < buffers.size(); i++) {
            JsonObject bufferObj = buffers.get(i).getAsJsonObject();
            String bufferUri = bufferObj.get("uri").getAsString();
            JGemsPath pathToBin = new JGemsPath(pathToMainFile.getDirectory(), bufferUri);
            try (InputStream binInput = JGems3D.loadFileFromJar(pathToBin)) {
                buffersList.add(JGemsHelper.files().toByteBufferSized(binInput, bufferObj.get("byteLength").getAsInt()));
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
        }

        final int defaultScene = rootObject.get("scene").getAsInt();
        JsonArray scenes = rootObject.getAsJsonArray("scenes");
        JsonObject sceneJson = scenes.get(defaultScene).getAsJsonObject();
        JsonArray sceneNodes = sceneJson.getAsJsonArray("nodes");
        JsonArray nodesArray = rootObject.getAsJsonArray("nodes");
        JsonObject asset = rootObject.getAsJsonObject("asset");

        final String sceneName = sceneJson.get("name").getAsString();
        final String generator = asset.getAsJsonPrimitive("generator").getAsString();
        final String version = asset.getAsJsonPrimitive("version").getAsString();

        List<GLTF2Node> nodeList = new ArrayList<>();
        List<GLTF2Material> materialList = new ArrayList<>();

        for (int i = 0; i < sceneNodes.size(); i++) {
            int nodeIndex = sceneNodes.get(i).getAsInt();
            GLTF2Parser.readNodeRecursive(pathToMainFile, nodeList, rootObject, nodesArray, nodeIndex, new Matrix4f().identity(), buffersList);
        }

        if (rootObject.has("materials")) {
            JsonArray materials = rootObject.getAsJsonArray("materials");

            for (int j = 0; j < materials.size(); j++) {
                JsonArray textures = rootObject.getAsJsonArray("textures");
                JsonArray images = rootObject.getAsJsonArray("images");

                JsonObject material = materials.get(j).getAsJsonObject();
                materialList.add(GLTF2Parser.loadMaterial(material, textures, images));
            }
        }

        GLTF2Scene gltf2Scene = new GLTF2Scene(sceneName, nodeList, materialList);
        buffersList.forEach(MemoryUtil::memFree);
        buffersList.clear();

        return new GLTF2RawData(new GLTF2Asset(generator, version), gltf2Scene);
    }

    private static void readNodeRecursive(JGemsPath pathToMainFile, List<GLTF2Node> list, JsonObject rootObject, JsonArray nodesArray, int nodeIndex, Matrix4f parentTransform, List<ByteBuffer> buffersList) {
        JsonObject node = nodesArray.get(nodeIndex).getAsJsonObject();
        String nodeName = node.has("name") ? node.get("name").getAsString() : "unnamed";

        Matrix4f localTransform = new Matrix4f().identity();
        if (node.has("scale")) {
            JsonArray scale = node.getAsJsonArray("scale");
            localTransform.scale(scale.get(0).getAsFloat(), scale.get(1).getAsFloat(), scale.get(2).getAsFloat());
        }

        if (node.has("rotation")) {
            JsonArray rotation = node.getAsJsonArray("rotation");
            Quaternionf q = new Quaternionf(rotation.get(0).getAsFloat(), rotation.get(1).getAsFloat(), rotation.get(2).getAsFloat(), rotation.get(3).getAsFloat());
            localTransform.rotate(q);
        }

        if (node.has("translation")) {
            JsonArray translation = node.getAsJsonArray("translation");
            localTransform.translate(translation.get(0).getAsFloat(), translation.get(1).getAsFloat(), translation.get(2).getAsFloat());
        }

        Matrix4f worldTransform = new Matrix4f(parentTransform).mul(localTransform);

        int meshIndex = node.has("mesh") ? node.get("mesh").getAsInt() : -1;
        GLTF2Mesh gltf2Mesh = meshIndex != -1 ? GLTF2Parser.readMesh(pathToMainFile, buffersList, rootObject, meshIndex) : null;
        GLTF2Node gltf2Node = new GLTF2Node(nodeName, gltf2Mesh);

        if (gltf2Mesh != null) {
            for (GLTF2Primitive primitive : gltf2Mesh.getPrimitives()) {
                List<Float> positions = primitive.getPOSITION().getObjects();

                for (int i = 0; i < positions.size(); i += 3) {
                    Vector4f pos = new Vector4f(positions.get(i), positions.get(i + 1), positions.get(i + 2), 1.0f);
                    worldTransform.transform(pos);
                    positions.set(i, pos.x);
                    positions.set(i + 1, pos.y);
                    positions.set(i + 2, pos.z);
                }

                if (primitive.getNORMAL() != null) {
                    List<Float> normals = primitive.getNORMAL().getObjects();
                    Matrix3f normalMatrix = new Matrix3f();
                    worldTransform.normal(normalMatrix);

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
                    List<Float> tangents = primitive.getTANGENT().getObjects();
                    Matrix3f normalMatrix = new Matrix3f();
                    worldTransform.normal(normalMatrix);

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

        if (node.has("children")) {
            JsonArray children = node.getAsJsonArray("children");
            for (int i = 0; i < children.size(); i++) {
                int childIndex = children.get(i).getAsInt();
                GLTF2Parser.readNodeRecursive(pathToMainFile, list, rootObject, nodesArray, childIndex, worldTransform, buffersList);
            }
        }

        list.add(gltf2Node);
    }


    private static GLTF2Mesh readMesh(JGemsPath pathToMainFile, List<ByteBuffer> buffersList, JsonObject rootObject, int meshIndex) {
        JsonArray meshes = rootObject.getAsJsonArray("meshes");
        JsonObject mesh = meshes.get(meshIndex).getAsJsonObject();

        final String meshName = mesh.get("name").getAsString();
        JsonArray primitives = mesh.getAsJsonArray("primitives");
        GLTF2Mesh gltf2Mesh = new GLTF2Mesh(meshName);
        for (int j = 0; j < primitives.size(); j++) {
            JsonObject primitive = primitives.get(j).getAsJsonObject();
            JsonObject primitiveAttributes = primitive.getAsJsonObject("attributes");

            if (!primitiveAttributes.has("POSITION")) {
                throw new JGemsIOException("Couldn't find attribute: POSITION");
            }

            if (!primitiveAttributes.has("NORMAL")) {
                throw new JGemsIOException("Couldn't find attribute: NORMAL");
            }

            if (!primitive.has("indices")) {
                throw new JGemsIOException("Couldn't find attribute: indices");
            }

            int POSITION_ACCESSOR_ID = primitiveAttributes.get("POSITION").getAsInt();
            int NORMAL_ACCESSOR_ID = primitiveAttributes.get("NORMAL").getAsInt();
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
                @NotNull JsonObject NORMAL_ACC_DATA_JS = accessors.get(NORMAL_ACCESSOR_ID).getAsJsonObject();
                @NotNull JsonObject indices_ACC_DATA_JS = accessors.get(indices_ACCESSOR_ID).getAsJsonObject();

                @NotNull GLTF2AccessorData POSITION_ACC_DATA = GLTF2Parser.readAccessorData(POSITION_ACC_DATA_JS);
                @NotNull GLTF2AccessorData NORMAL_ACC_DATA = GLTF2Parser.readAccessorData(NORMAL_ACC_DATA_JS);
                @NotNull GLTF2AccessorData indices_ACC_DATA = GLTF2Parser.readAccessorData(indices_ACC_DATA_JS);
                @Nullable GLTF2AccessorData TEXCOORD_0_ACC_DATA = null;
                @Nullable GLTF2AccessorData TANGENT_ACC_DATA = null;
                @Nullable GLTF2AccessorData JOINTS_0_ACC_DATA = null;
                @Nullable GLTF2AccessorData WEIGHTS_0_ACC_DATA = null;

                @NotNull GLTF2BufferView POSITION_BV = GLTF2Parser.readBufferView(bufferViews.get(POSITION_ACC_DATA.getBufferView()).getAsJsonObject());
                @NotNull GLTF2BufferView NORMAL_BV = GLTF2Parser.readBufferView(bufferViews.get(NORMAL_ACC_DATA.getBufferView()).getAsJsonObject());
                @NotNull GLTF2BufferView indices_BV = GLTF2Parser.readBufferView(bufferViews.get(indices_ACC_DATA.getBufferView()).getAsJsonObject());
                @Nullable GLTF2BufferView TEXCOORD_0_BV = null;
                @Nullable GLTF2BufferView TANGENT_BV = null;
                @Nullable GLTF2BufferView JOINTS_0_BV = null;
                @Nullable GLTF2BufferView WEIGHTS_0_BV = null;

                POSITION = GLTF2Parser.readAccessor(POSITION_BV, POSITION_ACC_DATA, buffersList);
                NORMAL = GLTF2Parser.readAccessor(NORMAL_BV, NORMAL_ACC_DATA, buffersList);
                indices = GLTF2Parser.readAccessor(indices_BV, indices_ACC_DATA, buffersList);

                if (TEXCOORD_0_ACCESSOR_ID >= 0) {
                    JsonObject TEXCOORD_0_ACC_DATA_JS = accessors.get(TEXCOORD_0_ACCESSOR_ID).getAsJsonObject();
                    TEXCOORD_0_ACC_DATA = GLTF2Parser.readAccessorData(TEXCOORD_0_ACC_DATA_JS);
                    TEXCOORD_0_BV = GLTF2Parser.readBufferView(bufferViews.get(TEXCOORD_0_ACC_DATA.getBufferView()).getAsJsonObject());
                    TEXCOORD_0 = GLTF2Parser.readAccessor(TEXCOORD_0_BV, TEXCOORD_0_ACC_DATA, buffersList);
                }

                if (TANGENT_ACCESSOR_ID >= 0) {
                    JsonObject TANGENT_ACC_DATA_JS = accessors.get(TANGENT_ACCESSOR_ID).getAsJsonObject();
                    TANGENT_ACC_DATA = GLTF2Parser.readAccessorData(TANGENT_ACC_DATA_JS);
                    TANGENT_BV = GLTF2Parser.readBufferView(bufferViews.get(TANGENT_ACC_DATA.getBufferView()).getAsJsonObject());
                    TANGENT = GLTF2Parser.readAccessor(TANGENT_BV, TANGENT_ACC_DATA, buffersList);
                    BiTANGENT = new GLTF2Accessor<>(GLTF2Parser.calculateBiTangents(TANGENT.getObjects(), NORMAL.getObjects()), null);
                } else {
                    if (TEXCOORD_0 != null) {
                        Pair<List<Float>, List<Float>> tangents = GLTF2Parser.calculateTangentsAndBiTangents(indices.getObjects(), POSITION.getObjects(), TEXCOORD_0.getObjects(), POSITION.getObjects().size() / 3);
                        TANGENT = new GLTF2Accessor<>(tangents.getFirst(), null);
                        BiTANGENT = new GLTF2Accessor<>(tangents.getSecond(), null);
                    } else {
                        Log.get().warn("The model: " + pathToMainFile + " doesn't have UV to calculate tangents and biTangents");
                        List<Float> uv = new ArrayList<>();
                        List<Float> bi_tangents = new ArrayList<>();

                        for (int i = 0; i < POSITION.getObjects().size() / 3; i++) {
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
                    JOINTS_0_BV = GLTF2Parser.readBufferView(bufferViews.get(JOINTS_0_ACC_DATA.getBufferView()).getAsJsonObject());
                    JOINTS_0 = GLTF2Parser.readAccessor(JOINTS_0_BV, JOINTS_0_ACC_DATA, buffersList);
                }

                if (WEIGHTS_0_ACCESSOR_ID >= 0) {
                    JsonObject WEIGHTS_0_ACC_DATA_JS = accessors.get(WEIGHTS_0_ACCESSOR_ID).getAsJsonObject();
                    WEIGHTS_0_ACC_DATA = GLTF2Parser.readAccessorData(WEIGHTS_0_ACC_DATA_JS);
                    WEIGHTS_0_BV = GLTF2Parser.readBufferView(bufferViews.get(WEIGHTS_0_ACC_DATA.getBufferView()).getAsJsonObject());
                    WEIGHTS_0 = GLTF2Parser.readAccessor(WEIGHTS_0_BV, WEIGHTS_0_ACC_DATA, buffersList);
                }
            }

            GLTF2Primitive gltf2Primitive = null;
            if (primitive.has("material")) {
                // "primitive materials"
                {
                    final int primitiveMaterialId = primitive.getAsJsonPrimitive("material").getAsInt();
                    gltf2Primitive = new GLTF2Primitive(POSITION, NORMAL, TEXCOORD_0, TANGENT, BiTANGENT, JOINTS_0, WEIGHTS_0, primitiveMaterialId, indices);
                }
            }

            gltf2Mesh.getPrimitives().add(gltf2Primitive);
        }

        return gltf2Mesh;
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

    private static GLTF2ImageTexture getTextureFromIndex(int texIndex, JsonArray textures, JsonArray images) {
        JsonObject textureObj = textures.get(texIndex).getAsJsonObject();
        int sourceIndex = textureObj.get("source").getAsInt();
        JsonObject imageObj = images.get(sourceIndex).getAsJsonObject();

        String name = imageObj.has("name") ? imageObj.get("name").getAsString() : "unnamed";
        String uri = imageObj.get("uri").getAsString();

        return new GLTF2ImageTexture(name, uri);
    }

    public static GLTF2Material loadMaterial(JsonObject materialJson, JsonArray textures, JsonArray images) {
        String name = materialJson.has("name") ? materialJson.get("name").getAsString() : "unknown";
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

        return new GLTF2AccessorData(bufferView, count, componentType, typeStr);
    }

    private static GLTF2BufferView readBufferView(JsonObject jsonObject) {
        final int bufferId = jsonObject.get("buffer").getAsInt();
        final int byteLength = jsonObject.get("byteLength").getAsInt();
        final int byteOffset = jsonObject.get("byteOffset").getAsInt();
        final int byteStride = jsonObject.has("byteStride") ? jsonObject.get("byteStride").getAsInt() : 0;
        final int target = jsonObject.get("target").getAsInt();

        return new GLTF2BufferView(bufferId, byteLength, byteOffset, byteStride, target);
    }

    @SuppressWarnings("unchecked")
    private static <T> GLTF2Accessor<T> readAccessor(GLTF2BufferView gltf2BufferView, GLTF2AccessorData gltf2AccessorData, List<ByteBuffer> bufferList) {
        final int bytesOfType = GLTF2Parser.getBytesOfType(gltf2AccessorData.getComponentType());
        final int typeSize = GLTF2Accessor.ValueType.getTypeSize(gltf2AccessorData.getTypeStr());
        final int elementByteSize = typeSize * bytesOfType;
        ByteBuffer buffer = bufferList.get(gltf2BufferView.getId());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int stride = gltf2BufferView.getByteStride() == 0 ? elementByteSize : gltf2BufferView.getByteStride();

        final List<T> readObjects = new ArrayList<>(gltf2AccessorData.getCount() * typeSize);

        for (int k = 0; k < gltf2AccessorData.getCount(); k++) {
            int basePosition = gltf2BufferView.getByteOffset() + k * stride;
            buffer.position(basePosition);
            for (int i = 0; i < typeSize; i++) {
                T component = (T) GLTF2Parser.readComponent(buffer, gltf2AccessorData.getComponentType());
                readObjects.add(component);
            }
        }

        return new GLTF2Accessor<>(readObjects, gltf2AccessorData);
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
