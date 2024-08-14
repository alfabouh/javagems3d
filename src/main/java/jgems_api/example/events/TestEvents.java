package jgems_api.example.events;

import ru.jgems3d.engine_api.events.SubscribeEvent;
import ru.jgems3d.engine_api.events.bus.Events;

public class TestEvents {
    @SubscribeEvent
    public static void onWorldTick(Events.PhysWorldTickPre event) {
        event.setCancelled(false);
    }
}
