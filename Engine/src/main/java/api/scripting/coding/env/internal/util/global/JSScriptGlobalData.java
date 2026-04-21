package api.scripting.coding.env.internal.util.global;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.JSControllableItem;
import api.scripting.coding.env.internal.util.controlling.JSController;
import api.scripting.coding.env.internal.util.controlling.JSControllerDispatcher;
import api.scripting.coding.env.internal.util.controlling.bind.JSBindingManager;
import api.scripting.coding.env.internal.util.lang.JSLang;
import api.scripting.coding.env.internal.util.lang.JSLocalization;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.mapping.player.JSPlayerCreatorFunction;
import api.scripting.coding.env.internal.util.mapping.player.JSSpawnPlayerTranslateData;
import api.scripting.coding.env.internal.util.mapping.player.JSSpawnPlayerWorldData;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.misc.JSPair;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItemI;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import api.scripting.coding.env.internal.util.world.physical.zones.instances.JSLiquid;
import api.scripting.coding.env.internal.util.world.render.JSScene;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.data.JSLiquidRenderData;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightAttachableI;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.lighting.JSPointLight;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCameraI;
import api.scripting.coding.env.internal.util.world.render.screen.timer.JSTimedAction;
import api.scripting.coding.env.internal.util.world.render.screen.timer.JSTimerPool;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneEntityI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSScenePropI;
import api.system.scripting.JavaToJsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@JSCodingClass(
        binding = "JSScriptGlobalData",
        description = "Provides access to global script data such as game folder path, screen, settings, scene, world, and resources. " +
                "All returned objects may interact with game state and rendering."
)
public final class JSScriptGlobalData implements JSGlobalVarFactory<JSScriptGlobalData> {

    @JSHideFromDoc
    public static JSPath absPath;

    @JSHideFromDoc
    public static JSScreen jsScreen;

    @JSHideFromDoc
    public static JSGameSettings jsGameSettings;

    @JSHideFromDoc
    public static JSLocalization jsLocalization;

    public JSScriptGlobalData() {}

    // ----------------------
    // Paths and Settings
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Get absolute path to the game's folder.")
    public JSPath getGameFolderPath() {
        return JSScriptGlobalData.absPath;
    }

    @JSCodingFunctionOrMethod(description = "Get current game localisation object. Use to read localized strings.")
    public static JSLocalization getLocalisation() {
        return JSScriptGlobalData.jsLocalization;
    }

    @JSCodingFunctionOrMethod(description = "Get current game settings object. Use to read or modify game parameters.")
    public static JSGameSettings getGameSettings() {
        return JSScriptGlobalData.jsGameSettings;
    }

    // ----------------------
    // Screen and Scene
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Get current screen object. The screen manages rendering, window, and timers.")
    public JSScreen getScreen() {
        return JSScriptGlobalData.jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "Get current scene object. Scene contains all renderable objects, lights, environment, and timers. Changes affect world rendering.")
    public JSScene getScene() {
        return JSScriptGlobalData.jsScreen.getJsScene();
    }

    @JSCodingFunctionOrMethod(description = "Get current environment from scene.")
    public JSEnvironment getSceneEnvironment() {
        return JSScriptGlobalData.jsScreen.getJsScene().getSceneWorld().getEnvironment();
    }

    @JSCodingFunctionOrMethod(description = "Get current window object.")
    public JSWindow getWindow() {
        return JSScriptGlobalData.jsScreen.getWindow();
    }

    @JSCodingFunctionOrMethod(description = "Check if the screen object is valid (not null).")
    public boolean isScreenValid() {
        return JSScriptGlobalData.jsScreen != null;
    }

    // ----------------------
    // Resources
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Get local game resources. May consume memory; call `clear()` or `destroy()` after use if available.")
    public JSSystemResources getLocalGameResources() {
        return new JSSystemResources(JGemsHelper.resources().getLocalGameResources());
    }

    @JSCodingFunctionOrMethod(description = "Get global game resources. May consume memory; call `clear()` or `destroy()` after use if available.")
    public JSSystemResources getGlobalGameResources() {
        return new JSSystemResources(JGemsHelper.resources().getGlobalGameResources());
    }

    @JSCodingFunctionOrMethod(description = "Get animations texture buffer. Remember to release memory after use if supported.")
    public JSTexture2D getAnimationsTextureBuffer() {
        return new JSTexture2D(JGemsHelper.resources().getAnimationsTextureBuffer());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java resource manager.")
    public JGemsResourceManager getJavaResourceManager() {
        return JGemsHelper.resources().getResourceManager();
    }

    @JSCodingFunctionOrMethod(description = "Reload all resources. Be aware that previous objects may need to be cleared to avoid memory leaks.")
    public void reloadResources() {
        JGemsHelper.resources().reloadResources();
    }

    // ----------------------
    // UI
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Open main menu UI.")
    public void openMainMenu() {
        JGemsHelper.ui().openMainMenu();
    }

    @JSCodingFunctionOrMethod(description = "Open UI panel by name. Use panel name from registered panels map.", paramNames = {"panelUI"})
    public void openPanel(String panelUI) {
        JGemsHelper.ui().openPanel(JavaToJsAPI.uiContainer.getPanelUIMap().get(panelUI));
    }

    @JSCodingFunctionOrMethod(description = "Close current UI panel.")
    public void closePanel() {
        JGemsHelper.ui().closePanel();
    }

    // ----------------------
    // World manipulation
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Remove all items in world.")
    public void killItemsInWorld() {
        JGemsHelper.world().killItems();
    }

    @JSCodingFunctionOrMethod(description = "Add scene prop to world.", paramNames = {"sceneProp"})
    public void addProp(JSScenePropI sceneProp) {
        JGemsHelper.world().addProp(sceneProp.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Remove scene prop from world.", paramNames = {"sceneProp"})
    public void removeProp(JSScenePropI sceneProp) {
        JGemsHelper.world().removeProp(sceneProp.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Add world object.", paramNames = {"worldItem"})
    public void addWorldObject(JSWorldObjectI worldItem) {
        JGemsHelper.world().addWorldObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Remove world object.", paramNames = {"worldItem"})
    public void removeWorldObject(JSWorldObjectI worldItem) {
        JGemsHelper.world().removeWorldObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Add world item with render data.", paramNames = {"worldItem", "renderData"})
    public void addWorldItem(JSWorldItemI worldItem, JSEntityRenderData renderData) {
        JGemsHelper.world().addWorldItem(worldItem.getJavaWorldObject(), renderData.getJavaEntityRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Remove world item.", paramNames = {"worldItem"})
    public void removeWorldItem(JSWorldItemI worldItem) {
        JGemsHelper.world().removeWorldItem((WorldItem) worldItem);
    }

    @JSCodingFunctionOrMethod(description = "Add liquid to world.", paramNames = {"liquid", "liquidRenderData"})
    public void addLiquid(JSLiquid liquid, JSLiquidRenderData liquidRenderData) {
        JGemsHelper.world().addLiquid(liquid.getJavaLiquid(), liquidRenderData.getJavaLiquidRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Remove liquid from world.", paramNames = {"liquid"})
    public void removeLiquid(JSLiquid liquid) {
        JGemsHelper.world().removeLiquid(liquid.getJavaLiquid());
    }

    @JSCodingFunctionOrMethod(description = "Add light to world.", paramNames = {"light"})
    public void addLight(JSLightI light) {
        JGemsHelper.world().addLight(light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Remove light from world.", paramNames = {"light"})
    public void removeLight(JSLightI light) {
        JGemsHelper.world().removeLight(light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Add light to lighted object.", paramNames = {"light", "lighted"})
    public void addLight(JSLightI light, JSSceneObjectWithLightsI lighted) {
        JGemsHelper.world().addLight(light.getJavaLight(), lighted.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Attach light to world item.", paramNames = {"worldItem", "light"})
    public void addWorldItemLight(JSWorldItemI worldItem, JSLightAttachableI light) {
        JGemsHelper.world().addWorldItemLight(worldItem.getJavaWorldObject(), light.getJavaLightAttached());
    }

    @JSCodingFunctionOrMethod(description = "Get scene object by world item.", paramNames = {"worldItem"})
    public JSSceneObjectI tryGetSceneObjectByWorldItem(JSWorldItemI worldItem) {
        return () -> JSScriptGlobalData.this.getScene().getSceneWorld().getJavaSceneWorld().getSceneObject(worldItem.getJavaWorldObject());
    }

    // ----------------------
    // Render and timers
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Reset render tick.")
    public void zeroRenderTick() {
        JGemsHelper.screen().zeroRenderTick();
    }

    @JSCodingFunctionOrMethod(description = "Get timer pool.")
    public JSTimerPool getTimerPool() {
        return this.getScreen().getTimerPool();
    }

    @JSCodingFunctionOrMethod(description = "Create a new timed action.")
    public JSTimedAction createTimer() {
        return this.getScreen().getTimerPool().createTimer();
    }

    // ----------------------
    // Window / focus
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Set window focus.", paramNames = {"focus"})
    public void setWindowFocus(boolean focus) {
        JGemsHelper.screen().setWindowFocus(focus);
    }

    @JSCodingFunctionOrMethod(description = "Check if window is active.")
    public boolean isWindowActive() {
        return JGemsHelper.screen().isWindowActive();
    }

    // ----------------------
    // Localization
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Load language file from path.", paramNames = {"lang", "path"})
    public void loadLanguage(JSLang lang, JSPath path) throws Exception {
        JGems3D.get().getLocalization().readLanguageMap(lang.getRaw(), new JGemsPathSource(path.getJavaPath(), ISource.Source.OUTSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "Get current language.")
    public JSLang getCurrentLanguage() {
        return new JSLang(JGems3D.get().getLocalization().getCurrentLang());
    }

    @JSCodingFunctionOrMethod(description = "Set current language.", paramNames = {"lang"})
    public void setCurrentLanguage(JSLang lang) {
        JGems3D.get().getLocalization().setCurrentLang(lang.getRaw());
    }

    @JSCodingFunctionOrMethod(description = "Format localized string by key.", paramNames = {"key", "args"})
    public String formatText(String key, Object... args) {
        return JGems3D.get().getLocalization().format(key, args);
    }

    @JSCodingFunctionOrMethod(description = "Get language by index.", paramNames = {"id"})
    public JSLang getLanguage(int id) {
        return new JSLang(JGems3D.get().getLocalization().getLangByID(id));
    }

    @JSCodingFunctionOrMethod(description = "Get total languages count.")
    public int getLanguagesCount() {
        return JGems3D.get().getLocalization().max();
    }

    // ----------------------
    // Map loading
    // ----------------------

    @JSCodingFunctionOrMethod(
            description = "Get real map path from relative path. The path should be relative to the game maps folder (absolute game folder path + '/game_maps/'). " +
                    "Example: if the map is located at 'MyGame/game_maps/level1.map', pass 'level1.map'. " +
                    "Ensure the map file is copied to the game_maps folder before calling this method.",
            paramNames = {"relativePath"}
    )
    public JSPath getRealMapPath(String relativePath) {
        return new JSPath(JGemsHelper.map().getMapPath(relativePath));
    }

    @JSCodingFunctionOrMethod(
            description = "Load map by relative path. The path is relative to the game maps folder ('<game folder>/game_maps/'). " +
                    "Make sure the map file exists at this location before loading. Function used to create player instance",
            paramNames = {"mapRelativePath, createPlayerConsumer"}
    )
    public void loadMap(String mapRelativePath, JSPlayerCreatorFunction createPlayerConsumer) {
        JGemsPath mapPath = JGemsHelper.map().getMapPath(mapRelativePath);
        if (mapPath == null) {
            Log.get().warn("Map file at " + mapRelativePath + " does not exist.");
        } else {
            JGemsHelper.map().loadMap(new ExternalMapProcessor.Default(JGemsHelper.map().getMapPath(mapRelativePath),
                    (world, spawnPlayerData) -> {
                JSPair<JSPlayer, JSEntityRenderData> pair = createPlayerConsumer.createPlayer(new JSPhysicsWorld(world), spawnPlayerData.stream().map(e -> new JSSpawnPlayerTranslateData(new JSVector3f(e.spawnPos()), new JSVector3f(e.spawnRot()))).collect(Collectors.toSet()));
                return new Pair<>(pair.getFirst().getJavaPlayer(), pair.getSecond().getJavaEntityRenderData());
            }));
        }
    }

    @JSCodingFunctionOrMethod(
            description = "Load map by relative path. The path is relative to the game maps folder ('<game folder>/game_maps/'). " +
                    "Make sure the map file exists at this location before loading.",
            paramNames = {"mapRelativePath"}
    )
    public void loadMap(String mapRelativePath) {
        JGemsPath mapPath = JGemsHelper.map().getMapPath(mapRelativePath);
        if (mapPath == null) {
            Log.get().warn("Map file at " + mapRelativePath + " does not exist.");
        } else {
            JGemsHelper.map().loadMap(new ExternalMapProcessor.Default(JGemsHelper.map().getMapPath(mapRelativePath), ExternalMapProcessor.Default.getDefaultPlayerConstructor()));
        }
    }

    @JSCodingFunctionOrMethod(description = "Exit current map.")
    public void exitMap() {
        JGemsHelper.map().exitMap();
    }

    // ----------------------
    // Controller and camera
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Get controller dispatcher.")
    public JSControllerDispatcher getControllerDispatcher() {
        return new JSControllerDispatcher(JGemsHelper.screen().getScreen().getControllerDispatcher());
    }

    @JSCodingFunctionOrMethod(description = "Get current controller.")
    public JSController getCurrentController() {
        return new JSController(this.getControllerDispatcher().getCurrentController().getJavaController());
    }

    @JSCodingFunctionOrMethod(description = "Get binding manager.")
    public JSBindingManager getBindingManager() {
        return new JSBindingManager(this.getCurrentController().getBindingManager().getJavaManager());
    }

    @JSCodingFunctionOrMethod(description = "Center cursor in screen.")
    public void setCursorInCenter() {
        JGemsHelper.controller().setCursorInCenter();
    }

    @JSCodingFunctionOrMethod(description = "Attach controller to remote controllable.", paramNames = {"controller", "remoteController"})
    public void attachControllerTo(JSController controller, JSControllableItem remoteController) {
        JGemsHelper.controller().attachControllerTo(controller.getJavaController(), remoteController.getJavaControllable());
    }

    @JSCodingFunctionOrMethod(description = "Detach controller.")
    public void detachController() {
        JGemsHelper.controller().detachController();
    }

    @JSCodingFunctionOrMethod(description = "Lock controller input.")
    public void lockController() {
        JGemsHelper.controller().lockController();
    }

    @JSCodingFunctionOrMethod(description = "Unlock controller input.")
    public void unLockController() {
        JGemsHelper.controller().unLockController();
    }

    @JSCodingFunctionOrMethod(description = "Get current camera.")
    public JSCameraI getCurrentCamera() {
        return new JSCamera(JGemsHelper.camera().getCurrentCamera());
    }

    @JSCodingFunctionOrMethod(description = "Set current camera.", paramNames = {"camera"})
    public void setCurrentCamera(JSCameraI camera) {
        JGemsHelper.camera().setCurrentCamera(camera.getJavaCamera());
    }

    @JSCodingFunctionOrMethod(description = "Enable free camera.", paramNames = {"controller", "pos", "rot"})
    public void enableFreeCamera(JSController controller, JSVector3f pos, JSVector3f rot) {
        JGemsHelper.camera().enableFreeCamera(controller.getJavaController(), pos.getJavaVector3f(), rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Enable camera attached to a world item.", paramNames = {"worldItem"})
    public void enableAttachedCamera(JSWorldItemI worldItem) {
        JGemsHelper.camera().enableAttachedCamera(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Enable camera attached to a scene entity.", paramNames = {"abstractSceneEntity"})
    public void enableAttachedCamera(JSSceneEntityI abstractSceneEntity) {
        JGemsHelper.camera().enableAttachedCamera(abstractSceneEntity.getJavaSceneEntity());
    }

    // ----------------------
    // Game state
    // ----------------------

    @JSCodingFunctionOrMethod(description = "Pause game and lock resume.", paramNames = {"pauseSounds"})
    public void pauseGameAndLockResume(boolean pauseSounds) {
        JGemsHelper.state().pauseGameAndLockResume(pauseSounds);
    }

    @JSCodingFunctionOrMethod(description = "Unpause game and unlock unpausing.")
    public void unPauseGameAndUnLockUnPausing() {
        JGemsHelper.state().unPauseGameAndUnLockUnPausing();
    }

    @JSCodingFunctionOrMethod(description = "Pause game.", paramNames = {"pauseSounds"})
    public void pauseGame(boolean pauseSounds) {
        JGemsHelper.state().pauseGame(pauseSounds);
    }

    @JSCodingFunctionOrMethod(description = "Resume game.")
    public void resumeGame() {
        JGemsHelper.state().resumeGame();
    }

    // ----------------------
    // Hidden / internal
    // ----------------------

    @JSHideFromDoc
    public static void setLocalisation(JSLocalization localisation) {
        JSScriptGlobalData.jsLocalization = localisation;
    }

    @JSHideFromDoc
    public static void setSettings(JSGameSettings settings) {
        JSScriptGlobalData.jsGameSettings = settings;
    }

    @JSHideFromDoc
    public static void setScreen(JSScreen jsScreen) {
        JSScriptGlobalData.jsScreen = jsScreen;
    }

    @JSHideFromDoc
    public static void dispose() {
        JSScriptGlobalData.jsScreen = null;
    }

    @JSHideFromDoc
    public static void setAbsoluteSystemPath(JGemsPath path) {
        JSScriptGlobalData.absPath = new JSPath(path);
    }

    @JSHideFromDoc
    @Override
    public JSScriptGlobalData newGlobalVar() {
        return new JSScriptGlobalData();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_Global";
    }
}