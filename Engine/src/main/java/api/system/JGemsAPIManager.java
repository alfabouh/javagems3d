package api.system;

import api.application.JGemsApplication;
import api.application.events.AppEventSubscriber;
import api.application.events.SubscribeEvent;
import api.application.resources.AppResources;
import api.application.scripts.AppScriptContextContextRegistry;
import api.application.workbench.manager.APIWBenchDataManager;
import api.events.EventBus;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
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
    private final AppScriptContextContextRegistry appScriptContextRegistry;

    JGemsAPIManager() {
        this.eventMap = new HashMap<>();
        this.appScriptContextRegistry = new AppScriptContextContextRegistry();
    }

    void pullDataFromApplication(JGemsAPIEditorResources apiEditorResources, JGemsAPIData appData, Pair<JGemsApplication, JGemsAppEntry> pair) {
        JGemsApplication jGemsApplication = pair.first();
        JGemsAppEntry jGemsAppEntry = pair.second();

        final AppEventSubscriber appEventSubscriber = new AppEventSubscriber();
        final AppResources appResources = new AppResources();

        jGemsApplication.initEvents(appEventSubscriber);
        jGemsApplication.initResources(appResources);
        jGemsApplication.initScripts(this.getAppScriptRegistry());
        this.initEvents(EventBus.class, appEventSubscriber.getClassesWithEvents());

        appData.setApplication(pair.first());
        appData.setId(jGemsAppEntry.id());
        appData.setAppResources(appResources);
        appData.setAppEventSubscriber(appEventSubscriber);

        this.pullDataForEditor(jGemsApplication, apiEditorResources);
    }

    public void pullDataForEditor(JGemsApplication jGemsApplication, JGemsAPIEditorResources apiEditorResources) {
        APIWBenchDataManager APIWBenchDataManager = new APIWBenchDataManager();
        jGemsApplication.setupEditorResources(APIWBenchDataManager);
        jGemsApplication.initScripts(this.getAppScriptRegistry());
        apiEditorResources.setEditorResourcesManager(APIWBenchDataManager);
    }

    public void pushEvent(EventBus.IEvent event) {
        if (!this.eventMap.containsKey(event.getClass())) {
            return;
        }
        for (PriorityMethod priorityMethod : this.eventMap.get(event.getClass())) {
            Method method = priorityMethod.method();
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
                if (interfaces.length == 1 && EventBus.IEvent.class.isAssignableFrom(interfaces[0])) {
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
                    this.eventMap.put((Class<EventBus.IEvent>) cl, new TreeSet<PriorityMethod>(Comparator.comparingInt(PriorityMethod::priority).thenComparingInt(System::identityHashCode)));
                    Log.get().debug("Created API ClassEvent: " + cl.getName());
                }
            }
        }
        {
            for (Class<?> clazz : classSet) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(SubscribeEvent.class)) {
                        Class<?>[] parameters = method.getParameterTypes();
                        if (parameters.length != 1) {
                            Log.get().error("Method has more(or less) than 1 argument(? -> IEvent): " + method.getName() + " - Skip");
                            continue;
                        }
                        if (parameters.length != 1 || !EventBus.IEvent.class.isAssignableFrom(parameters[0])) {
                            Log.get().error("Method has wrong argument(? -> IEvent): " + method.getName());
                            continue;
                        }
                        TreeSet<PriorityMethod> priorityMethods = this.eventMap.get(parameters[0]);
                        if (priorityMethods == null) {
                            Log.get().error("Couldn't find event in class: " + clazz.getName());
                            continue;
                        }
                        SubscribeEvent subscribeEvent = method.getAnnotation(SubscribeEvent.class);
                        priorityMethods.add(new PriorityMethod(method, subscribeEvent.priority()));
                    }
                }
            }
        }
    }

    public AppScriptContextContextRegistry getAppScriptRegistry() {
        return this.appScriptContextRegistry;
    }

    private record PriorityMethod(Method method, int priority) {
    }
}