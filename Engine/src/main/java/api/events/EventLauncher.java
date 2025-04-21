package api.events;


import api.system.JGemsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static @NotNull EventBus.IEvent pushEvent(EventBus.IEvent event) {
        if (!JGemsAPI.isValid()) {
            return null;
        }
        JGemsAPI.pushEvent(event);
        return event;
    }
}