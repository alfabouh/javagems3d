package javagems3d.mapping.data.items;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.mapping.data.templates.MapObjectTemplate;

import java.lang.reflect.Type;
import java.util.Set;

public class ObjectsData implements SectionData<ObjectsData> {
    public Set<MapObjectTemplate> propObjects;
    public Set<MapObjectTemplate> markerObjects;
    public Set<MapObjectTemplate> entityObjects;
    public Set<MapObjectTemplate> pointLights;

    private ObjectsData() {
    }

    public ObjectsData(Set<MapObjectTemplate> propObjects, Set<MapObjectTemplate> markerObjects, Set<MapObjectTemplate> entityObjects, Set<MapObjectTemplate> pointLights) {
        this.propObjects = propObjects;
        this.markerObjects = markerObjects;
        this.entityObjects = entityObjects;
        this.pointLights = pointLights;
    }

    public Set<MapObjectTemplate> getPropObjects() {
        return this.propObjects;
    }

    public Set<MapObjectTemplate> getMarkerObjects() {
        return this.markerObjects;
    }

    public Set<MapObjectTemplate> getEntityObjects() {
        return this.entityObjects;
    }

    public Set<MapObjectTemplate> getPointLights() {
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

                    objectsData.propObjects = context.deserialize(jsonObject.get("propObjects"), new TypeToken<Set<MapObjectTemplate>>() {}.getType());
                    objectsData.markerObjects = context.deserialize(jsonObject.get("markerObjects"), new TypeToken<Set<MapObjectTemplate>>() {}.getType());
                    objectsData.entityObjects = context.deserialize(jsonObject.get("entityObjects"), new TypeToken<Set<MapObjectTemplate>>() {}.getType());
                    objectsData.pointLights = context.deserialize(jsonObject.get("pointLights"), new TypeToken<Set<MapObjectTemplate>>() {}.getType());

                    return objectsData;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read ObjectsData", e);
                }
            }
        };
    }
}
