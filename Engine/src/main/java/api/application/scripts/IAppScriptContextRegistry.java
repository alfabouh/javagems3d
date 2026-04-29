package api.application.scripts;

public interface IAppScriptContextRegistry {
    void addGlobalGameContextScript(String packageWithScripts);
    void addLocalMapContextScript(String packageWithScripts);
}
