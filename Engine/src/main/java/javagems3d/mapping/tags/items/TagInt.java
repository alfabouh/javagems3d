package javagems3d.mapping.tags.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;

import java.lang.reflect.Type;

public class TagInt implements TagItem {
    private int value;
    private final int min;
    private final int max;

    public TagInt(int value, int min, int max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagInt setValue(int value) {
        this.value = value;
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagInt(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public JSONFileManaging.SerializationRules<TagItem> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<TagItem>() {
            @Override
            public String write(TagItem toWrite, Type typeOfSrc, JsonSerializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                TagInt tagInt = (TagInt) toWrite;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", tagInt.getValue());
                jsonObject.addProperty("min", tagInt.getMin());
                jsonObject.addProperty("max", tagInt.getMax());
                return jsonObject.toString();
            }

            @Override
            public TagItem read(String readString, Type typeOfT, JsonDeserializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                JsonObject jsonObject = JsonParser.parseString(readString).getAsJsonObject();
                int value = jsonObject.get("value").getAsInt();
                int min = jsonObject.get("min").getAsInt();
                int max = jsonObject.get("max").getAsInt();
                return new TagInt(value, min, max);
            }
        };
    }
}
