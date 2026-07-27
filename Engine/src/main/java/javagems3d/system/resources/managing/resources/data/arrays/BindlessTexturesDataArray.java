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

package javagems3d.system.resources.managing.resources.data.arrays;

import javagems3d.graphics.rendering.programs.textures.base.ITextureBindless;

import java.util.ArrayList;
import java.util.List;

public final class BindlessTexturesDataArray implements IDataArray {
    private final List<ITextureBindless> bindlessTextureList;

    private BindlessTexturesDataArray(List<ITextureBindless> bindlessTextureList) {
        this.bindlessTextureList = bindlessTextureList;
    }

    public BindlessTexturesDataArray() {
        this.bindlessTextureList = new ArrayList<>();
    }

    public void clear() {
        this.getBindlessTextureList().clear();
    }

    public void add(ITextureBindless bindlessTexture) {
        this.getBindlessTextureList().add(bindlessTexture);
    }

    public List<ITextureBindless> getBindlessTextureList() {
        return this.bindlessTextureList;
    }

    public BindlessTexturesDataArray copy() {
        return new BindlessTexturesDataArray(new ArrayList<>(this.bindlessTextureList));
    }
}