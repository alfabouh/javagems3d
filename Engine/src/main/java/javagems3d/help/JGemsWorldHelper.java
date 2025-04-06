package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.physics.world.triggers.zones.base.ITriggerZone;
import javagems3d.system.service.graph.Graph;
import javagems3d.system.navigation.pathgen.MapNavGraphGenerator;
import org.joml.Vector3f;

public abstract class JGemsWorldHelper {
    public static Graph genSimpleMapGraphFromStartPoint(Vector3f start) {
        return MapNavGraphGenerator.createGraphWithStartPoint(JGems3D.get().getPhysics().getPhysicsProcessor().getDynamicsSystem(), DynamicsUtils.convertV3F_JME(start));
    }

    public static void removePropFromScene(SceneProp sceneProp) {
        JGems3D.get().getScreen().getScene().getWorld().removeObjectFromWorld(sceneProp);
    }

    public static void addPropInScene(SceneProp sceneProp) {
        JGems3D.get().getScreen().getScene().getWorld().addObjectInWorld(sceneProp);
    }

    public static void removeItemFromWorld(WorldItem worldItem) {
        worldItem.setDead();
    }

    public static void addItemInWorld(WorldItem worldItem, EntityRenderData renderData) {
        JGemsCoreHelper.getPhysicsWorld().addItem(worldItem);
        JGemsCoreHelper.getSceneWorld().addItem(worldItem, renderData);
    }

    public static void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
        addLight(worldItem, light);
        JGemsEnvironmentHelper.getWorldEnvironment().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
    }

    public static void addPointLight(PointLight light, int attachShadowScene) {
        addLight(light);
        JGemsEnvironmentHelper.getWorldEnvironment().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
    }

    public static void addLiquid(Liquid liquid, LiquidRenderData liquidRenderData) {
        JGemsCoreHelper.getPhysicsWorld().addItem(liquid);
        JGemsCoreHelper.getSceneWorld().addLiquid(liquid, liquidRenderData);
    }

    public static void addTriggerZone(ITriggerZone triggerZone) {
        JGemsCoreHelper.getPhysicsWorld().addItem(triggerZone);
    }

    public static void addLight(WorldItem worldItem, Light light) {
        JGemsCoreHelper.getSceneWorld().addWorldItemLight(worldItem, light);
    }

    public static void addLight(Light light) {
        light.on();
        JGemsEnvironmentHelper.getWorldEnvironment().getLightManager().addLight(light);
    }
}
