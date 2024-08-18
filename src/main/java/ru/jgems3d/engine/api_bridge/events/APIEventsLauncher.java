/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.api_bridge.events;

import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine_api.events.bus.Events;

@SuppressWarnings("all")
public abstract class APIEventsLauncher {
    public static Events.IEvent pushEvent(Events.IEvent event) {
        APIContainer.pushEvent(event);
        return event;
    }
}