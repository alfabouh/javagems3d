package javagems3d.system.external.mapping.data.templates;

import com.google.gson.*;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.IJSONSerializable;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public final class RowMapObjectData implements IJSONSerializable<RowMapObjectData> {
    private int id;
    private String objectId;
    private String objectPath;
    private TagsContainer tagsContainer;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scaling;

    private RowMapObjectData() {
    }

    public RowMapObjectData(int id, String objectId, String objectPath, TagsContainer tagsContainer, Vector3f position, Vector3f rotation, Vector3f scaling) {
        this.id = id;
        this.objectPath = objectPath;
        this.objectId = objectId;
        this.tagsContainer = tagsContainer;
        this.position = position;
        this.rotation = rotation;
        this.scaling = scaling;
    }

    public int getId() {
        return this.id;
    }

    public String getObjectPath() {
        return this.objectPath;
    }

    public String getObjectNameId() {
        return this.objectId;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public Vector3f getScaling() {
        return this.scaling;
    }

    public boolean checkGroupName(String group, String name) {
        return this.getObjectPath().equals(group) && this.getObjectNameId().equals(name);
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<RowMapObjectData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<RowMapObjectData>() {
            @Override
            public JsonElement write(RowMapObjectData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.add("id", context.serialize(toWrite.id));
                    jsonObject.add("objectPath", context.serialize(toWrite.objectPath));
                    jsonObject.add("objectId", context.serialize(toWrite.objectId));
                    jsonObject.add("position", context.serialize(toWrite.position));
                    jsonObject.add("rotation", context.serialize(toWrite.rotation));
                    jsonObject.add("scaling", context.serialize(toWrite.scaling));
                    jsonObject.add("tagsContainer", context.serialize(toWrite.tagsContainer));

                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public RowMapObjectData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    RowMapObjectData rowMapObjectData = new RowMapObjectData();

                    rowMapObjectData.id = context.deserialize(jsonObject.get("id"), String.class);
                    rowMapObjectData.objectPath = context.deserialize(jsonObject.get("objectPath"), String.class);
                    rowMapObjectData.objectId = context.deserialize(jsonObject.get("objectId"), String.class);
                    rowMapObjectData.position = context.deserialize(jsonObject.get("position"), Vector3f.class);
                    rowMapObjectData.rotation = context.deserialize(jsonObject.get("rotation"), Vector3f.class);
                    rowMapObjectData.scaling = context.deserialize(jsonObject.get("scaling"), Vector3f.class);
                    rowMapObjectData.tagsContainer = context.deserialize(jsonObject.get("tagsContainer"), TagsContainer.class);

                    return rowMapObjectData;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
