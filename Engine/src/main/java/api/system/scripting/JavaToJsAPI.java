package api.system.scripting;

import api.scripting.coding.APICodingContext;
import api.scripting.coding.env.internal.game.init.JSGameRegistry;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitAssetsEvent;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitShadersEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSAfterSettingsPerfTestEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSInitSettingsEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSRegisterUiEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.ui.JSUiBehaviourEvent;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventSubscriber;
import api.scripting.coding.env.internal.util.global.JSScriptGlobalData;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.JSPerfTestResult;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.JGemsAPI;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JavaToJsAPI {
    public static final JSEventSubscriber gameEventSubscriber = new JSEventSubscriber();
    public static final JSEventSubscriber mapEventSubscriber = new JSEventSubscriber();
    public static final UIContainer uiContainer = new UIContainer();

    public static boolean DEBUG_MODE = false;

    private static JSEventSubscriber pick(@NotNull Target target) {
        return target.equals(Target.Game) ? JavaToJsAPI.gameEventSubscriber : JavaToJsAPI.mapEventSubscriber;
    }

    public static void callEvent(@NotNull Target target, @NotNull JSEventI eventI, @NotNull Object... args) {
        JavaToJsAPI.callFunction(target, JavaToJsAPI.pick(target).getEventName(eventI), args);
    }

    public static @Nullable Value callFunction(@NotNull Target target, @NotNull String functionName, @NotNull Object... args) {
        if (DEBUG_MODE) {
            Log.get().debug("Call function " + target + " - " + functionName + " : " + Arrays.toString(args));
        }
        return Objects.requireNonNull(JavaToJsAPI.apiCodeContext(target)).callFunctionNoExc(functionName, args);
    }

    public static void ScriptEnd(@NotNull Target target) {
        APICodingContext apiCodingContext = JavaToJsAPI.apiCodeContext(target);
        if (apiCodingContext != null) {
            apiCodingContext.callFunctionNoExc(JavaToJsFunctionsList.ENTRY_ENDPOINT_FUNCTION_DESC);
        } else {
            Log.get().error("API code context is null!");
        }
    }

    public static void ScriptInit(@NotNull Target target) {
        APICodingContext apiCodingContext = JavaToJsAPI.apiCodeContext(target);
        if (apiCodingContext != null) {
            apiCodingContext.callFunctionNoExc(JavaToJsFunctionsList.ENTRY_POINT_FUNCTION);
        } else {
            Log.get().error("API code context is null!");
        }
    }

    public static void Js_MAP_initEvents() {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsFunctionsList.SUBSCRIBE_EVENTS_FUNCTION, new JSGameRegistry(JavaToJsAPI.mapEventSubscriber));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_initEvents() {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsFunctionsList.SUBSCRIBE_EVENTS_FUNCTION, new JSGameRegistry(JavaToJsAPI.gameEventSubscriber));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_initAssets__EVENT(IAssetsInitializer assetsInitializer, SystemResources manager) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_ASSETS);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.INIT_ASSETS.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSInitAssetsEvent(assetsInitializer, new JSSystemResources(manager)));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_initShaders__EVENT(ShadersInitializer<JGemsShaderManager> shadersInitializer, SystemResources manager) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_SHADERS);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.INIT_SHADERS.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSInitShadersEvent(shadersInitializer, new JSSystemResources(manager)));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_registerUI__EVENT() {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.REGISTER_UI);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.REGISTER_UI.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSRegisterUiEvent(JSScriptGlobalData.jsScreen));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_registerUIBehaviour__EVENT(JGemsUI jGemsUI) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.REGISTER_UI_BEHAVIOUR);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.REGISTER_UI_BEHAVIOUR.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSUiBehaviourEvent(new JSUIDrawer(jGemsUI, JSScriptGlobalData.jsScreen), JSScriptGlobalData.jsScreen));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_settingsInitEvent__EVENT(JSGameSettings gameSettings) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_SETTINGS);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.INIT_SETTINGS.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSInitSettingsEvent(gameSettings));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_afterSettingsPerfTestEvent__EVENT(JSPerfTestResult perfTestResult, JSGameSettings gameSettings) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.AFTER_SETTINGS_PERF_TEST_EVENT);
            if (eventName == null) {
                Log.get().error("Event is null: " + JSGameRegistry.AFTER_SETTINGS_PERF_TEST_EVENT.name());
                return;
            }
            JavaToJsAPI.callFunction(Target.Game, eventName, new JSAfterSettingsPerfTestEvent(perfTestResult, gameSettings));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static boolean Js_GAME_SomeEvent__EVENT(JSEventI eventI, Target target) {
        try {
            final String eventName = JavaToJsAPI.gameEventSubscriber.getEventName(eventI);
            if (eventName == null) {
                return false;
            }
            JavaToJsAPI.callFunction(target, eventName, eventI);
            if (eventI instanceof JSEventCancellableI cancellableI) {
                return cancellableI.isCancelled();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }


    public static void setJSScreen(JGemsScreen gemsScreen) {
        JSScriptGlobalData.setScreen(new JSScreen(gemsScreen));
    }

    public static void disposeRender() {
        JSScriptGlobalData.dispose();
    }


    public static IAssetsInitializer createJSAssetsInitializer(JGemsResourceManager resourceManager) {
        return new IAssetsInitializer() {
            @Override
            public void load(SystemResources systemResources) {
                {
                    JavaToJsAPI.Js_GAME_initAssets__EVENT(this, systemResources);
                }
            }
            @Override
            public LaunchMode loadMode() {
                return LaunchMode.REGULAR;
            }
            @Override
            public LoadPriority loadPriority() {
                return LoadPriority.NORMAL;
            }
        };
    }

    public static ShadersInitializer<JGemsShaderManager> createJSShadersInitializer(SystemResources systemResources) {
        return new ShadersInitializer<>() {
            @Override
            protected void initObjects(ResourceCache resourceCache) {
                {
                    JavaToJsAPI.Js_GAME_initShaders__EVENT(this, systemResources);
                }
            }

            @Override
            protected JGemsShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
                return new JGemsShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
            }

            @Override
            protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
            }

            @Override
            protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
            }
        };
    }


    private static APICodingContext apiCodeContext(Target target) {
       switch (target) {
           case Map -> {
               return JGemsAPI.getAPIScriptingCore().getLocalMapContext();
           }
           case Game -> {
               return JGemsAPI.getAPIScriptingCore().getGlobalGameContext();
           }
       }
       return null;
   }

    public enum Target {
        Game,
        Map
    }

    public static class UIContainer {
        private Pair<String, JSUIPanelWrapper> mainMenuPanel;
        private Map<String, JSUIPanelWrapper> panelUIMap;

        public UIContainer() {
            this.panelUIMap = new HashMap<>();
            this.mainMenuPanel = null;
        }

        public Pair<String, JSUIPanelWrapper> getMainMenuPanel() {
            return this.mainMenuPanel;
        }

        public UIContainer setMainMenuPanel(Pair<String, JSUIPanelWrapper> mainMenuPanel) {
            this.mainMenuPanel = mainMenuPanel;
            return this;
        }

        public Map<String, JSUIPanelWrapper> getPanelUIMap() {
            return this.panelUIMap;
        }

        public UIContainer setPanelUI(String id, JSUIPanelWrapper panel) {
            this.panelUIMap.put(id, panel);
            return this;
        }
    }
}
