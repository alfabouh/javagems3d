package api.scripting.classes.world;

import api.scripting.JGemsAPIScriptingManaging;
import api.scripting.classes.global.timer.TimerManagerJS;
import api.scripting.classes.init.templates.EntityTemplateJS;
import api.scripting.classes.init.templates.PropTemplateJS;
import api.scripting.classes.util.Vec3f;
import api.scripting.classes.world.background.BackgroundJS;
import api.scripting.classes.world.environment.EnvironmentJS;
import api.scripting.classes.world.objects.EntityJS;
import api.scripting.classes.world.objects.PointLightJS;
import api.scripting.classes.world.objects.PropJS;
import api.scripting.classes.world.objects.UtilsJS;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import api.scripting.functions.APIScriptsListing;
import api.system.JGemsAPI;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.basic.WorldItem;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JSTypeDoc(description = "Game World. General realization of the scene and physics world", priority = JSTypeDoc.Priority.HIGH)
public final class GameWorldJS extends ObjectJS {
    public final Map<Integer, ObjectJS> mapObjectsIdMap;
    private final JGemsAPIScriptingManaging scriptingManaging;
    private final EnvironmentJS environmentJS;
    private final BackgroundJS backgroundJS;
    private final TimerManagerJS timerManagerJS;

    public GameWorldJS(@NotNull JGemsAPIScriptingManaging scriptingManaging) {
        this.scriptingManaging = scriptingManaging;
        this.mapObjectsIdMap = new HashMap<>();
        this.environmentJS = new EnvironmentJS();
        this.backgroundJS = new BackgroundJS(this, scriptingManaging);
        this.timerManagerJS = new TimerManagerJS();
    }

    public void clear() {
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapClear, this);
        this.getBackgroundJS().clear();
        this.mapObjectsIdMap.clear();
    }

    @JSMethodDoc(description = "Get background control", args = {}, order = -2)
    public BackgroundJS getBackgroundJS() {
        return this.backgroundJS;
    }

    @JSMethodDoc(description = "Get environment control", args = {}, order = -1)
    public EnvironmentJS getEnvironmentJS() {
        return this.environmentJS;
    }

    @JSMethodDoc(description = "Spawn static(non-moving) entity in physics world", args = {"entityTemplateJS", "position", "rotation", "scaling"}, order = 0)
    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        return this.spawnEntity(entityTemplateJS, true, position, rotation, scaling);
    }

    @JSMethodDoc(description = "Spawn dynamic(moving) entity in physics world", args = {"entityTemplateJS", "position", "rotation", "scaling"}, order = 1)
    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        return this.spawnEntity(entityTemplateJS, false, position, rotation, scaling);
    }

    @JSMethodDoc(description = "Spawn static(non-moving) entity in physics world", args = {"entityTemplateJS", "position", "rotation"}, order = 2)
    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation) {
        return this.spawnEntity(entityTemplateJS, true, position, rotation, new Vec3f(1.0f, 1.0f, 1.0f));
    }

    @JSMethodDoc(description = "Spawn dynamic(moving) entity in physics world", args = {"entityTemplateJS", "position", "rotation"}, order = 3)
    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation) {
        return this.spawnEntity(entityTemplateJS, false, position, rotation, new Vec3f(1.0f, 1.0f, 1.0f));
    }

    @JSMethodDoc(description = "Spawn static(non-moving) entity in physics world", args = {"entityTemplateJS", "position"}, order = 4)
    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position) {
        return this.spawnEntity(entityTemplateJS, true, position, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    @JSMethodDoc(description = "Spawn dynamic(moving) entity in physics world", args = {"entityTemplateJS", "position"}, order = 5)
    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position) {
        return this.spawnEntity(entityTemplateJS, false, position, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    @JSMethodDoc(description = "Spawn static(non-moving) entity in physics world", args = {"entityTemplateJS"}, order = 6)
    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS) {
        return this.spawnEntity(entityTemplateJS, true, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    @JSMethodDoc(description = "Spawn dynamic(moving) entity in physics world", args = {"entityTemplateJS"}, order = 7)
    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS) {
        return this.spawnEntity(entityTemplateJS, false, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    private EntityJS spawnEntity(EntityTemplateJS entityTemplateJS, boolean isStatic, Vec3f position, Vec3f rotation, Vec3f scaling) {
        if (entityTemplateJS == null) {
            Log.get().warn("Tried to spawn NULL entity from script");
            return null;
        }

        final EntityRenderData entityRenderData = this.getScriptingManaging().getEntityRenderDataMap().get(entityTemplateJS);
        final JGemsBody jGemsBody = !isStatic
                ? new JGemsDynamicBody(MeshCollider.getDynamic(entityRenderData.getMeshStructure()), JGemsHelper.get().getPhysicsWorld(), entityTemplateJS.getName())
                : new JGemsStaticBody(MeshCollider.getStatic(entityRenderData.getMeshStructure()), JGemsHelper.get().getPhysicsWorld(), entityTemplateJS.getName());
        jGemsBody.setStartTransformations(position.createJOML(), rotation.createJOML(), scaling.createJOML());
        JGemsHelper.world().addWorldItem(jGemsBody, entityRenderData);
        return new EntityJS(jGemsBody);
    }

    @JSMethodDoc(description = "Spawn prop(decoration) in scene world", args = {"propTemplateJS", "position", "rotation", "scaling"}, order = 8)
    public PropJS spawnProp(PropTemplateJS propTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        if (propTemplateJS == null) {
            Log.get().warn("Tried to spawn NULL prop from script");
            return null;
        }

        final PropRenderData propRenderData = this.getScriptingManaging().getPropRenderDataMap().get(propTemplateJS);
        final SceneProp sceneProp = new SceneWorldProp(propTemplateJS.getName(), JGemsHelper.get().getSceneWorld(), propRenderData);
        JGemsHelper.world().addProp(sceneProp);
        if (sceneProp.hasModel()) {
            sceneProp.getModel().getPose().setPosition(position.createJOML());
            sceneProp.getModel().getPose().setRotation(rotation.createJOML());
            sceneProp.getModel().getPose().setScaling(scaling.createJOML());
        }
        return new PropJS(sceneProp);
    }

    @JSMethodDoc(description = "Spawn point light in scene world", args = {"brightness", "light position", "light color", "offset"}, order = 9)
    public PointLightJS spawnPointLight(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor, @NotNull Vec3f offset) {
        PointLightJS pointLightJS = new PointLightJS(brightness, lightPos, lightColor, offset);
        JGemsHelper.world().addLight(UtilsJS.getPointlight(pointLightJS));
        return pointLightJS;
    }

    @JSMethodDoc(description = "Spawn point light in scene world", args = {"brightness", "light position", "light color"}, order = 10)
    public PointLightJS spawnPointLight(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor) {
        PointLightJS pointLightJS = new PointLightJS(brightness, lightPos, lightColor, new Vec3f(0.0f, 0.0f, 0.0f));
        JGemsHelper.world().addLight(UtilsJS.getPointlight(pointLightJS));
        return pointLightJS;
    }

    @JSMethodDoc(description = "Remove point light from scene world", args = {"pointLightJS"}, order = 11)
    public void removePointLight(PointLightJS pointLightJS) {
        pointLightJS.remove();
    }

    @JSMethodDoc(description = "Remove entity from physics world", args = {"entityJS"}, order = 12)
    public void removeEntity(EntityJS entityJS) {
        entityJS.remove();
    }

    @JSMethodDoc(description = "Remove prop from scene world", args = {"propJS"}, order = 13)
    public void removeProp(PropJS propJS) {
        propJS.remove();
    }

    public void onMapSpawnedWorldItemEvent(@NotNull WorldItem worldItem, int templateId) {
        final EntityJS entityJS = new EntityJS(worldItem);
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapSpawnedEntity, this, entityJS);
        this.mapObjectsIdMap.put(templateId, entityJS);
    }

    public void onMapSpawnedPropEvent(@NotNull SceneProp sceneProp, int templateId) {
        final PropJS propJS = new PropJS(sceneProp);
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapSpawnedProp, this, propJS);
        this.mapObjectsIdMap.put(templateId, propJS);
    }

    public void onMapSpawnedPointLightEvent(@NotNull PointLight pointLight, int templateId) {
        final PointLightJS pointLightJS = new PointLightJS(pointLight);
        JGemsAPI.executeScriptFunction(null, APIScriptsListing.onMapSpawnedPointLight, this, pointLightJS);
        this.mapObjectsIdMap.put(templateId, pointLightJS);
    }

    public TimerManagerJS getTimerManagerJS() {
        return this.timerManagerJS;
    }

    JGemsAPIScriptingManaging getScriptingManaging() {
        return this.scriptingManaging;
    }
}