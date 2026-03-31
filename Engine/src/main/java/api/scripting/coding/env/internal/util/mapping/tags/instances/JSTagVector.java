package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSVectorMode;
import api.scripting.coding.env.internal.util.math.JSVector4f;
import javagems3d.system.external.mapping.tags.base.VectorMode;
import javagems3d.system.external.mapping.tags.items.TagVector;

@JSCodingClass(binding = "JSTagVector", description = "Vector tag item.")
public class JSTagVector {

    @JSHideFromDoc
    private final TagVector tag;

    @JSCodingConstructor(description = "Create vector tag with mode, values, min and max.")
    public JSTagVector(JSVectorMode mode, JSVector4f values, float min, float max) {
        this.tag = new TagVector(mode != null ? mode.getJava() : VectorMode.VEC4F, values.getJavaVector4f(), min, max);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagVector.")
    public JSTagVector(JSTagItem tagItem) {
        this.tag = (TagVector) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagVector getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set values of the vector tag.", paramNames = {"values"})
    public JSTagVector setValues(JSVector4f values) {
        this.tag.setValue(values.getJavaVector4f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get current vector values.")
    public JSVector4f getValues() {
        return new JSVector4f(this.tag.getValues());
    }

    @JSCodingFunctionOrMethod(description = "Get vector mode of the tag.")
    public VectorMode getVectorMode() {
        return this.tag.getVectorMode();
    }

    @JSCodingFunctionOrMethod(description = "Get minimum allowed value of the vector components.")
    public float getMin() {
        return this.tag.getMin();
    }

    @JSCodingFunctionOrMethod(description = "Get maximum allowed value of the vector components.")
    public float getMax() {
        return this.tag.getMax();
    }

    @JSCodingFunctionOrMethod(description = "Copy the vector tag.")
    public JSTagVector copy() {
        return new JSTagVector(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagVector(" + this.tag.getValues() + ", mode=" + this.tag.getVectorMode() + ", min=" + this.tag.getMin() + ", max=" + this.tag.getMax() + ")";
    }
}