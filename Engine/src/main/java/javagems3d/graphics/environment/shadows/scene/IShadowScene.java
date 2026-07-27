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

package javagems3d.graphics.environment.shadows.scene;

import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface IShadowScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    SunLightShadow getSunLightShadow();
    List<PointLightShadow> getPointLightShadows();
    HashMap<PointLight, Integer> getSortedPointLightMapReadyToBind(Vector3f viewPoint, Collection<PointLight> pointLightsRaw);

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
