package api.events;


import api.system.JGemsAPI;

@SuppressWarnings("all")
public abstract class EventLauncher {
    public static EventBus.IEvent pushEvent(EventBus.IEvent event) {
        JGemsAPI.pushEvent(event);
        return event;
    }
}