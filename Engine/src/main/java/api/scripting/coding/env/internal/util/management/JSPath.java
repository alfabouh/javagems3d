package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.misc.JSString;
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
    public JSPath(JSString path) {
        this.jGemsPath = new JGemsPath(path.string());
    }

    @JSCodingConstructor(description = "...", paramNames = {"root", "foldersTrace"})
    public JSPath(JSString root, JSString... foldersTrace) {
        this.jGemsPath = new JGemsPath(root.string(), Arrays.stream(foldersTrace).map(JSString::string).toArray(String[]::new));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"..."})
    public JGemsPath getJavaPath() {
        return this.jGemsPath;
    }
}