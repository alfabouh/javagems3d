package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class TagItem implements ICopyable<TagItem> {
    public static final Map<String, TypeToken<? extends TagItem>> tokensMap = new HashMap<>();

    public static void REGISTER_ALL_TAGS() {
        TagItem.putTypeToken(TagCheckBoolean.TYPE_STRING, new TypeToken<TagCheckBoolean>() {});
        TagItem.putTypeToken(TagColor.TYPE_STRING, new TypeToken<TagColor>() {});
        TagItem.putTypeToken(TagFloat.TYPE_STRING, new TypeToken<TagFloat>() {});
        TagItem.putTypeToken(TagInt.TYPE_STRING, new TypeToken<TagInt>() {});
        TagItem.putTypeToken(TagRadioBoolean.TYPE_STRING, new TypeToken<TagRadioBoolean>() {});
        TagItem.putTypeToken(TagString.TYPE_STRING, new TypeToken<TagString>() {});
        TagItem.putTypeToken(TagObjectsList.TYPE_STRING, new TypeToken<TagObjectsList>() {});
        TagItem.putTypeToken(TagVector.TYPE_STRING, new TypeToken<TagVector>() {});

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
            throw new JGemsRuntimeException("TagItem " + this.getClass() +" should not have empty type");
        }
        this.typeString = typeString;
    }

    public abstract void ImGuiRendering(TagsContainer tagsContainer, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet);

    public String getTypeString() {
        return this.typeString;
    }
}
