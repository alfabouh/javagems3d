package jgems_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;
import api.system.JGemsAPI;
import jgems_app.AppTest;

public class TestEvents {

    @SubscribeEvent
    public static void onWorldState(EventBus.PhysicsWorldState event) {
    }

    @SubscribeEvent
    public static void onWorldUpdate(EventBus.PhysicsWorldUpdate event) {
    }
}
