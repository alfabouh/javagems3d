package api.scripting.coding.env.internal.util.mapping.row;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.JSTagsContainer;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSRenderProperties;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;

@JSCodingClass(binding = "JSRowMapObjectData", description = "Wrapper for RowMapObjectData representing raw map object data.")
public class JSRowMapObjectData {

    @JSHideFromDoc
    private final RowMapObjectData data;

    @JSCodingConstructor(description = "Wrap existing RowMapObjectData", paramNames = {"data"})
    public JSRowMapObjectData(RowMapObjectData data) {
        this.data = data;
    }

    @JSCodingFunctionOrMethod(description = "Get object ID", paramNames = {})
    public int getId() {
        return this.data.getId();
    }

    @JSCodingFunctionOrMethod(description = "Get object unique string identifier", paramNames = {})
    public String getObjectId() {
        return this.data.getObjectNameId();
    }

    @JSCodingFunctionOrMethod(description = "Get object path", paramNames = {})
    public String getObjectPath() {
        return this.data.getObjectPath();
    }

    @JSCodingFunctionOrMethod(description = "Get object tags container", paramNames = {})
    public JSTagsContainer getTagsContainer() {
        return new JSTagsContainer(this.data.getTagsContainer());
    }

    //TODO
   // @JSCodingFunctionOrMethod(description = "Get object render properties", paramNames = {})
   // public JSRenderProperties getRenderProperties() {
   //     return new JSRenderProperties(this.data.getRenderProperties());
   // }

    @JSCodingFunctionOrMethod(description = "Get object position", paramNames = {})
    public JSVector3f getPosition() {
        return new JSVector3f(this.data.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get object rotation", paramNames = {})
    public JSVector3f getRotation() {
        return new JSVector3f(this.data.getRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get object scaling", paramNames = {})
    public JSVector3f getScaling() {
        return new JSVector3f(this.data.getScaling());
    }

    @JSHideFromDoc
    public RowMapObjectData getJava() {
        return this.data;
    }
}