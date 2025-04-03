package javagems3d.mapping.tags.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;

import java.lang.reflect.Type;

public class TagCheckBoolean implements TagItem {
    private boolean flag;

    public TagCheckBoolean(boolean flag) {
        this.flag = flag;
    }

    public TagCheckBoolean setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public TagItem copy() {
        return new TagCheckBoolean(this.isFlag());
    }

    @Override
    public JSONFileManaging.SerializationRules<TagItem> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<TagItem>() {
            @Override
            public String write(TagItem toWrite, Type typeOfSrc, JsonSerializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                TagCheckBoolean tagCheckBoolean = (TagCheckBoolean) toWrite;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("flag", tagCheckBoolean.isFlag());
                return jsonObject.toString();
            }

            @Override
            public TagItem read(String readString, Type typeOfT, JsonDeserializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                JsonObject jsonObject = JsonParser.parseString(readString).getAsJsonObject();
                boolean flag = jsonObject.get("flag").getAsBoolean();
                return new TagCheckBoolean(flag);
            }
        };
    }
}
