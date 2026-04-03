package api.events;


import api.scripting.coding.env.internal.game.init.events.rendering.JSRenderUIEvent;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.system.JGemsAPI;
import api.system.scripting.JavaToJsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static @NotNull EventBus.IEvent pushEvent(EventBus.IEvent event, @Nullable JSEventI jsEventLaunch) {
        if (!JGemsAPI.ALLOW_EVENTS() || !JGemsAPI.isValid()) {
            return new EventBus.IEvent.EmptyEvent();
        }
        JGemsAPI.pushEvent(event);
        if (jsEventLaunch != null) {
            if (event.canBeCancelled() && !event.isCancelled()) {
                if (JavaToJsAPI.Js_GAME_SomeEvent__EVENT(jsEventLaunch)) {
                    if (event.canBeCancelled()) {
                        ((EventBus.Cancellable) event).setCancelled(true);
                    }
                }
            }
        }
        return event;
    }
}