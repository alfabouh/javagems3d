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

package javagems3d.graphics.environment;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.decals.scene.IDecalsScene;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.particles.scene.IParticlesScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.physics.world.IWorld;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector3f;

public interface IEnvironment {
    void createEnvironment(OpenGLRenderer openGLRenderer);
    void updateEnvironment(ICamera camera);
    void destroyEnvironment();

    default void setEnvironmentDefaults() {
        this.getSkyBox().setSky2DTexture(null);
        this.getSkyBox().setSkyCoveredByFog(true);

        this.getLightScene().getSunLight().setLightColor(new Vector3f(1.0f));
        this.getLightScene().getSunLight().setLightPosition(new Vector3f(1.0f));
        this.getLightScene().getSunLight().setSunBrightness(1.0f);
        this.getShadowScene().getSunLightShadow().setDefaultCascadeSplits();

        this.getLightScene().setBloomEnabled(true);
        this.getLightScene().setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
        this.getLightScene().setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);

        this.getLightScene().setSsaoRange(JGemsConfig.SYSTEM.SSAO_RANGE);
        this.getLightScene().setSsaoBias(JGemsConfig.SYSTEM.SSAO_BIAS);
        this.getLightScene().setSsaoRadius(JGemsConfig.SYSTEM.SSAO_RADIUS);

        this.getFogScene().setFogColor(new Vector3f(0.85f));
        this.getFogScene().setFogDensity(0.0f);
    }

    IParticlesScene getParticlesScene();
    IDecalsScene getDecalsScene();
    IWorld getWorld();
    IShadowScene getShadowScene();
    ILightScene getLightScene();
    IFogScene getFogScene();
    ISkyBox getSkyBox();
}