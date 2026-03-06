package api.scripting.legacy.classes.world.environment;

import api.scripting.legacy.doc.annotations.JSMethodDoc;
import api.scripting.legacy.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Environment object. Contains fog and sky systems", priority = JSTypeDoc.Priority.MED)
public final class EnvironmentJS {
    private final FogJS fogJS;
    private final SkyJS skyJS;

    public EnvironmentJS() {
        this.fogJS = new FogJS();
        this.skyJS = new SkyJS();
    }

    @JSMethodDoc(description = "Get fog control", args = {}, order = 0)
    public FogJS getFogJS() {
        return this.fogJS;
    }

    @JSMethodDoc(description = "Get sky control", args = {}, order = 1)
    public SkyJS getSkyJS() {
        return this.skyJS;
    }
}
