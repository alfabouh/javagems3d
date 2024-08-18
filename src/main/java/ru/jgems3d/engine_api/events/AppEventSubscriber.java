/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

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
