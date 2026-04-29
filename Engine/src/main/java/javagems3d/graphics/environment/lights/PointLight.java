package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.physics.world.basic.IWorldObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.physics.world.IWorld;

public class PointLight extends Light implements ILightAttachable {
    private boolean enableShadowMap;
    private float brightness;
    private IObjectWithLights lighted;
    private ActionOnDetach actionOnDetach;

    public PointLight() {
        super();
        this.brightness = 1.0f;
        this.lighted = null;
        this.actionOnDetach = ActionOnDetach.DESTROY;
    }

    public PointLight(@NotNull Vector3f lightPos, @NotNull Vector3f lightColor, @NotNull Vector3f offset) {
        super(lightPos, lightColor, offset);
        this.brightness = 1.0f;
    }

    public PointLight(Vector3f lightPos, Vector3f lightColor) {
        this(lightPos, lightColor, new Vector3f(0.0f));
    }

    public PointLight(Vector3f lightPos) {
        this(lightPos, new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public PointLight(SceneEntity abstractSceneEntity) {
        this(abstractSceneEntity.getRenderPosition(), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, new Vector3f(0.0f));
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, offset);
    }

    public boolean isEnableShadowMap() {
        return this.enableShadowMap;
    }

    public PointLight setEnableShadowMap(boolean enableShadowMap) {
        this.enableShadowMap = enableShadowMap;
        return this;
    }

    public float getBrightness() {
        return !this.isActive() ? -1.0f : this.brightness;
    }

    public PointLight setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }

    @Override
    public PointLight on() {
        return (PointLight) super.on();
    }

    @Override
    public PointLight off() {
        return (PointLight) super.off();
    }

    @Override
    public LightType getLightType() {
        return LightType.POINT;
    }

    public PointLight setActionOnDetach(ActionOnDetach actionOnDetach) {
        this.actionOnDetach = actionOnDetach;
        return this;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getAttachedTo() != null && ((IWorldObject) this.getAttachedTo()).isAlive()) {
            this.setLightPosition(this.getAttachedTo().getPositionToAttachLights());
        }
    }

    @Override
    public void attachTo(@Nullable IObjectWithLights lighted) {
        this.lighted = lighted;
    }

    @Override
    public @Nullable IObjectWithLights getAttachedTo() {
        return this.lighted;
    }

    @Override
    public ActionOnDetach getActionOnDeath() {
        return this.actionOnDetach;
    }
}
