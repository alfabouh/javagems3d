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
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SpotLight extends Light implements ILightAttachable {
    private boolean enableShadowMap;
    private float brightness;
    private Vector3f direction;
   // private float innerCutoff;
    public float cutOff;
    private float attenuationFactor;
    private IObjectWithLights lighted;
    private ActionOnDetach actionOnDetach;
    private float clipRadius;

    public SpotLight() {
        super();
        this.brightness = 1.0f;
        this.direction = new Vector3f(0.0f, 0.0f, 0.0f);
       // this.innerCutoff = (float) Math.cos(Math.toRadians(12.5f));
        this.cutOff = 17.5f;
        this.actionOnDetach = ActionOnDetach.DESTROY;
        this.attenuationFactor = 64.0f;
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), SpotLight.EPS(), this.getAttenuationFactor());
    }

    public SpotLight(Vector3f lightPos, Vector3f lightColor, Vector3f offset) {
        super(lightPos, lightColor, offset);
        this.brightness = 1.0f;
        this.direction = new Vector3f(0.0f, 0.0f, 0.0f);
        //this.innerCutoff = (float) Math.cos(Math.toRadians(12.5f));
        this.cutOff = 17.5f;
        this.actionOnDetach = ActionOnDetach.DESTROY;
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), SpotLight.EPS(), this.getAttenuationFactor());
    }

    public static float EPS() {
        return 256.0f / 32.0f;
    }

    public Vector3f getLightAngle() {
        return new Vector3f(this.direction);
    }

    public Vector3f getLightDirection() {
        final Vector3f basic = new Vector3f(0.0f, -1.0f, 0.0f);
        Matrix4f mat = new Matrix4f().identity().rotateXYZ(new Vector3f(this.direction).negate());
        return new Vector4f(basic, 0.0f).mul(mat).xyz(new Vector3f());
    }

    public SpotLight setDirection(Vector3f direction) {
        this.direction = new Vector3f(direction);
        return this;
    }

    //public float getInnerCutoff() {
    //    return this.innerCutoff;
    //}
//
    //public SpotLight setInnerCutoff(float degrees) {
    //    this.innerCutoff = (float) Math.cos(Math.toRadians(degrees));
    //    return this;
    //}


    @Override
    public SpotLight setOffset(Vector3f offset) {
        return (SpotLight) super.setOffset(offset);
    }

    public float getFOV() {
        return (float) Math.toRadians(this.cutOff * 2.0f);
    }

    public float getCutOffDegrees() {
        return (float) this.cutOff;
    }

    public float getCutOff() {
        return (float) Math.cos(Math.toRadians(this.cutOff));
    }

    public SpotLight setCutOff(float degrees) {
        this.cutOff = degrees;
        return this;
    }

    public boolean isEnableShadowMap() {
        return this.enableShadowMap;
    }

    public SpotLight setEnableShadowMap(boolean enableShadowMap) {
        this.enableShadowMap = enableShadowMap;
        return this;
    }

    public float getBrightness() {
        return !this.isActive() ? -1.0f : this.brightness;
    }

    public SpotLight setBrightness(float brightness) {
        this.clipRadius = PointLight.calcLightClipRadius(brightness, this.getLightColor(), SpotLight.EPS(), this.getAttenuationFactor());
        this.brightness = brightness;
        return this;
    }

    @Override
    public SpotLight setLightColor(Vector3f lightColor) {
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), lightColor, SpotLight.EPS(), this.getAttenuationFactor());
        super.setLightColor(lightColor);
        return this;
    }

    @Override
    public SpotLight on() {
        return (SpotLight) super.on();
    }

    @Override
    public SpotLight off() {
        return (SpotLight) super.off();
    }

    @Override
    public LightType getLightType() {
        return LightType.SPOT;
    }

    public float getAttenuationFactor() {
        return this.attenuationFactor;
    }

    public SpotLight setAttenuationFactor(float attenuationFactor) {
        this.clipRadius = PointLight.calcLightClipRadius(this.getBrightness(), this.getLightColor(), SpotLight.EPS(), attenuationFactor);
        this.attenuationFactor = attenuationFactor;
        return this;
    }

    public SpotLight setActionOnDetach(ActionOnDetach actionOnDetach) {
        this.actionOnDetach = actionOnDetach;
        return this;
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

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getAttachedTo() != null && ((IWorldObject) this.getAttachedTo()).isAlive()) {
            this.setLightPosition(this.getAttachedTo().getPositionToAttachLights());
            this.setDirection(this.getAttachedTo().getRotationAngle());
        }
    }
}