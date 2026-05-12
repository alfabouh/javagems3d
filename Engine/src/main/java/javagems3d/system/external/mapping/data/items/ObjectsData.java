package javagems3d.system.external.mapping.data.items;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class ObjectsData implements SectionData<ObjectsData> {
    public Set<RowMapObjectData> backgroundProps;
    public Set<RowMapObjectData> propObjects;
    public Set<RowMapObjectData> markerObjects;
    public Set<RowMapObjectData> entityObjects;

    private ObjectsData() {
    }

    public ObjectsData(Set<RowMapObjectData> propObjects, Set<RowMapObjectData> markerObjects, Set<RowMapObjectData> entityObjects, Set<RowMapObjectData> backgroundProps) {
        this.propObjects = propObjects;
        this.markerObjects = markerObjects;
        this.entityObjects = entityObjects;
        this.backgroundProps = backgroundProps;
    }

    public Set<RowMapObjectData> getPropObjects() {
        return this.propObjects;
    }

    public Set<RowMapObjectData> getMarkerObjects() {
        return this.markerObjects;
    }

    public Set<RowMapObjectData> getEntityObjects() {
        return this.entityObjects;
    }

    public Set<RowMapObjectData> getBackgroundProps() {
        return this.backgroundProps;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<ObjectsData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<ObjectsData>() {
            @Override
            public JsonElement write(ObjectsData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.add("propObjects", context.serialize(toWrite.propObjects));
                    jsonObject.add("markerObjects", context.serialize(toWrite.markerObjects));
                    jsonObject.add("entityObjects", context.serialize(toWrite.entityObjects));
                    jsonObject.add("backgroundProps", context.serialize(toWrite.backgroundProps));

                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write ObjectsData", e);
                }
            }

            @Override
            public ObjectsData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    ObjectsData objectsData = new ObjectsData();

                    objectsData.propObjects = context.deserialize(jsonObject.get("propObjects"), new TypeToken<Set<RowMapObjectData>>() {}.getType());
                    objectsData.markerObjects = context.deserialize(jsonObject.get("markerObjects"), new TypeToken<Set<RowMapObjectData>>() {}.getType());
                    objectsData.entityObjects = context.deserialize(jsonObject.get("entityObjects"), new TypeToken<Set<RowMapObjectData>>() {}.getType());
                    objectsData.backgroundProps = context.deserialize(jsonObject.get("backgroundProps"), new TypeToken<Set<RowMapObjectData>>() {}.getType());

                    return objectsData;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read ObjectsData", e);
                }
            }
        };
    }
}
