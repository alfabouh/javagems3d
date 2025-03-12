package api.system;

import api.application.workbench.manager.APIWBenchDataManager;
import api.events.EventBus;
import api.application.JGemsApplication;
import api.application.resources.AppResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
import api.application.events.AppEventSubscriber;
import api.application.events.SubscribeEvent;
import logger.Log;
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

    void pullDataFromApplication(JGemsAPIEditorResources apiEditorResources, JGemsAPIData appData, Pair<JGemsApplication, JGemsAppEntry> pair) {
        JGemsApplication jGemsApplication = pair.getFirst();
        JGemsAppEntry jGemsAppEntry = pair.getSecond();

        final AppEventSubscriber appEventSubscriber = new AppEventSubscriber();
        final AppResources appResources = new AppResources();

        jGemsApplication.initEvents(appEventSubscriber);
        jGemsApplication.initResources(appResources);
        this.initEvents(EventBus.class, appEventSubscriber.getClassesWithEvents());

        appData.setApplication(pair.getFirst());
        appData.setId(jGemsAppEntry.id());
        appData.setAppResources(appResources);
        appData.setAppEventSubscriber(appEventSubscriber);

        this.pullDataForEditor(jGemsApplication, apiEditorResources);
    }

    public void pullDataForEditor(JGemsApplication jGemsApplication, JGemsAPIEditorResources apiEditorResources) {
        APIWBenchDataManager APIWBenchDataManager = new APIWBenchDataManager();
        jGemsApplication.setupEditorResources(APIWBenchDataManager);
        apiEditorResources.setEditorResourcesManager(APIWBenchDataManager);
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
                throw new JGemsAPIException(e);
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
                        Log.get().error(cl.getName() + " should be final class");
                        continue;
                    }
                    if (!Modifier.isPublic(cl.getModifiers())) {
                        Log.get().error(cl.getName() + " should be public class");
                        continue;
                    }
                    if (!Modifier.isStatic(cl.getModifiers())) {
                        Log.get().error(cl.getName() + " should be static class");
                        continue;
                    }
                    this.eventMap.put((Class<EventBus.IEvent>) cl, new TreeSet<PriorityMethod>(Comparator.comparingInt(PriorityMethod::getPriority).thenComparingInt(System::identityHashCode)));
                    Log.get().debug("Created API ClassEvent: " + cl.getName());
                }
            }
        }
        {
            for (Class<?> clazz : classSet) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length != 1) {
                        Log.get().error("Method has more(or less) than 1 argument(? -> IEvent): " + method.getName());
                        continue;
                    }
                    Class<?>[] interfaces = parameters[0].getInterfaces();
                    if (interfaces.length != 1 || interfaces[0] != EventBus.IEvent.class) {
                        Log.get().error("Method has wrong argument(? -> IEvent): " + method.getName());
                        continue;
                    }
                    TreeSet<PriorityMethod> priorityMethods = this.eventMap.get(parameters[0]);
                    if (priorityMethods == null) {
                        Log.get().error("Couldn't find event with name: " + clazz.getName());
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