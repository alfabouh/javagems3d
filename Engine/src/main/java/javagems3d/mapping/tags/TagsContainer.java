package javagems3d.mapping.tags;

import javagems3d.mapping.tags.items.TagItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class TagsContainer {
    private final Map<TagID, Tag<? extends TagItem>> tags;

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

    public void save() {

    }

    public void load() {

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
