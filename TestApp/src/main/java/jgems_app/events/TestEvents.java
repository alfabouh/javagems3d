package jgems_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;

public class TestEvents {
    @SubscribeEvent
    public static void onWorldTick(EventBus.PhysWorldTickPre event) {
        event.setCancelled(false);
    }
}
