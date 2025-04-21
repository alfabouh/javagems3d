package api.scripting.functions;

import api.scripting.classes.init.InitializationJS;
import api.scripting.classes.world.background.BackgroundJS;
import api.scripting.classes.world.objects.BackgroundPropJS;
import api.scripting.classes.world.objects.EntityJS;
import api.scripting.classes.world.GameWorldJS;
import api.scripting.classes.world.objects.PointLightJS;
import api.scripting.classes.world.objects.PropJS;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.collections.Pair;

import java.util.*;

public abstract class APIScriptsListing {
    private static final String apiScriptTemplate;
    static final Set<APIScriptingFunction> apiScriptingFunctions = new LinkedHashSet<>();

    public static final APIScriptingFunction onInitialization = new APIScriptingFunction("onInitialization", "Game objects initialization manager", new Pair<>(InitializationJS.class, "onitializationjs"));
    public static final APIScriptingFunction onMapPreGeneration = new APIScriptingFunction("onMapPreGeneration", "Called before map init", new Pair<>(GameWorldJS.class, "gameworldjs"));
    public static final APIScriptingFunction onMapPostGeneration = new APIScriptingFunction("onMapPostGeneration", "Called after map init", new Pair<>(GameWorldJS.class, "gameworldjs"));
    public static final APIScriptingFunction onMapClear = new APIScriptingFunction("onMapClear", "Called before map clear", new Pair<>(GameWorldJS.class, "gameworldjs"));
    public static final APIScriptingFunction onBackgroundClear = new APIScriptingFunction("onBackgroundClear", "Called before background clear", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(BackgroundJS.class, "backgroundjs"));
    public static final APIScriptingFunction onPhysicsWorldUpdate = new APIScriptingFunction("onPhysicsWorldUpdate", "Called on physics world update", new Pair<>(GameWorldJS.class, "gameworldjs"));
    public static final APIScriptingFunction onSceneWorldUpdate = new APIScriptingFunction("onSceneWorldUpdate", "Called on scene world update", new Pair<>(GameWorldJS.class, "gameworldjs"));

    public static final APIScriptingFunction onMapSpawnedEntity = new APIScriptingFunction("onMapSpawnedEntity", "Called when an entity is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(EntityJS.class, "entityjs"));
    public static final APIScriptingFunction onMapSpawnedProp = new APIScriptingFunction("onMapSpawnedProp", "Called when a prop is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(PropJS.class, "propjs"));
    public static final APIScriptingFunction onMapSpawnedBackgroundProp = new APIScriptingFunction("onMapSpawnedBackgroundProp", "Called when a background prop is spawned from a map template into the background", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(BackgroundJS.class, "backgroundjs"), new Pair<>(BackgroundPropJS.class, "propjs"));
    public static final APIScriptingFunction onMapSpawnedPointLight = new APIScriptingFunction("onMapSpawnedPointLight", "Called when a point light is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(PointLightJS.class, "pointlightjs"));

    static {
        APIScriptsListing.addFunction(APIScriptsListing.onInitialization);
        APIScriptsListing.addFunction(APIScriptsListing.onMapPreGeneration);
        APIScriptsListing.addFunction(APIScriptsListing.onMapPostGeneration);
        APIScriptsListing.addFunction(APIScriptsListing.onMapClear);
        APIScriptsListing.addFunction(APIScriptsListing.onPhysicsWorldUpdate);
        APIScriptsListing.addFunction(APIScriptsListing.onSceneWorldUpdate);
        APIScriptsListing.addFunction(APIScriptsListing.onMapSpawnedEntity);
        APIScriptsListing.addFunction(APIScriptsListing.onMapSpawnedProp);
        APIScriptsListing.addFunction(APIScriptsListing.onMapSpawnedPointLight);
    }

    static {
        Set<APIScriptingFunction> apiDefaultFunctions = new LinkedHashSet<APIScriptingFunction>()
        {{
            add(APIScriptsListing.onInitialization);
            add(APIScriptsListing.onMapPreGeneration);
            add(APIScriptsListing.onMapPostGeneration);
            add(APIScriptsListing.onPhysicsWorldUpdate);
            add(APIScriptsListing.onSceneWorldUpdate);
        }};

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("//AUTO-GENERATED " + JGemsCore.ENG_VER);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        for (APIScriptingFunction apiScriptingFunction : apiDefaultFunctions) {
            stringBuilder.append(apiScriptingFunction.toString());
            stringBuilder.append("(");

            List<Pair<Class<?>, String>> classList = apiScriptingFunction.getArgs();
            final int size = classList.size();
            for (int i = 0; i < size; i++) {
                Class<?> clazz = classList.get(i).getFirst();
                String className = clazz.getSimpleName();
                stringBuilder.append(className.toLowerCase());
                if (i != size - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(")");
            stringBuilder.append(" {");
            stringBuilder.append("\n");
            stringBuilder.append("  //Your code.");
            stringBuilder.append("\n");
            stringBuilder.append("}");

            stringBuilder.append("\n");
            stringBuilder.append("\n");
        }

        apiScriptTemplate = stringBuilder.toString();
    }

    public static Set<APIScriptingFunction> getAllFunctions() {
        return APIScriptsListing.apiScriptingFunctions;
    }

    public static String getApiScriptTemplate() {
        return APIScriptsListing.apiScriptTemplate;
    }

    public static void addFunction(APIScriptingFunction function) {
        APIScriptsListing.apiScriptingFunctions.add(function);
    }

    public static boolean check(APIScriptingFunction function, Object... args) {
        if (!APIScriptsListing.apiScriptingFunctions.contains(function)) {
            return false;
        }
        if (args != null && function.getArgs() != null) {
            for (int i = 0; i < args.length; i++) {
                Class<?> expectedClass = function.getArgs().get(i).getFirst();
                Object actualArg = args[i];
                if (actualArg == null || !expectedClass.isAssignableFrom(actualArg.getClass())) {
                    return false;
                }
            }
            return args.length == function.getArgs().size();
        } else return args == null && function.getArgs() == null;
    }
}
