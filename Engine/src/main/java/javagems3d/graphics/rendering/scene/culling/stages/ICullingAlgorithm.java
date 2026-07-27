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

package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface ICullingAlgorithm {
    void filter(@NotNull Collection<? extends ICulled> sceneObjects);

    default void createResources() {
    }

    default void destroyResources() {
    }
}
