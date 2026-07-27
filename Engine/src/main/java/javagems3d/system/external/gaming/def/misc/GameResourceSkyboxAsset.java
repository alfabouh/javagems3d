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

package javagems3d.system.external.gaming.def.misc;

import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.external.gaming.def.IAsset;

public class GameResourceSkyboxAsset implements IAsset {
    private final String name;
    private ICubeMapProgram.CMTextures cmTextures;

    public GameResourceSkyboxAsset(String name) {
        this(name, new ICubeMapProgram.CMTextures(null, null, null, null, null, null));
    }

    public GameResourceSkyboxAsset(String name, @NotNull ICubeMapProgram.CMTextures cmTextures) {
        this.name = name;
        this.cmTextures = cmTextures;
    }

    public ICubeMapProgram.CMTextures getCmTextures() {
        return this.cmTextures;
    }

    public GameResourceSkyboxAsset setCmTextures(ICubeMapProgram.CMTextures cmTextures) {
        this.cmTextures = cmTextures;
        return this;
    }

    @Override
    public String name() {
        return this.name;
    }
}
