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

package javagems3d.graphics.screen;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScreen {
    void createObjects(IWindow window);
    void createScreenAndContext();
    void runRenderThread();

    IWindow getWindow();

    void zeroRenderTick();
    float getRenderTicks();

    default void setIcon(@Nullable JGemsPath icon, ISource.Source source) {
        this.getWindow().setIcon(icon, source);
    }

    default void setTitle(@NotNull String title) {
        this.getWindow().setTitle(title);
    }
}
