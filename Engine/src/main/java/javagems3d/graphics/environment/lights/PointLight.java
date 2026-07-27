/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.transformation.TransformUtils;
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
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(Vector3f lightPos, Vector3f lightColor) {
        this(lightPos, lightColor, new Vector3f(0.0f));
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(Vector3f lightPos) {
        this(lightPos, new Vector3f(1.0f), new Vector3f(0.0f));
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity) {
        this(abstractSceneEntity.getRenderPosition(), new Vector3f(1.0f), new Vector3f(0.0f));
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, new Vector3f(0.0f));
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, offset);
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), EPS());
    }

    public static float calcLightClipRadius(float brightness, Vector3f color, float epsilon) {
        return calcLightClipRadius(brightness, color, epsilon, 1.f);
    }


    public static float calcLightClipRadius(float brightness, Vector3f color, float epsilon, float att) {
        Vector3f maxColor = new Vector3f(color).mul(brightness);
        final float maxC = maxColor.get(maxColor.maxComponent());

        final float q = JGemsConfig.SYSTEM.LIGHT_EXP_ATT / att;
        return (float) ((-JGemsConfig.SYSTEM.LIGHT_LINEAR_ATT + Math.sqrt(Math.pow(JGemsConfig.SYSTEM.LIGHT_LINEAR_ATT, 2.0f) - 4.0f * q * (JGemsConfig.SYSTEM._LIGHT_CONSTANT_ATT - maxC * epsilon))) / (2.0f * q));
    }

    public static float EPS() {
        return 256.0f / 26.0f;
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
        this.clipRadius = PointLight.calcLightClipRadius(brightness, lightColor, EPS());
        return super.setLightColor(lightColor);
    }

    public PointLight setBrightness(float brightness) {
        this.brightness = brightness;
        this.clipRadius = PointLight.calcLightClipRadius(brightness, this.getLightColor(), EPS());
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
