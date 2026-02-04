package javagems3d.mapping.data.items;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class SkyData implements SectionData<SkyData> {
    private String textureUPPath;
    private String textureBOTTOMPath;
    private String textureFRONTPath;
    private String textureBACKPath;
    private String textureLEFTPath;
    private String textureRIGHTPath;
    public float backGroundScaling;

    private SkyData() {
    }

    public SkyData(@Nullable String textureUPPath,
                   @Nullable String textureBOTTOMPath,
                   @Nullable String textureFRONTPath,
                   @Nullable String textureBACKPath,
                   @Nullable String textureLEFTPath,
                   @Nullable String textureRIGHTPath,
                   float backGroundScaling) {
        this.textureUPPath = textureUPPath;
        this.textureBOTTOMPath = textureBOTTOMPath;
        this.textureFRONTPath = textureFRONTPath;
        this.textureBACKPath = textureBACKPath;
        this.textureLEFTPath = textureLEFTPath;
        this.textureRIGHTPath = textureRIGHTPath;
        this.backGroundScaling = backGroundScaling;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SkyData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SkyData>() {
            @Override
            public JsonElement write(SkyData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("textureUPPath", context.serialize(toWrite.textureUPPath));
                    jsonObject.add("textureBOTTOMPath", context.serialize(toWrite.textureBOTTOMPath));
                    jsonObject.add("textureFRONTPath", context.serialize(toWrite.textureFRONTPath));
                    jsonObject.add("textureBACKPath", context.serialize(toWrite.textureBACKPath));
                    jsonObject.add("textureLEFTPath", context.serialize(toWrite.textureLEFTPath));
                    jsonObject.add("textureRIGHTPath", context.serialize(toWrite.textureRIGHTPath));
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
                    final String textureUPPath = context.deserialize(jsonObject.get("textureUPPath"), String.class);
                    final String textureBOTTOMPath = context.deserialize(jsonObject.get("textureBOTTOMPath"), String.class);
                    final String textureFRONTPath = context.deserialize(jsonObject.get("textureFRONTPath"), String.class);
                    final String textureBACKPath = context.deserialize(jsonObject.get("textureBACKPath"), String.class);
                    final String textureLEFTPath = context.deserialize(jsonObject.get("textureLEFTPath"), String.class);
                    final String textureRIGHTPath = context.deserialize(jsonObject.get("textureRIGHTPath"), String.class);
                    final float backGroundScaling = context.deserialize(jsonObject.get("backGroundScaling"), Float.class);
                    return new SkyData(textureUPPath, textureBOTTOMPath, textureFRONTPath, textureBACKPath, textureLEFTPath, textureRIGHTPath, backGroundScaling);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
