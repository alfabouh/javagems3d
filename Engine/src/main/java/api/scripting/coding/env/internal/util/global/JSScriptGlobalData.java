package api.scripting.coding.env.internal.util.global;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.JSControllableItem;
import api.scripting.coding.env.internal.util.controlling.JSController;
import api.scripting.coding.env.internal.util.controlling.JSControllerDispatcher;
import api.scripting.coding.env.internal.util.controlling.bind.JSBindingManager;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.map.JSGameMap;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
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
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.base.IController;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.JGemsPath;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSScriptGlobalData", description = "Provides access to global script data such as game folder path, screen, and settings.")
public final class JSScriptGlobalData implements JSGlobalVarFactory<JSScriptGlobalData> {
    @JSHideFromDoc public static JSPath absPath;
    @JSHideFromDoc public static JSScreen jsScreen;
    @JSHideFromDoc public static JSGameSettings jsGameSettings;

    public JSScriptGlobalData() {
    }

    @JSCodingFunctionOrMethod(description = "Get absolute path to the game's folder.")
    public JSPath getGameFolderPath() {
        return JSScriptGlobalData.absPath;
    }

    @JSCodingFunctionOrMethod(description = "Get current game settings.")
    public static JSGameSettings getGameSettings() {
        return JSScriptGlobalData.jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get current screen object.")
    public JSScreen getScreen() {
        return JSScriptGlobalData.jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "Get current scene object.")
    public JSScene getScene() {
        return JSScriptGlobalData.jsScreen.getJsScene();
    }

    @JSCodingFunctionOrMethod(description = "Get current environment object.")
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


    @JSCodingFunctionOrMethod(description = "Get local game resources")
    public JSSystemResources getLocalGameResources() {
        return new JSSystemResources(JGemsHelper.resources().getLocalGameResources());
    }

    @JSCodingFunctionOrMethod(description = "Get global game resources")
    public JSSystemResources getGlobalGameResources() {
        return new JSSystemResources(JGemsHelper.resources().getGlobalGameResources());
    }

    @JSCodingFunctionOrMethod(description = "Get animations texture buffer")
    public JSTexture2D getAnimationsTextureBuffer() {
        return new JSTexture2D(JGemsHelper.resources().getAnimationsTextureBuffer());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java resource manager")
    public JGemsResourceManager getJavaResourceManager() {
        return JGemsHelper.resources().getResourceManager();
    }

    @JSCodingFunctionOrMethod(description = "Reload all resources")
    public void reloadResources() {
        JGemsHelper.resources().reloadResources();
    }

    @JSCodingFunctionOrMethod(description = "Open main menu")
    public void openMainMenu() {
        JGemsHelper.ui().openMainMenu();
    }

    @JSCodingFunctionOrMethod(description = "Open UI panel by name", paramNames = {"panelUI"})
    public void openPanel(String panelUI) {
        JGemsHelper.ui().openPanel(JavaToJsAPI.uiContainer.getPanelUIMap().get(panelUI));
    }

    @JSCodingFunctionOrMethod(description = "Close current UI panel")
    public void closePanel() {
        JGemsHelper.ui().closePanel();
    }

    @JSCodingFunctionOrMethod(description = "Remove all items in world")
    public void killItemsInWorld() {
        JGemsHelper.world().killItems();
    }

    @JSCodingFunctionOrMethod(description = "Bind point light shadow to scene", paramNames = {"sceneId", "pointLight"})
    public void bindPointLightShadow(int sceneId, JSPointLight pointLight) {
        JGemsHelper.world().bindPointLightShadow(sceneId, pointLight.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Add scene prop to world", paramNames = {"sceneProp"})
    public void addProp(JSScenePropI sceneProp) {
        JGemsHelper.world().addProp(sceneProp.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Remove scene prop from world", paramNames = {"sceneProp"})
    public void removeProp(JSScenePropI sceneProp) {
        JGemsHelper.world().removeProp(sceneProp.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Add world object", paramNames = {"worldItem"})
    public void addWorldObject(JSWorldObjectI worldItem) {
        JGemsHelper.world().addWorldObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Remove world object", paramNames = {"worldItem"})
    public void removeWorldObject(JSWorldObjectI worldItem) {
        JGemsHelper.world().removeWorldObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Add world item with render data", paramNames = {"worldItem", "renderData"})
    public void addWorldItem(JSWorldItemI worldItem, JSEntityRenderData renderData) {
        JGemsHelper.world().addWorldItem(worldItem.getJavaWorldObject(), renderData.getJavaEntityRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Remove world item", paramNames = {"worldItem"})
    public void removeWorldItem(JSWorldItemI worldItem) {
        JGemsHelper.world().removeWorldItem((WorldItem) worldItem);
    }

    @JSCodingFunctionOrMethod(description = "Add liquid to world", paramNames = {"liquid", "liquidRenderData"})
    public void addLiquid(JSLiquid liquid, JSLiquidRenderData liquidRenderData) {
        JGemsHelper.world().addLiquid(liquid.getJavaLiquid(), liquidRenderData.getJavaLiquidRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Remove liquid from world", paramNames = {"liquid"})
    public void removeLiquid(JSLiquid liquid) {
        JGemsHelper.world().removeLiquid(liquid.getJavaLiquid());
    }

    @JSCodingFunctionOrMethod(description = "Remove light from world", paramNames = {"light"})
    public void removeLight(JSLightI light) {
        JGemsHelper.world().removeLight(light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Add light to world", paramNames = {"light"})
    public void addLight(JSLightI light) {
        JGemsHelper.world().addLight(light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Add light to lighted object", paramNames = {"light", "lighted"})
    public void addLight(JSLightI light, JSSceneObjectWithLightsI lighted) {
        JGemsHelper.world().addLight(light.getJavaLight(), lighted.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Attach light to world item", paramNames = {"worldItem", "light"})
    public void addWorldItemLight(JSWorldItemI worldItem, JSLightAttachableI light) {
        JGemsHelper.world().addWorldItemLight(worldItem.getJavaWorldObject(), light.getJavaLightAttached());
    }

    @JSCodingFunctionOrMethod(description = "Get scene object by world item", paramNames = {"worldItem"})
    public JSSceneObjectI tryGetSceneObjectByWorldItem(JSWorldItemI worldItem) {
        return () -> JSScriptGlobalData.this.getScene().getSceneWorld().getJavaSceneWorld().getSceneObject(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Reset render tick")
    public void zeroRenderTick() {
        JGemsHelper.screen().zeroRenderTick();
    }

    @JSCodingFunctionOrMethod(description = "Get timer pool")
    public JSTimerPool getTimerPool() {
        return this.getScreen().getTimerPool();
    }

    @JSCodingFunctionOrMethod(description = "Create a new timed action")
    public JSTimedAction createTimer() {
        return this.getScreen().getTimerPool().createTimer();
    }

    @JSCodingFunctionOrMethod(description = "Set window focus", paramNames = {"focus"})
    public void setWindowFocus(boolean focus) {
        JGemsHelper.screen().setWindowFocus(focus);
    }

    @JSCodingFunctionOrMethod(description = "Check if window is active")
    public boolean isWindowActive() {
        return JGemsHelper.screen().isWindowActive();
    }

    @JSCodingFunctionOrMethod(description = "Get current game map")
    public JSGameMap getCurrentGameMap() {
        return new JSGameMap(JGemsHelper.map().getCurrentGameMap());
    }

    @JSCodingFunctionOrMethod(description = "Check if current game map has a valid player")
    public boolean isCurrentGameMapPlayerValid() {
        return JGemsHelper.map().isCurrentGameMapPlayerValid();
    }

    @JSCodingFunctionOrMethod(description = "Check if current game map is valid")
    public boolean isCurrentGameMapValid() {
        return JGemsHelper.map().isCurrentGameMapValid();
    }

    @JSCodingFunctionOrMethod(description = "Get current game map player")
    public JSPlayer getCurrentGameMapPlayer() {
        return () -> JGemsHelper.map().getCurrentGameMapPlayer();
    }

    @JSCodingFunctionOrMethod(description = "Get real map path from relative path", paramNames = {"relativePath"})
    public JSPath getRealMapPath(String relativePath) {
        return new JSPath(JGemsHelper.map().getMapPath(relativePath));
    }

    @JSCodingFunctionOrMethod(description = "Exit current map")
    public void exitMap() {
        JGemsHelper.map().exitMap();
    }

    @JSCodingFunctionOrMethod(description = "Get controller dispatcher")
    public JSControllerDispatcher getControllerDispatcher() {
        return new JSControllerDispatcher(JGemsHelper.screen().getScreen().getControllerDispatcher());
    }

    @JSCodingFunctionOrMethod(description = "Get current controller")
    public JSController getCurrentController() {
        return new JSController(this.getControllerDispatcher().getCurrentController().getJavaController());
    }

    @JSCodingFunctionOrMethod(description = "Get binding manager")
    public JSBindingManager getBindingManager() {
        return new JSBindingManager(this.getCurrentController().getBindingManager().getJavaManager());
    }

    @JSCodingFunctionOrMethod(description = "Center cursor")
    public void setCursorInCenter() {
        JGemsHelper.controller().setCursorInCenter();
    }

    @JSCodingFunctionOrMethod(description = "Attach controller to remote controllable", paramNames = {"controller", "remoteController"})
    public void attachControllerTo(JSController controller, JSControllableItem remoteController) {
        JGemsHelper.controller().attachControllerTo(controller.getJavaController(), remoteController.getJavaControllable());
    }

    @JSCodingFunctionOrMethod(description = "Detach controller")
    public void detachController() {
        JGemsHelper.controller().detachController();
    }

    @JSCodingFunctionOrMethod(description = "Lock controller")
    public void lockController() {
        JGemsHelper.controller().lockController();
    }

    @JSCodingFunctionOrMethod(description = "Unlock controller")
    public void unLockController() {
        JGemsHelper.controller().unLockController();
    }

    @JSCodingFunctionOrMethod(description = "Get current camera")
    public JSCameraI getCurrentCamera() {
        return new JSCamera(JGemsHelper.camera().getCurrentCamera());
    }

    @JSCodingFunctionOrMethod(description = "Set current camera", paramNames = {"camera"})
    public void setCurrentCamera(JSCameraI camera) {
        JGemsHelper.camera().setCurrentCamera(camera.getJavaCamera());
    }

    @JSCodingFunctionOrMethod(description = "Enable free camera", paramNames = {"controller", "pos", "rot"})
    public void enableFreeCamera(JSController controller, JSVector3f pos, JSVector3f rot) {
        JGemsHelper.camera().enableFreeCamera(controller.getJavaController(), pos.getJavaVector3f(), rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Enable attached camera to world item", paramNames = {"worldItem"})
    public void enableAttachedCamera(JSWorldItemI worldItem) {
        JGemsHelper.camera().enableAttachedCamera(worldItem.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Enable attached camera to scene entity", paramNames = {"abstractSceneEntity"})
    public void enableAttachedCamera(JSSceneEntityI abstractSceneEntity) {
        JGemsHelper.camera().enableAttachedCamera(abstractSceneEntity.getJavaSceneEntity());
    }

    @JSCodingFunctionOrMethod(description = "Pause game and lock resume", paramNames = {"pauseSounds"})
    public void pauseGameAndLockResume(boolean pauseSounds) {
        JGemsHelper.state().pauseGameAndLockResume(pauseSounds);
    }

    @JSCodingFunctionOrMethod(description = "Unpause game and unlock unpausing")
    public void unPauseGameAndUnLockUnPausing() {
        JGemsHelper.state().unPauseGameAndUnLockUnPausing();
    }

    @JSCodingFunctionOrMethod(description = "Pause game", paramNames = {"pauseSounds"})
    public void pauseGame(boolean pauseSounds) {
        JGemsHelper.state().pauseGame(pauseSounds);
    }

    @JSCodingFunctionOrMethod(description = "Resume game")
    public void resumeGame() {
        JGemsHelper.state().resumeGame();
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
        return "JSGlobal";
    }
}
