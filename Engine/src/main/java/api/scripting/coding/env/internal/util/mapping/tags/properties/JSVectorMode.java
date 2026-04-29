package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.VectorMode;

@JSCodingClass(binding = "JSVectorMode", description = "Vector mode enum.")
public enum JSVectorMode {

    @JSCodingField(description = "2D vector (vec2f)")
    VEC2F(VectorMode.VEC2F),

    @JSCodingField(description = "3D vector (vec3f)")
    VEC3F(VectorMode.VEC3F),

    @JSCodingField(description = "4D vector (vec4f)")
    VEC4F(VectorMode.VEC4F);

    @JSHideFromDoc
    private final VectorMode mode;

    @JSHideFromDoc
    JSVectorMode(VectorMode mode) {
        this.mode = mode;
    }

    @JSHideFromDoc
    public VectorMode getJava() {
        return this.mode;
    }
}