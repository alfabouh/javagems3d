package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSCodingClass;

@FunctionalInterface
@JSCodingClass(binding = "JSDrawFunction", description = "..")
public interface JSDrawFunction {
    void run(float frameDeltaTicks);
}
