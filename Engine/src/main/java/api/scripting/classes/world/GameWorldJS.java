package api.scripting.classes.world;

import api.scripting.JGemsAPIScriptingManaging;
import api.scripting.classes.init.templates.EntityTemplateJS;
import api.scripting.classes.init.templates.PropTemplateJS;
import api.scripting.classes.util.Vec3f;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.help.JGemsCoreHelper;
import javagems3d.help.JGemsWorldHelper;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class GameWorldJS {
    private final JGemsAPIScriptingManaging scriptingManaging;

    public GameWorldJS(@NotNull JGemsAPIScriptingManaging scriptingManaging) {
        this.scriptingManaging = scriptingManaging;
    }

    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        return this.spawnEntity(entityTemplateJS, true, position, rotation, scaling);
    }

    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        return this.spawnEntity(entityTemplateJS, false, position, rotation, scaling);
    }

    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation) {
        return this.spawnEntity(entityTemplateJS, true, position, rotation, new Vec3f(1.0f, 1.0f, 1.0f));
    }

    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position, Vec3f rotation) {
        return this.spawnEntity(entityTemplateJS, false, position, rotation, new Vec3f(1.0f, 1.0f, 1.0f));
    }

    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS, Vec3f position) {
        return this.spawnEntity(entityTemplateJS, true, position, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    public EntityJS spawnDynamicEntity(EntityTemplateJS entityTemplateJS, Vec3f position) {
        return this.spawnEntity(entityTemplateJS, false, position, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

    public EntityJS spawnStaticEntity(EntityTemplateJS entityTemplateJS) {
        return this.spawnEntity(entityTemplateJS, true, new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f));
    }

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
                ? new JGemsDynamicBody(MeshCollider.getDynamic(entityRenderData.getMeshStructure()), JGemsCoreHelper.getPhysicsWorld(), entityTemplateJS.getName())
                : new JGemsStaticBody(MeshCollider.getStatic(entityRenderData.getMeshStructure()), JGemsCoreHelper.getPhysicsWorld(), entityTemplateJS.getName());
        jGemsBody.setStartTransformations(position.createJOML(), rotation.createJOML(), scaling.createJOML());
        JGemsWorldHelper.addItemInWorld(jGemsBody, entityRenderData);
        return new EntityJS(jGemsBody);
    }

    public PropJS spawnProp(PropTemplateJS propTemplateJS, Vec3f position, Vec3f rotation, Vec3f scaling) {
        if (propTemplateJS == null) {
            Log.get().warn("Tried to spawn NULL prop from script");
            return null;
        }

        final PropRenderData propRenderData = this.getScriptingManaging().getPropRenderDataMap().get(propTemplateJS);
        final SceneProp sceneProp = new SceneWorldProp(propTemplateJS.getName(), JGemsCoreHelper.getSceneWorld(), propRenderData);
        JGemsWorldHelper.addPropInScene(sceneProp);
        if (sceneProp.hasModel()) {
            sceneProp.getModel().getPose().setPosition(position.createJOML());
            sceneProp.getModel().getPose().setRotation(rotation.createJOML());
            sceneProp.getModel().getPose().setScaling(scaling.createJOML());
        }
        return new PropJS(sceneProp);
    }

    public PointLightJS spawnPointLight(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor, @NotNull Vec3f offset) {
        PointLightJS pointLightJS = new PointLightJS(brightness, lightPos, lightColor, offset);
        JGemsWorldHelper.addLight(pointLightJS.getPointLight());
        return pointLightJS;
    }

    public PointLightJS spawnPointLight(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor) {
        PointLightJS pointLightJS = new PointLightJS(brightness, lightPos, lightColor, new Vec3f(0.0f, 0.0f, 0.0f));
        JGemsWorldHelper.addLight(pointLightJS.getPointLight());
        return pointLightJS;
    }

    public void removePointLight(PointLightJS pointLightJS) {
        pointLightJS.remove();
    }

    public void removeEntity(EntityJS entityJS) {
        entityJS.remove();
    }

    public void removeProp(PropJS propJS) {
        propJS.remove();
    }

    public JGemsAPIScriptingManaging getScriptingManaging() {
        return this.scriptingManaging;
    }
}