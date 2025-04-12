package api.scripting.classes.init;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.scripting.JGemsAPIScriptingManaging;
import api.scripting.classes.init.templates.EntityTemplateJS;
import api.scripting.classes.init.templates.PropTemplateJS;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class InitializationJS {
    private final JGemsAPIScriptingManaging scriptingManaging;
    private final APIWBenchDataManager apiwBenchDataManager;
    private final JGemsResourceManager jGemsResourceManager;

    public InitializationJS(@NotNull JGemsResourceManager resourceManager, @NotNull APIWBenchDataManager dataManager, @NotNull JGemsAPIScriptingManaging scriptingManaging) {
        this.jGemsResourceManager = resourceManager;
        this.scriptingManaging = scriptingManaging;
        this.apiwBenchDataManager = dataManager;
    }

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

    public PropTemplateJS registerProp(String group, String entRawProp) {
        Map<String, APIWBenchDataManager.TemplatesTable<ResourceProp>> map = this.getApiwBenchDataManager().getResourcePropMap();
        APIWBenchDataManager.TemplatesTable<ResourceProp> templatesTable = map.get(group);
        if (templatesTable != null) {
            if (!templatesTable.getTemplateMap().containsKey(entRawProp)) {
                Log.get().error("API doesn't contain: " + group + "/" + entRawProp);
                return null;
            }
            JGemsPropData jGemsPropData = templatesTable.find(entRawProp).getFabricGame().create();
            MeshBuffer meshBuffer = this.getResourceManager().getLocalResources().createMeshBuffer(jGemsPropData.getPathToModel(), false, false);

            PropTemplateJS propTemplateJS = new PropTemplateJS(group, entRawProp);
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