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

package javagems3d.graphics.rendering.programs.shaders;

import org.lwjgl.opengl.GL46;

public interface IShaderProgram {
    int getProgramId();

    default void clear() {
        this.unbind();
        if (this.getProgramId() != 0) {
            GL46.glDeleteProgram(this.getProgramId());
        }
    }

    default void bind() {
        GL46.glUseProgram(this.getProgramId());
    }

    default void unbind() {
        GL46.glUseProgram(0);
    }
}
