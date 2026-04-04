package api.scripting.coding.env.internal.util.mapping.player;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;

@JSCodingClass(binding = "JSSpawnPlayerTranslateData", description = "Player spawn position and rotation")
public class JSSpawnPlayerTranslateData {
    @JSHideFromDoc
    private final JSVector3f spawnPos;
    @JSHideFromDoc
    private final JSVector3f spawnRot;

    @JSHideFromDoc
    public JSSpawnPlayerTranslateData(JSVector3f spawnPos, JSVector3f spawnRot) {
        this.spawnPos = spawnPos;
        this.spawnRot = spawnRot;
    }

    @JSCodingFunctionOrMethod(description = "Get spawn position")
    public JSVector3f getSpawnPos() { return this.spawnPos; }

    @JSCodingFunctionOrMethod(description = "Get spawn rotation")
    public JSVector3f getSpawnRot() { return this.spawnRot; }
}