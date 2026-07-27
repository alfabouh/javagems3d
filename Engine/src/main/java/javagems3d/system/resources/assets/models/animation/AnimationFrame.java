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

package javagems3d.system.resources.assets.models.animation;

import org.joml.Matrix4f;

public final class AnimationFrame {
    private Matrix4f[] boneMatrices;
    private int offset;

    public AnimationFrame(Matrix4f[] boneMatrices) {
        this.boneMatrices = boneMatrices;
    }

    public void clear() {
        this.boneMatrices = null;
    }

    public Matrix4f[] getBoneMatrices() {
        return this.boneMatrices;
    }

    public void setBoneMatrices(Matrix4f[] boneMatrices) {
        this.boneMatrices = boneMatrices;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
