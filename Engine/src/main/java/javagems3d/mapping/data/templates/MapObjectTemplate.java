package javagems3d.mapping.data.templates;

import com.google.gson.*;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.IJSONSerializable;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public final class MapObjectTemplate implements IJSONSerializable<MapObjectTemplate> {
    private int id;
    private String objectId;
    private String objectGroup;

    private TagsContainer tagsContainer;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scaling;

    private MapObjectTemplate() {
    }

    public MapObjectTemplate(int id, String objectId, String objectGroup, TagsContainer tagsContainer, Vector3f position, Vector3f rotation, Vector3f scaling) {
        this.id = id;
        this.objectGroup = objectGroup;
        this.objectId = objectId;
        this.tagsContainer = tagsContainer;
        this.position = position;
        this.rotation = rotation;
        this.scaling = scaling;
    }

    public int getId() {
        return this.id;
    }

    public String getObjectGroup() {
        return this.objectGroup;
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

    @Override
    public JSONFileManaging.@NotNull SerializationRules<MapObjectTemplate> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<MapObjectTemplate>() {
            @Override
            public JsonElement write(MapObjectTemplate toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.add("id", context.serialize(toWrite.id));
                    jsonObject.add("objectGroup", context.serialize(toWrite.objectGroup));
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
            public MapObjectTemplate read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    MapObjectTemplate mapObjectTemplate = new MapObjectTemplate();

                    mapObjectTemplate.id = context.deserialize(jsonObject.get("id"), String.class);
                    mapObjectTemplate.objectGroup = context.deserialize(jsonObject.get("objectGroup"), String.class);
                    mapObjectTemplate.objectId = context.deserialize(jsonObject.get("objectId"), String.class);
                    mapObjectTemplate.position = context.deserialize(jsonObject.get("position"), Vector3f.class);
                    mapObjectTemplate.rotation = context.deserialize(jsonObject.get("rotation"), Vector3f.class);
                    mapObjectTemplate.scaling = context.deserialize(jsonObject.get("scaling"), Vector3f.class);
                    mapObjectTemplate.tagsContainer = context.deserialize(jsonObject.get("tagsContainer"), TagsContainer.class);

                    return mapObjectTemplate;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
