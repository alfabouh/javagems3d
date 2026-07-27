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

import org.joml.Vector2f;

public record Color2Texture(Vector2f color) implements ISampleColor2 {
    public Color2Texture(float r, float g) {
        this(new Vector2f(r, g));
    }

    public void setColor(Vector2f color) {
        this.color.set(color);
    }

    @Override
    public Vector2f color() {
        return new Vector2f(this.color);
    }
}