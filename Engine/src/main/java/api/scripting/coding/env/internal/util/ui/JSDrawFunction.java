package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingClass;

@FunctionalInterface
@JSCodingClass(binding = "JSDrawFunction", description = "Represents a function that executes drawing logic each frame with access to the frame delta time.")
public interface JSDrawFunction {
    void run(float frameDeltaTicks);
}