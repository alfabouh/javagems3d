package jgems_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;
import api.system.JGemsAPI;
import jgems_app.AppTest;

public class TestEvents {
    @SubscribeEvent
    public static void onWorldTick(EventBus.PhysicsWorldState event) {
        if (event.state == EventBus.State.START) {
            JGemsAPI.getAPIScripting().execFunction(null, AppTest.scriptingFunction);
        }
    }
}
