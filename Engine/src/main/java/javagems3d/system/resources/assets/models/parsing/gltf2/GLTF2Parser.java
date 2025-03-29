package javagems3d.system.resources.assets.models.parsing.gltf2;

import com.google.gson.*;
import javagems3d.JGems3D;
import javagems3d.help.JGemsFilesHelper;
import javagems3d.help.JGemsUtils;
import javagems3d.system.resources.assets.models.parsing.IParser;
import javagems3d.system.resources.assets.models.parsing.animation.Bone;
import javagems3d.system.resources.assets.models.parsing.animation.SkeletonAttributes;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMaterialData;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMesh;
import javagems3d.system.resources.assets.models.parsing.space.ParsedVertexData;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.*;

public final class GLTF2Parser implements IParser {
    private final JGemsPath json;

    public GLTF2Parser(@NotNull JGemsPath json) {
        this.json = json;
    }

    public ParsedMesh parse() throws JGemsIOException {
        Log.get().debug("Parsing " + this.descriptor() + " - " + this.getJson());
        Gson gson = new Gson();
        JsonObject root = null;
        try {
            root = gson.fromJson(this.readJsonText(this.getJson()), JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw this.newException(e);
        }

        ByteBuffer buffer = this.getBytes(root);
        try {
            Set<Integer> rootNodes = new HashSet<>();
            JsonArray scenes = root.getAsJsonArray("scenes");
            if (scenes == null || scenes.isEmpty()) {
                throw this.newException("Corrupted model file: no scenes data!");
            }

            for (int i = 0; i < scenes.size(); i++) {
                JsonObject object = scenes.get(i).getAsJsonObject();
                JsonArray nodesData = object.getAsJsonArray("nodes");
                for (int k = 0; k < nodesData.size(); k++) {
                    rootNodes.add(nodesData.get(k).getAsInt());
                }
            }

            JsonArray nodes = root.getAsJsonArray("nodes");
            if (nodes == null || nodes.isEmpty()) {
                throw this.newException("Corrupted model file: no nodes data!");
            }

            ParsedMesh meshTable = new ParsedMesh();

            for (int i : rootNodes) {
                JsonElement element = nodes.get(i);
                JsonObject node = element.getAsJsonObject();
                Matrix4f transformation = this.constructTransformationMatrix(node, null);

                if (node.has("mesh")) {
                    int meshIndex = node.get("mesh").getAsInt();
                    String name = node.has("name") ? node.get("name").getAsString() : ("unknown" + i);
                    meshTable.createRow(name);
                    List<ParsedVertexData> parsedVertexData = this.readMeshData(root, meshIndex, buffer, transformation);
                    Log.get().debug("Read mesh: " + name + " (size): " + parsedVertexData.size());
                    meshTable.add(name, parsedVertexData);
                }

                if (node.has("children")) {
                    JsonArray children = node.getAsJsonArray("children");
                    for (JsonElement childElement : children) {
                        int childId = childElement.getAsInt();
                        this.processChildMesh(nodes, nodes.get(childId).getAsJsonObject(), root, buffer, transformation, meshTable);
                    }
                }
            }

            List<ParsedMaterialData> parsedMaterialData = this.readMaterials(root);
            if (parsedMaterialData != null) {
                Log.get().debug("Read materials: (size): " + parsedMaterialData.size());
                meshTable.setMaterialsData(parsedMaterialData);
            }

          // if (root.has("animations")) {
          //     this.readAnimations(nodes, root, buffer);
          //     System.out.println("f");
          // }
            Log.get().debug("End parsing");
            return meshTable;
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    private void processChildMesh(JsonArray nodes, JsonObject childNode, JsonObject root, ByteBuffer buffer, @Nullable Matrix4f parentTransformation, ParsedMesh meshTable) {
        Matrix4f childTransformation = this.constructTransformationMatrix(childNode, null);
        if (childTransformation == null) {
            childTransformation = new Matrix4f().identity();
        }
        Matrix4f transformation = parentTransformation == null ? childTransformation : childTransformation.mul(parentTransformation);

        if (childNode.has("mesh")) {
            int meshIndex = childNode.get("mesh").getAsInt();
            String name = childNode.has("name") ? childNode.get("name").getAsString() : "unknown_child";
            meshTable.createRow(name);

            List<ParsedVertexData> parsedVertexData = this.readMeshData(root, meshIndex, buffer, transformation);
            Log.get().debug("Read child mesh: " + name + " (size): " + parsedVertexData.size());
            meshTable.add(name, parsedVertexData);
        }

        if (childNode.has("children")) {
            JsonArray children = childNode.getAsJsonArray("children");
            for (JsonElement childElement : children) {
                int childId = childElement.getAsInt();
                this.processChildMesh(nodes, nodes.get(childId).getAsJsonObject(), root, buffer, transformation, meshTable);
            }
        }
    }

    private List<ParsedVertexData> readMeshData(JsonObject root, int index, ByteBuffer buffer, @Nullable Matrix4f transformation) throws JGemsIOException {
        List<ParsedVertexData> list = new ArrayList<>();

        JsonArray meshes = root.getAsJsonArray("meshes");
        if (meshes == null || meshes.isEmpty()) {
            throw this.newException("Corrupted model file: no meshes data!");
        }

        JsonElement meshElement = meshes.get(index);
        JsonObject mesh = meshElement.getAsJsonObject();
        JsonArray primitives = mesh.getAsJsonArray("primitives");
        if (primitives == null || primitives.isEmpty()) {
            throw this.newException("Corrupted model file: no mesh primitives data!");
        }

        for (JsonElement primitiveElement : primitives) {
            JsonObject primitive = primitiveElement.getAsJsonObject();
            JsonObject attributes = primitive.getAsJsonObject("attributes");

            List<Integer> indexes = this.readAttributesI("indices", primitive, root, buffer, 1);
            if (indexes.isEmpty()) {
                throw this.newException("Model should contain indexes for each mesh!");
            }
            int materialId = -1;
            if (primitive.has("material")) {
                materialId = primitive.get("material").getAsInt();
            }
            List<Float> vertexes = this.readAttributesF("POSITION", attributes, root, buffer, transformation, 3);
            if (vertexes.isEmpty()) {
                throw this.newException("Model should contain vertexes for each mesh!");
            }

            int totalVertexes = vertexes.size() / 3;

            List<Float> uv = this.readAttributesF("TEXCOORD_0", attributes, root, buffer, null, 2);
            List<Float> normals = this.readAttributesF("NORMAL", attributes, root, buffer, transformation, 3);
            List<Float> tangents = this.readAttributesF("TANGENT", attributes, root, buffer, transformation, 4);
            List<Integer> joints = this.readAttributesI("JOINTS_0", attributes, root, buffer, 4);
            List<Float> weights = this.readAttributesF("WEIGHTS_0", attributes, root, buffer, transformation, 4);
            SkeletonAttributes skeletonAttributes = new SkeletonAttributes(weights, joints);

            if (uv.isEmpty()) {
                for (int i = 0; i < totalVertexes; i++) {
                    uv.add(0.0f);
                    uv.add(0.0f);
                }
            }

            if (normals.isEmpty()) {
                throw this.newException("Model should contain normals!");
            }

            Pair<List<Float>, List<Float>> newTangents = null;
            if (tangents.isEmpty()) {
                newTangents = this.generateTangents(indexes, vertexes, uv, totalVertexes);
            } else {
                newTangents = this.processTangentsAndGetBiTangents(normals, tangents);
            }

            list.add(new ParsedVertexData(indexes, vertexes, uv, normals, newTangents.getFirst(), newTangents.getSecond(), null, null, materialId));
        }

        return list;
    }

    private void readAnimations(JsonArray nodes, JsonObject root, ByteBuffer buffer) {
        JsonArray animations = root.has("animations") ? root.getAsJsonArray("animations") : null;
        JsonArray skins = root.has("skins") ? root.getAsJsonArray("skins") : null;
        if (animations == null || skins == null) {
            return;
        }
        JsonObject basicSkin = skins.get(0).getAsJsonObject();
        Map<Integer, Bone> bones = new HashMap<>();
        List<Float> inverseBindingMatrixList = this.readBufferDataFloatMat16(basicSkin.get("inverseBindMatrices").getAsInt(), root, buffer);
        Matrix4f inverseBindingMatrix = JGemsUtils.getMatrixFromArray(inverseBindingMatrixList);

        JsonArray joints = basicSkin.get("joints").getAsJsonArray();
        for (int i = 0; i < joints.size(); i++) {
            int nodeId = joints.get(i).getAsInt();
            JsonObject nodeObject = nodes.get(nodeId).getAsJsonObject();
            Matrix4f transformation = this.constructTransformationMatrix(nodeObject, null);
            if (nodeObject.has("children")) {
                JsonArray children = nodeObject.getAsJsonArray("children");
                for (JsonElement childElement : children) {
                    int childId = childElement.getAsInt();
                    transformation = this.processChildAnimation(nodes, nodes.get(childId).getAsJsonObject(), transformation);
                }
            }

            Bone bone = new Bone(transformation, i);
            bones.put(nodeId, bone);
        }

        for (JsonElement animElem : animations) {
            JsonObject animObj = animElem.getAsJsonObject();
            String animName = animObj.has("name") ? animObj.get("name").getAsString() : "Unnamed";
            Log.get().debug("Read animation: " + animName);

            JsonArray channels = animObj.getAsJsonArray("channels");
            JsonArray samplers = animObj.getAsJsonArray("samplers");

            for (int i = 0; i < channels.size(); i++) {
                JsonObject object = channels.get(i).getAsJsonObject();
                JsonObject target = object.get("target").getAsJsonObject();
                String path = target.get("path").getAsString();
                if (path.equals("weights")) {
                    continue;
                }
                int samplerId = object.get("sampler").getAsInt();
                int boneId = target.get("node").getAsInt();

                JsonObject samplerObject = samplers.get(samplerId).getAsJsonObject();
                int input = samplerObject.get("input").getAsInt();
                int output = samplerObject.get("output").getAsInt();

                float animationDuration = 0.0f;
                List<Float> time = this.readBufferDataFloat(input, root, buffer, null, 1);
                List<Float> outputs = this.readBufferDataFloat(output, root, buffer, null, path.equals("rotation") ? 4 : 3);

                if (time.isEmpty() || outputs.isEmpty()) {
                    throw this.newException("Wrong animation data!");
                }

                for (float timeStamp : time) {
                    animationDuration += timeStamp;

                }
            }
        }
    }

    private Matrix4f processChildAnimation(JsonArray nodes, JsonObject childNode, @Nullable Matrix4f parentTransformation) {
        Matrix4f childTransformation = this.constructTransformationMatrix(childNode, null);
        if (childTransformation == null) {
            childTransformation = new Matrix4f().identity();
        }
        Matrix4f transformation = parentTransformation == null ? childTransformation : childTransformation.mul(parentTransformation);

        if (childNode.has("children")) {
            JsonArray children = childNode.getAsJsonArray("children");
            for (JsonElement childElement : children) {
                int childId = childElement.getAsInt();
                this.processChildAnimation(nodes, nodes.get(childId).getAsJsonObject(), transformation);
            }
        }

        return transformation;
    }

    private Pair<List<Float>, List<Float>> processTangentsAndGetBiTangents(List<Float> normals, List<Float> tangents) {
        List<Float> _biTangents = new ArrayList<>();
        List<Float> _tangents = new ArrayList<>();

        for (int i = 0; i < normals.size(); i += 3) {
            float nx = normals.get(i);
            float ny = normals.get(i + 1);
            float nz = normals.get(i + 2);

            int tangentIndex = (i / 3) * 4;
            float tx = tangents.get(tangentIndex);
            float ty = tangents.get(tangentIndex + 1);
            float tz = tangents.get(tangentIndex + 2);
            float tw = tangents.get(tangentIndex + 3);

            _tangents.add(tx);
            _tangents.add(ty);
            _tangents.add(tz);

            float handedness = Math.signum(tw);
            float bx = (ny * tz - nz * ty) * handedness;
            float by = (nz * tx - nx * tz) * handedness;
            float bz = (nx * ty - ny * tx) * handedness;

            float length = (float) Math.sqrt(bx * bx + by * by + bz * bz);
            if (length > 0.0f) {
                bx /= length;
                by /= length;
                bz /= length;
            }

            _biTangents.add(bx);
            _biTangents.add(by);
            _biTangents.add(bz);
        }
        return new Pair<>(_tangents, _biTangents);
    }

    public Pair<List<Float>, List<Float>> generateTangents(List<Integer> indexes, List<Float> positions, List<Float> uv, int totalVertices) {
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

            Vector3f tangent = new Vector3f(
                    (deltaPos1.x * deltaUV2y - deltaPos2.x * deltaUV1y) * r,
                    (deltaPos1.y * deltaUV2y - deltaPos2.y * deltaUV1y) * r,
                    (deltaPos1.z * deltaUV2y - deltaPos2.z * deltaUV1y) * r
            );

            Vector3f biTangent = new Vector3f(
                    (deltaPos2.x * deltaUV1x - deltaPos1.x * deltaUV2x) * r,
                    (deltaPos2.y * deltaUV1x - deltaPos1.y * deltaUV2x) * r,
                    (deltaPos2.z * deltaUV1x - deltaPos1.z * deltaUV2x) * r
            );

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

    private List<ParsedMaterialData> readMaterials(JsonObject root) {
        Map<Integer, Integer> texturesLinks = new HashMap<>();
        Map<Integer, String> idImages = new HashMap<>();

        JsonArray textures = root.getAsJsonArray("textures");
        if (textures != null && !textures.isEmpty()) {
            for (int i = 0; i < textures.size(); i++) {
                JsonElement jsonElement = textures.get(i);
                JsonObject texture = jsonElement.getAsJsonObject();
                texturesLinks.put(i, texture.get("source").getAsInt());
            }
        }

        JsonArray images = root.getAsJsonArray("images");
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                JsonElement jsonElement = images.get(i);
                JsonObject image = jsonElement.getAsJsonObject();
                idImages.put(i, image.get("uri").getAsString());
            }
        }

        List<ParsedMaterialData> list = new ArrayList<>();
        JsonArray materials = root.getAsJsonArray("materials");
        if (materials == null || materials.isEmpty()) {
            return null;
        }

        Log.get().debug("Loading " + materials.size() + " materials");
        for (int i = 0; i < materials.size(); i++) {
            JsonElement jsonElement = materials.get(i);
            JsonObject material = jsonElement.getAsJsonObject();

            JsonObject pbrMaterialObject = material.getAsJsonObject("pbrMetallicRoughness");
            if (pbrMaterialObject == null) {
                throw this.newException("Provided wrong material: " + material.get("name").getAsString());
            }

            Vector4f diffuseColor = null;
            Vector3f emissionColor = null;

            String emissionTexture = null;
            String normalsTexture = null;
            String diffuseTexture = null;
            String metallicRoughnessTexture = null;

            float metallicFactor = 0.0f;
            float roughnessFactor = 1.0f;

            float emissionIntensity = 5.0f;

            if (material.has("extensions")) {
                JsonObject object = material.getAsJsonObject("extensions");
                if (object.has("KHR_materials_emissive_strength")) {
                    JsonObject object1 = material.getAsJsonObject("KHR_materials_emissive_strength");
                    if (object1 != null && object1.has("emissiveStrength")) {
                        emissionIntensity = object1.get("emissiveStrength").getAsFloat();
                    }
                }
            }

            if (material.has("normalTexture")) {
                JsonObject object = material.getAsJsonObject("normalTexture");
                int index = object.get("index").getAsInt();
                normalsTexture = idImages.get(texturesLinks.get(index));
            }
            if (material.has("emissiveFactor")) {
                JsonArray array = material.getAsJsonArray("emissiveFactor");
                emissionColor = new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
            }
            if (emissionColor != null) {
                emissionColor.mul(emissionIntensity);
            }

            if (material.has("emissiveTexture")) {
                JsonObject object = material.getAsJsonObject("emissiveTexture");
                int index = object.get("index").getAsInt();
                emissionTexture = idImages.get(texturesLinks.get(index));
            }

            if (pbrMaterialObject.has("metallicRoughnessTexture")) {
                JsonObject object = pbrMaterialObject.getAsJsonObject("metallicRoughnessTexture");
                int index = object.get("index").getAsInt();
                metallicRoughnessTexture = idImages.get(texturesLinks.get(index));
                metallicFactor = 0.5f;
            }
            if (pbrMaterialObject.has("baseColorFactor")) {
                JsonArray array = pbrMaterialObject.getAsJsonArray("baseColorFactor");
                diffuseColor = new Vector4f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
            }
            if (pbrMaterialObject.has("baseColorTexture")) {
                JsonObject object = pbrMaterialObject.getAsJsonObject("baseColorTexture");
                int index = object.get("index").getAsInt();
                diffuseTexture = idImages.get(texturesLinks.get(index));
            }

            if (pbrMaterialObject.has("metallicFactor")) {
                JsonPrimitive object = pbrMaterialObject.getAsJsonPrimitive("metallicFactor");
                metallicFactor = object.getAsFloat();
            }
            if (pbrMaterialObject.has("roughnessFactor")) {
                JsonPrimitive object = pbrMaterialObject.getAsJsonPrimitive("roughnessFactor");
                roughnessFactor = object.getAsFloat();
            }
            list.add(new ParsedMaterialData(diffuseColor, emissionColor, emissionTexture, normalsTexture, diffuseTexture, metallicRoughnessTexture, metallicFactor, roughnessFactor));
        }
        return list;
    }

    private List<Integer> readAttributesI(String key, JsonObject object, JsonObject root, ByteBuffer buffer, int amount) {
        if (object.has(key)) {
            int accessorIndex = object.get(key).getAsInt();
            return this.readBufferDataInt(accessorIndex, root, buffer, amount);
        } else {
            return new ArrayList<>();
        }
    }

    private List<Float> readAttributesF(String key, JsonObject object, JsonObject root, ByteBuffer buffer, @Nullable Matrix4f transformation, int amount) {
        if (object.has(key)) {
            int accessorIndex = object.get(key).getAsInt();
            return this.readBufferDataFloat(accessorIndex, root, buffer, transformation, amount);
        } else {
            return new ArrayList<>();
        }
    }

    private List<Float> readBufferDataFloat(int accessorIndex, JsonObject root, ByteBuffer buffer, @Nullable Matrix4f transformation, int amount) {
        List<Float> values = new ArrayList<>();

        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();
        boolean normalized = accessor.has("normalized") && accessor.get("normalized").getAsBoolean();

        final int stockStride = amount * Float.BYTES;

        JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
        int byteStride = bufferView.has("byteStride") ? bufferView.get("byteStride").getAsInt() : stockStride;
        int bufferOffset = bufferView.get("byteOffset").getAsInt() + byteOffset;

        buffer.position(bufferOffset);

        for (int i = 0; i < count; i++) {
            Vector4f vector = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

            for (int k = 0; k < amount; k++) {
                float value;
                switch (componentType) {
                    case GL46.GL_FLOAT:
                        value = buffer.getFloat();
                        break;
                    case GL46.GL_UNSIGNED_BYTE:
                        value = normalized ? (buffer.get() & 0xFF) / 255.0f : buffer.get() & 0xFF;
                        break;
                    case GL46.GL_UNSIGNED_SHORT:
                        value = normalized ? (buffer.getShort() & 0xFFFF) / 65535.0f : buffer.getShort() & 0xFFFF;
                        break;
                    case GL46.GL_UNSIGNED_INT:
                        value = buffer.getInt();
                        break;
                    default:
                        throw this.newException("Unsupported component type!");
                }
                vector.setComponent(k, value);
            }

            if (transformation != null) {
                vector.mul(transformation);
            }

            for (int k = 0; k < amount; k++) {
                values.add(vector.get(k));
            }

            if (byteStride > stockStride) {
                buffer.position(buffer.position() + (byteStride - stockStride));
            }
        }

        return values;
    }

    private List<Float> readBufferDataFloatMat16(int accessorIndex, JsonObject root, ByteBuffer buffer) {
        List<Float> values = new ArrayList<>();

        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();
        boolean normalized = accessor.has("normalized") && accessor.get("normalized").getAsBoolean();

        final int stockStride = 16 * Float.BYTES;

        JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
        int byteStride = bufferView.has("byteStride") ? bufferView.get("byteStride").getAsInt() : stockStride;
        int bufferOffset = bufferView.get("byteOffset").getAsInt() + byteOffset;

        buffer.position(bufferOffset);

        for (int i = 0; i < count; i++) {
            for (int k = 0; k < 16; k++) {
                float value;
                switch (componentType) {
                    case GL46.GL_FLOAT:
                        value = buffer.getFloat();
                        break;
                    case GL46.GL_UNSIGNED_BYTE:
                        value = normalized ? (buffer.get() & 0xFF) / 255.0f : buffer.get() & 0xFF;
                        break;
                    case GL46.GL_UNSIGNED_SHORT:
                        value = normalized ? (buffer.getShort() & 0xFFFF) / 65535.0f : buffer.getShort() & 0xFFFF;
                        break;
                    case GL46.GL_UNSIGNED_INT:
                        value = buffer.getInt();
                        break;
                    default:
                        throw this.newException("Unsupported component type!");
                }
                values.add(value);
            }

            if (byteStride > stockStride) {
                buffer.position(buffer.position() + (byteStride - stockStride));
            }
        }

        return values;
    }

    private List<Integer> readBufferDataInt(int accessorIndex, JsonObject root, ByteBuffer buffer, int amount) {
        List<Integer> values = new ArrayList<>();

        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();

        final int stockStride = amount * Integer.BYTES;

        JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
        int byteStride = bufferView.has("byteStride") ? bufferView.get("byteStride").getAsInt() : stockStride;
        int bufferOffset = bufferView.get("byteOffset").getAsInt() + byteOffset;

        buffer.position(bufferOffset);

        for (int i = 0; i < count; i++) {
            for (int k = 0; k < amount; k++) {
                int value;
                switch (componentType) {
                    case GL46.GL_UNSIGNED_BYTE:
                        value = buffer.get() & 0xFF;
                        break;
                    case GL46.GL_UNSIGNED_SHORT:
                        value = buffer.getShort() & 0xFFFF;
                        break;
                    case GL46.GL_UNSIGNED_INT:
                        value = buffer.getInt();
                        break;
                    default:
                        throw this.newException("Unsupported component type for integers!");
                }
                values.add(value);
            }
            buffer.position(buffer.position() + (byteStride - stockStride));
        }

        return values;
    }

    private Matrix4f constructTransformationMatrix(JsonObject structure, @Nullable String path) {
        boolean flag = false;

        Quaternionf quaternionf = new Quaternionf();
        Vector3f scaling = new Vector3f(1.0f);
        Vector3f position = new Vector3f(0.0f);
        if (path == null || path.equals("translation")) {
            if (structure.has("translation")) {
                JsonArray array = structure.getAsJsonArray("translation");
                position.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
                flag = true;
            }
        }
        if (path == null || path.equals("rotation")) {
            if (structure.has("rotation")) {
                JsonArray array = structure.getAsJsonArray("rotation");
                quaternionf.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
                flag = true;
            }
        }
        if (path == null || path.equals("scale")) {
            if (structure.has("scale")) {
                JsonArray array = structure.getAsJsonArray("scale");
                scaling.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
                flag = true;
            }
        }
        return flag ? new Matrix4f().identity().translate(position).rotate(quaternionf).scale(scaling) : null;
    }

    private ByteBuffer getBytes(JsonObject root) {
        ByteBuffer buffer = null;
        JsonArray array = root.getAsJsonArray("buffers");
        if (array != null && !array.isEmpty()) {
            String binUri = array.get(0).getAsJsonObject().get("uri").getAsString();
            JGemsPath binPath = new JGemsPath(this.getJson().getDirectory(), binUri);
            Log.get().debug("Parsing " + this.descriptor() + " - " + binUri);
            try (InputStream inputStream = JGems3D.loadFileFromJar(binPath)){
                buffer = JGemsFilesHelper.toByteBuffer(inputStream);
            } catch (IOException e) {
                throw this.newException(e);
            }
        }
        return buffer;
    }

    private String readJsonText(JGemsPath path) {
        return JGemsFilesHelper.readTextFromFile(path);
    }

    public JGemsPath getJson() {
        return this.json;
    }

    @Override
    public String descriptor() {
        return "GLTF2";
    }
}