package api.scripting.coding.env.internal.util.world.render.world;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.physical.zones.instances.JSLiquid;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.data.JSLiquidRenderData;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSAttachedCamera;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCameraBasic;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCameraI;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneAnimatedObject;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneEntity;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldLiquid;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSAnimatedObjectI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.world.SceneWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JSCodingClass(binding = "JSSceneWorld", description = "Wrapper for SceneWorld, providing access to scene objects, cameras, lights, and liquids.")
public class JSSceneWorld {

    @JSHideFromDoc
    private final SceneWorld sceneWorld;

    @JSCodingConstructor(description = "Creates a JSSceneWorld wrapper around a native SceneWorld object.", paramNames = {"sceneWorld"})
    public JSSceneWorld(@NotNull SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Returns the current camera.", paramNames = {})
    public JSCamera getCamera() {
        return new JSCamera(this.sceneWorld.getCamera());
    }

    @JSCodingFunctionOrMethod(description = "Returns the current camera.", paramNames = {})
    public JSCameraBasic tryGetBasicCamera() {
        ICamera cam = this.sceneWorld.getCamera();
        if (cam instanceof CameraBase base) {
            return new JSCameraBasic(base) {};
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Sets the active camera for the scene world.", paramNames = {"camera"})
    public void setCamera(@Nullable JSCameraI camera) {
        this.sceneWorld.setCamera(camera != null ? camera.getJavaCamera() : null);
    }

    @JSCodingFunctionOrMethod(description = "Creates and returns an attached camera for a scene entity.", paramNames = {"entity"})
    public JSAttachedCamera createAttachedCamera(@NotNull JSSceneEntity entity) {
        AttachedCamera cam = this.sceneWorld.createAttachedCamera(entity.getJavaSceneEntity());
        return new JSAttachedCamera(cam);
    }

    @JSCodingFunctionOrMethod(description = "Adds a world item to the scene with its render data.", paramNames = {"worldItem", "renderData"})
    public void addWorldItem(@NotNull JSWorldItem worldItem, @NotNull JSEntityRenderData renderData) {
        this.sceneWorld.addWorldItem(worldItem.getJavaWorldObject(), renderData.getJavaEntityRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Attaches an existing camera to a scene entity.", paramNames = {"item", "camera"})
    public boolean attachCameraOn(@NotNull JSWorldItem item, @NotNull JSAttachedCamera camera) {
        return this.sceneWorld.attachCameraOn(item.getJavaWorldObject(), camera.getJavaAttachedCamera());
    }

    @JSCodingFunctionOrMethod(description = "Adds a liquid volume to the scene world.", paramNames = {"liquid", "renderData"})
    public void addLiquid(@NotNull JSLiquid liquid, @NotNull JSLiquidRenderData renderData) {
        this.sceneWorld.addLiquid(liquid.getJavaLiquid(), renderData.getJavaLiquidRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Removes a liquid volume from the scene world.", paramNames = {"liquid"})
    public void removeLiquid(@NotNull JSSceneWorldLiquid liquid) {
        this.sceneWorld.removeLiquid(liquid.getJavaSceneWorldLiquid());
    }

    @JSCodingFunctionOrMethod(description = "Adds a light to the world with optional attachment.", paramNames = {"light", "attachable"})
    public void addLight(@NotNull JSLightI light, @Nullable JSSceneObjectWithLightsI attachable) {
        this.sceneWorld.addLight(light.getJavaLight(), attachable != null ? attachable.getJavaLightedObject() : null);
    }

    @JSCodingFunctionOrMethod(description = "Adds a light attached to a specific world item.", paramNames = {"item", "light"})
    public void addWorldItemLight(@NotNull JSWorldItem item, @NotNull JSLightI light) {
        this.sceneWorld.addWorldItemLight(item.getJavaWorldObject(), (ILightAttachable) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Returns current world ticks.", paramNames = {})
    public int getTicks() {
        return this.sceneWorld.getTicks();
    }

    @JSCodingFunctionOrMethod(description = "Returns the environment of the scene world.", paramNames = {})
    public JSEnvironment getEnvironment() {
        return new JSEnvironment((JGemsEnvironment) this.sceneWorld.getEnvironment());
    }

    @JSCodingFunctionOrMethod(description = "Adds a scene object to the world.", paramNames = {"object"})
    public void addObject(@NotNull JSSceneObjectI object) {
        this.sceneWorld.addObject(object.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Removes a scene object from the world.", paramNames = {"object"})
    public void removeObject(@NotNull JSSceneObjectI object) {
        this.sceneWorld.removeObject(object.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Returns number of scene objects in the world.", paramNames = {})
    public int getObjectCount() {
        return this.sceneWorld.getSceneObjects().size();
    }

    @JSCodingFunctionOrMethod(description = "Checks if the world contains a specific scene object.", paramNames = {"object"})
    public boolean contains(@NotNull JSSceneObjectI object) {
        return this.sceneWorld.contains(object.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Returns the animated object associated with a world item.", paramNames = {"item"})
    public JSAnimatedObjectI getAnimatedObject(@NotNull JSWorldItem item) {
        IAnimated obj = this.sceneWorld.getAnimatedObject(item.getJavaWorldObject());
        return obj != null ? new JSSceneAnimatedObject(obj) : null;
    }

    @JSCodingFunctionOrMethod(description = "Real java object.", paramNames = {""})
    public SceneWorld getJavaSceneWorld() {
        return this.sceneWorld;
    }
}