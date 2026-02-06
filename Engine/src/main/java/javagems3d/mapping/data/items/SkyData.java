package javagems3d.mapping.data.items;

import com.google.gson.*;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class SkyData implements SectionData<SkyData> {
    private final String nameId;
    public ICubeMapProgram.CMTextures cmTextures;
    public float backGroundScaling;

    public SkyData(@NotNull String nameId, @Nullable ICubeMapProgram.CMTextures cmTextures, float backGroundScaling) {
        this.nameId = nameId;
        this.cmTextures = cmTextures;
        this.backGroundScaling = backGroundScaling;
    }

    public String getNameId() {
        return this.nameId;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SkyData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SkyData>() {
            @Override
            public JsonElement write(SkyData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("nameId", context.serialize(toWrite.nameId));
                    jsonObject.add("textures", context.serialize(toWrite.cmTextures));
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
                    final ICubeMapProgram.CMTextures textures = context.deserialize(jsonObject.get("textures"), ICubeMapProgram.CMTextures.class);
                    final float backGroundScaling = context.deserialize(jsonObject.get("backGroundScaling"), Float.class);
                    final String nameId = context.deserialize(jsonObject.get("nameId"), String.class);
                    return new SkyData(nameId, textures, backGroundScaling);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
