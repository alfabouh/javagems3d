package api.scripting;

import api.scripting.classes.global.GlobalJS;
import api.scripting.classes.init.InitializationJS;
import api.scripting.classes.init.logging.LogJS;
import api.scripting.classes.util.Vec3f;
import api.scripting.classes.util.Vec4f;
import api.scripting.classes.world.GameWorldJS;
import api.scripting.functions.APIScriptingFunction;
import api.scripting.functions.APIScriptsListing;
import api.system.JGemsAPIEditorResources;
import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsAPIException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.*;

public final class JGemsAPIScriptingEngine {
    private final JGemsAPIScriptingManaging scriptingManaging;
    private final JGemsAPIEditorResources apiEditorResources;
    private final GameWorldJS gameWorldJS;
    private ScriptEngine engine;

    public JGemsAPIScriptingEngine(@NotNull JGemsAPIEditorResources apiEditorResources) {
        this.scriptingManaging = new JGemsAPIScriptingManaging();
        this.apiEditorResources = apiEditorResources;
        this.gameWorldJS = new GameWorldJS(this.getScriptingManaging());
        this.clearEngine();
    }

    public static void warn(APIScriptingFunction apiScriptingFunction) {
        Log.get().warn("Couldn't execute script: " + apiScriptingFunction);
    }

    private void eval(String script) {
        try {
            this.getEngine().eval(script);
        } catch (ScriptException e) {
            throw new JGemsAPIException(e);
        }
    }

    public void executeScript(String script) {
        Log.get().debug("API executing script...");
        this.eval(script);
    }

    public InitializationJS createInitializationJS() {
        return new InitializationJS(JGems3D.get().getResourceManager(), this.getApiEditorResources().getEditorResourcesManager(), this.getScriptingManaging());
    }

    public void registerScriptBinding(String key, Object value, int scope) {
        Bindings bindings = this.getEngine().getBindings(scope);
        bindings.put(key, value);
    }

    public void clearEngine() {
        Log.get().debug("Created API script engine");
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
        this.registerScriptBinding("log", new LogJS(), ScriptContext.GLOBAL_SCOPE);
        this.registerScriptBinding("global", new GlobalJS(this.getGameWorldJS()), ScriptContext.GLOBAL_SCOPE);
        this.eval("var Vec3f = Java.type('" + Vec3f.class.getName() + "');");
        this.eval("var Vec4f = Java.type('" + Vec4f.class.getName() + "');");
    }

    public boolean execFunction(@Nullable Object[] result, @NotNull APIScriptingFunction apiScriptingFunction, Object... args) {
        if (!APIScriptsListing.check(apiScriptingFunction, args)) {
            Log.get().error("API found wrong function execution: " + apiScriptingFunction.getName());
            return false;
        }

        Object fn = this.getEngine().get(apiScriptingFunction.getName());
        if (fn instanceof ScriptObjectMirror && ((ScriptObjectMirror) fn).isFunction()) {
            try {
                Invocable invocable = (Invocable) this.getEngine();
                Object o = invocable.invokeFunction(apiScriptingFunction.getName(), args);
                if (result != null) {
                    result[0] = o;
                }
                return true;
            } catch (ScriptException | NoSuchMethodException e) {
                Log.get().exception(e);
            }
        }
        return false;
    }

    public GameWorldJS getGameWorldJS() {
        return this.gameWorldJS;
    }

    public JGemsAPIEditorResources getApiEditorResources() {
        return this.apiEditorResources;
    }

    public JGemsAPIScriptingManaging getScriptingManaging() {
        return this.scriptingManaging;
    }

    public ScriptEngine getEngine() {
        return this.engine;
    }
}