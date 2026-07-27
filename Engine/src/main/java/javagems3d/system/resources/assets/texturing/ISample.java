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

package javagems3d.system.resources.assets.texturing;

import org.lwjgl.opengl.GL46;

public interface ISample {
    interface IProperties {
        default int getTextureTypeByFormat(int format) {
            return switch (format) {
                case GL46.GL_R, GL46.GL_RG, GL46.GL_RGB, GL46.GL_RGBA, GL46.GL_RGBA32F, GL46.GL_RGB32F, GL46.GL_RG32F, GL46.GL_R32F, GL46.GL_RGBA16F, GL46.GL_RGB16F, GL46.GL_RG16F, GL46.GL_R16F -> GL46.GL_FLOAT;
                case GL46.GL_R16UI ->  GL46.GL_UNSIGNED_SHORT;
                default -> GL46.GL_UNSIGNED_BYTE;
            };
        }
    }
}
