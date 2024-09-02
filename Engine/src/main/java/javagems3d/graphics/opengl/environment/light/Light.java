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

package javagems3d.graphics.opengl.environment.light;

import org.joml.Vector3f;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.physics.world.basic.IWorldTicked;

public abstract class Light implements IWorldTicked {
    public static final int
            POINT_LIGHT = (1 << 2);

    private final Vector3f offset;
    private final Vector3f lightColor;
    private final Vector3f lightPos;
    private boolean enabled;
    private boolean isActive;

    public Light() {
        this(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(Vector3f lightPos, Vector3f lightColor) {
        this(lightPos, lightColor, new Vector3f(0.0f));
    }

    public Light(Vector3f lightPos) {
        this(lightPos, new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(Vector3f lightPos, Vector3f lightColor, Vector3f offset) {
        this.lightColor = new Vector3f(lightColor);
        this.lightPos = new Vector3f(lightPos);
        this.offset = new Vector3f(offset);
        this.enabled = true;
        this.isActive = true;
    }

    public Light(AbstractSceneEntity abstractSceneEntity) {
        this(abstractSceneEntity.getRenderPosition(), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public Light(AbstractSceneEntity abstractSceneEntity, Vector3f lightColor) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, new Vector3f(0.0f));
    }

    public Light(AbstractSceneEntity abstractSceneEntity, Vector3f lightColor, Vector3f offset) {
        this(abstractSceneEntity.getRenderPosition(), lightColor, offset);
    }

    public void start() {
        this.isActive = true;
    }

    public void stop() {
        this.isActive = false;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isEnabled() {
        return this.isActive() && this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract int lightCode();

    public Vector3f getLightColor() {
        return new Vector3f(this.lightColor);
    }

    public Light setLightColor(Vector3f lightColor) {
        this.lightColor.set(lightColor);
        return this;
    }

    public Vector3f getLightPos() {
        return new Vector3f(this.lightPos).add(this.getOffset());
    }

    public Light setLightPos(Vector3f lightPos) {
        this.lightPos.set(lightPos);
        return this;
    }

    public Vector3f getOffset() {
        return new Vector3f(this.offset);
    }

    public Light setOffset(Vector3f offset) {
        this.offset.set(offset);
        return this;
    }
}
