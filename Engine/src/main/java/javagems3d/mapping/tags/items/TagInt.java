package javagems3d.mapping.tags.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class TagInt extends TagItem {
    public static final String TYPE_STRING = "TagInt";

    private int value;
    private final int min;
    private final int max;

    public TagInt(int value, int min, int max) {
        super(TagInt.TYPE_STRING);
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
}
