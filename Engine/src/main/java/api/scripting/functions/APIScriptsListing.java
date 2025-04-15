package api.scripting.functions;

import api.scripting.classes.init.InitializationJS;
import api.scripting.classes.world.GameWorldJS;
import javagems3d.JGems3D;
import javagems3d.system.core.JGemsCore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class APIScriptsListing {
    private static final String apiScriptTemplate;
    static final Set<APIScriptingFunction> apiScriptingFunctions = new HashSet<>();

    public static final APIScriptingFunction onInitialization = new APIScriptingFunction("onInitialization", InitializationJS.class);
    public static final APIScriptingFunction onWorldPreGeneration = new APIScriptingFunction("onWorldPreGeneration", GameWorldJS.class);
    public static final APIScriptingFunction onWorldPostGeneration = new APIScriptingFunction("onWorldPostGeneration", GameWorldJS.class);
    public static final APIScriptingFunction onWorldUpdate = new APIScriptingFunction("onWorldUpdate", GameWorldJS.class);

    static {
        Set<APIScriptingFunction> apiDefaultFunctions = new HashSet<APIScriptingFunction>()
        {{
            add(APIScriptsListing.onInitialization);
            add(APIScriptsListing.onWorldPreGeneration);
            add(APIScriptsListing.onWorldPostGeneration);
            add(APIScriptsListing.onWorldUpdate);
        }};

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("//AUTO-GENERATED " + JGemsCore.ENG_VER);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        for (APIScriptingFunction apiScriptingFunction : apiDefaultFunctions) {
            stringBuilder.append(apiScriptingFunction.toString());
            stringBuilder.append("(");

            List<Class<?>> classList = apiScriptingFunction.getArgs();
            final int size = classList.size();
            for (int i = 0; i < size; i++) {
                Class<?> clazz = classList.get(i);
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
                Class<?> expectedClass = function.getArgs().get(i);
                Object actualArg = args[i];
                if (actualArg == null || !expectedClass.isAssignableFrom(actualArg.getClass())) {
                    return false;
                }
            }
            return args.length == function.getArgs().size();
        } else return args == null && function.getArgs() == null;
    }
}
