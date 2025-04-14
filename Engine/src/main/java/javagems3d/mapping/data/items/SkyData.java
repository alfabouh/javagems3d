package javagems3d.mapping.data.items;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class SkyData implements SectionData<SkyData> {
    public String skyboxPath;
    public float backGroundScaling;

    private SkyData() {
    }

    public SkyData(String skyboxPath, float backGroundScaling) {
        this.skyboxPath = skyboxPath;
        this.backGroundScaling = backGroundScaling;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SkyData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SkyData>() {
            @Override
            public JsonElement write(SkyData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("skyboxPath", context.serialize(toWrite.skyboxPath));
                    jsonObject.add("backGroundScaling", context.serialize(toWrite.backGroundScaling));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public SkyData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String skyboxPath = context.deserialize(jsonObject.get("skyboxPath"), String.class);
                    float backGroundScaling = context.deserialize(jsonObject.get("backGroundScaling"), Float.class);
                    return new SkyData(skyboxPath, backGroundScaling);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
