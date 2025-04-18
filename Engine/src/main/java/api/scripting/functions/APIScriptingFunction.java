package api.scripting.functions;

import javagems3d.system.service.collections.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class APIScriptingFunction {
    private final String name;
    private final String description;
    private final List<Pair<Class<?>, String>> args;

    @SafeVarargs
    APIScriptingFunction(String name, String description, Pair<Class<?>, String>... args) {
        this.name = name;
        this.description = description;
        this.args = new ArrayList<>(Arrays.asList(args));
    }

    @Override
    public String toString() {
        return "function " + this.getName();
    }

    public List<Pair<Class<?>, String>> getArgs() {
        return this.args;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}
