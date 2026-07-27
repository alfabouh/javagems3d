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

package A_default_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultGamePanel;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultPausePanel;
import javagems3d.help.JGemsHelper;

public class TestEvents {
    @SubscribeEvent
    public static void onPause(EventBus.OnPauseFromButtonPressEvent event) {
        JGemsHelper.ui().openPanel(new DefaultPausePanel(null));
    }

    @SubscribeEvent
    public static void onUnPause(EventBus.OnUnPauseFromButtonPressEvent event) {
        JGemsHelper.ui().openPanel(new DefaultGamePanel(null));
    }

   // @SubscribeEvent
   // public static void onWorldState(EventBus.PhysicsWorldState event) {
   // }
//
   // @SubscribeEvent
   // public static void onWorldUpdate(EventBus.PhysicsWorldUpdate event) {
   // }
}
