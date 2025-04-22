package api.application.workbench.resources.data.wbench;

import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.mapping.tags.items.TagItem;

public abstract class WBenchData {
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchData(TagsContainer tagsContainer, TranslationConstraints translationConstraints) {
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
    }

    @SuppressWarnings("all")
    public WBenchData addTags(Tag<? extends TagItem>... tags) {
        for (Tag<? extends TagItem> tag : tags) {
            this.addTag(tag);
        }
        return this;
    }

    public WBenchData addTag(Tag<? extends TagItem> tag) {
        this.getTagsContainer().addTag(tag);
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }
}
