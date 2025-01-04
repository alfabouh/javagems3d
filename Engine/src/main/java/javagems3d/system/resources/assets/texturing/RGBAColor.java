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

package javagems3d.system.resources.assets.texturing;

import org.joml.Vector4f;
import javagems3d.system.resources.assets.texturing.base.ISample;

public final class RGBAColor implements ISample {
    private final Vector4f color;

    public RGBAColor(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public RGBAColor(float r, float g, float b, float a) {
        this(new Vector4f(r, g, b, a));
    }

    public RGBAColor(Vector4f color) {
        this.color = color;
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    public Vector4f getColor() {
        return new Vector4f(this.color);
    }

    @Override
    public String toString() {
        return "RGBAColor{" + "color=" + color + '}';
    }
}