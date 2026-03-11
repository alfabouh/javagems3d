package javagems3d.system.core;

import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import java.util.*;

public final class JGemsLaunchArgsRegistry {
    public static JGemsLaunchArgsRegistry INSTANCE = new JGemsLaunchArgsRegistry();
    private static final Map<String, JGemsLaunchArgs> reverseArgMap = new HashMap<>();
    private final Map<JGemsLaunchArgs, Object> argTypeObjectMap;

    static {
        for (JGemsLaunchArgs argType : JGemsLaunchArgs.values()) {
            JGemsLaunchArgsRegistry.reverseArgMap.put(argType.getArgument(), argType);
        }
    }

    private JGemsLaunchArgsRegistry() {
        this.argTypeObjectMap = new HashMap<>();
    }

    public static void clear() {
        JGemsLaunchArgsRegistry.INSTANCE.argTypeObjectMap.clear();
        JGemsLaunchArgsRegistry.reverseArgMap.clear();
    }

    @SafeVarargs
    public static String[] getArgumentFrom(@NotNull Pair<JGemsLaunchArgs, String>... a) {
        String[] arg = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            arg[i] = a[i].first().getArgument() + "=" + a[i].second();
        }
        return arg;
    }

    public void read(@Nullable String[] args) {
        if (args == null) {
            return;
        }
        this.argTypeObjectMap.clear();
        for (String s : args) {
            try {
                if (s == null) {
                    continue;
                }
                String[] arg = s.split("=");
                JGemsLaunchArgs argType = JGemsLaunchArgsRegistry.reverseArgMap.get(arg[0]);
                if (argType != null) {
                    this.argTypeObjectMap.put(argType, argType.getArgReaderFunction().read(arg[1]));
                } else {
                    Log.get().info("Unknown arg: " + arg[0]);
                }
            } catch (Exception e) {
                Log.get().exception(e);
            }
        }
    }

    public void printArgs() {
        Log.get().separator();
        Log.get().warn("Input args:");
        this.argTypeObjectMap.forEach((k, v) -> {
            Log.get().warn(k.getArgument() + "=" + v);
        });
        Log.get().separator();
    }

    @SuppressWarnings("all")
    public @Nullable <T> T getValue(@NotNull JGemsLaunchArgsRegistry.JGemsLaunchArgs argType) {
        try {
            return (T) this.argTypeObjectMap.getOrDefault(argType, argType.getDefaultValue());
        } catch (Exception e) {
            Log.get().exception(e);
            return null;
        }
    }

    public enum JGemsLaunchArgs {
        WIN_SIZE("win_size", (arg -> {
            String[] size = arg.split(";");
            int i1 = Integer.parseInt(size[0]);
            int i2 = Integer.parseInt(size[1]);
            return new Vector2i(i1, i2);
        }), null),
        MAP_TEST("map_test", (args -> args.equals("true")), false),
        TEST_MAP_ID("map_path", (args -> args), null),
        NO_FULL_SCREEN("no_full_screen", (args -> args.equals("true")), false),
        WORKBENCH("workbench", (args -> args.equals("true")), false),
        EXTERNAL_GAME_DEF("external_def", (args -> args), null),
        NO_SOUND("no_sound", (args -> args.equals("true")), false),
        DEBUG("debug", (args -> args.equals("true")), false);

        private final String argument;
        private final ArgReaderFunction<?> argReaderFunction;
        private final Object defaultValue;

        JGemsLaunchArgs(@NotNull String argument, @NotNull ArgReaderFunction<?> argReaderFunction, @Nullable Object defaultValue) {
            this.argument = argument;
            this.argReaderFunction = argReaderFunction;
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return this.defaultValue;
        }

        public String getArgument() {
            return this.argument;
        }

        @SuppressWarnings("all")
        <T> ArgReaderFunction<T> getArgReaderFunction() {
            return (ArgReaderFunction<T>) this.argReaderFunction;
        }
    }

    @FunctionalInterface
    public interface ArgReaderFunction<T> {
        T read(String args);
    }
}
