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
import javagems3d.graphics.environment.skybox.background.JGemsSkyBackground;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class SkyBox implements ISkyBox {
    private ICubeMapProgram sky2DTexture;
    private ISkyBackground background;
    private boolean isSkyCoveredByFog;
    private boolean drawSunOnSkyBox;

    public SkyBox(@NotNull ISkyBackground skyBackground, @Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
        this.isSkyCoveredByFog = true;
        this.background = skyBackground;
        this.drawSunOnSkyBox = true;
    }

    @Override
    public void setSkyCoveredByFog(boolean skyCoveredByFog) {
        this.isSkyCoveredByFog = skyCoveredByFog;
    }

    @Override
    public void setSky2DTexture(@Nullable ICubeMapProgram sky2DTexture) {
        this.sky2DTexture = sky2DTexture;
    }

    public SkyBox setBackground(@NotNull ISkyBackground background) {
        this.background = background;
        return this;
    }

    public boolean isSkyCoveredByFog() {
        return this.isSkyCoveredByFog;
    }

    public ISkyBackground getBackground() {
        return this.background;
    }

    public ICubeMapProgram getTexture() {
        return this.sky2DTexture;
    }

    @Override
    public void updateSkyBox(IWorld world, ICamera camera) {
        this.getBackground().update(camera);
    }

    @Override
    public void createSkyBox(IWorld world) {
        this.getBackground().create(world);
    }

    @Override
    public void destroySkyBox(IWorld world) {
        this.getBackground().destroy(world);
    }

    public boolean isDrawSunOnSkyBox() {
        return this.drawSunOnSkyBox;
    }

    public SkyBox setDrawSunOnSkyBox(boolean drawSunOnSkyBox) {
        this.drawSunOnSkyBox = drawSunOnSkyBox;
        return this;
    }
}
