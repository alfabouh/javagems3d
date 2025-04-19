package api.scripting.classes.world.objects;

import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.physics.world.basic.WorldItem;

public abstract class UtilsJS {
    public static PointLight getPointlight(PointLightJS pointLightJS) {
        return pointLightJS.getPointLight();
    }

    public static WorldItem getWorldItem(EntityJS entityJS) {
        return entityJS.getWorldItem();
    }

    public static SceneProp getSceneProp(PropJS propJS) {
        return propJS.getSceneObject();
    }

    public static Object getObject(UnknownObjectJS unknownObjectJS) {
        return unknownObjectJS.getObject();
    }
}
