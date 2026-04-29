package api.scripting.coding.env.internal.util.mapping.player;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;

@JSCodingClass(binding = "JSSpawnPlayerTranslateData", description = "Player spawn position and rotation")
public class JSSpawnPlayerWorldData {
    @JSHideFromDoc
    private final JSPhysicsWorld physicsWorld;

    @JSHideFromDoc
    public JSSpawnPlayerWorldData(JSPhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get physics world spawning in")
    public JSPhysicsWorld getPhysicsWorld() { return this.physicsWorld; }
}