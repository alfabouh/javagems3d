package javagems3d.mapping.tags.items;

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;

import java.lang.reflect.Type;

public class TagRadioBoolean implements TagItem {
    private final Info[] values;

    public TagRadioBoolean(Info... values) {
        this.values = values;
    }

    public Info[] getValues() {
        return this.values;
    }

    public Info[] getCopiedValues() {
        Info[] copy = new Info[this.values.length];
        for (int i = 0; i < this.values.length; i++) {
            Info original = this.values[i];
            copy[i] = new Info(original.getName(), original.isFlag());
        }
        return copy;
    }

    @Override
    public TagItem copy() {
        return new TagRadioBoolean(this.getCopiedValues());
    }

    public static final class Info {
        private final String name;
        private boolean flag;

        public Info(String name, boolean flag) {
            this.name = name;
            this.flag = flag;
        }

        public String getName() {
            return this.name;
        }

        public Info setFlag(boolean flag) {
            this.flag = flag;
            return this;
        }

        public boolean isFlag() {
            return this.flag;
        }
    }

    @Override
    public JSONFileManaging.SerializationRules<TagItem> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<TagItem>() {
            @Override
            public String write(TagItem toWrite, Type typeOfSrc, JsonSerializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                TagRadioBoolean tag = (TagRadioBoolean) toWrite;
                JsonArray jsonArray = new JsonArray();
                for (Info info : tag.getValues()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("name", info.getName());
                    jsonObject.addProperty("flag", info.isFlag());
                    jsonArray.add(jsonObject);
                }
                return jsonArray.toString();
            }

            @Override
            public TagItem read(String readString, Type typeOfT, JsonDeserializationContext context, ArbitraryArguments metaData) throws JGemsIOException {
                JsonArray jsonArray = JsonParser.parseString(readString).getAsJsonArray();
                Info[] values = new Info[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    String name = jsonObject.get("name").getAsString();
                    boolean flag = jsonObject.get("flag").getAsBoolean();
                    values[i] = new Info(name, flag);
                }
                return new TagRadioBoolean(values);
            }
        };
    }
}
