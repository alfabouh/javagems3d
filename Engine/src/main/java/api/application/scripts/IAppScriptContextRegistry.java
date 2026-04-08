package api.application.scripts;

public interface IAppScriptContextRegistry {
    void addGlobalGameContextScript(String packageWithScripts);
    void addGLocalMapContextScript(String packageWithScripts);
}
