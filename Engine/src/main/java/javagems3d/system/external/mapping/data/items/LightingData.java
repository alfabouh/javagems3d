package javagems3d.system.external.mapping.data.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class LightingData implements SectionData<LightingData> {
    public boolean bloomEffect;
    public float exposure;
    public float gamma;

    private LightingData() {
    }

    public LightingData(boolean bloomEffect, float exposure, float gamma) {
        this.bloomEffect = bloomEffect;
        this.exposure = exposure;
        this.gamma = gamma;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<LightingData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<>() {
            @Override
            public JsonElement write(LightingData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("bloomEffect", context.serialize(toWrite.bloomEffect));
                    jsonObject.add("exposure", context.serialize(toWrite.exposure));
                    jsonObject.add("gamma", context.serialize(toWrite.gamma));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public LightingData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    boolean bloomEffect = context.deserialize(jsonObject.get("bloomEffect"), boolean.class);
                    float exposure = context.deserialize(jsonObject.get("exposure"), float.class);
                    float gamma = context.deserialize(jsonObject.get("gamma"), float.class);
                    return new LightingData(bloomEffect, exposure, gamma);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
