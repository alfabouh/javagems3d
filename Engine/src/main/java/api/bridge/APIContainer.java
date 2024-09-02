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

package api.bridge;

import api.bridge.data.APIGameInfo;
import api.bridge.data.APITBoxInfo;
import org.jetbrains.annotations.NotNull;
import javagems3d.JGemsHelper;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import api.app.main.tbox.TBoxEntitiesObjectData;
import api.app.main.tbox.TBoxEntitiesUserData;
import api.app.events.AppEventSubscriber;
import api.app.events.SubscribeEvent;
import api.app.events.bus.Events;
import api.app.resources.AppResourceLoader;
import logger.SystemLogging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class APIContainer {
    private static APIContainer INSTANCE;

    static {
        APIContainer.INSTANCE = new APIContainer();
    }

    private final HashMap<Class<Events.IEvent>, TreeSet<PriorityMethod>> eventMap;
    private final AppResourceLoader appResourceLoader;
    private final AppEventSubscriber appEventSubscriber;
    private final TBoxEntitiesObjectData tBoxEntitiesObjectData;
    private final TBoxEntitiesUserData tBoxEntitiesUserData;
    private APITBoxInfo apiTBoxInfo;
    private APIGameInfo apiGameInfo;

    private APIContainer() {
        this.apiGameInfo = null;
        this.apiTBoxInfo = null;
        this.appResourceLoader = new AppResourceLoader();
        this.appEventSubscriber = new AppEventSubscriber();
        this.tBoxEntitiesObjectData = new TBoxEntitiesObjectData();
        this.tBoxEntitiesUserData = new TBoxEntitiesUserData();

        this.eventMap = new HashMap<>();
    }

    public static void pushEvent(Events.IEvent event) {
        if (!APIContainer.INSTANCE.eventMap.containsKey(event.getClass())) {
            return;
        }
        for (PriorityMethod priorityMethod : APIContainer.INSTANCE.eventMap.get(event.getClass())) {
            Method method = priorityMethod.getMethod();
            if (method == null) {
                SystemLogging.get().getLogManager().warn("Couldn't find event " + event.getClass().getName() + " in API Container!");
                return;
            }
            try {
                method.invoke(Events.IEvent.class, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new JGemsRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("all")
    static void loadEventClasses(Class<?> APIEventsClass, Set<Class<?>> classSet) {
        {
            Class<?>[] eventClasses = APIEventsClass.getClasses();
            for (Class<?> cl : eventClasses) {
                Class<?>[] interfaces = cl.getInterfaces();
                if (interfaces.length == 1 && interfaces[0] == Events.IEvent.class) {
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
                    APIContainer.INSTANCE.eventMap.put((Class<Events.IEvent>) cl, new TreeSet<PriorityMethod>(Comparator.comparingInt(PriorityMethod::getPriority).thenComparingInt(System::identityHashCode)));
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
                    if (interfaces.length != 1 || interfaces[0] != Events.IEvent.class) {
                        JGemsHelper.getLogger().error("Method has wrong argument(? -> IEvent): " + method.getName());
                        continue;
                    }
                    TreeSet<PriorityMethod> priorityMethods = APIContainer.INSTANCE.eventMap.get(parameters[0]);
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

    public static APIContainer get() {
        return APIContainer.INSTANCE;
    }

    public @NotNull AppResourceLoader getAppResourceLoader() {
        return this.appResourceLoader;
    }

    public @NotNull AppEventSubscriber getAppEventSubscriber() {
        return this.appEventSubscriber;
    }

    public @NotNull TBoxEntitiesObjectData getTBoxEntitiesObjectData() {
        return this.tBoxEntitiesObjectData;
    }

    public @NotNull TBoxEntitiesUserData getTBoxEntitiesUserData() {
        return this.tBoxEntitiesUserData;
    }

    public @NotNull APITBoxInfo getApiTBoxInfo() {
        return this.apiTBoxInfo;
    }

    void setApiTBoxInfo(APITBoxInfo apiTBoxInfo) {
        this.apiTBoxInfo = apiTBoxInfo;
    }

    public @NotNull APIGameInfo getApiGameInfo() {
        return this.apiGameInfo;
    }

    void setApiGameInfo(APIGameInfo apiGameInfo) {
        this.apiGameInfo = apiGameInfo;
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
