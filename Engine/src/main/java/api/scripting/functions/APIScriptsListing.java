package api.scripting.functions;

import api.scripting.classes.init.InitializationJS;
import api.scripting.classes.world.GameWorldJS;
import api.scripting.classes.world.background.BackgroundJS;
import api.scripting.classes.world.objects.BackgroundPropJS;
import api.scripting.classes.world.objects.EntityJS;
import api.scripting.classes.world.objects.PointLightJS;
import api.scripting.classes.world.objects.PropJS;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.collections.Pair;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class APIScriptsListing {
    public static APIScriptingFunction onInitialization = null;
    public static APIScriptingFunction onMapPreGeneration = null;
    public static APIScriptingFunction onMapPostGeneration = null;
    public static APIScriptingFunction onMapClear = null;
    public static APIScriptingFunction onBackgroundClear = null;
    public static APIScriptingFunction onPhysicsWorldUpdate = null;
    public static APIScriptingFunction onSceneWorldUpdate = null;
    public static APIScriptingFunction onMapSpawnedEntity = null;
    public static APIScriptingFunction onMapSpawnedProp = null;
    public static APIScriptingFunction onMapSpawnedBackgroundProp = null;
    public static APIScriptingFunction onMapSpawnedPointLight = null;

    static final Set<APIScriptingFunction> apiScriptingFunctions = new LinkedHashSet<>();
    private static final String apiScriptTemplate;

    @SafeVarargs
    public static APIScriptingFunction createNewFunction(String name, String description, Pair<Class<?>, String>... args) {
        APIScriptingFunction apiScriptingFunction = new APIScriptingFunction(name, description, args);
        APIScriptsListing.addFunction(apiScriptingFunction);
        return apiScriptingFunction;
    }

    static {
        onInitialization = APIScriptsListing.createNewFunction("onInitialization", "Game objects initialization manager", new Pair<>(InitializationJS.class, "onitializationjs"));
        onMapPreGeneration = APIScriptsListing.createNewFunction("onMapPreGeneration", "Called before map init", new Pair<>(GameWorldJS.class, "gameworldjs"));
        onMapPostGeneration = APIScriptsListing.createNewFunction("onMapPostGeneration", "Called after map init", new Pair<>(GameWorldJS.class, "gameworldjs"));
        onMapClear = APIScriptsListing.createNewFunction("onMapClear", "Called before map clear", new Pair<>(GameWorldJS.class, "gameworldjs"));
        onBackgroundClear = APIScriptsListing.createNewFunction("onBackgroundClear", "Called before background clear", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(BackgroundJS.class, "backgroundjs"));
        onPhysicsWorldUpdate = APIScriptsListing.createNewFunction("onPhysicsWorldUpdate", "Called on physics world update", new Pair<>(GameWorldJS.class, "gameworldjs"));
        onSceneWorldUpdate = APIScriptsListing.createNewFunction("onSceneWorldUpdate", "Called on scene world update", new Pair<>(GameWorldJS.class, "gameworldjs"));
        onMapSpawnedEntity = APIScriptsListing.createNewFunction("onMapSpawnedEntity", "Called when an entity is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(EntityJS.class, "entityjs"));
        onMapSpawnedProp = APIScriptsListing.createNewFunction("onMapSpawnedProp", "Called when a prop is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(PropJS.class, "propjs"));
        onMapSpawnedBackgroundProp = APIScriptsListing.createNewFunction("onMapSpawnedBackgroundProp", "Called when a background prop is spawned from a map template into the background", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(BackgroundJS.class, "backgroundjs"), new Pair<>(BackgroundPropJS.class, "propjs"));
        onMapSpawnedPointLight = APIScriptsListing.createNewFunction("onMapSpawnedPointLight", "Called when a point light is spawned from a map template into the game world", new Pair<>(GameWorldJS.class, "gameworldjs"), new Pair<>(PointLightJS.class, "pointlightjs"));

        Set<APIScriptingFunction> apiDefaultFunctions = new LinkedHashSet<APIScriptingFunction>() {{
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
