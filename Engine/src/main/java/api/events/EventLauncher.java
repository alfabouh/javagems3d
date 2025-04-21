package api.events;


import api.system.JGemsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static @NotNull EventBus.IEvent pushEvent(EventBus.IEvent event) {
        if (!JGemsAPI.ALLOW_EVENTS() || !JGemsAPI.isValid()) {
            return new EventBus.IEvent.EmptyEvent();
        }
        JGemsAPI.pushEvent(event);
        return event;
    }
}