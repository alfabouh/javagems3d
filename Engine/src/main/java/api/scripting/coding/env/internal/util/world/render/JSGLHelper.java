package api.scripting.coding.env.internal.util.world.render;
import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.lwjgl.opengl.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@JSCodingClass(binding = "JSGLHelper", description = "Provides access to OpenGL constants and functions dynamically.")
public class JSGLHelper implements JSGlobalVarFactory<JSGLHelper> {

    @JSHideFromDoc private final Map<String, Object> constantsMap = new HashMap<>();
    @JSHideFromDoc private final Map<String, Method> glMethodsMap = new HashMap<>();

    @JSHideFromDoc
    public JSGLHelper() {
        Class<?>[] glClasses = {
                GL11.class, GL12.class, GL13.class, GL14.class, GL15.class,
                GL20.class, GL21.class, GL30.class, GL31.class, GL32.class,
                GL33.class, GL40.class, GL41.class, GL42.class, GL43.class,
                GL44.class, GL45.class, GL46.class
        };

        for (Class<?> glClass : glClasses) {
            this.loadConstants(glClass);
            this.loadFunctions(glClass);
        }
    }

    @JSHideFromDoc
    private void loadConstants(Class<?> glClass) {
        for (Field field : glClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    constantsMap.put(field.getName(), field.get(null));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    @JSHideFromDoc
    private void loadFunctions(Class<?> glClass) {
        for (Method method : glClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                glMethodsMap.put(method.getName(), method);
            }
        }
    }

    @JSCodingFunctionOrMethod(description = "Get OpenGL constant by name (returns Object: int, boolean, float etc.)", paramNames = {"name"})
    public Object getConst(String name) {
        return constantsMap.getOrDefault(name, null);
    }

    @JSCodingFunctionOrMethod(description = "Call OpenGL function by name with arguments. Returns Object or null.", paramNames = {"funcName", "args"})
    public Object callFunction(String funcName, Object... args) {
        Method method = glMethodsMap.get(funcName);
        if (method == null) return null;
        try {
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke OpenGL function: " + funcName, e);
        }
    }

    @JSHideFromDoc
    @Override
    public JSGLHelper newGlobalVar() {
        return new JSGLHelper();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_GL";
    }
}