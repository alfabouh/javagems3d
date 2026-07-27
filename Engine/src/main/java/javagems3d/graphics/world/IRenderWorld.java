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

package javagems3d.graphics.world;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;

public interface IRenderWorld extends IWorld {
    void setCamera(ICamera camera);

    IEnvironment getEnvironment();
    ICamera getCamera();

    void removeObject(SceneObject sceneObject);
    void addObject(SceneObject sceneObject);

    TimerPool getTimerPool();

    default JGemsTimedAction createTimer() {
        return this.getTimerPool().createTimer();
    }

    void removeLight(Light light);
    void addLight(Light light, @Nullable IObjectWithLights lighted);

    void clearAll();
}
