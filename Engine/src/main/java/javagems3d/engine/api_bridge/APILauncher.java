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

package javagems3d.engine.api_bridge;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import javagems3d.engine.api_bridge.data.APIGameInfo;
import javagems3d.engine.api_bridge.data.APITBoxInfo;
import javagems3d.engine.system.service.exceptions.JGemsException;
import javagems3d.engine.system.service.exceptions.JGemsRuntimeException;
import engine_api.app.*;
import engine_api.events.bus.Events;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

public class APILauncher {
    private static APILauncher INSTANCE;

    static {
        APILauncher.INSTANCE = new APILauncher();
    }

    private Reflections reflections;

    private APILauncher() {
        this.reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("jgems_api")).setScanners(Scanners.SubTypes.filterResultsBy(s -> s.startsWith("jgems_api")), Scanners.TypesAnnotated));
    }

    public static APILauncher get() {
        return APILauncher.INSTANCE;
    }

    public void launchGameAPI() {
        try {
            APIContainer.get().setApiGameInfo(this.loadGameInfo());
            APIContainer.get().getApiGameInfo().getAppInstance().loadResources(APIContainer.get().getAppResourceLoader());
            APIContainer.get().getApiGameInfo().getAppInstance().subscribeEvents(APIContainer.get().getAppEventSubscriber());
            APIContainer.loadEventClasses(Events.class, APIContainer.get().getAppEventSubscriber().getClassesWithEvents());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    public void launchToolBoxAPI() {
        try {
            APIContainer.get().setApiTBoxInfo(this.loadTBoxInfo());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    public void disposeReflection() {
        this.reflections = null;
        System.gc();
    }

    private APITBoxInfo loadTBoxInfo() throws JGemsException, InstantiationException, IllegalAccessException {
        Set<Class<?>> annotatedClass = this.reflections.getTypesAnnotatedWith(JGemsTBoxEntry.class);
        if (annotatedClass.size() > 1) {
            throw new JGemsRuntimeException("Couldn't load more than 1 TBox entry class!");
        }
        if (annotatedClass.isEmpty()) {
            throw new JGemsRuntimeException("Couldn't find TBox entry class!");
        }
        Optional<Class<?>> aClass = annotatedClass.stream().findAny();
        JGemsTBoxEntry jGemsTBoxEntry = aClass.get().getAnnotation(JGemsTBoxEntry.class);
        JGemsTBoxApplication application = (JGemsTBoxApplication) aClass.get().newInstance();
        return new APITBoxInfo(application, jGemsTBoxEntry);
    }

    private APIGameInfo loadGameInfo() throws JGemsException, InstantiationException, IllegalAccessException {
        Set<Class<?>> annotatedClass = this.reflections.getTypesAnnotatedWith(JGemsGameEntry.class);
        if (annotatedClass.size() > 1) {
            throw new JGemsRuntimeException("Couldn't load more than 1 JGems3D entry class!");
        }
        if (annotatedClass.isEmpty()) {
            throw new JGemsRuntimeException("Couldn't find JGems3D entry class!");
        }
        Optional<Class<?>> aClass = annotatedClass.stream().findAny();
        JGemsGameEntry jGemsGameEntry = aClass.get().getAnnotation(JGemsGameEntry.class);
        JGemsGameApplication application = (JGemsGameApplication) aClass.get().newInstance();

        Field[] f1 = aClass.get().getDeclaredFields();

        for (Field f : f1) {
            if (f.isAnnotationPresent(JGemsGameInstance.class)) {
                f.setAccessible(true);
                f.set(application, application);
            }
        }

        return new APIGameInfo(application.createAppManager(), application, jGemsGameEntry);
    }

}
