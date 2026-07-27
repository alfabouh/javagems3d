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

package javagems3d.graphics.environment.fog;

import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public abstract class FogScene implements IFogScene {
    private float density;
    private Vector3f color;

    public FogScene() {
        this.density = 0.0f;
        this.color = new Vector3f(0.85f);
    }

    public abstract void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, ISkyBox skyBox, ILightScene lightScene, MemoryStack stack);

    public void setFogColor(Vector3f color) {
        this.color = color;
    }

    public void setFogDensity(float density) {
        this.density = density;
    }

    public void disable() {
        this.setFogDensity(0.0f);
    }

    public Vector3f getFogColor() {
        return new Vector3f(this.color);
    }

    public float getFogDensity() {
        return this.density;
    }
}
