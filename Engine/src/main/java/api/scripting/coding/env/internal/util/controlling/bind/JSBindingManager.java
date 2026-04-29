package api.scripting.coding.env.internal.util.controlling.bind;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.bind.keys.JSKey;
import javagems3d.system.controller.binding.BindingManager;

@JSCodingClass(binding = "JSBindingManager", description = "Manages a set of key bindings and provides access to movement keys.")
public class JSBindingManager {
    @JSHideFromDoc
    protected final BindingManager manager;

    @JSCodingConstructor(description = "Wraps an existing BindingManager.", paramNames = {"manager"})
    public JSBindingManager(BindingManager manager) {
        this.manager = manager;
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move left.")
    public JSKey keyMoveLeft() {
        return new JSKey(this.manager.keyMoveLeft());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move right.")
    public JSKey keyMoveRight(){
        return new JSKey(this.manager.keyMoveRight());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move forward.")
    public JSKey keyMoveForward(){
        return new JSKey(this.manager.keyMoveForward());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move backward.")
    public JSKey keyMoveBackward(){
        return new JSKey(this.manager.keyMoveBackward());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move up.")
    public JSKey keyMoveUp(){
        return new JSKey(this.manager.keyMoveUp());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move down.")
    public JSKey keyMoveDown(){
        return new JSKey(this.manager.keyMoveDown());
    }

    @JSCodingFunctionOrMethod(description = "Adds a new binding.")
    public void addBinding(JSBinding binding) {
        this.manager.addBinding(binding.getJavaBinding());
    }

    @JSCodingFunctionOrMethod(description = "Removes a binding by JSKey.")
    public void removeBinding(JSKey key) {
        this.manager.removeBinding(key.getJavaKey());
    }

    @JSCodingFunctionOrMethod(description = "Returns all bindings managed by this manager.")
    public JSBinding[] getBindings() {
        return this.manager.getBindingSet().stream().map(JSBinding::new).toArray(JSBinding[]::new);
    }

    @JSHideFromDoc
    public BindingManager getJavaManager() {
        return this.manager;
    }
}