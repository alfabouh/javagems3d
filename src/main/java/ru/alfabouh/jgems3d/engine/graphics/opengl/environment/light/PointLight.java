package ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;

public class PointLight extends Light {
    private int attachedShadowSceneId = -1;
    private float brightness;

    public PointLight() {
        super(new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(0.0f));
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

    public PointLight(AbstractSceneItemObject abstractSceneItemObject) {
        super(abstractSceneItemObject);
    }

    public PointLight(AbstractSceneItemObject abstractSceneItemObject, Vector3f lightColor) {
        super(abstractSceneItemObject, lightColor);
    }

    public PointLight(AbstractSceneItemObject abstractSceneItemObject, Vector3f lightColor, Vector3f offset) {
        super(abstractSceneItemObject, lightColor, offset);
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
            JGems.get().getScreen().getScene().getSceneRender().getShadowScene().unBindPointLightFromShadowScene(this);
        }
    }

    @Override
    public int lightCode() {
        return POINT_LIGHT;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isAttached()) {
            this.setLightPos(this.getAttachedTo().getRenderPosition());
        }
    }
}