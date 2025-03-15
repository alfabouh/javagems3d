package javagems3d.mapping.tags;

import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

public final class Tag <T extends TagItem> implements ICopyable<Tag<?>> {
    private final T item;
    private final TagID tagID;

    public Tag(@NotNull TagID tagID, @NotNull T defaultItem) {
        this.item = defaultItem;
        this.tagID = tagID;
    }

    @SuppressWarnings("all")
    public <R extends TagItem> R getTagItemUnsafeCast() {
        return (R) this.getTagItem();
    }

    public boolean check(Class<? extends TagItem> clazz) {
        return this.getTagItem().getClass().isAssignableFrom(clazz);
    }

    public TagID getTagID() {
        return this.tagID;
    }

    public T getTagItem() {
        return this.item;
    }

    @Override
    @SuppressWarnings("all")
    public Tag<T> copy() {
        return new Tag<T>(this.getTagID(), (T) this.getTagItem().copy());
    }
}
