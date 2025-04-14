package api.scripting.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class APIScriptingFunction {
    private final String name;
    private final List<Class<?>> args;

    APIScriptingFunction(String name, Class<?>... args) {
        this.name = name;
        this.args = new ArrayList<>(Arrays.asList(args));
        APIScriptsListing.apiScriptingFunctions.add(this);
    }

    @Override
    public String toString() {
        return "function " + this.getName();
    }

    public List<Class<?>> getArgs() {
        return this.args;
    }

    public String getName() {
        return this.name;
    }
}
