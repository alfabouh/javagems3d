package javagems3d.system.external.mapping.data;

import com.google.gson.*;
import javagems3d.system.external.mapping.data.items.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.IJSONSerializable;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public final class MapObjectsDataPack implements IJSONSerializable<MapObjectsDataPack> {
    private FogData fogData;
    private SunData sunData;
    private ObjectsData objectsData;
    private SkyData skyData;
    private ShadowsData shadowsData;

    public MapObjectsDataPack() {
    }

    public MapObjectsDataPack(FogData fogData, SunData sunData, ObjectsData objectsData, SkyData skyData, ShadowsData shadowsData) {
        this.fogData = fogData;
        this.sunData = sunData;
        this.objectsData = objectsData;
        this.skyData = skyData;
        this.shadowsData = shadowsData;
    }

    public void set(FogData fogData, SunData sunData, ObjectsData objectsData, SkyData skyData, ShadowsData shadowsData) {
        this.fogData = fogData;
        this.sunData = sunData;
        this.objectsData = objectsData;
        this.skyData = skyData;
        this.shadowsData = shadowsData;
    }

    public FogData getFogData() {
        return this.fogData;
    }

    public SunData getSunData() {
        return this.sunData;
    }

    public ObjectsData getObjectsData() {
        return this.objectsData;
    }

    public SkyData getSkyData() {
        return this.skyData;
    }

    public ShadowsData getShadowsData() {
        return shadowsData;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<MapObjectsDataPack> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<MapObjectsDataPack>() {
            @Override
            public JsonElement write(MapObjectsDataPack toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.add("fogData", context.serialize(toWrite.fogData, FogData.class));
                    jsonObject.add("sunData", context.serialize(toWrite.sunData, SunData.class));
                    jsonObject.add("objectsData", context.serialize(toWrite.objectsData, ObjectsData.class));
                    jsonObject.add("skyData", context.serialize(toWrite.skyData, SkyData.class));
                    jsonObject.add("shadowsData", context.serialize(toWrite.shadowsData, ShadowsData.class));

                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public MapObjectsDataPack read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    FogData fogData = context.deserialize(jsonObject.get("fogData"), FogData.class);
                    SunData sunData = context.deserialize(jsonObject.get("sunData"), SunData.class);
                    ObjectsData objectsData = context.deserialize(jsonObject.get("objectsData"), ObjectsData.class);
                    SkyData skyData = context.deserialize(jsonObject.get("skyData"), SkyData.class);
                    ShadowsData shadowsData = context.deserialize(jsonObject.get("shadowsData"), ShadowsData.class);

                    return new MapObjectsDataPack(fogData, sunData, objectsData, skyData, shadowsData);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
