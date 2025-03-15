package javagems3d.mapping.tags.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;

import java.lang.reflect.Type;

public class TagFloat implements TagItem {
    private final float value;
    private final float min;
    private final float max;

    public TagFloat(float value, float min, float max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public float getValue() {
        return this.value;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagFloat(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public JSONFileManaging.SerializationRules<TagItem> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<TagItem>() {
            @Override
            public String write(TagItem toWrite, Type typeOfSrc, JsonSerializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                TagFloat tagInt = (TagFloat) toWrite;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", tagInt.getValue());
                jsonObject.addProperty("min", tagInt.getMin());
                jsonObject.addProperty("max", tagInt.getMax());
                return jsonObject.toString();
            }

            @Override
            public TagItem read(String readString, Type typeOfT, JsonDeserializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                JsonObject jsonObject = JsonParser.parseString(readString).getAsJsonObject();
                float value = jsonObject.get("value").getAsFloat();
                float min = jsonObject.get("min").getAsFloat();
                float max = jsonObject.get("max").getAsFloat();
                return new TagFloat(value, min, max);
            }
        };
    }
}
