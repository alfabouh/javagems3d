package api.system;

import api.application.JGemsApplication;
import api.events.EventBus;
import api.scripting.JGemsAPIScriptingCore;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.exceptions.JGemsException;
import logger.Log;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.Set;

public final class JGemsAPI implements Closeable {
    public static String DEF_API_PACKAGE = "jgems_app";

    private static JGemsAPIData appData = null;
    private static JGemsAPIEditorResources appEditorResources = null;
    private static JGemsAPIScriptingCore apiScriptingCore;

    private static JGemsAPI INSTANCE;
    private static JGemsAPIManager M_INSTANCE;

    private static boolean ALLOW_EVENTS = false;
    private static String EXTERNAL_CLASS_API_DEF = null;

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

    public static JGemsAPIScriptingCore getAPIScriptingCore() {
        return apiScriptingCore;
    }

    public static void pushEvent(EventBus.IEvent event) {
        JGemsAPI.getManager().pushEvent(event);
    }

    public void launchAPI(@Nullable String apiAppClasspath) throws JGemsAPIException {
        try {
            JGemsAPI.appData = new JGemsAPIData();
            JGemsAPI.appEditorResources = new JGemsAPIEditorResources();
            JGemsAPI.apiScriptingCore = new JGemsAPIScriptingCore();
            JGemsAPI.ALLOW_EVENTS = true;
            Pair<JGemsApplication, JGemsAppEntry> pair = this.createApplication(apiAppClasspath);
            Log.get().debug("Init API-App: id=" + pair.second().id());
            JGemsAPI.getManager().pullDataFromApplication(JGemsAPI.APIEditorResources(), JGemsAPI.APIAppData(), pair);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsAPIException(e);
        } finally {
            this.dispose();
        }
    }

    public JGemsAPIEditorResources launchAPIEditorData(@Nullable String apiAppClasspath) throws JGemsAPIException {
        try {
            JGemsAPI.appEditorResources = new JGemsAPIEditorResources();
            JGemsAPI.apiScriptingCore = new JGemsAPIScriptingCore();
            JGemsAPI.ALLOW_EVENTS = false;
            Pair<JGemsApplication, JGemsAppEntry> pair = this.createApplication(apiAppClasspath);
            Log.get().debug("Init API-App(ONLY EDITOR DATA): id=" + pair.second().id());
            JGemsAPI.getManager().pullDataForEditor(pair.first(), JGemsAPI.APIEditorResources());
            JGemsAPI.apiScriptingCore.scanJavaCodeGame();
            JGemsAPI.apiScriptingCore.scanJavaCodeMap();
            return JGemsAPI.APIEditorResources();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsAPIException(e);
        } finally {
            this.dispose();
        }
    }

    public void dispose() {
        this.reflections = null;
        System.gc();
    }

    private Pair<Class<?>, JGemsAppEntry> findApiAppInFile() {
        try {
            File dir = new File("./api");
            if (!dir.exists() || !dir.isDirectory()) {
                throw new JGemsAPIException("'api' directory not found");
            }

            File[] jars = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
            if (jars == null || jars.length == 0) {
                throw new JGemsAPIException("No .jar found in 'api'");
            }
            if (jars.length > 1) {
                throw new JGemsAPIException("Multiple .jar found in 'api', expected exactly one");
            }
            File jar = jars[0];
            URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Reflections reflections = new Reflections(new ConfigurationBuilder().addClassLoaders(loader).setUrls(ClasspathHelper.forClassLoader(loader)).setScanners(Scanners.TypesAnnotated));
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(JGemsAppEntry.class);
            if (classes.isEmpty()) {
                throw new JGemsAPIException("No JGemsAppEntry found in jar: " + jar.getName());
            }
            if (classes.size() > 1) {
                throw new JGemsAPIException("Multiple JGemsAppEntry classes found in jar: " + jar.getName());
            }
            Class<?> clazz = classes.iterator().next();
            JGemsAppEntry entry = clazz.getAnnotation(JGemsAppEntry.class);
            return new Pair<>(clazz, entry);
        } catch (Exception e) {
            Log.get().error("Couldn't find ANY API in 'api' directory! Returned default", e);
            return null;
        }
    }

    private Pair<JGemsApplication, JGemsAppEntry> createApplication(@Nullable String apiAppClasspath) throws JGemsException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = null;
        JGemsAppEntry jGemsAppEntry = null;
        try {
            if (apiAppClasspath != null && !apiAppClasspath.equals("null")) {
                clazz = Class.forName(apiAppClasspath);
                jGemsAppEntry = clazz.getAnnotation(JGemsAppEntry.class);
                JGemsAPI.EXTERNAL_CLASS_API_DEF = apiAppClasspath;
            } else {
                Pair<Class<?>, JGemsAppEntry> found = this.findApiAppInFile();
                if (found == null) {
                    Log.get().error("Couldn't find JGems3D entry class, Returned Default!");
                    clazz = Class.forName("A_default_app.AppDefault");
                    jGemsAppEntry = clazz.getAnnotation(JGemsAppEntry.class);
                } else {
                    clazz = found.first();
                    jGemsAppEntry = found.second();
                }
            }
        } catch (Exception e) {
            throw new JGemsAPIException("Couldn't open ANY API ENTRY!", e);
        }
        Log.get().info("Found JGems3D entry class: " + clazz.getName());
        Constructor<?> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        JGemsApplication application = (JGemsApplication) constructor.newInstance();
        constructor.setAccessible(false);

        Field[] f1 = clazz.getDeclaredFields();

        for (Field f : f1) {
            if (f.isAnnotationPresent(JGemsAppInstance.class)) {
                f.setAccessible(true);
                f.set(application, application);
            }
        }

        return new Pair<>(application, jGemsAppEntry);
    }

    public static String getExternalClassApiDef() {
        return EXTERNAL_CLASS_API_DEF;
    }

    @Override
    public void close() {
        JGemsAPI.appData = null;
        JGemsAPI.appEditorResources = null;
        JGemsAPI.getAPIScriptingCore().clearGame();
        JGemsAPI.getAPIScriptingCore().clearMap();
        Log.get().debug("CLOSED API");
    }
}