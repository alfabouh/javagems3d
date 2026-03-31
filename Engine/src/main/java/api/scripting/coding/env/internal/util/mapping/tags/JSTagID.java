package api.scripting.coding.env.internal.util.mapping.tags;

import api.scripting.coding.env.def.*;
import javagems3d.system.external.mapping.tags.TagID;

@JSCodingClass(binding = "JSTagID", description = "Tag identifier used to describe and categorize tags.")
public class JSTagID {
    @JSHideFromDoc
    private final TagID tagID;

    @JSCodingConstructor(description = "Create TagID with id, description and tooltip.", paramNames = {"id", "description", "toolTip"})
    public JSTagID(String id, String description, String toolTip) {
        this.tagID = new TagID(id, description, toolTip);
    }

    @JSCodingConstructor(description = "Create TagID with id and description.", paramNames = {"id", "description"})
    public JSTagID(String id, String description) {
        this.tagID = new TagID(id, description);
    }

    @JSCodingConstructor(description = "Create TagID with id only.", paramNames = {"id"})
    public JSTagID(String id) {
        this.tagID = new TagID(id);
    }

    @JSCodingConstructor(description = "Copy TagID with new description and tooltip.", paramNames = {"tagID", "description", "toolTip"})
    public JSTagID(JSTagID tagID, String description, String toolTip) {
        this.tagID = new TagID(tagID.getJavaTagID(), description, toolTip);
    }

    @JSCodingConstructor(description = "Copy TagID with new description.", paramNames = {"tagID", "description"})
    public JSTagID(JSTagID tagID, String description) {
        this.tagID = new TagID(tagID.getJavaTagID(), description);
    }

    @JSCodingConstructor(description = "Copy TagID.", paramNames = {"tagID"})
    public JSTagID(JSTagID tagID) {
        this.tagID = new TagID(tagID.getJavaTagID());
    }

    @JSCodingConstructor(description = "Copy TagID.", paramNames = {"tagID"})
    public JSTagID(TagID tagID) {
        this.tagID = new TagID(tagID);
    }

    @JSHideFromDoc
    public TagID getJavaTagID() {
        return this.tagID;
    }

    @JSCodingFunctionOrMethod(description = "Get id string.")
    public String getId() {
        return this.tagID.getId();
    }

    @JSCodingFunctionOrMethod(description = "Get description.")
    public String getDescription() {
        return this.tagID.getDescription();
    }

    @JSCodingFunctionOrMethod(description = "Set description.", paramNames = {"description"})
    public JSTagID setDescription(String description) {
        this.tagID.setDescription(description);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get tooltip.")
    public String getToolTip() {
        return this.tagID.getToolTip();
    }

    @JSCodingFunctionOrMethod(description = "Set tooltip.", paramNames = {"toolTip"})
    public JSTagID setToolTip(String toolTip) {
        this.tagID.setToolTip(toolTip);
        return this;
    }

    @Override
    public String toString() {
        return "JSTagID(" + this.tagID.getId() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSTagID other)) return false;
        return this.tagID.equals(other.tagID);
    }

    @Override
    public int hashCode() {
        return this.tagID.hashCode();
    }
}