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

public class ShadowsData implements SectionData<ShadowsData> {
    public Vector3f splits;

    private ShadowsData() {
    }

    public ShadowsData(Vector3f splits) {
        this.splits = splits;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<ShadowsData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<ShadowsData>() {
            @Override
            public JsonElement write(ShadowsData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("splits", context.serialize(toWrite.splits));
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
                    return new ShadowsData(splits);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
