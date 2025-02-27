package api.system;

import api.events.EventBus;
import api.application.JGemsApplication;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

public final class JGemsAPI {
    public static String DEF_API_PACKAGE = "jgems_app";

    private static JGemsAPIData appData = null;

    private static JGemsAPI INSTANCE;
    private static JGemsAPIManager M_INSTANCE;

    static {
        JGemsAPI.INSTANCE = new JGemsAPI();
        JGemsAPI.M_INSTANCE = new JGemsAPIManager();
    }

    private Reflections reflections;

    private JGemsAPI() {
        this.reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(JGemsAPI.DEF_API_PACKAGE)).setScanners(Scanners.SubTypes.filterResultsBy(s -> s.startsWith(JGemsAPI.DEF_API_PACKAGE)), Scanners.TypesAnnotated));
    }

    public static JGemsAPIManager getManager() {
        return JGemsAPI.M_INSTANCE;
    }

    public static JGemsAPI get() {
        return JGemsAPI.INSTANCE;
    }

    public static JGemsAPIData APIAppData() {
        return JGemsAPI.appData;
    }

    public void launchAPI() {
        try {
            JGemsAPI.appData = new JGemsAPIData();
            Pair<JGemsApplication, JGemsAppEntry> pair = this.createApplication(JGemsAPI.APIAppData());
            Log.get().debug("Init API-App: id=" + pair.getSecond().id());
            JGemsAPI.getManager().pullDataFromApplication(JGemsAPI.APIAppData(), pair);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new JGemsRuntimeException(e);
        } finally {
            this.disposeReflection();
        }
    }

    public static void pushEvent(EventBus.IEvent event) {
        JGemsAPI.getManager().pushEvent(event);
    }

    public void disposeReflection() {
        this.reflections = null;
        System.gc();
    }

    private Pair<JGemsApplication, JGemsAppEntry> createApplication(JGemsAPIData appData) throws JGemsException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Set<Class<?>> annotatedClass = this.reflections.getTypesAnnotatedWith(JGemsAppEntry.class);
        if (annotatedClass.size() > 1) {
            throw new JGemsRuntimeException("Couldn't load more, than 1 JGems3D entry class");
        }
        if (annotatedClass.isEmpty()) {
            throw new JGemsRuntimeException("Couldn't find JGems3D entry class");
        }
        Optional<Class<?>> aClass = annotatedClass.stream().findAny();
        JGemsAppEntry jGemsAppEntry = aClass.get().getAnnotation(JGemsAppEntry.class);
        Class<?> clazz = aClass.get();
        Constructor<?> constructor = clazz.getConstructor();
       // constructor.setAccessible(true);
        JGemsApplication application = (JGemsApplication) constructor.newInstance();
       // constructor.setAccessible(false);

        Field[] f1 = aClass.get().getDeclaredFields();

        for (Field f : f1) {
            if (f.isAnnotationPresent(JGemsAppInstance.class)) {
                f.setAccessible(true);
                f.set(application, application);
            }
        }

        return new Pair<>(application, jGemsAppEntry);
    }
}
