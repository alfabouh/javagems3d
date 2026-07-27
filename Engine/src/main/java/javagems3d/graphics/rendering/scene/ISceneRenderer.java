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

package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.ticking.FrameTicking;

import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model2D;
import org.jetbrains.annotations.NotNull;

public interface ISceneRenderer extends IWindow.ResizeEvent {
    void onStartRender();
    void onRender(FrameTicking frameTicking);
    void onStopRender();

    @NotNull Model2D getScreenModel();

    @NotNull IWorld getWorld();
    @NotNull IWindow getWindow();
}
