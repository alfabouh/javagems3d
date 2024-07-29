package ru.jgems3d.engine_api.events;

import java.util.HashSet;
import java.util.Set;

public final class AppEventSubscriber implements IAppEventSubscriber {
    private final Set<Class<?>> classesWithEvents;

    public AppEventSubscriber() {
        this.classesWithEvents = new HashSet<>();
    }

    @Override
    public void addClassWithEvents(Class<?> clazz) {
        this.getClassesWithEvents().add(clazz);
    }

    public Set<Class<?>> getClassesWithEvents() {
        return this.classesWithEvents;
    }
}
