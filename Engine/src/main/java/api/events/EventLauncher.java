package api.events;


import api.scripting.coding.env.internal.util.events.JSEventI;
import api.system.JGemsAPI;
import api.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static @NotNull EventBus.IEvent pushEvent(EventBus.IEvent event, @Nullable Pair<JSEventI, JavaToJsAPI.Target> jsEventLaunch) {
        if (!JGemsAPI.ALLOW_EVENTS() || !JGemsAPI.isValid()) {
            return new EventBus.IEvent.EmptyEvent();
        }
        JGemsAPI.pushEvent(event);
        if (jsEventLaunch != null) {
            if (event.canBeCancelled() && !event.isCancelled()) {
                if (JavaToJsAPI.Js_GAME_SomeEvent__EVENT(jsEventLaunch.first(), jsEventLaunch.second())) {
                    if (event.canBeCancelled() && event.isCancelled()) {
                        ((EventBus.Cancellable) event).setCancelled(true);
                    }
                }
            }
        }
        return event;
    }
}