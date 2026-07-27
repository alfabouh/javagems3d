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

package javagems3d.graphics.environment.lights.scene;

import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.physics.world.IWorld;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Set;

public interface ILightScene extends IHasHDR, IHasSSAO {
    void updateBuffers(MemoryStack stack, HashMap<PointLight, Integer> lightIntegerHashMap, HashMap<SpotLight, Integer> spotLightIntegerHashMap, IWorld world, Matrix4f viewMatrix);
    void addLight(Light light);
    void removeLight(Light light);

    SunLight getSunLight();
    Set<PointLight> getPointLights();
    Set<SpotLight> getSpotLights();
}
