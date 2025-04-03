package javagems3d.mapping.tags.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import javagems3d.mapping.tags.base.Colors;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.lang.reflect.Type;

public class TagColor implements TagItem {
    private final Vector4f colorVector;
    private final Colors colors;

    public TagColor(Colors colors, @NotNull Vector4f colorVector) {
        this.colorVector = colorVector;
        this.colors = colors;
    }

    public Colors getColorMode() {
        return this.colors;
    }

    public void setColor(Vector4f color) {
        this.getColorVector().set(color);
    }

    public Vector4f getColorVector() {
        return this.colorVector;
    }

    @Override
    public TagItem copy() {
        return new TagColor(this.getColorMode(), new Vector4f(this.getColorVector()));
    }

    @Override
    public JSONFileManaging.SerializationRules<TagItem> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<TagItem>() {
            @Override
            public String write(TagItem toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                TagColor tagColor = (TagColor) toWrite;
                JsonObject jsonObject = new JsonObject();

                JsonObject object = new JsonObject();
                object.addProperty("x", tagColor.getColorVector().x);
                object.addProperty("y", tagColor.getColorVector().y);
                object.addProperty("z", tagColor.getColorVector().z);
                object.addProperty("w", tagColor.getColorVector().w);
                jsonObject.add("color", object);

                JsonObject transformationsObject = context.serialize(tagColor.getColorMode()).getAsJsonObject();
                jsonObject.add("colors", transformationsObject);

                return jsonObject.toString();
            }

            @Override
            public TagItem read(String readString, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                JsonObject jsonObject = JsonParser.parseString(readString).getAsJsonObject();

                JsonObject object = jsonObject.getAsJsonObject("color");
                float x = object.get("x").getAsFloat();
                float y = object.get("y").getAsFloat();
                float z = object.get("z").getAsFloat();
                float w = object.get("w").getAsFloat();
                Vector4f transformation = new Vector4f(x, y, z, w);

                Colors colors1 = context.deserialize(jsonObject.get("colors"), Colors.class);
                return new TagColor(colors1, transformation);
            }
        };
    }
}
