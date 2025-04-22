package api.scripting.classes.global;

import api.scripting.classes.global.timer.TimerCallbackJS;
import api.scripting.classes.world.GameWorldJS;
import api.scripting.classes.world.ObjectJS;
import api.scripting.classes.world.objects.*;
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

    @JSMethodDoc(description = "Returns BackgroundPropJS by id", args = {"Entity ID"}, order = 1)
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

    @JSMethodDoc(description = "Creates a new timer with the given ID, duration in seconds, number of repetitions, and a callback to invoke", args = {"id", "seconds", "repeatTimes", "callback"}, order = 2)
    public void pushTimer(String id, int seconds, int repeatTimes, TimerCallbackJS timerCallbackJS) {
        this.getGameWorldJS().getTimerManagerJS().pushTimer(id, seconds, repeatTimes, timerCallbackJS);
    }

    @JSMethodDoc(description = "Stops and removes the timer with the given ID", args = {"id"}, order = 3)
    public void stopTimer(String id) {
        this.getGameWorldJS().getTimerManagerJS().removeTimer(id);
    }

    @JSMethodDoc(description = "Clears all timers", args = {}, order = 4)
    public void clearTimers() {
        this.getGameWorldJS().getTimerManagerJS().clear();
    }

    @JSMethodDoc(description = "Checks if the timer is active", args = {"id"}, order = 5)
    public boolean isTimerActive(String id) {
        return this.getGameWorldJS().getTimerManagerJS().isTimerActive(id);
    }

    GameWorldJS getGameWorldJS() {
        return this.gameWorldJS;
    }
}