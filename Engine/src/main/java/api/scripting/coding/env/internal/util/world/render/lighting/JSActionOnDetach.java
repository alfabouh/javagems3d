package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSActionOnDetach", description = "Defines behavior when light is detached")
public enum JSActionOnDetach {
    @JSCodingField(description = "DESTROY") DESTROY,
    @JSCodingField(description = "KEEP_IN_WORLD") KEEP_IN_WORLD
}