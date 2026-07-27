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

package javagems3d.graphics.rendering.programs.textures.base;

import org.lwjgl.opengl.ARBBindlessTexture;

public interface ITextureBindless {
    long getBindingHandler();

    default boolean isHandlerExists() {
        return this.canBeBindless() && this.getBindingHandler() != 0;
    }

    boolean canBeBindless();

    default long createBindlessHandler(int textureId) {
        if (!this.canBeBindless()) {
            return 0L;
        }
        return ARBBindlessTexture.glGetTextureHandleARB(textureId);
    }

    default long createBindlessHandler(int textureId, int samplerId) {
        if (!this.canBeBindless()) {
            return 0L;
        }
        return ARBBindlessTexture.glGetTextureSamplerHandleARB(textureId, samplerId);
    }

    default void createARB64Handling() {
        if (!this.isHandlerExists()) {
            return;
        }
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.getBindingHandler());
    }

    default void removeARB64Handling() {
        if (!this.isHandlerExists()) {
            return;
        }
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(this.getBindingHandler());
    }
}
