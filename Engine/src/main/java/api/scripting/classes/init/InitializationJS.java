package api.scripting.classes.init;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.scripting.JGemsAPIScriptingManaging;
import api.scripting.classes.init.templates.EntityTemplateJS;
import api.scripting.classes.init.templates.PropTemplateJS;
import api.scripting.doc.annotations.JSCommentary;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    @JSMethodDoc(description = "Registers new entity template in engine system", args = {"group", "entName"}, order = 0)
    public EntityTemplateJS registerEntity(String group, String entName) {
        Map<String, APIWBenchDataManager.TemplatesTable<ResourceEntity>> map = this.getApiwBenchDataManager().getResourceEntityMap();
        APIWBenchDataManager.TemplatesTable<ResourceEntity> templatesTable = map.get(group);
        if (templatesTable != null) {
            if (!templatesTable.getTemplateMap().containsKey(entName)) {
                Log.get().error("API doesn't contain: " + group + "/" + entName);
                return null;
            }
            JGemsEntityData jGemsEntityData = templatesTable.find(entName).getFabricGame().create();
            MeshBuffer meshBuffer = this.getResourceManager().getLocalResources().createMeshBuffer(jGemsEntityData.getPathToModel(), false, false);

            EntityTemplateJS entityTemplateJS = new EntityTemplateJS(group, entName);
            this.getScriptingManaging().getEntityRenderDataMap().put(entityTemplateJS, new EntityRenderData(jGemsEntityData.getEntityRenderData(), meshBuffer));
            return entityTemplateJS;
        } else {
            Log.get().error("API doesn't contain group: " + group);
        }
        return null;
    }

    @JSCommentary(commentary = "This method registers a new prop template, which is later used to instantiate objects in the game world. The template is defined based on components registered via the Java API - IAPIWBenchDataManager.")
    @JSMethodDoc(description = "Registers new prop template in engine system", args = {"group", "propName"}, order = 1)
    public PropTemplateJS registerProp(String group, String propName) {
        Map<String, APIWBenchDataManager.TemplatesTable<ResourceProp>> map = this.getApiwBenchDataManager().getResourcePropMap();
        APIWBenchDataManager.TemplatesTable<ResourceProp> templatesTable = map.get(group);
        if (templatesTable != null) {
            if (!templatesTable.getTemplateMap().containsKey(propName)) {
                Log.get().error("API doesn't contain: " + group + "/" + propName);
                return null;
            }
            JGemsPropData jGemsPropData = templatesTable.find(propName).getFabricGame().create();
            MeshBuffer meshBuffer = this.getResourceManager().getLocalResources().createMeshBuffer(jGemsPropData.getPathToModel(), false, false);

            PropTemplateJS propTemplateJS = new PropTemplateJS(group, propName);
            this.getScriptingManaging().getPropRenderDataMap().put(propTemplateJS, new PropRenderData(jGemsPropData.getPropRenderData(), meshBuffer));
            return propTemplateJS;
        } else {
            Log.get().error("API doesn't contain group: " + group);
        }
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