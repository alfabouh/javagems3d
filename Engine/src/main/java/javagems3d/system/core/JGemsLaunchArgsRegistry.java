package javagems3d.system.core;

import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import java.util.*;

public final class JGemsLaunchArgsRegistry {
    private final Map<String, LaunchArg> reverseArgMap;
    private final Map<String, String> gotArgs;
    public static JGemsLaunchArgsRegistry INSTANCE = new JGemsLaunchArgsRegistry();
    public static DefaultLaunchArgs DEFAULT_ARGS = new DefaultLaunchArgs();

    private JGemsLaunchArgsRegistry() {
        this.gotArgs = new HashMap<>();
        this.reverseArgMap = new HashMap<>();
    }

    public static void clear() {
        JGemsLaunchArgsRegistry.INSTANCE.gotArgs.clear();
        JGemsLaunchArgsRegistry.INSTANCE.reverseArgMap.clear();
    }

    @SafeVarargs
    public static String[] getArgumentFrom(@NotNull Pair<LaunchArg, String>... a) {
        String[] arg = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            arg[i] = a[i].first().argument() + "=" + a[i].second();
        }
        return arg;
    }

    public void read(@Nullable String[] args) {
        if (args == null) {
            return;
        }
        this.gotArgs.clear();
        for (String s : args) {
            try {
                if (s == null) {
                    continue;
                }
                String[] arg = s.split("=");
                if (arg.length == 2) {
                    this.gotArgs.put(arg[0], arg[1]);
                }
            } catch (Exception e) {
                Log.get().exception(e);
            }
        }
    }

    public void printArgs() {
        Log.get().separator();
        Log.get().warn("Input args:");
        this.gotArgs.forEach((k, v) -> {
            Log.get().warn(k + "=" + v);
        });
        Log.get().separator();
    }

    @SuppressWarnings("all")
    public @Nullable <T> T getValue(@NotNull LaunchArg input) {
        try {
            String key = this.gotArgs.get(input.argument());
            if (key != null) {
                LaunchArg launchArg = this.reverseArgMap.get(input.argument());
                if (launchArg != null) {
                    return (T) launchArg.argReaderFunction().read(key);
                } else {
                    System.out.println("Unknown resolver: " + input.argument() + " / " + key);
                }
            }
            return (T) input.defaultValue();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public void putManually(String key, String value) {
        this.gotArgs.put(key, value);
    }

    public static class DefaultLaunchArgs {
        public DefaultLaunchArgs() {
        }

        public final LaunchArg WIN_SIZE = LaunchArg.create("win_size", (arg -> {
            String[] size = arg.split(";");
            int i1 = Integer.parseInt(size[0]);
            int i2 = Integer.parseInt(size[1]);
            return new Vector2i(i1, i2);
        }), null);
        public final LaunchArg API_APP_CLASSPATH = LaunchArg.create("api_app_classpath", (args -> args), null);
        public final LaunchArg MAP_TEST = LaunchArg.create("map_test", (args -> args.equals("true")), false);
        public final LaunchArg TEST_MAP_ID = LaunchArg.create("map_path", (args -> args), null);
        public final LaunchArg NO_FULL_SCREEN = LaunchArg.create("no_full_screen", (args -> args.equals("true")), false);
        public final LaunchArg WORKBENCH = LaunchArg.create("workbench", (args -> args.equals("true")), false);
        public final LaunchArg EXTERNAL_GAME_DEF = LaunchArg.create("external_def", (args -> args), null);
        public final LaunchArg NO_SOUND = LaunchArg.create("no_sound", (args -> args.equals("true")), false);
        public final LaunchArg DEBUG = LaunchArg.create("debug", (args -> args.equals("true")), false);
    }

    public static class LaunchArg {
        private final @NotNull String argument;
        private final @NotNull ArgReaderFunction<?> argReaderFunction;
        private final @Nullable Object defaultValue;

        private LaunchArg(@NotNull String argument, @NotNull ArgReaderFunction<?> argReaderFunction, @Nullable Object defaultValue) {
            this.argument = argument;
            this.argReaderFunction = argReaderFunction;
            this.defaultValue = defaultValue;
        }

        public @NotNull String argument() {
            return this.argument;
        }

        public @NotNull ArgReaderFunction<?> argReaderFunction() {
            return this.argReaderFunction;
        }

        public @Nullable Object defaultValue() {
            return this.defaultValue;
        }

        public static LaunchArg create(@NotNull String argument, @NotNull ArgReaderFunction<?> argReaderFunction, @Nullable Object defaultValue) {
            LaunchArg launchArg = new LaunchArg(argument, argReaderFunction, defaultValue);
            JGemsLaunchArgsRegistry.INSTANCE.reverseArgMap.put(argument, launchArg);
            return launchArg;
        }
    }

    @FunctionalInterface
    public interface ArgReaderFunction<T> {
        T read(String args);
    }
}
