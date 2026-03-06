package api.scripting.legacy.classes.init;

import api.application.workbench.manager.APIWBenchDataManager;
import api.scripting.legacy.JGemsAPIScriptingManaging;
import api.scripting.legacy.classes.init.templates.EntityTemplateJS;
import api.scripting.legacy.classes.init.templates.PropTemplateJS;
import api.scripting.legacy.doc.annotations.JSCommentary;
import api.scripting.legacy.doc.annotations.JSMethodDoc;
import api.scripting.legacy.doc.annotations.JSTypeDoc;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

@JSTypeDoc(description = "Used to initialize new objects in engine", priority = JSTypeDoc.Priority.HIGH)
public final class InitializationJS {
    private final JGemsAPIScriptingManaging scriptingManaging;
    private final APIWBenchDataManager apiwBenchDataManager;
    private final JGemsResourceManager jGemsResourceManager;

    public InitializationJS(@NotNull JGemsResourceManager resourceManager, @NotNull APIWBenchDataManager dataManager, @NotNull JGemsAPIScriptingManaging scriptingManaging) {
        this.jGemsResourceManager = resourceManager;
        this.scriptingManaging = scriptingManaging;
        this.apiwBenchDataManager = dataManager;
    }

    @JSCommentary(commentary = "This method registers a new entity template, which is later used to instantiate objects in the game world. The template is defined based on components registered via the Java API - IAPIWBenchDataManager.")
    @JSMethodDoc(description = "Registers new entity template in engine system", args = {"util", "entName"}, order = 0)
    public EntityTemplateJS registerEntity(String group, String entName) {
        //Map<String, APIWBenchDataManager.TemplatesTable<ApiResourceEntity>> map = this.getApiwBenchDataManager().getResourceEntityMap();
        //APIWBenchDataManager.TemplatesTable<ApiResourceEntity> templatesTable = map.get(util);
        //if (templatesTable != null) {
        //    if (!templatesTable.getTemplateMap().containsKey(entName)) {
        //        Log.get().error("API doesn't contain: " + util + "/" + entName);
        //        return null;
        //    }
        //    JGemsEntityData jGemsEntityData = templatesTable.find(entName).getFabricGame().create();
        //    MeshBuffer meshBuffer = this.getResourceManager().getLocalResources().createMeshBuffer(JGems3D.GetSource.EXTERNAL, jGemsEntityData.getPathToModel(), false);
        //    EntityTemplateJS entityTemplateJS = new EntityTemplateJS(util, entName);
        //    this.getScriptingManaging().getEntityRenderDataMap().put(entityTemplateJS, new EntityRenderData(jGemsEntityData.getEntityRenderData(), meshBuffer));
        //    return entityTemplateJS;
        //} else {
        //    Log.get().error("API doesn't contain util: " + util);
        //}
        return null;
    }

    @JSCommentary(commentary = "This method registers a new prop template, which is later used to instantiate objects in the game world. The template is defined based on components registered via the Java API - IAPIWBenchDataManager.")
    @JSMethodDoc(description = "Registers new prop template in engine system", args = {"util", "propName"}, order = 1)
    public PropTemplateJS registerProp(String group, String propName) {
        //Map<String, APIWBenchDataManager.TemplatesTable<ApiResourceProp>> map = this.getApiwBenchDataManager().getResourcePropMap();
        //APIWBenchDataManager.TemplatesTable<ApiResourceProp> templatesTable = map.get(util);
        //if (templatesTable != null) {
        //    if (!templatesTable.getTemplateMap().containsKey(propName)) {
        //        Log.get().error("API doesn't contain: " + util + "/" + propName);
        //        return null;
        //    }
        //    JGemsPropData jGemsPropData = templatesTable.find(propName).getFabricGame().create();
        //    MeshBuffer meshBuffer = this.getResourceManager().getLocalResources().createMeshBuffer(JGems3D.GetSource.EXTERNAL, jGemsPropData.getPathToModel(), false);
        //    PropTemplateJS propTemplateJS = new PropTemplateJS(util, propName);
        //    this.getScriptingManaging().getPropRenderDataMap().put(propTemplateJS, new PropRenderData(jGemsPropData.getPropRenderData(), meshBuffer));
        //    return propTemplateJS;
        //} else {
        //    Log.get().error("API doesn't contain util: " + util);
        //}
        return null;
    }

    public JGemsResourceManager getResourceManager() {
        return this.jGemsResourceManager;
    }

    public APIWBenchDataManager getApiwBenchDataManager() {
        return this.apiwBenchDataManager;
    }

    public JGemsAPIScriptingManaging getScriptingManaging() {
        return this.scriptingManaging;
    }
}