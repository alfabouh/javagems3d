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

package api.newer.system;

import api.newer.events.EventBus;
import api.newer.application.JGemsApplication;
import api.newer.application.resources.AppResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.JGemsHelper;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import api.newer.application.events.AppEventSubscriber;
import api.newer.application.events.SubscribeEvent;
import logger.SystemLogging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public final class JGemsAPIManager {
    private final HashMap<Class<EventBus.IEvent>, TreeSet<PriorityMethod>> eventMap;

    JGemsAPIManager() {
        this.eventMap = new HashMap<>();
    }

    void pullDataFromApplication(JGemsAPIData appData, Pair<JGemsApplication, JGemsAppEntry> pair) {
        JGemsApplication jGemsApplication = pair.getFirst();
        JGemsAppEntry jGemsAppEntry = pair.getSecond();

        final AppEventSubscriber appEventSubscriber = new AppEventSubscriber();
        final AppResources appResources = new AppResources();

        jGemsApplication.initEvents(appEventSubscriber);
        jGemsApplication.initResources(appResources);
        this.initEvents(EventBus.class, appEventSubscriber.getClassesWithEvents());

        appData.setApplication(pair.getFirst());
        appData.setId(pair.getSecond().id());
        appData.setAppResources(appResources);
        appData.setAppEventSubscriber(appEventSubscriber);
    }

    public void pushEvent(EventBus.IEvent event) {
        if (!this.eventMap.containsKey(event.getClass())) {
            return;
        }
        for (PriorityMethod priorityMethod : this.eventMap.get(event.getClass())) {
            Method method = priorityMethod.getMethod();
            if (method == null) {
                SystemLogging.get().getLogManager().warn("Couldn't find event " + event.getClass().getName() + " in API Container");
                return;
            }
            try {
                method.invoke(EventBus.IEvent.class, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new JGemsRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("all")
    void initEvents(Class<?> APIEventsClass, Set<Class<?>> classSet) {
        {
            Class<?>[] eventClasses = APIEventsClass.getClasses();
            for (Class<?> cl : eventClasses) {
                Class<?>[] interfaces = cl.getInterfaces();
                if (interfaces.length == 1 && interfaces[0] == EventBus.IEvent.class) {
                    if (!Modifier.isFinal(cl.getModifiers())) {
                        JGemsHelper.getLogger().error(cl.getName() + " should be final class");
                        continue;
                    }
                    if (!Modifier.isPublic(cl.getModifiers())) {
                        JGemsHelper.getLogger().error(cl.getName() + " should be public class");
                        continue;
                    }
                    if (!Modifier.isStatic(cl.getModifiers())) {
                        JGemsHelper.getLogger().error(cl.getName() + " should be static class");
                        continue;
                    }
                    this.eventMap.put((Class<EventBus.IEvent>) cl, new TreeSet<PriorityMethod>(Comparator.comparingInt(PriorityMethod::getPriority).thenComparingInt(System::identityHashCode)));
                    JGemsHelper.getLogger().debug("Created API ClassEvent: " + cl.getName());
                }
            }
        }
        {
            for (Class<?> clazz : classSet) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length != 1) {
                        JGemsHelper.getLogger().error("Method has more(or less) than 1 argument(? -> IEvent): " + method.getName());
                        continue;
                    }
                    Class<?>[] interfaces = parameters[0].getInterfaces();
                    if (interfaces.length != 1 || interfaces[0] != EventBus.IEvent.class) {
                        JGemsHelper.getLogger().error("Method has wrong argument(? -> IEvent): " + method.getName());
                        continue;
                    }
                    TreeSet<PriorityMethod> priorityMethods = this.eventMap.get(parameters[0]);
                    if (priorityMethods == null) {
                        JGemsHelper.getLogger().error("Couldn't find event with name: " + clazz.getName());
                        continue;
                    }
                    if (method.isAnnotationPresent(SubscribeEvent.class)) {
                        SubscribeEvent subscribeEvent = method.getAnnotation(SubscribeEvent.class);
                        priorityMethods.add(new PriorityMethod(method, subscribeEvent.priority()));
                    }
                }
            }
        }
    }

    private static class PriorityMethod {
        private final Method method;
        private final int priority;

        public PriorityMethod(Method method, int priority) {
            this.method = method;
            this.priority = priority;
        }

        public Method getMethod() {
            return this.method;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}