package api.scripting.coding.env.internal.util.controlling.bind;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.bind.keys.JSKey;
import javagems3d.system.controller.binding.DefaultBindings;

@JSCodingClass(binding = "JSDefaultBindings", description = "Default key bindings accessible from JS.")
public class JSDefaultBindings {
    @JSHideFromDoc
    private final DefaultBindings bindings;

    @JSCodingConstructor(description = "Creates default bindings.")
    public JSDefaultBindings() {
        this.bindings = new DefaultBindings();
    }

    @JSCodingFunctionOrMethod(description = "Returns key A (move left).")
    public JSKey getKeyA() { return new JSKey(this.bindings.keyA); }

    @JSCodingFunctionOrMethod(description = "Returns key D (move right).")
    public JSKey getKeyD() { return new JSKey(this.bindings.keyD); }

    @JSCodingFunctionOrMethod(description = "Returns key W (move forward).")
    public JSKey getKeyW() { return new JSKey(this.bindings.keyW); }

    @JSCodingFunctionOrMethod(description = "Returns key S (move backward).")
    public JSKey getKeyS() { return new JSKey(this.bindings.keyS); }

    @JSCodingFunctionOrMethod(description = "Returns jump key.")
    public JSKey getKeyUp() { return new JSKey(this.bindings.keyUp); }

    @JSCodingFunctionOrMethod(description = "Returns crouch key.")
    public JSKey getKeyDown() { return new JSKey(this.bindings.keyDown); }

    @JSCodingFunctionOrMethod(description = "Returns escape key.")
    public JSKey getKeyEsc() { return new JSKey(this.bindings.keyEsc); }

    @JSCodingFunctionOrMethod(description = "Returns selection key (mouse left).")
    public JSKey getKeySelection() { return new JSKey(this.bindings.keySelection); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving left.")
    public JSKey keyMoveLeft() { return new JSKey(this.bindings.keyMoveLeft()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving right.")
    public JSKey keyMoveRight() { return new JSKey(this.bindings.keyMoveRight()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving forward.")
    public JSKey keyMoveForward() { return new JSKey(this.bindings.keyMoveForward()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving backward.")
    public JSKey keyMoveBackward() { return new JSKey(this.bindings.keyMoveBackward()); }

    @JSHideFromDoc
    public DefaultBindings getJavaBindings() {
        return this.bindings;
    }
}