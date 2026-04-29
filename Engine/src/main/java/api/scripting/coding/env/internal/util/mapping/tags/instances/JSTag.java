package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.JSTagID;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.items.TagItem;

@JSCodingClass(binding = "JSTag", description = "Generic tag wrapper containing TagItem and TagID.")
public class JSTag {
    @JSHideFromDoc
    private final Tag<? extends TagItem> tag;

    @JSCodingConstructor(description = "Create tag with id and default item.", paramNames = {"tagID", "defaultItem"})
    public JSTag(JSTagID tagID, JSTagItem defaultItem) {
        this.tag = new Tag<>(tagID.getJavaTagID(), defaultItem.getJavaTagItem());
    }

    @JSCodingFunctionOrMethod(description = "Create tag.", paramNames = {"tagID", "defaultItem"})
    public static JSTag create(JSTagID tagID, JSTagItem defaultItem) {
        return new JSTag(tagID, defaultItem);
    }

    @JSHideFromDoc
    public Tag<? extends TagItem> getJavaTag() {
        return this.tag;
    }

    @JSCodingFunctionOrMethod(description = "Get tag ID.")
    public JSTagID getTagID() {
        return new JSTagID(this.tag.getTagID());
    }

    @JSCodingFunctionOrMethod(description = "Get tag item.")
    public JSTagItem getTagItem() {
        return new JSTagItem(this.tag.getTagItem());
    }

    @JSCodingFunctionOrMethod(description = "Check tag item type.", paramNames = {"className"})
    public boolean check(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return this.tag.getTagItem().getClass().isAssignableFrom(clazz);
        } catch (Exception e) {
            return false;
        }
    }

    @JSCodingFunctionOrMethod(description = "Copy tag.")
    public JSTag copy() {
        return new JSTag(new JSTagID(this.tag.getTagID()), new JSTagItem(this.tag.getTagItem().copy()));
    }

    @Override
    public String toString() {
        return "JSTag(" + this.tag.getTagID().getId() + ")";
    }
}