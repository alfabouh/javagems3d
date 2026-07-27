/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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