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

package api.scripting.coding.env;

import api.scripting.JGemsAPIScriptingCore;
import api.scripting.coding.APICodingContext;
import api.scripting.coding.env.def.*;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.Closeable;
import java.lang.reflect.*;
import java.util.*;

public class APICodeEnvironmentController implements Closeable {
    private final Map<String, JSGlobalVarData> globalVarFactoryKeys;
    private final Map<String, JSClassData> classRegistry;
    private final APICodingContext apiCodingContext;
    private JSSampleClassData entryPoint;
    private final List<JSSampleClassData> eventHandlers;
    private final Map<String, String> argumentsMap;
    private final Set<String> fields;

    public APICodeEnvironmentController(@NotNull APICodingContext apiCodingContext) {
        this.apiCodingContext = apiCodingContext;
        this.classRegistry = new LinkedHashMap<>();
        this.globalVarFactoryKeys = new LinkedHashMap<>();
        this.argumentsMap = new HashMap<>();
        this.fields = new HashSet<>();
        this.eventHandlers = new ArrayList<>();
        this.entryPoint = null;
    }

    public void close() {
        this.classRegistry.clear();
        this.globalVarFactoryKeys.clear();
        this.argumentsMap.clear();
        this.fields.clear();
        this.eventHandlers.clear();
        this.entryPoint = null;
    }

    public void scan(@NotNull String... packs) {
        final List<String> globalVarKeys = new ArrayList<>();
        Log.get().debug("Script-Env Scan-Pack: " + Arrays.toString(packs));
        for (String pack : packs) {
            FilterBuilder filter = new FilterBuilder();
            filter.includePackage(pack);
            final Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packs).filterInputsBy(filter).addScanners(Scanners.TypesAnnotated));
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(JSCodingClass.class);
            for (Class<?> clazz : classes) {
                JSEntryPointSampleClass entryPointClassAnnotation = clazz.getAnnotation(JSEntryPointSampleClass.class);
                if (entryPointClassAnnotation == null) {
                    JSCodingClass annotation = clazz.getAnnotation(JSCodingClass.class);
                    if (annotation != null) {
                        String binding = annotation.binding();
                        if (binding == null || binding.isBlank()) {
                            binding = clazz.getSimpleName();
                        }
                        String full = clazz.getPackageName();
                        String relative = full.startsWith(pack) ? full.substring(pack.lastIndexOf('.')) : full;
                        if (relative.startsWith(".")) {
                            relative = relative.substring(1);
                        }
                        relative = relative.replace('.', '/');
                        this.classRegistry.put(binding, new JSClassData(annotation, clazz, new LinkedHashSet<>(), new StringBuilder(), relative));
                        if (Arrays.asList(clazz.getInterfaces()).contains(JSGlobalVarFactory.class)) {
                            globalVarKeys.add(binding);
                        }
                    }
                } else {
                    if (this.entryPoint != null) {
                        throw new JGemsAPIException(String.format("Duplicate entry point sample-class %s found", clazz.getName()));
                    }
                    this.entryPoint = new JSSampleClassData(entryPointClassAnnotation, clazz, new LinkedHashSet<>(), new StringBuilder());
                }
            }
        }
        this.process(this.classRegistry, this.entryPoint, globalVarKeys);
    }

    private void process(@NotNull Map<String, JSClassData> classRegistry, @Nullable JSSampleClassData sampleClass, @NotNull List<String> globalVarFactoryKeys) {
        classRegistry.forEach((binding,jsClassData) -> {
            Class<?> clazz = jsClassData.aClass();
            if (clazz == null) {
                return;
            }
            Log.get().debug("Reading script-class: " + binding);
            if (this.apiCodingContext.getContext() != null) {
                String jsInit = String.format("var %s = Java.type('%s');", binding, clazz.getCanonicalName());
                this.apiCodingContext.getContext().eval(JGemsAPIScriptingCore.LAN, jsInit);
            }
            this.fillClassDoc(new Pair<>(binding, jsClassData));
        });
        globalVarFactoryKeys.forEach(e -> {
            try {
                final JSGlobalVarFactory<?> factory = (JSGlobalVarFactory<?>) ((JSGlobalVarFactory<?>) (this.getClassRegistry().get(e).aClass().getConstructor().newInstance())).newGlobalVar();
                Log.get().debug("Reading script-global_var: " + this.getClassRegistry().get(e).codingClass().binding());
                if (this.apiCodingContext.getContext() != null) {
                    this.apiCodingContext.getBindings().putMember(factory.getVarName(), factory);
                }
                this.globalVarFactoryKeys.put(factory.getVarName(), new JSGlobalVarData(factory.getVarName(), e));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new JGemsAPIException(ex);
            }
        });
        if (sampleClass != null) {
            Log.get().debug("Reading class-entrypoint: " + sampleClass.aClass().getSimpleName());
            this.fillEntrypointDoc(sampleClass);
        }

        {
            if (this.apiCodingContext.getContext() != null) {
                String jsInitGl = "var GL30 = Java.type('org.lwjgl.opengl.GL30');";
                this.apiCodingContext.getContext().eval(JGemsAPIScriptingCore.LAN, jsInitGl);
            }
        }
    }

    private void fillClassDoc(Pair<String, JSClassData> codingClassPair) {
        final String binding = codingClassPair.first();
        final JSClassData jsClassData = codingClassPair.second();
        final Class<?> clazz = jsClassData.aClass();
        final StringBuilder doc = jsClassData.docBuilder();
        doc.append("// ").append(jsClassData.codingClass().description()).append("\n");
        final int mod = clazz.getModifiers();
        if (Modifier.isPublic(mod)) {
            doc.append("public ");
        } else if (Modifier.isProtected(mod)) {
            doc.append("protected ");
        } else if (Modifier.isPrivate(mod)) {
            doc.append("private ");
        }
        if (Modifier.isAbstract(mod) && !clazz.isInterface()) {
            doc.append("abstract ");
        }
        if (clazz.isInterface()) {
            doc.append("interface ");
        } else if (clazz.isRecord()) {
            doc.append("record ");
        } else if (clazz.isEnum()) {
            doc.append("enum ");
        } else {
            doc.append("class ");
        }
        doc.append(binding);
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            doc.append(" extends ").append(this.resolveType(superClass));
        }
        Class<?>[] interfaces = Arrays.stream(clazz.getInterfaces()).filter(e -> !e.equals(JSGlobalVarFactory.class)).toArray(Class<?>[]::new);
        if (interfaces.length > 0) {
            doc.append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                doc.append(this.resolveType(interfaces[i]));
                if (i < interfaces.length - 1) {
                    doc.append(", ");
                }
            }
        }
        doc.append(" {\n\n");
        doc.append(" \n");

        if (!clazz.isEnum()) {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(JSHideFromDoc.class)) {
                    continue;
                }
                JSCodingConstructor constructorAnnotation = constructor.getAnnotation(JSCodingConstructor.class);
                doc.append("    ");
                if (constructorAnnotation != null) {
                    doc.append("// ").append(constructorAnnotation.description()).append("\n    ");
                } else {
                    doc.append("// Constructor\n    ");
                }
                int mod1 = constructor.getModifiers();
                if (Modifier.isPublic(mod1)) {
                    doc.append("public ");
                } else if (Modifier.isProtected(mod1)) {
                    doc.append("protected ");
                } else if (Modifier.isPrivate(mod1)) {
                    doc.append("private ");
                }
                doc.append(binding).append("(");
                Parameter[] params = constructor.getParameters();
                String[] customNames = (constructorAnnotation != null) ? constructorAnnotation.paramNames() : null;
                for (int i = 0; i < params.length; i++) {
                    String paramName;
                    if (customNames != null && i < customNames.length && !customNames[i].isBlank()) {
                        paramName = customNames[i];
                    } else {
                        paramName = params[i].getName();
                    }
                    paramName = "arg_" + paramName;
                    final String type = this.resolveType(params[i].getType());
                    this.argumentsMap.put(params[i].getType().getCanonicalName(), params[i].getType().getSimpleName());
                    doc.append(paramName).append(": ").append(type);
                    if (i < params.length - 1) {
                        doc.append(", ");
                    }
                }
                doc.append(")");
                doc.append(" { ...; }\n\n");
            }
            if (clazz.getDeclaredConstructors().length > 0) {
                doc.append(" \n");
            }
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(JSHideFromDoc.class)) {
                continue;
            }
            JSCodingField jsField = field.getAnnotation(JSCodingField.class);
            if (jsField == null) {
                continue;
            }
            int fmod = field.getModifiers();
            doc.append("    ");
            if (!jsField.description().isBlank()) {
                doc.append("// ").append(jsField.description()).append("\n    ");
                this.fields.add(field.getName());
            }
            if (Modifier.isPublic(fmod)) {
                doc.append("public ");
            } else if (Modifier.isProtected(fmod)) {
                doc.append("protected ");
            } else if (Modifier.isPrivate(fmod)) {
                doc.append("private ");
            }
            if (Modifier.isStatic(fmod)) {
                doc.append("static ");
            }
            if (Modifier.isFinal(fmod)) {
                doc.append("final ");
            }
            String name = (jsField.paramName() == null || jsField.paramName().isBlank()) ? field.getName() : jsField.paramName();
            doc.append(name).append(": ").append(this.resolveType(field.getType())).append(";\n\n");
            this.argumentsMap.put(field.getType().getCanonicalName(), field.getType().getSimpleName());
        }
        if (!clazz.isEnum()) {
            Method[] methods = clazz.getDeclaredMethods();
            Arrays.sort(methods, Comparator.comparingInt((Method m) -> (Modifier.isStatic(m.getModifiers())) ? 1 : 0).thenComparing((Method m) -> m.getName().length()).thenComparing(Method::getName));
            for (Method method : methods) {
                if (method.isAnnotationPresent(JSHideFromDoc.class)) {
                    continue;
                }
                if (method.getName().startsWith("lambda$")) {
                    continue;
                }
                JSCodingFunctionOrMethod jsMethod = method.getAnnotation(JSCodingFunctionOrMethod.class);
                if (jsMethod != null && !jsMethod.description().isBlank()) {
                    jsClassData.functions.add(new JSFunctionData(method.getName(), jsMethod));
                    doc.append("    // ").append(jsMethod.description()).append("\n");
                }
                doc.append("    ");
                int m = method.getModifiers();
                if (Modifier.isPublic(m)) {
                    doc.append("public ");
                } else if (Modifier.isProtected(m)) {
                    doc.append("protected ");
                } else if (Modifier.isPrivate(m)) {
                    doc.append("private ");
                }
                if (Modifier.isStatic(m)) {
                    doc.append("static ");
                }
                if (Modifier.isAbstract(m)) {
                    doc.append("abstract ");
                }
                if (Modifier.isFinal(m)) {
                    doc.append("final ");
                }
                doc.append(method.getName()).append("(");
                Parameter[] params = method.getParameters();
                for (int i = 0; i < params.length; i++) {
                    String paramName;
                    if (jsMethod != null && jsMethod.paramNames().length > i) {
                        paramName = jsMethod.paramNames()[i];
                    } else {
                        paramName = params[i].getName();
                    }
                    paramName = "arg_" + paramName;
                    doc.append(paramName).append(": ").append(this.resolveType(params[i].getType()));
                    this.argumentsMap.put(params[i].getType().getCanonicalName(), params[i].getType().getSimpleName());
                    if (i < params.length - 1) {
                        doc.append(", ");
                    }
                }
                doc.append(")");
                Class<?> returnType = method.getReturnType();
                if (returnType != void.class) {
                    doc.append(": ").append(this.resolveType(returnType));
                    this.argumentsMap.put(returnType.getCanonicalName(), returnType.getSimpleName());
                }
                doc.append(" { ...; }\n\n");
                doc.append(" \n");
            }
        }
        doc.append("}\n");
    }

    private String resolveType(Class<?> type) {
        if (type.isArray()) {
            return this.resolveType(type.getComponentType()) + "[]";
        }
        if (type.isAnnotationPresent(JSCodingClass.class)) {
            return type.getSimpleName();
        }
        return type.getCanonicalName();
    }

    private void fillEntrypointDoc(JSSampleClassData entryClass) {
        final StringBuilder doc = entryClass.sampleCode();
        doc.append("// Auto generated script\n\n");
        for (Method method : entryClass.aClass().getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (method.isAnnotationPresent(JSHideFromDoc.class)) continue;
            JSCodingFunctionOrMethod jsMethod = method.getAnnotation(JSCodingFunctionOrMethod.class);
            doc.append("/**\n");
            if (jsMethod != null && !jsMethod.description().isBlank()) {
                entryClass.functions.add(new JSFunctionData(method.getName(), jsMethod));
                doc.append(" * ").append(jsMethod.description()).append("\n");
            }
            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                String paramName;
                if (jsMethod != null && jsMethod.paramNames().length > i) {
                    paramName = jsMethod.paramNames()[i];
                } else {
                    paramName = params[i].getName();
                }
                paramName = "arg_" + paramName;
                String jsType = this.convertTypeToJS(params[i].getType());
                doc.append(" * @param {").append(jsType).append("} ").append(paramName).append("\n");
                this.argumentsMap.put(params[i].getType().getCanonicalName(), params[i].getType().getSimpleName());
            }
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class) {
                String jsType = this.convertTypeToJS(returnType);
                doc.append(" * @returns {").append(jsType).append("}\n");
                this.argumentsMap.put(returnType.getCanonicalName(), returnType.getSimpleName());
            }
            doc.append(" */\n");
            doc.append("function ").append(method.getName()).append("(");
            for (int i = 0; i < params.length; i++) {
                String paramName;
                if (jsMethod != null && jsMethod.paramNames().length > i) {
                    paramName = jsMethod.paramNames()[i];
                } else {
                    paramName = params[i].getName();
                }
                paramName = "arg_" + paramName;
                doc.append(paramName);
                if (i < params.length - 1) {
                    doc.append(", ");
                }
            }
            doc.append(") {\n");
            doc.append("    // Code here.\n");
            doc.append("}\n\n");
        }
    }

    private String convertTypeToJS(Class<?> type) {
        if (type == String.class) {
            return "string";
        }
        if (type == int.class || type == long.class || type == float.class || type == double.class) {
            return "number";
        }
        if (type == boolean.class) {
            return "boolean";
        }
        if (type.isArray()) {
            return "Array<" + this.convertTypeToJS(type.getComponentType()) + ">";
        }
        if (Collection.class.isAssignableFrom(type)) {
            return "Array<any>";
        }
        if (Map.class.isAssignableFrom(type)) {
            return "Map<any, any>";
        }
        if (type.isAnnotationPresent(JSCodingClass.class)) {
            return type.getSimpleName();
        }
        return type.getCanonicalName();

    }

    public Set<String> getFields() {
        return this.fields;
    }

    public Map<String, String> getArgumentsMap() {
        return this.argumentsMap;
    }

    public Map<String, JSGlobalVarData> getGlobalVarFactoryKeys() {
        return this.globalVarFactoryKeys;
    }

    public Map<String, JSClassData> getClassRegistry() {
        return this.classRegistry;
    }

    public JSSampleClassData getEntryPointClass() {
        return this.entryPoint;
    }

    public record JSClassData(JSCodingClass codingClass, Class<?> aClass, Set<JSFunctionData> functions, StringBuilder docBuilder, String path) {
    }
    public record JSSampleClassData(JSEntryPointSampleClass codingSampleClass, Class<?> aClass, Set<JSFunctionData> functions, StringBuilder sampleCode) {
    }
    public record JSFunctionData(String funName, JSCodingFunctionOrMethod funDescription) {}
    public record JSGlobalVarData(String varName, String varKey) {}
}