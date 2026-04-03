package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.ticking.FrameTicking;

@JSCodingClass(binding = "JSFrameTicking", description = "Frame timing data: physics ticks and delta time.")
public class JSFrameTicking {

    @JSHideFromDoc
    private final FrameTicking ticking;

    @JSCodingConstructor(description = "Wrap existing FrameTicking.")
    public JSFrameTicking(FrameTicking ticking) {
        this.ticking = ticking;
    }

    @JSHideFromDoc
    public FrameTicking getJava() {
        return this.ticking;
    }

    @JSCodingFunctionOrMethod(description = "Get physics sync ticks (used for physics-synced movement interpolation).")
    public float getPhysicsSyncTicks() {
        return this.ticking.physicsSyncTicks();
    }

    @JSCodingFunctionOrMethod(description = "Get frame delta time (time between this and previous frame).")
    public float getFrameDeltaTime() {
        return this.ticking.frameDeltaTime();
    }

    @Override
    public String toString() {
        return "JSFrameTicking{" +
                "physicsSyncTicks=" + ticking.physicsSyncTicks() +
                ", frameDeltaTime=" + ticking.frameDeltaTime() +
                '}';
    }
}