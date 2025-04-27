package jgems_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;
import api.system.JGemsAPI;
import jgems_app.AppTest;

public class TestEvents {

    @SubscribeEvent
    public static void onWorldState(EventBus.PhysicsWorldState event) {
        if (event.state == EventBus.State.START) {
            System.out.println("Event 1");
        }

        if (event.state == EventBus.State.END) {
            System.out.println("Event 2");
        }
    }

    @SubscribeEvent
    public static void onWorldUpdate(EventBus.PhysicsWorldUpdate event) {
    }

}
