package ru.jgems3d.engine.graphics.opengl.environment.light;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.physics.world.IWorld;

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

    public PointLight(AbstractSceneEntity abstractSceneEntity) {
        super(abstractSceneEntity);
    }

    public PointLight(AbstractSceneEntity abstractSceneEntity, Vector3f lightColor) {
        super(abstractSceneEntity, lightColor);
    }

    public PointLight(AbstractSceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
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

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
        if (this.getAttachedShadowSceneId() >= 0) {
            JGems3D.get().getScreen().getScene().getSceneRenderer().getShadowScene().unBindPointLightFromShadowScene(this);
        }
    }

    @Override
    public int lightCode() {
        return POINT_LIGHT;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
