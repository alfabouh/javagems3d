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

package api.events;


import api.scripting.coding.env.internal.util.events.JSEventI;
import api.system.JGemsAPI;
import api.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static @NotNull EventBus.IEvent pushEvent(EventBus.IEvent event, @Nullable Pair<JSEventI, JavaToJsAPI.Target> jsEventLaunch) {
        if (!JGemsAPI.ALLOW_EVENTS() || !JGemsAPI.isValid()) {
            return new EventBus.IEvent.EmptyEvent();
        }
        JGemsAPI.pushEvent(event);
        if (jsEventLaunch != null) {
            if (event.canBeCancelled() && !event.isCancelled()) {
                if (JavaToJsAPI.Js_GAME_SomeEvent__EVENT(jsEventLaunch.first(), jsEventLaunch.second())) {
                    if (event.canBeCancelled() && event.isCancelled()) {
                        ((EventBus.Cancellable) event).setCancelled(true);
                    }
                }
            }
        }
        return event;
    }
}