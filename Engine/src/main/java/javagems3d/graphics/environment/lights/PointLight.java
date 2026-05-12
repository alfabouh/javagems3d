package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.global.JGemsConfig;
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
    private float clipRadius;

    public PointLight() {
        super();
        this.brightness = 1.0f;
        this.lighted = null;
        this.actionOnDetach = ActionOnDetach.DESTROY;
    }

    public PointLight(@NotNull Vector3f lightPos, @NotNull Vector3f lightColor, @NotNull Vector3f offset) {
        super(lightPos, lightColor, offset);
        this.brightness = 1.0f;
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(Vector3f lightPos, Vector3f lightColor) {
        this(lightPos, lightColor, new Vector3f(0.0f));
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(Vector3f lightPos) {
        this(lightPos, new Vector3f(1.0f), new Vector3f(0.0f));
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity) {
        this(abstractSceneEntity.getRenderPosition(), new Vector3f(1.0f), new Vector3f(0.0f));
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, new Vector3f(0.0f));
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, offset);
        this.clipRadius = PointLight.calcPointLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public static float calcPointLightClipRadius(float brightness, Vector3f color, float epsilon) {
        Vector3f maxColor = new Vector3f(color).mul(brightness);
        final float maxC = maxColor.get(maxColor.maxComponent());

        return (float) ((-JGemsConfig.SYSTEM.POINT_LIGHT_LINEAR_ATT + Math.sqrt(Math.pow(JGemsConfig.SYSTEM.POINT_LIGHT_LINEAR_ATT, 2.0f) - 4.0f * JGemsConfig.SYSTEM.POINT_LIGHT_EXP_ATT * (JGemsConfig.SYSTEM.POINT_LIGHT_CONSTANT_ATT - maxC * epsilon))) / (2.0f * JGemsConfig.SYSTEM.POINT_LIGHT_EXP_ATT));
    }

    private static float EPS() {
        return 256.0f / 3.0f;
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

    @Override
    public Light setLightColor(Vector3f lightColor) {
        this.clipRadius = PointLight.calcPointLightClipRadius(brightness, lightColor, EPS());
        return super.setLightColor(lightColor);
    }

    public PointLight setBrightness(float brightness) {
        this.brightness = brightness;
        this.clipRadius = PointLight.calcPointLightClipRadius(brightness, this.getLightColor(), EPS());
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

    public float getClipRadius() {
        return this.clipRadius;
    }
}
