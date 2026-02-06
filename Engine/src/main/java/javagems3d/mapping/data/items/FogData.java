package javagems3d.mapping.data.items;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class FogData implements SectionData<FogData> {
    public float density;
    public Vector3f color;
    public boolean isSkyCoveredByFog;

    private FogData() {
    }

    public FogData(boolean isSkyCoveredByFog, float density, Vector3f color) {
        this.density = density;
        this.color = color;
        this.isSkyCoveredByFog = isSkyCoveredByFog;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<FogData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<FogData>() {
            @Override
            public JsonElement write(FogData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("density", context.serialize(toWrite.density));
                    jsonObject.add("color", context.serialize(toWrite.color));
                    jsonObject.add("isSkyCoveredByFog", context.serialize(toWrite.isSkyCoveredByFog));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public FogData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    boolean isSkyCoveredByFog = context.deserialize(jsonObject.get("isSkyCoveredByFog"), Boolean.class);
                    float density = context.deserialize(jsonObject.get("density"), Float.class);
                    Vector3f color = context.deserialize(jsonObject.get("color"), Vector3f.class);
                    return new FogData(isSkyCoveredByFog, density, color);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
