package api.scripting.coding.env.internal.util.controlling.bind;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.bind.keys.JSKey;
import javagems3d.system.controller.binding.Binding;

@JSCodingClass(binding = "JSBinding", description = "Wrapper for a key binding with optional description.")
public class JSBinding {
    @JSHideFromDoc
    private final Binding binding;

    @JSCodingConstructor(description = "Creates a JSBinding for a specific JSKey with description.", paramNames = {"key", "description"})
    public JSBinding(JSKey key, String description) {
        this.binding = Binding.createBinding(key.getJavaKey(), description);
    }

    @JSHideFromDoc
    public JSBinding(Binding binding) {
        this.binding = binding;
    }

    @JSCodingFunctionOrMethod(description = "Sets the key for this binding.")
    public void setKey(JSKey key) {
        this.binding.setKeyToBinding(key.getJavaKey());
    }

    @JSCodingFunctionOrMethod(description = "Returns the JSKey associated with this binding.")
    public JSKey getKey() {
        return new JSKey(this.binding.getKey().getKeyCode());
    }

    @JSCodingFunctionOrMethod(description = "Returns the description of this binding.")
    public String getDescription() {
        return this.binding.getDescription();
    }

    @JSHideFromDoc
    public Binding getJavaBinding() {
        return this.binding;
    }

    @JSCodingFunctionOrMethod(description = "Returns a string representation like 'SPACE - Jump'.")
    public String toString() {
        return this.binding.toString();
    }
}