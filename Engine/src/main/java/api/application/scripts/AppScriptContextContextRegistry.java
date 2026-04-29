package api.application.scripts;

import java.util.HashSet;
import java.util.Set;

public final class AppScriptContextContextRegistry implements IAppScriptContextRegistry {
    public Set<String> globalGameContextScripts;
    public Set<String> gLocalMapContextScripts;

    public AppScriptContextContextRegistry() {
        this.globalGameContextScripts = new HashSet<>();
        this.gLocalMapContextScripts = new HashSet<>();
    }

    @Override
    public void addGlobalGameContextScript(String packageWithScripts) {
        this.globalGameContextScripts.add(packageWithScripts);
    }

    @Override
    public void addLocalMapContextScript(String packageWithScripts) {
        this.gLocalMapContextScripts.add(packageWithScripts);
    }
}
