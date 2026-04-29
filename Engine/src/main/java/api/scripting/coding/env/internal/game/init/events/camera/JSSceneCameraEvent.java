package api.scripting.coding.env.internal.game.init.events.camera;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSSceneCameraEvent", description = "Event triggered for camera updates in the scene.")
public class JSSceneCameraEvent implements JSEventI {
    @JSCodingField(description = "Scene world associated with the camera")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "Camera involved in the event")
    @JSHideFromDoc
    private JSCamera jsCamera;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneCameraEvent() {
    }

    @JSHideFromDoc
    public JSSceneCameraEvent(JSSceneWorld jsSceneWorld, JSCamera jsCamera) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsCamera = jsCamera;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the camera involved in this event")
    public JSCamera getCamera() {
        return this.jsCamera;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneCameraEvent";
    }
}