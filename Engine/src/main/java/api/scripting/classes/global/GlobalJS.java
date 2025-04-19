package api.scripting.classes.global;

import api.scripting.classes.world.*;
import api.scripting.classes.world.objects.*;
import api.scripting.classes.world.ObjectJS;
import api.scripting.doc.annotations.JSGlobalVar;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.help.JGemsHelper;

@JSGlobalVar(varName = "global")
@JSTypeDoc(description = "Global utilities object", priority = JSTypeDoc.Priority.HIGH)
public final class GlobalJS {
    private final GameWorldJS gameWorldJS;

    public GlobalJS(GameWorldJS gameWorldJS) {
        this.gameWorldJS = gameWorldJS;
    }

    @JSMethodDoc(description = "Returns ObjectJS by id", args = {"Entity ID"}, order = 0)
    public ObjectJS getMapObjectByID(int id) {
        ObjectJS object = this.getGameWorldJS().mapObjectsIdMap.get(id);
        if (object == null) {
            return null;
        }

        boolean isValid = false;

        if (object instanceof PropJS) {
            PropJS prop = (PropJS) object;
            isValid = JGemsHelper.get().getSceneWorld().contains(UtilsJS.getSceneProp(prop));
        } else if (object instanceof EntityJS) {
            EntityJS entity = (EntityJS) object;
            isValid = JGemsHelper.get().getPhysicsWorld().contains(UtilsJS.getWorldItem(entity));
        } else if (object instanceof PointLightJS) {
            PointLightJS light = (PointLightJS) object;
            LightScene lightScene = (LightScene) JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene();
            isValid = lightScene.containsPointLight(UtilsJS.getPointlight(light));
        }

        if (!isValid) {
            this.getGameWorldJS().mapObjectsIdMap.remove(id);
            return null;
        }

        return object;
    }

    @JSMethodDoc(description = "Returns BackgroundPropJS by id", args = {"Entity ID"}, order = 0)
    public BackgroundPropJS getBackgroundPropByID(int id) {
        BackgroundPropJS object = this.getGameWorldJS().getBackgroundJS().mapObjectsIdMap.get(id);
        if (object == null) {
            return null;
        }

        if (!JGemsHelper.get().getSceneWorld().getEnvironment().getSkyBox().getBackground().contains(UtilsJS.getSceneProp(object))) {
            this.getGameWorldJS().mapObjectsIdMap.remove(id);
            return null;
        }

        return object;
    }

    GameWorldJS getGameWorldJS() {
        return this.gameWorldJS;
    }
}