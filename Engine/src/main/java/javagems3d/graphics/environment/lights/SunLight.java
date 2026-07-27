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

package javagems3d.graphics.environment.lights;

import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SunLight extends Light {
    private float sunBrightness;

    public SunLight(@NotNull Vector3f sunPos, @NotNull Vector3f sunColor, float sunBrightness) {
        super(sunPos, sunColor);
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    @Override
    public LightType getLightType() {
        return LightType.SUN;
    }
}
