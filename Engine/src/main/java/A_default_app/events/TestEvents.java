package A_default_app.events;

import api.application.events.SubscribeEvent;
import api.events.EventBus;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultGamePanel;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.DefaultPausePanel;
import javagems3d.help.JGemsHelper;

public class TestEvents {
    @SubscribeEvent
    public static void onPause(EventBus.OnPauseFromButtonPressEvent event) {
        JGemsHelper.ui().openPanel(new DefaultPausePanel(null));
    }

    @SubscribeEvent
    public static void onUnPause(EventBus.OnUnPauseFromButtonPressEvent event) {
        JGemsHelper.ui().openPanel(new DefaultGamePanel(null));
    }

   // @SubscribeEvent
   // public static void onWorldState(EventBus.PhysicsWorldState event) {
   // }
//
   // @SubscribeEvent
   // public static void onWorldUpdate(EventBus.PhysicsWorldUpdate event) {
   // }
}
