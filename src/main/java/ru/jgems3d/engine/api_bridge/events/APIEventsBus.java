package ru.jgems3d.engine.api_bridge.events;

import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine_api.events.bus.Events;

@SuppressWarnings("all")
public abstract class APIEventsBus {
    public static Events.IEvent onPhysWorldTick(Events.PhysWorldTickEvent physWorldTickEvent) {
        APIContainer.pushEvent(physWorldTickEvent);
        return physWorldTickEvent;
    }
}