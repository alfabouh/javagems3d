package api.scripting.coding.env.internal.util.controlling.bind.keys;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.controller.components.FunctionalKey;

@JSCodingClass(binding = "JSFunctionalKey", description = "Key with custom action callbacks.")
public class JSFunctionalKey extends JSKey {

    @JSCodingConstructor(description = "Creates functional key.", paramNames = {"action", "keyCode"})
    public JSFunctionalKey(JSKeyAction action, int keyCode) {
        super(new FunctionalKey(action::onTrigger, keyCode));
    }

    @JSCodingFunctionOrMethod(description = "Returns key action.")
    public JSKeyAction getAction() {
        FunctionalKey fk = (FunctionalKey) this.getJavaKey();
        return new JSKeyAction(fk.getKeyAction());
    }
}