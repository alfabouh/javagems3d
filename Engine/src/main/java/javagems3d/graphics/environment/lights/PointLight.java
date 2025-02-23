/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.environment.lights;

import javagems3d.JGemsHelper;
import org.joml.Vector3f;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.physics.world.IWorld;

public class PointLight extends Light {
    private int attachedShadowSceneId = -1;
    private float brightness;

    public PointLight() {
        super();
    }

    public PointLight(Vector3f lightPos, Vector3f lightColor) {
        super(lightPos, lightColor);
    }

    public PointLight(Vector3f lightPos) {
        super(lightPos);
    }

    public PointLight(Vector3f lightPos, Vector3f lightColor, Vector3f offset) {
        super(lightPos, lightColor, offset);
    }

    public PointLight(SceneEntity abstractSceneEntity) {
        super(abstractSceneEntity);
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor) {
        super(abstractSceneEntity, lightColor);
    }

    public PointLight(SceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        super(abstractSceneEntity, lightColor, offset);
    }

    public int getAttachedShadowSceneId() {
        return this.attachedShadowSceneId;
    }

    public void setAttachedShadowSceneId(int attachedShadowSceneId) {
        this.attachedShadowSceneId = attachedShadowSceneId;
    }

    public float getBrightness() {
        return !this.isEnabled() ? -1.0f : this.brightness;
    }

    public PointLight setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }

    public void on() {
        super.on();
    }

    public void off() {
        super.off();
        if (this.getAttachedShadowSceneId() >= 0) {
            JGemsHelper.ENVIRONMENT.getWorldEnvironment().getShadowScene().unBindPointLightFromShadowScene(this);
        }
    }

    @Override
    public LightType getLightType() {
        return LightType.POINT;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
