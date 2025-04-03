package javagems3d.graphics.environment.lights;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.physics.world.IWorld;

public class PointLight extends Light {
    private int attachedShadowSceneId = -1;
    private float brightness;

    public PointLight() {
        super();
        this.brightness = 1.0f;
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

    public int getAttachedShadowSceneId() {
        return this.attachedShadowSceneId;
    }

    public void setAttachedShadowSceneId(int attachedShadowSceneId) {
        this.attachedShadowSceneId = attachedShadowSceneId;
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

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
