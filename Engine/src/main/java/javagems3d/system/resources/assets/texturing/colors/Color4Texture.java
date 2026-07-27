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

package javagems3d.system.resources.assets.texturing.colors;

import org.joml.Vector4f;

public record Color4Texture(Vector4f color) implements ISampleColor4 {
    public Color4Texture(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color4Texture(float r, float g, float b, float a) {
        this(new Vector4f(r, g, b, a));
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    @Override
    public Vector4f color() {
        return new Vector4f(this.color);
    }
}