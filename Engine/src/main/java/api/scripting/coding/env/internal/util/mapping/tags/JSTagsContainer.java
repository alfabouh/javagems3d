package api.scripting.coding.env.internal.util.mapping.tags;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.instances.JSTag;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.items.TagItem;

import java.util.Collection;

@JSCodingClass(binding = "JSTagsContainer", description = "Container for storing and managing tags.")
public class JSTagsContainer {
    @JSHideFromDoc
    private final TagsContainer container;

    @JSCodingConstructor(description = "Create empty tags container.", paramNames = {})
    public JSTagsContainer() {
        this.container = new TagsContainer();
    }

    @JSHideFromDoc
    public JSTagsContainer(TagsContainer other) {
        this.container = other;
    }

    @JSCodingConstructor(description = "Copy tags container.", paramNames = {"other"})
    public JSTagsContainer(JSTagsContainer other) {
        this.container = new TagsContainer(other.getJavaContainer());
    }

    @JSHideFromDoc
    public TagsContainer getJavaContainer() {
        return this.container;
    }

    @JSCodingFunctionOrMethod(description = "Check if container has tag.", paramNames = {"tagID"})
    public boolean hasTag(JSTagID tagID) {
        return this.container.hasTag(tagID.getJavaTagID());
    }

    @JSCodingFunctionOrMethod(description = "Check if container is empty.")
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @JSCodingFunctionOrMethod(description = "Add tag.", paramNames = {"tag"})
    public JSTagsContainer addTag(JSTag tag) {
        this.container.addTag(tag.getJavaTag());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Remove tag.", paramNames = {"tagID"})
    public JSTagsContainer removeTag(JSTagID tagID) {
        this.container.removeTag(tagID.getJavaTagID());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Replace tag value.", paramNames = {"tagID", "newItem"})
    public JSTagsContainer replaceTag(JSTagID tagID, JSTagItem newItem) {
        this.container.replaceTag(tagID.getJavaTagID(), newItem.getJavaTagItem());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get tag.", paramNames = {"tagID"})
    public JSTag getTag(JSTagID tagID) {
        Tag<? extends TagItem> tag = this.container.getTag(tagID.getJavaTagID());
        if (tag == null) return null;
        return new JSTag(new JSTagID(tag.getTagID()), new JSTagItem(tag.getTagItem()));
    }

    @JSCodingFunctionOrMethod(description = "Get tag item.", paramNames = {"tagID"})
    public JSTagItem getTagItem(JSTagID tagID) {
        TagItem item = this.container.getTagItem(tagID.getJavaTagID());
        if (item == null) return null;
        return new JSTagItem(item);
    }

    @JSCodingFunctionOrMethod(description = "Copy all tags from another container.", paramNames = {"other"})
    public JSTagsContainer copyFrom(JSTagsContainer other) {
        this.container.copyTagsFrom(other.getJavaContainer());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get all tags as array.")
    public JSTag[] getAllTags() {
        Collection<Tag<? extends TagItem>> values = this.container.getTagCollection();
        JSTag[] result = new JSTag[values.size()];
        int i = 0;
        for (Tag<? extends TagItem> tag : values) {
            result[i++] = new JSTag(new JSTagID(tag.getTagID()), new JSTagItem(tag.getTagItem()));
        }
        return result;
    }

    @JSCodingFunctionOrMethod(description = "Copy container.")
    public JSTagsContainer copy() {
        return new JSTagsContainer(this);
    }

    @Override
    public String toString() {
        return "JSTagsContainer(size=" + this.container.tags().size() + ")";
    }
}