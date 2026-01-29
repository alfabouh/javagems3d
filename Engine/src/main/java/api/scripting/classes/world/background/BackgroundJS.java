package api.scripting.classes.world.background;

import api.scripting.JGemsAPIScriptingManaging;
import api.scripting.classes.init.templates.PropTemplateJS;
import api.scripting.classes.util.Vec3f;
import api.scripting.classes.world.GameWorldJS;
import api.scripting.classes.world.objects.BackgroundPropJS;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import api.scripting.functions.APIScriptsListing;
import api.system.JGemsAPI;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.help.JGemsHelper;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JSTypeDoc(description = "Background scenes object", priority = JSTypeDoc.Priority.HIGH)
public final class BackgroundJS {
    public final Map<Integer, BackgroundPropJS> mapObjectsIdMap;
    private final JGemsAPIScriptingManaging scriptingManaging;
    private final GameWorldJS gameWorldJS;

    public BackgroundJS(@NotNull GameWorldJS gameWorldJS, @NotNull JGemsAPIScriptingManaging scriptingManaging) {
        this.mapObjectsIdMap = new HashMap<>();
        this.scriptingManaging = scriptingManaging;
        this.gameWorldJS = gameWorldJS;
    }

    public void clear() {
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onBackgroundClear, this.getGameWorldJS(), this);
        this.mapObjectsIdMap.clear();
    }

    @JSMethodDoc(description = "Spawn prop(decoration) in background", args = {"propTemplateJS", "position", "rotation", "scaling"}, order = 0)
    public BackgroundPropJS spawnBackgroundProp(PropTemplateJS propTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        if (propTemplateJS == null) {
            Log.get().warn("Tried to spawn NULL prop from script");
            return null;
        }

        final PropRenderData propRenderData = this.getScriptingManaging().getPropRenderDataMap().get(propTemplateJS);
        final SceneProp sceneProp = new SceneWorldProp(propTemplateJS.getName(), JGemsHelper.get().getSceneWorld(), propRenderData);
        JGemsHelper.world().getEnvironment().getSkyBox().getBackground().addObject(sceneProp);
        if (sceneProp.hasModel()) {
            sceneProp.getModel().getPose().setPosition(position.createJOML());
            sceneProp.getModel().getPose().setRotation(rotation.createJOML());
            sceneProp.getModel().getPose().setScaling(scaling.createJOML());
        }
        return new BackgroundPropJS(sceneProp);
    }

    @JSMethodDoc(description = "Remove prop from background", args = {"backgroundPropJS"}, order = 1)
    public void removeProp(BackgroundPropJS backgroundPropJS) {
        backgroundPropJS.remove();
    }

    public void onMapSpawnedBackgroundPropEvent(@NotNull SceneProp sceneProp, int templateId) {
        final BackgroundPropJS propJS = new BackgroundPropJS(sceneProp);
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapSpawnedBackgroundProp, this.getGameWorldJS(), this, propJS);
        this.mapObjectsIdMap.put(templateId, propJS);
    }

    GameWorldJS getGameWorldJS() {
        return this.gameWorldJS;
    }

    JGemsAPIScriptingManaging getScriptingManaging() {
        return this.scriptingManaging;
    }
}