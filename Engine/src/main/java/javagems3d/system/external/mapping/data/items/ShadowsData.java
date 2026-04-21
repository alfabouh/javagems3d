package javagems3d.system.external.mapping.data.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class ShadowsData implements SectionData<ShadowsData> {
    public Vector3f splits;
    public boolean sunShadows;
    public int sunShadowRes;
    public int pointLightShadowRes;

    private ShadowsData() {
    }

    public ShadowsData(Vector3f splits, boolean sunShadows, int sunShadowRes, int pointLightShadowRes) {
        this.splits = splits;
        this.sunShadows = sunShadows;
        this.sunShadowRes = sunShadowRes;
        this.pointLightShadowRes = pointLightShadowRes;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<ShadowsData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<>() {
            @Override
            public JsonElement write(ShadowsData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("splits", context.serialize(toWrite.splits));
                    jsonObject.add("sunShadows", context.serialize(toWrite.sunShadows));
                    jsonObject.add("sunShadowRes", context.serialize(toWrite.sunShadowRes));
                    jsonObject.add("pointLightShadowRes", context.serialize(toWrite.pointLightShadowRes));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public ShadowsData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Vector3f splits = context.deserialize(jsonObject.get("splits"), Vector3f.class);
                    boolean sunShadows = jsonObject.has("sunShadows") ? context.deserialize(jsonObject.get("sunShadows"), boolean.class) : true;
                    int sunShadowRes = jsonObject.has("sunShadowRes") ? context.deserialize(jsonObject.get("sunShadowRes"), int.class) : JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
                    int pointLightShadowRes = jsonObject.has("pointLightShadowRes") ? context.deserialize(jsonObject.get("pointLightShadowRes"), int.class) : JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
                    return new ShadowsData(splits, sunShadows, sunShadowRes, pointLightShadowRes);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
