package api.scripting.coding.env;

import api.scripting.coding.APICodingContext;
import api.scripting.coding.env.def.*;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsAPIException;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.Closeable;
import java.lang.reflect.*;
import java.util.*;

public class APICodeEnvironmentController implements Closeable {
    private final List<String> globalVarFactoryKeys;
    private final Map<String, JSClassData> classRegistry;
    private final APICodingContext apiCodingContext;
    private JSSampleClassData entryPoint;

    public APICodeEnvironmentController(@NotNull APICodingContext apiCodingContext) {
        this.apiCodingContext = apiCodingContext;
        this.classRegistry = new LinkedHashMap<>();
        this.globalVarFactoryKeys = new ArrayList<>();
        this.entryPoint = null;
    }

    public void close() {
        this.classRegistry.clear();
        this.globalVarFactoryKeys.clear();
        this.entryPoint = null;
    }

    public void scan(@NotNull String... packs) {
        Log.get().debug("Script-Env Scan-Pack: " + Arrays.toString(packs));
        final Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packs).addScanners(Scanners.TypesAnnotated));
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(JSCodingClass.class);
        for (Class<?> clazz : classes) {
            JSEntryPointClass entryPointClassAnnotation = clazz.getAnnotation(JSEntryPointClass.class);
            if (entryPointClassAnnotation == null) {
                JSCodingClass annotation = clazz.getAnnotation(JSCodingClass.class);
                if (annotation != null) {
                    String binding = annotation.binding();
                    if (binding == null || binding.isBlank()) {
                        binding = clazz.getSimpleName();
                    }
                    this.classRegistry.put(binding, new JSClassData(annotation, clazz, new HashSet<>(), new StringBuilder()));
                    if (clazz.isAssignableFrom(JSGlobalVarFactory.class)) {
                        this.globalVarFactoryKeys.add(binding);
                    }
                }
            } else {
                if (this.entryPoint != null) {
                    throw new JGemsAPIException(String.format("Duplicate entry point class %s found", clazz.getName()));
                }
                this.entryPoint = new JSSampleClassData(entryPointClassAnnotation, clazz, new HashSet<>(), new StringBuilder());
            }
        }
        this.process(this.classRegistry, this.entryPoint, this.globalVarFactoryKeys);
    }

    private void process(@NotNull Map<String, JSClassData> classRegistry, @Nullable JSSampleClassData sampleClass, @NotNull List<String> globalVarFactoryKeys) {
        classRegistry.forEach((binding,jsClassData) -> {
            Class<?> clazz = jsClassData.aClass();
            if (clazz == null) {
                return;
            }
            Log.get().debug("Reading script-class: " + binding);
            if (this.apiCodingContext.getContext() != null) {
                this.apiCodingContext.getBindings().putMember(binding, clazz);
            }
            this.fillClassDoc(new Pair<>(binding, jsClassData));
        });
        if (this.apiCodingContext.getContext() != null) {
            globalVarFactoryKeys.forEach(e -> {
                try {
                    Log.get().debug("Reading script-global_var: " + this.getClassRegistry().get(e).codingClass().binding());
                    this.apiCodingContext.getBindings().putMember(e, ((JSGlobalVarFactory<?>) (this.getClassRegistry().get(e).aClass().getConstructor().newInstance())).newGlobalVar());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new JGemsAPIException(ex);
                }
            });
        }
        if (sampleClass != null) {
            Log.get().debug("Reading class-entrypoint: " + sampleClass.codingSampleClass().getClass().getSimpleName());
            this.fillEntrypointDoc(sampleClass);
        }
    }

    private void fillClassDoc(Pair<String, JSClassData> codingClassPair) {
        final String binding = codingClassPair.first();
        final JSClassData jsClassData = codingClassPair.second();
        final Class<?> clazz = jsClassData.aClass();
        final StringBuilder doc = jsClassData.docBuilder();
        doc.append("// ").append(jsClassData.codingClass().description()).append("\n\n");
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
            doc.append(" : ").append(this.resolveType(superClass));
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
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(JSHideFromDoc.class)) {
                continue;
            }
            doc.append("    ");
            doc.append("// Constructor\n    ");
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
            for (int i = 0; i < params.length; i++) {
                String paramName = params[i].getName();
                final String type = params[i].getType().isAnnotationPresent(JSCodingClass.class) ? params[i].getType().getSimpleName() : params[i].getType().getCanonicalName();
                doc.append(paramName).append(": ").append(type);
                if (i < params.length - 1) {
                    doc.append(", ");
                }
            }
            doc.append(")");
            doc.append(" { ... }\n\n");
        }
        doc.append(" \n");
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
            if (!jsField.description().isBlank()) {
                doc.append("// ").append(jsField.description()).append("\n    ");
            }
            doc.append(name).append(": ").append(this.resolveType(field.getType())).append(";\n\n");
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(JSHideFromDoc.class)) {
                continue;
            }
            JSCodingFunctionOrMethod jsMethod = method.getAnnotation(JSCodingFunctionOrMethod.class);
            if (jsMethod != null && !jsMethod.description().isBlank()) {
                jsClassData.functions.add(new JSFunction(method.getName(), jsMethod));
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
                doc.append(paramName).append(": ").append(this.resolveType(params[i].getType()));
                if (i < params.length - 1) {
                    doc.append(", ");
                }
            }
            doc.append(")");
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class) {
                doc.append(": ").append(this.resolveType(returnType));
            }
            doc.append(" { ... }\n\n");
            doc.append(" \n");
        }
        doc.append("}\n");
    }

    private String resolveType(Class<?> type) {
        if (type.isArray()) {
            return resolveType(type.getComponentType()) + "[]";
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
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (method.isAnnotationPresent(JSHideFromDoc.class)) {
                continue;
            }
            JSCodingFunctionOrMethod jsMethod = method.getAnnotation(JSCodingFunctionOrMethod.class);
            if (jsMethod != null && !jsMethod.description().isBlank()) {
                entryClass.functions.add(new JSFunction(method.getName(), jsMethod));
                doc.append("// ").append(jsMethod.description()).append("\n");
            }
            /*
            int mod = method.getModifiers();
            doc.append(Modifier.isPublic(mod) ? "public " : Modifier.isProtected(mod) ? "protected " : Modifier.isPrivate(mod) ? "private " : "");
            if (Modifier.isStatic(mod)) {
                doc.append("static ");
            }
            if (Modifier.isAbstract(mod)) {
                doc.append("abstract ");
            }
            if (Modifier.isFinal(mod)) {
                doc.append("final ");
            }
             */
            doc.append("function ").append(method.getName()).append("(");
            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                String paramName;
                if (jsMethod != null && jsMethod.paramNames().length > i) {
                    paramName = jsMethod.paramNames()[i];
                } else {
                    paramName = params[i].getName();
                }
                doc.append(paramName).append(": ").append(convertTypeToJS(params[i].getType()));
                if (i < params.length - 1) {
                    doc.append(", ");
                }
            }
            doc.append(")");
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class) {
                doc.append(": ").append(convertTypeToJS(returnType));
            }
            doc.append(" {\n");
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
            return "Array<" + convertTypeToJS(type.getComponentType()) + ">";
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

    public List<String> getGlobalVarFactoryKeys() {
        return this.globalVarFactoryKeys;
    }

    public Map<String, JSClassData> getClassRegistry() {
        return this.classRegistry;
    }

    public JSSampleClassData getEntryPointClass() {
        return this.entryPoint;
    }

    public record JSClassData(JSCodingClass codingClass, Class<?> aClass, Set<JSFunction> functions, StringBuilder docBuilder) {
    }
    public record JSSampleClassData(JSEntryPointClass codingSampleClass, Class<?> aClass, Set<JSFunction> functions, StringBuilder sampleCode) {
    }
    public record JSFunction(String funName, JSCodingFunctionOrMethod funDescription) {}
}