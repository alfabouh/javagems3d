package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;

@JSCodingClass(binding = "JSRequiresClearResources", description = "Marks objects that hold resources requiring explicit cleanup. " +
        "Objects implementing this interface should call clear() after use to release memory or GPU resources, " +
        "especially if they are created each frame or frequently.")
public interface JSRequiresClearResources {
    @JSCodingFunctionOrMethod(description = "Releases all allocated resources held by the object. " +
            "Must be called when the object is no longer needed to prevent memory or GPU leaks.")
    void clear();
}