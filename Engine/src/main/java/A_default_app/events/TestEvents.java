package A_default_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;

public class TestEvents {
    @SubscribeEvent
    public static void onRender(EventBus.RenderOGLNodeEvent event) {
    }

   // @SubscribeEvent
   // public static void onWorldState(EventBus.PhysicsWorldState event) {
   // }
//
   // @SubscribeEvent
   // public static void onWorldUpdate(EventBus.PhysicsWorldUpdate event) {
   // }
}
