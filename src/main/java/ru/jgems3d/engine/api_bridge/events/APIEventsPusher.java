package ru.jgems3d.engine.api_bridge.events;

import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine_api.events.bus.Events;

@SuppressWarnings("all")
public abstract class APIEventsPusher {
    public static Events.IEvent pushEvent(Events.IEvent event) {
        APIContainer.pushEvent(event);
        return event;
    }
}