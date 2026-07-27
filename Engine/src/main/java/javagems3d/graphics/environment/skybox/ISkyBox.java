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

package javagems3d.graphics.environment.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public interface ISkyBox {
    void updateSkyBox(IWorld world, ICamera camera);
    void createSkyBox(IWorld world);
    void destroySkyBox(IWorld world);
    boolean isDrawSunOnSkyBox();

    ISkyBackground getBackground();
    ICubeMapProgram getTexture();

    void setSkyCoveredByFog(boolean skyCoveredByFog);
    void setSky2DTexture(@Nullable ICubeMapProgram sky2DTexture);

    boolean isSkyCoveredByFog();
}
