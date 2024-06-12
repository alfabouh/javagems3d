package ru.alfabouh.jgems3d.engine.render.opengl.environment.light;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;

public class PointLight extends Light {
    private int attachedShadowSceneId = -1;
    private float brightness;

    public PointLight() {
        super(new Vector3d(0.0d), new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public PointLight(Vector3d lightPos, Vector3d lightColor) {
        super(lightPos, lightColor);
    }

    public PointLight(Vector3d lightPos) {
        super(lightPos);
    }

    public PointLight(Vector3d lightPos, Vector3d lightColor, Vector3d offset) {
        super(lightPos, lightColor, offset);
    }

    public PointLight(PhysicsObject physicsObject) {
        super(physicsObject);
    }

    public PointLight(PhysicsObject physicsObject, Vector3d lightColor) {
        super(physicsObject, lightColor);
    }

    public PointLight(PhysicsObject physicsObject, Vector3d lightColor, Vector3d offset) {
        super(physicsObject, lightColor, offset);
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
