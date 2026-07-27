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

import org.joml.Vector3f;

public record Color3Texture(Vector3f color) implements ISampleColor3 {
    public Color3Texture(float r, float g, float b) {
        this(new Vector3f(r, g, b));
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    @Override
    public Vector3f color() {
        return new Vector3f(this.color);
    }
}