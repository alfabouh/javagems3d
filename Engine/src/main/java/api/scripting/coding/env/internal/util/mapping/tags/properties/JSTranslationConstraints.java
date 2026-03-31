package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

@JSCodingClass(binding = "JSTranslationConstraints", description = "Translation constraints wrapper.")
public class JSTranslationConstraints {

    @JSHideFromDoc
    private final TranslationConstraints constraints;

    @JSCodingConstructor(description = "Create translation constraints.", paramNames = {"position", "rotation", "scaling"})
    public JSTranslationConstraints(JSAxisConstraints pos, JSAxisConstraints rot, JSAxisConstraints scale) {
        this.constraints = new TranslationConstraints(pos.getJava(), rot.getJava(), scale.getJava());
    }

    @JSHideFromDoc
    public TranslationConstraints getJava() {
        return this.constraints;
    }

    @JSCodingFunctionOrMethod(description = "Get position constraints")
    public JSAxisConstraints getPosition() {
        return map(this.constraints.positionConstraints());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation constraints")
    public JSAxisConstraints getRotation() {
        return map(this.constraints.rotationConstraints());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling constraints")
    public JSAxisConstraints getScaling() {
        return map(this.constraints.scalingConstraints());
    }

    @JSHideFromDoc
    private JSAxisConstraints map(AxisConstraints raw) {
        for (JSAxisConstraints v : JSAxisConstraints.values()) {
            if (v.getJava() == raw) return v;
        }
        return JSAxisConstraints.NONE;
    }
}