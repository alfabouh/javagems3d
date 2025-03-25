package javagems3d.system.resources.assets.models.parsing.gltf2;

import com.google.gson.*;
import javagems3d.JGems3D;
import javagems3d.help.JGemsFilesHelper;
import javagems3d.system.resources.assets.models.parsing.IParser;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMaterialsData;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMeshTable;
import javagems3d.system.resources.assets.models.parsing.space.ParsedVertexData;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GLTF2Parser implements IParser {
    private final JGemsPath json;
    private JsonObject root;

    public GLTF2Parser(@NotNull JGemsPath json) {
        this.json = json;
    }

    public ParsedMeshTable parse() throws JGemsIOException {
        Log.get().debug("Parsing " + this.descriptor() + " - " + this.getJson());
        Gson gson = new Gson();
        JsonObject root = null;
        try {
            root = gson.fromJson(this.readJsonText(this.getJson()), JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw this.newException(e);
        }

        ByteBuffer buffer = this.getBytes(root);
        JsonArray nodes = root.getAsJsonArray("nodes");
        if (nodes == null || nodes.isEmpty()) {
            throw this.newException("Corrupted model file: no nodes data!");
        }

        ParsedMeshTable meshTable = new ParsedMeshTable();

        for (int i = 0; i < nodes.size(); i++) {
            JsonElement element = nodes.get(i);
            JsonObject node = element.getAsJsonObject();
            int meshIndex = node.get("mesh").getAsInt();
            String name = node.has("name") ? node.get("name").getAsString() : ("unknown" + i);
            meshTable.createRow(name);
            Matrix4f transformation = this.constructTransformationMatrix(node);
            List<ParsedVertexData> parsedVertexData = this.readMeshData(root, meshIndex, buffer, transformation);
            Log.get().debug("Read mesh: " + name + " (size): " + parsedVertexData.size());
            meshTable.add(name, parsedVertexData);
        }

        MemoryUtil.memFree(buffer);
        Log.get().debug("End parsing");
        return meshTable;
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

            List<Integer> indexes = this.readAttributesI("indices", primitive, root, buffer);

            List<Float> uv = this.readAttributesF("TEXCOORD_0", attributes, root, buffer, null);
            List<Float> vertexes = this.readAttributesF("POSITION", attributes, root, buffer, transformation);
            List<Float> normals = this.readAttributesF("NORMAL", attributes, root, buffer, transformation);
            List<Float> tangents = this.readAttributesF("TANGENT", attributes, root, buffer, transformation);

            list.add(new ParsedVertexData(indexes, vertexes, uv, normals, tangents));
        }

        return list;
    }

    private List<ParsedMaterialsData> readMaterials(JsonObject root) {
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

        JsonArray images = root.getAsJsonArray("textures");
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                JsonElement jsonElement = images.get(i);
                JsonObject image = jsonElement.getAsJsonObject();
                idImages.put(i, image.get("uri").getAsString());
            }
        }

        List<ParsedMaterialsData> list = new ArrayList<>();
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
            Vector3f emissionColor = new Vector3f(1.f);

            String emissionTexture = null;
            String normalsTexture = null;
            String diffuseTexture = null;
            String metallicRoughnessTexture = null;

            float metallicFactor = 0.0f;
            float roughnessFactor = 0.0f;

            float emissionIntensity = 5.0f;

            if (material.has("extensions")) {
                JsonObject object = material.getAsJsonObject("extensions");
                if (object.has("KHR_materials_emissive_strength")) {
                    JsonObject object1 = material.getAsJsonObject("KHR_materials_emissive_strength");
                    emissionIntensity = object1.get("emissiveStrength").getAsFloat();
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
            emissionColor.mul(emissionIntensity);

            if (material.has("emissiveTexture")) {
                JsonObject object = material.getAsJsonObject("emissiveTexture");
                int index = object.get("index").getAsInt();
                emissionTexture = idImages.get(texturesLinks.get(index));
            }

            if (pbrMaterialObject.has("metallicRoughnessTexture")) {
                JsonObject object = pbrMaterialObject.getAsJsonObject("metallicRoughnessTexture");
                int index = object.get("index").getAsInt();
                metallicRoughnessTexture = idImages.get(texturesLinks.get(index));
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
                JsonObject object = pbrMaterialObject.getAsJsonObject("metallicFactor");
                metallicFactor = object.getAsFloat();
            }
            if (pbrMaterialObject.has("roughnessFactor")) {
                JsonObject object = pbrMaterialObject.getAsJsonObject("roughnessFactor");
                roughnessFactor = object.getAsFloat();
            }
            list.add(new ParsedMaterialsData(diffuseColor, emissionColor, emissionTexture, normalsTexture, diffuseTexture, metallicRoughnessTexture, metallicFactor, roughnessFactor));
        }
        return list;
    }

    private List<Integer> readAttributesI(String key, JsonObject object, JsonObject root, ByteBuffer buffer) {
        if (object.has(key)) {
            int accessorIndex = object.get(key).getAsInt();
            return this.readIntegers(accessorIndex, root, buffer);
        } else {
            throw this.newException("Model doesn't contain" + key + " attribute!");
        }
    }

    private List<Float> readAttributesF(String key, JsonObject object, JsonObject root, ByteBuffer buffer, @Nullable Matrix4f transformation) {
        if (object.has(key)) {
            int accessorIndex = object.get(key).getAsInt();
            return this.readFloats(accessorIndex, root, buffer, transformation);
        } else {
            throw this.newException("Model doesn't contain" + key + " attribute!");
        }
    }

    private List<Integer> readIntegers(int accessorIndex, JsonObject root, ByteBuffer buffer) {
        List<Integer> values = new ArrayList<>();

        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();

        JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
        int bufferOffset = bufferView.get("byteOffset").getAsInt() + byteOffset;

        buffer.position(bufferOffset);

        switch (componentType) {
            case GL46.GL_UNSIGNED_BYTE: {
                for (int i = 0; i < count; i++) {
                    values.add(buffer.get() & 0xFF);
                }
                break;
            }
            case GL46.GL_UNSIGNED_SHORT: {
                for (int i = 0; i < count; i++) {
                    values.add(buffer.getShort() & 0xFFFF);
                }
                break;
            }
            case GL46.GL_UNSIGNED_INT: {
                for (int i = 0; i < count; i++) {
                    values.add(buffer.getInt());
                }
                break;
            }
            default: {
                throw this.newException("Couldn't get component type!");
            }
        }

        if (values.isEmpty()) {
            return null;
        }

        return values;
    }

    private List<Float> readFloats(int accessorIndex, JsonObject root, ByteBuffer buffer, @Nullable Matrix4f transformation) {
        List<Float> values = new ArrayList<>();

        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();

        final int stockStride = 3 * Float.BYTES;

        JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;
        int byteStride = bufferView.has("byteStride") ? bufferView.get("byteStride").getAsInt() : stockStride;
        int bufferOffset = bufferView.get("byteOffset").getAsInt() + byteOffset;

        buffer.position(bufferOffset);

        if (componentType != GL46.GL_FLOAT) {
            throw this.newException("Component type should be FLOAT!");
        }

        for (int i = 0; i < count; i++) {
            Vector4f vector4f = new Vector4f(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), 1.0f);
            if (transformation != null) {
                vector4f.mul(transformation);
            }
            values.add(vector4f.x);
            values.add(vector4f.y);
            values.add(vector4f.z);
            buffer.position(buffer.position() + (byteStride - stockStride));
        }

        if (values.isEmpty()) {
            return null;
        }

        return values;
    }

    private Matrix4f constructTransformationMatrix(JsonObject structure) {
        boolean flag = false;

        Quaternionf quaternionf = new Quaternionf();
        Vector3f scaling = new Vector3f(1.0f);
        Vector3f position = new Vector3f(0.0f);
        if (structure.has("translation")) {
            JsonArray array = structure.getAsJsonArray("translation");
            position.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
            flag = true;
        }
        if (structure.has("rotation")) {
            JsonArray array = structure.getAsJsonArray("rotation");
            quaternionf.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
            flag = true;
        }
        if (structure.has("scale")) {
            JsonArray array = structure.getAsJsonArray("scale");
            scaling.set(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
            flag = true;
        }
        return flag ? new Matrix4f().identity().translate(position).rotate(quaternionf).scale(scaling) : null;
    }

    private ByteBuffer getBytes(JsonObject root) {
        ByteBuffer buffer = null;
        JsonArray array = root.getAsJsonArray("buffers");
        if (array != null && !array.isEmpty()) {
            String binUri = array.get(0).getAsJsonObject().get("uri").getAsString();
            int binSize = array.get(0).getAsJsonObject().get("byteLength").getAsInt();
            JGemsPath binPath = new JGemsPath(this.getJson().getDirectory(), binUri);
            Log.get().debug("Parsing " + this.descriptor() + " - " + binUri);
            try (InputStream inputStream = JGems3D.loadFileFromJar(binPath)){
                byte[] bytes = JGemsFilesHelper.toByteArray(inputStream, binSize);
                buffer = MemoryUtil.memAlloc(bytes.length);
                buffer.put(bytes);
                buffer.flip();
            } catch (IOException e) {
                throw this.newException(e);
            }
        }
        return buffer;
    }

    private String readJsonText(JGemsPath path) {
        return JGemsFilesHelper.readTextFromFile(path);
    }

    public JsonObject getRoot() {
        return this.root;
    }

    public JGemsPath getJson() {
        return this.json;
    }

    @Override
    public String descriptor() {
        return "GLTF2";
    }
}