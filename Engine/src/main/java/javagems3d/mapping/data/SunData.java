package javagems3d.mapping.data;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class SunData implements SectionData<SunData> {
    public float brightness;
    public Vector3f color;
    public Vector3f position;

    private SunData() {
    }

    public SunData(float brightness, Vector3f color, Vector3f position) {
        this.brightness = brightness;
        this.color = color;
        this.position = position;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SunData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SunData>() {
            @Override
            public JsonElement write(SunData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("brightness", context.serialize(toWrite.brightness));
                    jsonObject.add("color", context.serialize(toWrite.color));
                    jsonObject.add("position", context.serialize(toWrite.position));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public SunData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    float brightness = context.deserialize(jsonObject.get("brightness"), Float.class);
                    Vector3f color = context.deserialize(jsonObject.get("color"), Vector3f.class);
                    Vector3f position = context.deserialize(jsonObject.get("position"), Vector3f.class);
                    return new SunData(brightness, color, position);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
