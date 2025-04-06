package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class TagItem implements ICopyable<TagItem> {
    public static final Map<String, TypeToken<? extends TagItem>> tokensMap = new HashMap<>();

    public static void REGISTER_ALL_TAGS() {
        TagItem.putTypeToken(TagCheckBoolean.TYPE_STRING, new TypeToken<TagCheckBoolean>() {});
        TagItem.putTypeToken(TagColor.TYPE_STRING, new TypeToken<TagColor>() {});
        TagItem.putTypeToken(TagFloat.TYPE_STRING, new TypeToken<TagFloat>() {});
        TagItem.putTypeToken(TagInt.TYPE_STRING, new TypeToken<TagInt>() {});
        TagItem.putTypeToken(TagRadioBoolean.TYPE_STRING, new TypeToken<TagRadioBoolean>() {});
        TagItem.putTypeToken(TagString.TYPE_STRING, new TypeToken<TagString>() {});
    }

    public static void putTypeToken(String typeString, TypeToken<? extends TagItem> token) {
        TagItem.tokensMap.put(typeString, token);
    }

    public static TypeToken<? extends TagItem> getTypeToken(String type) {
        return TagItem.tokensMap.get(type);
    }

    private final String typeString;

    public TagItem(@NotNull String typeString) {
        if (typeString.isEmpty()) {
            throw new JGemsRuntimeException("TagItem " + this.getClass() +" should not have empty type!");
        }
        this.typeString = typeString;
    }

    public String getTypeString() {
        return this.typeString;
    }
}
