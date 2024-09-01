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

package javagems3d.engine.system.resources.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public final class Format2D implements IFormat {
    private final Vector2f position;
    private final Vector2f scale;
    private float rotation;

    public Format2D(@NotNull Vector2f position, float rotation, Vector2f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Format2D(Vector2f position, float rotation) {
        this(position, rotation, new Vector2f(1.0f));
    }

    public Format2D(Vector2f position) {
        this(position, 0.0f, new Vector2f(1.0f));
    }

    public Format2D() {
        this(new Vector2f(0.0f), 0.0f, new Vector2f(1.0f));
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public void setPosition(Vector2f position) {
        this.getPosition().set(position);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2f getScale() {
        return this.scale;
    }

    public void setScale(Vector2f scale) {
        this.getScale().set(scale);
    }

    @Override
    public IFormat copy() {
        return new Format2D(new Vector2f(this.getPosition()), this.getRotation(), new Vector2f(this.getScale()));
    }
}
