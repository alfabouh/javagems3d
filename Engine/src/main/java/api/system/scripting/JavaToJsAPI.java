package api.system.scripting;

import api.scripting.coding.APICodingContext;
import api.scripting.coding.env.internal.game.init.JSGameRegistry;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitAssetsEvent;
import api.scripting.coding.env.internal.game.init.events.resources.JSInitShadersEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSAfterSettingsPerfTestEvent;
import api.scripting.coding.env.internal.game.init.events.settings.JSInitSettingsEvent;
import api.scripting.coding.env.internal.game.init.events.ui.JSRegisterUiEvent;
import api.scripting.coding.env.internal.game.init.events.ui.JSUiBehaviourEvent;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.events.JSEventSubscriber;
import api.scripting.coding.env.internal.util.global.JSScriptGlobalData;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.screen.JSScreen;
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

    public static boolean DEBUG_MODE = true;

    public static void callEvent(@NotNull Target target, @NotNull JSEventI eventI, @NotNull Object... args) {
        JavaToJsAPI.callFunction(target, JavaToJsAPI.gameEventSubscriber.getEventName(eventI), args);
    }

    public static @Nullable Value callFunction(@NotNull Target target, @NotNull String functionName, @NotNull Object... args) {
        if (DEBUG_MODE) {
            Log.get().debug("Call function " + target + " - " + functionName + " : " + Arrays.toString(args));
        }
        return Objects.requireNonNull(JavaToJsAPI.apiCodeContext(target)).callFunctionNoExc(functionName, args);
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
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_ASSETS), new JSInitAssetsEvent(assetsInitializer, new JSSystemResources(manager)));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_initShaders__EVENT(ShadersInitializer<JGemsShaderManager> shadersInitializer, SystemResources manager) {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_SHADERS), new JSInitShadersEvent(shadersInitializer, new JSSystemResources(manager)));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_registerUI__EVENT() {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.REGISTER_UI), new JSRegisterUiEvent(JSScriptGlobalData.jsScreen));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_registerUIBehaviour__EVENT(JGemsUI jGemsUI) {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.REGISTER_UI_BEHAVIOUR), new JSUiBehaviourEvent(new JSUIDrawer(jGemsUI, JSScriptGlobalData.jsScreen), JSScriptGlobalData.jsScreen));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_settingsInitEvent__EVENT(JSGameSettings gameSettings) {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.INIT_SETTINGS), new JSInitSettingsEvent(gameSettings));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void Js_GAME_afterSettingsPerfTestEvent__EVENT(JSPerfTestResult perfTestResult, JSGameSettings gameSettings) {
        try {
            JavaToJsAPI.callFunction(Target.Game, JavaToJsAPI.gameEventSubscriber.getEventName(JSGameRegistry.AFTER_SETTINGS_PERF_TEST_EVENT), new JSAfterSettingsPerfTestEvent(perfTestResult, gameSettings));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }


    public static void setScreen(JGemsScreen gemsScreen) {
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
