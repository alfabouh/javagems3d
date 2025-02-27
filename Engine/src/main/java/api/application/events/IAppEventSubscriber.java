package api.application.events;

public interface IAppEventSubscriber {
    void addClassWithEvents(Class<?> clazz);
}
