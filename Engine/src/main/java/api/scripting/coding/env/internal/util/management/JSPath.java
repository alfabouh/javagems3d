package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.files.JGemsPath;

import java.util.Arrays;

@JSCodingClass(binding = "JSPath", description = "...")
public class JSPath {
    private final JGemsPath jGemsPath;

    @JSCodingConstructor(description = "...", paramNames = {"path"})
    public JSPath(JGemsPath path) {
        this.jGemsPath = path;
    }

    @JSCodingConstructor(description = "...", paramNames = {"path"})
    public JSPath(String path) {
        this.jGemsPath = new JGemsPath(path);
    }

    @JSCodingConstructor(description = "...", paramNames = {"root", "foldersTrace"})
    public JSPath(String root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root, foldersTrace);
    }

    @JSCodingConstructor(description = "...", paramNames = {"root", "foldersTrace"})
    public JSPath(JSPath root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root.getJavaPath(), foldersTrace);
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"..."})
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