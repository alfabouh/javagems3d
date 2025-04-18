package api.scripting.classes.world;

import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Abstract game object", priority = JSTypeDoc.Priority.MED)
public abstract class ObjectJS {
    @JSMethodDoc(description = "Returned true, if object is EntityJS", args = {}, order = 0)
    public final boolean isEntityJS() {
        return this instanceof EntityJS;
    }

    @JSMethodDoc(description = "Returned true, if object is PropJS", args = {}, order = 1)
    public final boolean isPropJS() {
        return this instanceof PropJS;
    }

    @JSMethodDoc(description = "Returned true, if object is PointLightJS", args = {}, order = 2)
    public final boolean isPointLightJS() {
        return this instanceof PointLightJS;
    }
}
