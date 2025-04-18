package api.scripting.classes.global;

import api.scripting.classes.world.*;
import api.scripting.doc.annotations.JSGlobalVar;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.help.JGemsHelper;
import logger.Log;

@JSGlobalVar(varName = "global")
@JSTypeDoc(description = "Global utilities object", priority = JSTypeDoc.Priority.HIGH)
public final class GlobalJS {
    private final GameWorldJS gameWorldJS;

    public GlobalJS(GameWorldJS gameWorldJS) {
        this.gameWorldJS = gameWorldJS;
    }

    @JSMethodDoc(description = "Returns ObjectJS by id", args = {"Entity ID"}, order = 0)
    public ObjectJS getObjectByID(int id) {
        ObjectJS object = this.getGameWorldJS().mapObjectsMap.get(id);
        if (object == null) {
            return null;
        }

        boolean isValid = false;

        if (object instanceof PropJS) {
            PropJS prop = (PropJS) object;
            isValid = JGemsHelper.get().getSceneWorld().contains(prop.getSceneObject());
        } else if (object instanceof EntityJS) {
            EntityJS entity = (EntityJS) object;
            isValid = JGemsHelper.get().getPhysicsWorld().contains(entity.getWorldItem());
        } else if (object instanceof PointLightJS) {
            PointLightJS light = (PointLightJS) object;
            LightScene lightScene = (LightScene) JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene();
            isValid = lightScene.containsPointLight(light.getPointLight());
        }

        if (!isValid) {
            this.getGameWorldJS().mapObjectsMap.remove(id);
            return null;
        }

        return object;
    }

    GameWorldJS getGameWorldJS() {
        return this.gameWorldJS;
    }
}