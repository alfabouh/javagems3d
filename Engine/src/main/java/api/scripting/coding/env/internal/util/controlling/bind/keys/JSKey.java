package api.scripting.coding.env.internal.util.controlling.bind.keys;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.controller.components.Key;

@JSCodingClass(binding = "JSKey", description = "Wrapper for a single keyboard key state and its code.")
public class JSKey {
    @JSHideFromDoc
    private final Key key;

    @JSCodingConstructor(description = "Creates a JSKey with a specific key code.", paramNames = {"keyCode"})
    public JSKey(int keyCode) {
        this.key = new Key(keyCode);
    }

    @JSHideFromDoc
    public JSKey(Key keyCode) {
        this.key = keyCode;
    }

    @JSCodingFunctionOrMethod(description = "Refreshes the state of the key (pressed or released).")
    public void refreshState(boolean press) {
        this.key.refreshState(press);
    }

    @JSCodingFunctionOrMethod(description = "Returns true if the key was clicked this frame.")
    public boolean isClicked() {
        return this.key.isClicked();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if the key is currently pressed.")
    public boolean isPressed() {
        return this.key.isPressed();
    }

    @JSCodingFunctionOrMethod(description = "Returns true if the key was released this frame.")
    public boolean isUnpressed() {
        return this.key.isUnpressed();
    }

    @JSCodingFunctionOrMethod(description = "Returns the integer key code.")
    public int getKeyCode() {
        return this.key.getKeyCode();
    }

    @JSCodingFunctionOrMethod(description = "Returns the name of the key (like 'A', 'SPACE', etc.).")
    public String getKeyName() {
        return this.key.getKeyName();
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public Key getJavaKey() {
        return this.key;
    }
}