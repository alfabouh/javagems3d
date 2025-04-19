package api.scripting.classes.world.objects;

import api.scripting.classes.world.ObjectJS;
import api.scripting.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Unknown object", priority = JSTypeDoc.Priority.MED)
public final class UnknownObjectJS extends ObjectJS {
    private final Object object;

    public UnknownObjectJS(Object object) {
        this.object = object;
    }

    Object getObject() {
        return this.object;
    }
}
