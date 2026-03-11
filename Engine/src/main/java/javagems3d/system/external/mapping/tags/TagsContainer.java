package javagems3d.system.external.mapping.tags;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.json.JSONFileManaging;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record TagsContainer(Map<TagID, Tag<? extends TagItem>> tags) implements ICopyable<TagsContainer> {
    public static @NotNull Map<Class<?>, JSONFileManaging.SerializationRules<?>> TAG_ITEMS_SERIALIZATION_RULES = new HashMap<>();

    static {
        TagItem.REGISTER_ALL_TAGS();
    }

    public static void addLazyItemSerializationRule(TagItem tagItem) {
        if (tagItem.getSerializationRule() != null) {
            TagsContainer.TAG_ITEMS_SERIALIZATION_RULES.put(tagItem.getClass(), tagItem.getSerializationRule());
        }
    }

    public static JSONFileManaging createJSONFileManaging() {
        final JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules(new Pair<>(TagsContainer.class, TagsContainer.TAGS_CONTAINER_SERIALIZATION_RULE));
        for (Map.Entry<Class<?>, JSONFileManaging.SerializationRules<?>> pair : TagsContainer.TAG_ITEMS_SERIALIZATION_RULES.entrySet()) {
            jsonFileManaging.setMatchUnsafe(pair.getKey(), pair.getValue());
        }
        return jsonFileManaging;
    }

    public static final JSONFileManaging.SerializationRules<TagsContainer> TAGS_CONTAINER_SERIALIZATION_RULE =
            JSONFileManaging.createSerializationRules(
                    (object, context) -> {
                        JsonArray tagsArray = new JsonArray();
                        for (Tag<? extends TagItem> tag : object.tags().values()) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("type", tag.getTagItem().getTypeString());
                            jsonObject.add("tag", context.serialize(tag.getTagItem(), tag.getTagItem().getClass()));
                            jsonObject.add("tagID", context.serialize(tag.getTagID(), TagID.class));
                            tagsArray.add(jsonObject);
                        }
                        return tagsArray;
                    },
                    (json, context) -> {
                        JsonArray tagsArray = json.getAsJsonArray();
                        Set<Tag<? extends TagItem>> tagsSet = new HashSet<>();

                        for (JsonElement element : tagsArray) {
                            JsonObject tagObj = element.getAsJsonObject();
                            String type = tagObj.get("type").getAsString();

                            TypeToken<? extends TagItem> token = TagItem.getTypeToken(type);

                            if (token == null) {
                                Log.get().error("Couldn't create tag: " + type);
                                continue;
                            }

                            TagItem tagItem = context.deserialize(tagObj.get("tag"), token.getType());
                            TagID tagID = context.deserialize(tagObj.get("tagID"), TagID.class);

                            Tag<TagItem> tag = new Tag<>(tagID, tagItem);
                            tagsSet.add(tag);
                        }

                        return new TagsContainer(tagsSet);
                    }
            );

    public TagsContainer(Collection<Tag<? extends TagItem>> tags) {
        this(new HashMap<>());
        for (Tag<? extends TagItem> tag : tags) {
            this.addTag(tag.copy());
        }
    }

    @SuppressWarnings("all")
    public TagsContainer(TagsContainer tagsContainer) {
        this(tagsContainer.tags().values());
    }

    public TagsContainer() {
        this(new HashMap<>());
    }

    public boolean hasTag(TagID id) {
        return this.getTag(id) != null;
    }

    public boolean isEmpty() {
        return this.tags().isEmpty();
    }

    public TagsContainer replaceTag(TagID id, TagItem newValue) {
        this.tags().replace(id, new Tag<>(id, newValue));
        return this;
    }

    public TagsContainer removeTag(TagID id) {
        this.tags().remove(id);
        return this;
    }

    public TagsContainer addTag(Tag<? extends TagItem> tag) {
        this.tags().put(tag.getTagID(), tag);
        return this;
    }

    public TagsContainer copyTagsFrom(@NotNull TagsContainer tagsContainer) {
        this.tags().putAll(tagsContainer.tags());
        return this;
    }

    public Tag<? extends TagItem> getTag(TagID id) {
        return this.tags().get(id);
    }

    public <T extends TagItem> T getTagItem(TagID id) {
        if (!this.hasTag(id)) {
            return null;
        }
        return this.tags().get(id).getTagItemUnsafeCast();
    }

    public Collection<Tag<? extends TagItem>> getTagCollection() {
        return this.tags().values();
    }

    public TagsContainer copy() {
        return new TagsContainer(this);
    }
}
