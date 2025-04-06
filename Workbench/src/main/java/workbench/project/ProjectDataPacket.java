package workbench.project;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.IJSONSerializable;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.mapping.data.FogData;
import javagems3d.mapping.data.ObjectsData;
import javagems3d.mapping.data.SkyData;
import javagems3d.mapping.data.SunData;

import java.lang.reflect.Type;

public final class ProjectDataPacket implements IJSONSerializable<ProjectDataPacket> {
    private FogData fogData;
    private SunData sunData;
    private ObjectsData objectsData;
    private SkyData skyData;

    public ProjectDataPacket() {
    }

    public ProjectDataPacket(FogData fogData, SunData sunData, ObjectsData objectsData, SkyData skyData) {
        this.fogData = fogData;
        this.sunData = sunData;
        this.objectsData = objectsData;
        this.skyData = skyData;
    }

    public void set(FogData fogData, SunData sunData, ObjectsData objectsData, SkyData skyData) {
        this.fogData = fogData;
        this.sunData = sunData;
        this.objectsData = objectsData;
        this.skyData = skyData;
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

    @Override
    public JSONFileManaging.@NotNull SerializationRules<ProjectDataPacket> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<ProjectDataPacket>() {
            @Override
            public JsonElement write(ProjectDataPacket toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.add("fogData", context.serialize(toWrite.fogData, FogData.class));
                    jsonObject.add("sunData", context.serialize(toWrite.sunData, SunData.class));
                    jsonObject.add("objectsData", context.serialize(toWrite.objectsData, ObjectsData.class));
                    jsonObject.add("skyData", context.serialize(toWrite.skyData, SkyData.class));

                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public ProjectDataPacket read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    FogData fogData = context.deserialize(jsonObject.get("fogData"), FogData.class);
                    SunData sunData = context.deserialize(jsonObject.get("sunData"), SunData.class);
                    ObjectsData objectsData = context.deserialize(jsonObject.get("objectsData"), ObjectsData.class);
                    SkyData skyData = context.deserialize(jsonObject.get("skyData"), SkyData.class);

                    return new ProjectDataPacket(fogData, sunData, objectsData, skyData);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
