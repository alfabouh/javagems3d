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

package javagems3d.graphics.environment.skybox.background;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ISkyBackground {
    @NotNull FixedCamera getScaledCameraBackground();

    @NotNull Set<? extends SceneProp> getSkySceneObjectsFiltered();
    @NotNull Set<? extends SceneProp> getSkySceneObjects();

    IWorld getWorld();

    void update(ICamera mainViewCamera);

    void addObject(SceneProp object);

    void removeObject(SceneProp object);

    void clearBackGround();

    void setViewScaling(float scaling);

    float getViewScaling();

    void destroy(IWorld world);
    void create(IWorld world);

    default boolean contains(SceneProp sceneProp) {
        return this.getSkySceneObjects().contains(sceneProp);
    }
}
