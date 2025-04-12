package api.scripting.functions;

import api.scripting.classes.init.InitializationJS;
import api.scripting.classes.world.GameWorldJS;

import java.util.HashSet;
import java.util.Set;

public abstract class APIScriptingFunctions {
    static final Set<APIScriptingFunction> apiScriptingFunctions = new HashSet<>();

    public static final APIScriptingFunction onInitialization = new APIScriptingFunction("onInitialization", InitializationJS.class);
    public static final APIScriptingFunction onWorldPreGeneration = new APIScriptingFunction("onWorldPreGeneration", GameWorldJS.class);
    public static final APIScriptingFunction onWorldPostGeneration = new APIScriptingFunction("onWorldPostGeneration", GameWorldJS.class);
    public static final APIScriptingFunction onWorldUpdate = new APIScriptingFunction("onWorldUpdate", GameWorldJS.class);

    public static void addFunction(APIScriptingFunction function) {
        APIScriptingFunctions.apiScriptingFunctions.add(function);
    }

    public static boolean check(APIScriptingFunction function, Object... args) {
        if (!APIScriptingFunctions.apiScriptingFunctions.contains(function)) {
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
