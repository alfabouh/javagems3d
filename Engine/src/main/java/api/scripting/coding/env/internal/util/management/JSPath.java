package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.files.JGemsPath;

import java.util.Arrays;

@JSCodingClass(binding = "JSPath", description = "Wrapper for file system paths used in scripting, allowing hierarchical construction and access to Java-side path objects.")
public class JSPath {
    private final JGemsPath jGemsPath;

    @JSCodingConstructor(description = "Create a JSPath from an existing JGemsPath instance.", paramNames = {"path"})
    public JSPath(JGemsPath path) {
        this.jGemsPath = path;
    }

    @JSCodingConstructor(description = "Create a JSPath from a string representing the path.", paramNames = {"path"})
    public JSPath(String path) {
        this.jGemsPath = new JGemsPath(path);
    }

    @JSCodingConstructor(description = "Create a JSPath from a root string and a sequence of folder names.", paramNames = {"root", "foldersTrace"})
    public JSPath(String root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root, foldersTrace);
    }

    @JSCodingConstructor(description = "Create a JSPath from another JSPath as root and additional folder names.", paramNames = {"root", "foldersTrace"})
    public JSPath(JSPath root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root.getJavaPath(), foldersTrace);
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java JGemsPath object.")
    public JGemsPath getJavaPath() {
        return this.jGemsPath;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "JSPath{" +
                "jGemsPath=" + jGemsPath +
                '}';
    }
}