package api.scripting.coding.env.internal.util.controlling.bind.keys;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.controller.components.IKeyAction;

@JSCodingClass(binding = "JSKeyAction", description = "Represents key action callback.")
public class JSKeyAction {
    @JSHideFromDoc
    private final IKeyAction action;

    @JSCodingConstructor(description = "Creates key action.", paramNames = {"consumer"})
    public JSKeyAction(java.util.function.Consumer<String> consumer) {
        this.action = keyAction -> consumer.accept(keyAction.name());
    }

    @JSHideFromDoc
    public JSKeyAction(IKeyAction action) {
        this.action = action;
    }

    @JSHideFromDoc
    public void onTrigger(IKeyAction.KeyAction keyAction) {
        this.action.onTrigger(keyAction);
    }

    @JSHideFromDoc
    public IKeyAction getJavaAction() {
        return this.action;
    }
}