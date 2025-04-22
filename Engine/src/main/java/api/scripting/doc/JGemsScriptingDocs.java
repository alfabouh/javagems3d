package api.scripting.doc;

import api.scripting.doc.annotations.JSCommentary;
import api.scripting.doc.annotations.JSGlobalVar;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public final class JGemsScriptingDocs {
    private static final Set<String> packagesWithJSCLasses = new HashSet<>();

    static {
        JGemsScriptingDocs.addPackageWithJSCodeDocumentary("api.scripting.classes");
    }

    private List<ClassDesc> types;

    public JGemsScriptingDocs() {
        this.types = new ArrayList<>();
        this.load(JGemsScriptingDocs.packagesWithJSCLasses);
    }

    public static void addPackageWithJSCodeDocumentary(String javaPath) {
        JGemsScriptingDocs.packagesWithJSCLasses.add(javaPath);
    }

    public void load(Set<String> packagePaths) {
        final List<ClassDesc> classesDesc = new ArrayList<>();

        for (String packagePath : packagePaths) {
            Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packagePath)).setScanners(Scanners.SubTypes.filterResultsBy(s -> s.startsWith(packagePath)), Scanners.TypesAnnotated, Scanners.MethodsSignature));
            final Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(JSTypeDoc.class);
            final List<Class<?>> sortedClasses = classSet.stream().sorted(Comparator.comparingInt((e) -> e.getAnnotation(JSTypeDoc.class).priority().ordinal())).collect(Collectors.toList());

            for (Class<?> clazz : sortedClasses) {
                ClassDesc classDesc = this.getClassDesc(clazz);
                final List<Method> sortedMethods = Arrays.stream(clazz.getDeclaredMethods()).filter(e -> e.isAnnotationPresent(JSMethodDoc.class)).sorted(Comparator.comparingInt((e) -> e.getAnnotation(JSMethodDoc.class).order())).collect(Collectors.toList());

                for (Method method : sortedMethods) {
                    MethodDesc methodDesc = this.getMethodDesc(method);
                    classDesc.getMethods().add(methodDesc);
                }

                classesDesc.add(classDesc);
            }

            reflections = null;
        }
        System.gc();

        this.types = classesDesc;
    }

    private @NotNull ClassDesc getClassDesc(Class<?> clazz) {
        JSTypeDoc JSTypeDocAnnotation = clazz.getAnnotation(JSTypeDoc.class);

        String commentary = null;
        String description = JSTypeDocAnnotation.description();
        String globalVar = null;
        String parent = null;

        if (clazz.isAnnotationPresent(JSGlobalVar.class)) {
            JSGlobalVar JSGlobalVarAnnotation = clazz.getAnnotation(JSGlobalVar.class);
            globalVar = JSGlobalVarAnnotation.varName();
        }
        if (clazz.isAnnotationPresent(JSCommentary.class)) {
            JSCommentary JSCommentaryAnnotation = clazz.getAnnotation(JSCommentary.class);
            commentary = JSCommentaryAnnotation.commentary();
        }
        if (clazz.getSuperclass() != null) {
            parent = clazz.getSuperclass().getSimpleName();
        }

        return new ClassDesc(commentary, parent, clazz.getSimpleName(), clazz.getSimpleName().toLowerCase(), description, globalVar);
    }

    private @NotNull MethodDesc getMethodDesc(Method method) {
        JSMethodDoc jsMethodDocAnnotation = method.getAnnotation(JSMethodDoc.class);
        String commentary2 = null;
        String description2 = jsMethodDocAnnotation.description();
        String[] argsDescription = jsMethodDocAnnotation.args();

        List<Pair<String, String>> args = new ArrayList<>();
        String returnValue = method.getReturnType().getSimpleName();

        for (int i = 0; i < method.getParameterCount(); i++) {
            args.add(new Pair<>(method.getParameterTypes()[i].getSimpleName(), argsDescription[i]));
        }

        if (method.isAnnotationPresent(JSCommentary.class)) {
            JSCommentary JSCommentaryAnnotation = method.getAnnotation(JSCommentary.class);
            commentary2 = JSCommentaryAnnotation.commentary();
        }

        return new MethodDesc(commentary2, method.getName(), returnValue, args, description2);
    }

    public List<ClassDesc> getTypes() {
        return this.types;
    }

    interface Desc {
        String description();

        String commentary();
    }

    public static class MethodDesc implements Desc {
        private final String commentary;
        private final String methodName;
        private final String returnValue;
        private final List<Pair<String, String>> args;
        private final String description;

        MethodDesc(String commentary, String methodName, String returnValue, List<Pair<String, String>> args, String description) {
            this.args = args;
            this.commentary = commentary;
            this.methodName = methodName;
            this.returnValue = returnValue;
            this.description = description;
        }

        public String commentary() {
            return this.commentary;
        }

        public String methodName() {
            return this.methodName;
        }

        public String returnValue() {
            return this.returnValue;
        }

        public List<Pair<String, String>> args() {
            return this.args;
        }

        public String description() {
            return this.description;
        }
    }

    public static class ClassDesc implements Desc {
        private final String commentary;
        private final String classSimpleName;
        private final String classVarName;
        private final String description;
        private final List<MethodDesc> methods;
        private final String globalVarName;
        private final String parent;

        ClassDesc(String commentary, String parent, String classSimpleName, String classVarName, String description, String globalVarName) {
            this.commentary = commentary;
            this.parent = parent;
            this.classSimpleName = classSimpleName;
            this.classVarName = classVarName;
            this.description = description;
            this.globalVarName = globalVarName;
            this.methods = new ArrayList<>();
        }

        public List<MethodDesc> getMethods() {
            return this.methods;
        }

        public String parent() {
            return this.parent;
        }

        public String commentary() {
            return this.commentary;
        }

        public String classSimpleName() {
            return this.classSimpleName;
        }

        public String classVarName() {
            return this.classVarName;
        }

        public String description() {
            return this.description;
        }

        public String globalVarName() {
            return this.globalVarName;
        }
    }
}