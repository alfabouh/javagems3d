package javagems3d.mapping.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.mapping.data.templates.SavedObjectTemplate;

import java.lang.reflect.Type;
import java.util.Set;

public class ObjectsData implements SectionData<ObjectsData> {
    public Set<SavedObjectTemplate> propObjects;
    public Set<SavedObjectTemplate> markerObjects;
    public Set<SavedObjectTemplate> entityObjects;
    public Set<SavedObjectTemplate> pointLights;

    private ObjectsData() {
    }

    public ObjectsData(Set<SavedObjectTemplate> propObjects, Set<SavedObjectTemplate> markerObjects, Set<SavedObjectTemplate> entityObjects, Set<SavedObjectTemplate> pointLights) {
        this.propObjects = propObjects;
        this.markerObjects = markerObjects;
        this.entityObjects = entityObjects;
        this.pointLights = pointLights;
    }

    public Set<SavedObjectTemplate> getPropObjects() {
        return this.propObjects;
    }

    public Set<SavedObjectTemplate> getMarkerObjects() {
        return this.markerObjects;
    }

    public Set<SavedObjectTemplate> getEntityObjects() {
        return this.entityObjects;
    }

    public Set<SavedObjectTemplate> getPointLights() {
        return this.pointLights;
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
                    jsonObject.add("pointLights", context.serialize(toWrite.pointLights));

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

                    objectsData.propObjects = context.deserialize(jsonObject.get("propObjects"), new TypeToken<Set<SavedObjectTemplate>>() {}.getType());
                    objectsData.markerObjects = context.deserialize(jsonObject.get("markerObjects"), new TypeToken<Set<SavedObjectTemplate>>() {}.getType());
                    objectsData.entityObjects = context.deserialize(jsonObject.get("entityObjects"), new TypeToken<Set<SavedObjectTemplate>>() {}.getType());
                    objectsData.pointLights = context.deserialize(jsonObject.get("pointLights"), new TypeToken<Set<SavedObjectTemplate>>() {}.getType());

                    return objectsData;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read ObjectsData", e);
                }
            }
        };
    }
}
