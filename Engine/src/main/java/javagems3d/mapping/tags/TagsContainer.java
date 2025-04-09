package javagems3d.mapping.tags;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.service.json.JSONFileManaging;
import logger.Log;

import java.util.*;

public final class TagsContainer {
    static {
        TagItem.REGISTER_ALL_TAGS();
    }

    public static final JSONFileManaging.SerializationRules<TagsContainer> TAGS_CONTAINER_SERIALIZATION_RULE =
            JSONFileManaging.create(
                    (object, context) -> {
                        JsonArray tagsArray = new JsonArray();
                        for (Tag<? extends TagItem> tag : object.getTags().values()) {
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

    private final Map<TagID, Tag<? extends TagItem>> tags;

    public TagsContainer(Map<TagID, Tag<? extends TagItem>> tags) {
        this.tags = tags;
    }

    public TagsContainer(Collection<Tag<? extends TagItem>> tags) {
        this.tags = new HashMap<>();
        for (Tag<? extends TagItem> tag : tags) {
            this.addTag(tag.copy());
        }
    }

    @SuppressWarnings("all")
    public TagsContainer(TagsContainer tagsContainer) {
        this(tagsContainer.getTags().values());
    }

    public TagsContainer() {
        this.tags = new HashMap<>();
    }

    public boolean hasTag(TagID id) {
        return this.getTag(id) != null;
    }

    public void addTag(Tag<? extends TagItem> tag) {
        this.getTags().put(tag.getTagID(), tag);
    }

    public Tag<? extends TagItem> getTag(TagID id) {
        return this.getTags().get(id);
    }

    public Collection<Tag<? extends TagItem>> getTagCollection() {
        return this.getTags().values();
    }

    public Map<TagID, Tag<? extends TagItem>> getTags() {
        return this.tags;
    }
}
