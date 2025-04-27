package api.system;

import api.application.JGemsApplication;
import api.events.EventBus;
import api.scripting.JGemsAPIScriptingEngine;
import api.scripting.functions.APIScriptingFunction;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.exceptions.JGemsException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

public final class JGemsAPI implements Closeable {
    public static String DEF_API_PACKAGE = "jgems_app";

    private static JGemsAPIData appData = null;
    private static JGemsAPIEditorResources appEditorResources = null;
    private static JGemsAPIScriptingEngine apiScriptingEngine;

    private static JGemsAPI INSTANCE;
    private static JGemsAPIManager M_INSTANCE;

    private static boolean ALLOW_EVENTS = false;
    private Reflections reflections;

    private JGemsAPI() {
        this.reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(JGemsAPI.DEF_API_PACKAGE)).setScanners(Scanners.SubTypes.filterResultsBy(s -> s.startsWith(JGemsAPI.DEF_API_PACKAGE)), Scanners.TypesAnnotated));
    }

    public static void INIT_JGEMS() {
        JGemsAPI.INSTANCE = new JGemsAPI();
        JGemsAPI.M_INSTANCE = new JGemsAPIManager();
    }

    public static boolean ALLOW_EVENTS() {
        return JGemsAPI.ALLOW_EVENTS;
    }

    public static boolean isValid() {
        return JGemsAPI.get() != null;
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

    public static JGemsAPIEditorResources APIEditorResources() {
        return JGemsAPI.appEditorResources;
    }

    public static JGemsAPIScriptingEngine getAPIScripting() {
        return JGemsAPI.apiScriptingEngine;
    }

    public static void clearScriptingEngine() {
        JGemsAPI.getAPIScripting().clearEngine();
    }

    public static void registerScriptBinding(String key, Object value, int scope) {
        JGemsAPI.getAPIScripting().registerScriptBinding(key, value, scope);
    }

    public static void executeScript(@NotNull String script) {
        JGemsAPI.getAPIScripting().executeScript(script);
    }

    public static boolean executeScriptFunction(@Nullable Object[] result, @NotNull APIScriptingFunction apiScriptingFunction, Object... args) {
        return JGemsAPI.getAPIScripting().execFunction(result, apiScriptingFunction, args);
    }

    public static void pushEvent(EventBus.IEvent event) {
        JGemsAPI.getManager().pushEvent(event);
    }

    public void launchAPI() throws JGemsAPIException {
        try {
            JGemsAPI.appData = new JGemsAPIData();
            JGemsAPI.appEditorResources = new JGemsAPIEditorResources();
            JGemsAPI.apiScriptingEngine = new JGemsAPIScriptingEngine(JGemsAPI.appEditorResources);
            JGemsAPI.ALLOW_EVENTS = true;

            Pair<JGemsApplication, JGemsAppEntry> pair = this.createApplication();
            Log.get().debug("Init API-App: id=" + pair.getSecond().id());
            JGemsAPI.getManager().pullDataFromApplication(JGemsAPI.APIEditorResources(), JGemsAPI.APIAppData(), pair);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsAPIException(e);
        } finally {
            this.disposeReflection();
        }
    }

    public JGemsAPIEditorResources launchAPIEditorData() throws JGemsAPIException {
        try {
            JGemsAPI.appEditorResources = new JGemsAPIEditorResources();
            JGemsAPI.ALLOW_EVENTS = false;
            Pair<JGemsApplication, JGemsAppEntry> pair = this.createApplication();
            Log.get().debug("Init API-App(ONLY EDITOR DATA): id=" + pair.getSecond().id());
            JGemsAPI.getManager().pullDataForEditor(pair.getFirst(), JGemsAPI.APIEditorResources());
            return JGemsAPI.APIEditorResources();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsAPIException(e);
        } finally {
            this.disposeReflection();
        }
    }

    public void disposeReflection() {
        this.reflections = null;
        System.gc();
    }

    private Pair<JGemsApplication, JGemsAppEntry> createApplication() throws JGemsException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Set<Class<?>> annotatedClass = this.reflections.getTypesAnnotatedWith(JGemsAppEntry.class);
        if (annotatedClass.size() > 1) {
            throw new JGemsAPIException("Couldn't load more, than 1 JGems3D entry class");
        }
        if (annotatedClass.isEmpty()) {
            throw new JGemsAPIException("Couldn't find JGems3D entry class");
        }
        Optional<Class<?>> aClass = annotatedClass.stream().findAny();
        JGemsAppEntry jGemsAppEntry = aClass.get().getAnnotation(JGemsAppEntry.class);
        Class<?> clazz = aClass.get();
        Constructor<?> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        JGemsApplication application = (JGemsApplication) constructor.newInstance();
        constructor.setAccessible(false);

        Field[] f1 = aClass.get().getDeclaredFields();

        for (Field f : f1) {
            if (f.isAnnotationPresent(JGemsAppInstance.class)) {
                f.setAccessible(true);
                f.set(application, application);
            }
        }

        return new Pair<>(application, jGemsAppEntry);
    }

    @Override
    public void close() {
        JGemsAPI.appData = null;
        JGemsAPI.appEditorResources = null;
        Log.get().debug("CLOSED API");
    }
}