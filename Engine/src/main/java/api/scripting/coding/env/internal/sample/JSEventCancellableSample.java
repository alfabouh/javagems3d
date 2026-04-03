package api.scripting.coding.env.internal.sample;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;

@JSCodingClass(binding = "JSEventCancellableSample", description = "...")
public class JSEventCancellableSample implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSEventCancellableSample() {
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSEventCancellableSample";
    }
}