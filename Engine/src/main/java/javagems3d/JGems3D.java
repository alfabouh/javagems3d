package javagems3d;

import api.system.JGemsAPIData;
import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcessBuilder;
import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.os.OS;
import javagems3d.system.service.os.SysOSValidation;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import api.system.JGemsAPI;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.resources.localisation.JGemsLocalisation;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNotFoundException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.system.service.synchronizing.SyncManager;
import javagems3d.system.settings.JGemsSettings;
import logger.SystemLogging;
import logger.managers.JGemsLogging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public final class JGems3D {
    private final OS os;
    public static int MAP_MAX_SIZE = 128;

    public static boolean DEBUG_MODE = false;
    public static boolean FIRST_LAUNCH = false;
    public static long rngSeed;
    public static Random random;
    private static JGems3D mainObject;

    private JGemsCore core;
    private final JGemsSettings jGemsSettings;
    private final JGemsLocalisation jGemsLocalisation;

    private boolean shouldBeClosed;

    public static JGems3D get() {
        return JGems3D.mainObject;
    }

    private JGems3D() throws JGemsRuntimeException {
        try {
            SystemLogging.get().setCurrentLogging(new JGemsLogging("JGemsLogger"));
            JGemsAPI.INIT_JGEMS();
            JGemsAPI.get().launchAPI();
            JGems3D.checkFilesDirectory();
        } catch (IOException | JGemsAPIException e) {
            throw new JGemsRuntimeException(e);
        }
        this.os = SysOSValidation.getCurrentOS();

        JGems3D.rngSeed = JGems3D.systemTime();
        JGems3D.random = new Random(JGems3D.rngSeed);

        this.jGemsSettings = new JGemsSettings(new File(JGems3D.getGameFilesFolder().toFile(), "jgems_settings.txt"));
        this.jGemsLocalisation = new JGemsLocalisation();
        this.shouldBeClosed = false;
    }

    public OS getOS() {
        return this.os;
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static JGemsAPIData getAPIAppData() {
        return JGemsAPI.APIAppData();
    }

    @SuppressWarnings("all")
    public static void launch(@NotNull JGemsLaunchArgsRegistry argsRegistry) {
        if (JGems3D.mainObject != null) {
            throw new JGemsRuntimeException("Couldn't launch JavaGems more than 1 times");
        }
        try {
            JGems3D.mainObject = new JGems3D();
            JGems3D.LAUNCH_ARGS_RESOLVE(argsRegistry);
        } catch (JGemsRuntimeException e) {
            LoggingManager.showExceptionDialog("Where was an error, while creating an application instance!", e);
            Log.get().exception(e);
            return;
        }
        JGems3D.start();
    }

    public static void LAUNCH_ARGS_RESOLVE(@NotNull JGemsLaunchArgsRegistry argsRegistry) {
        if (argsRegistry.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.DEBUG) == Boolean.TRUE) {
            JGems3D.DEBUG_MODE = true;
        }
        Vector2i newWinSize = argsRegistry.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.WIN_SIZE);
        if (newWinSize != null) {
            JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH = newWinSize.x;
            JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT = newWinSize.y;
        }
        Boolean noSound = argsRegistry.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_SOUND);
        if (noSound != null) {
            JGemsConfig.SYSTEM.DISABLE_SOUNDS = noSound;
        }
        Boolean noFullScreen = argsRegistry.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_FULL_SCREEN);
        if (noFullScreen != null) {
            JGemsConfig.SYSTEM.DISABLE_FULLSCREEN_START_ADJUSTMENT = noFullScreen;
        }
    }

    private static void start() {
        try {
            JGemsLaunchArgsRegistry.INSTANCE.printArgs();
            Log.get().debug("BEGIN");
            Log.get().info("Starting system! Date: " + JGems3D.date());
            Log.get().info(JGems3D.get().toString());
            Log.get().separator();
            Log.get().info("Loading settings from path...");
            if (JGems3D.get().getGameSettings().makeSettingDirs()) {
                JGems3D.FIRST_LAUNCH = true;
            } else {
                JGems3D.get().getGameSettings().loadOptions();
            }
            JGems3D.get().core = new JGemsCore();
            JGems3D.get().getCore().startSystem();
        } catch (Exception e) {
            Log.get().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.", e);
        }
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(JGems3D.getGameFilesFolder())) {
            JGems3D.getGameFilesFolder().toFile().mkdirs();
            Log.get().debug("Created system folder");
        }
    }

    public static String date() {
        LocalDateTime date = LocalDateTime.now();
        return date.toString();
    }

    public static String getGamePath() {
        return new File(JGems3D.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static boolean checkIfFileExists(@NotNull JGems3D.GetSource source, JGemsPath path) {
        try (InputStream stream = JGems3D.getInputStream(source, path)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static InputStream getInputStream(@NotNull JGems3D.GetSource getSource, JGemsPath path) throws JGemsIOException {
        InputStream inputStream = null;
        try {
            switch (getSource) {
                case JAR: {
                    inputStream = JGems3D.class.getResourceAsStream(path.getFullPath());
                    break;
                }
                case EXTERNAL: {
                    String p = path.getFullPath();
                    if (p.startsWith("/") && p.length() > 2 && p.charAt(2) == ':') {
                        p = p.substring(1);
                    }
                    inputStream = Files.newInputStream(Paths.get(p));
                    break;
                }
            }
        } catch (IOException e) {
            Log.get().error(getSource.name());
            throw new JGemsIOException(e);
        }
        if (inputStream == null) {
            throw new JGemsNotFoundException("Couldn't find: " + path);
        }
        return inputStream;
    }

    public static Path getEngineFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getGameFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase() + File.separator + JGems3D.getAPIAppData().getId();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public String I18n(String key, Object... objects) {
        if (this.getLocalisation() == null) {
            Log.get().warn("Tried to get localised name from NULL Localisation Manager");
            return key;
        }
        return String.format(this.getLocalisation().format(key), objects);
    }

    public void changeIcon(@Nullable JGemsPath icon) {
        this.getScreen().setIcon(icon);
    }

    public void changeTitle(@NotNull String title) {
        this.getScreen().setTitle(title);
    }

    public static void close(@Nullable Exception exception) {
        Log.get().warn("Exit...");
        synchronized (JGems3D.get()) {
            JGems3D.get().shouldBeClosed = true;
            JGems3D.get().getCore().addExceptionInTrace(exception);
        }
    }

    public static void freeSync() {
        SyncManager.freeAll();
        synchronized (JGemsPhysics.locker) {
            JGemsPhysics.locker.notifyAll();
        }
    }

    public ISceneRenderer getSceneRenderer() {
        return this.getScreen().getScene().getSceneRenderer();
    }

    public JGemsScreen getScreen() {
        return this.getCore().getScreen();
    }

    public JGemsPhysics getPhysics() {
        return this.getCore().getPhysics();
    }

    public JGemsSettings getGameSettings() {
        synchronized (this.jGemsSettings) {
            return this.jGemsSettings;
        }
    }

    public JGemsSoundManager getSoundManager() {
        synchronized (this.getCore().getSoundManager()) {
            return this.getCore().getSoundManager();
        }
    }

    public JGemsCore getCore() {
        synchronized (this) {
            return this.core;
        }
    }

    public JGemsLocalisation getLocalisation() {
        synchronized (this.jGemsLocalisation) {
            return this.jGemsLocalisation;
        }
    }

    public boolean isCurrentGameMapValid() {
        return this.getCore().isCurrentGameMapValid();
    }

    public JGemsResourceManager getResourceManager() {
        return this.getCore().getResourceManager();
    }

    @SuppressWarnings("all")
    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public boolean isCurrentGameMapPlayerValid() {
        return this.getCore().isCurrentGameMapPlayerValid();
    }

    public IPlayer getCurrentGameMapPlayer() {
        return this.getCore().getCurrentGameMapPlayer();
    }

    public boolean isPaused() {
        return this.getCore().engineState().isPaused();
    }

    public String toString() {
        return JGemsCore.ENG_NAME + ": " + JGemsCore.ENG_VER + " | appId = " + JGems3D.getAPIAppData().getId();
    }

    public static abstract class DEFAULT_WORKBENCH_PROJECT_CONSTANTS {
        public static final String MAPPING_DATA_COMM = "JAVAGEMS3D MAP FILE";
        public static final String GAME_DATA_COMM = "JAVAGEMS3D GAME FILE";

        public static final String GAME_DATA_VERSION = "1.0";
        public static final String MAPPING_DATA_VERSION = "1.0";

        public static final String MAPPING_PROJECT_FILE = ".jg3dmp";
        public static final String GAME_PROJECT_FILE = ".jg3dgm";

        public static final String MAPPING_DATA_FILE = ".jg3mp_data";
        public static final String GAME_DATA_FILE = ".jg3gm_data";

        public static final String MAPPING_SCRIPT_FILE = ".js";

        //public static final Set<String> MAPPING_SUPPORTED_VERSIONS = new HashSet<String>() {{
        //    add("1.0");
        //}};

        //public static final Set<String> GAME_SUPPORTED_VERSIONS = new HashSet<String>() {{
        //    add("1.0");
        //}};

        public static final String MAPPING_DATA_INFO = DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_COMM + " VER: " + DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_VERSION;
        public static final String GAME_DATA_INFO = DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_COMM + " VER: " + DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_VERSION;
    }

    public static abstract class DEFAULT_PATHS {
        public static final String PARTICLES = "/assets/jgems/textures/particles/";
        public static final String CUBE_MAPS = "/assets/jgems/textures/cubemaps/";
        public static final String TEXTURES = "/assets/jgems/textures/";
        public static final String MODELS = "/assets/jgems/models/";
        public static final String SHADERS = "/assets/jgems/shaders/";
        public static final String SOUNDS = "/assets/jgems/sounds/";
        public static final String MAPS = "/assets/jgems/maps/";
        public static final String LANG = "/assets/jgems/lang/";
        public static final String ICONS = "/assets/jgems/icons/";
    }

    public enum GetSource {
        JAR,
        EXTERNAL
    }

    public static class IsolatedProcessLauncher {
        public static void EXEC(String[] args) {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");

            List<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-Dfile.encoding=UTF-8");
            command.add("-Xms512m");
            command.add("-Xmx2048m");
            command.add("-XX:+UseG1GC");
            command.add("-cp");
            command.add(classpath);
            command.add("javagems3d.JGems3D.IsolatedProcessLauncher");

            if (args != null) {
                command.addAll(Arrays.asList(args));
            }

            NuProcessBuilder pb = new NuProcessBuilder(command);
            pb.setProcessListener(new SimpleProcessHandler());
            pb.environment().putAll(System.getenv());
            pb.setCwd(Paths.get("."));
            pb.start();
        }

        public static void main(String[] args) {
            JGemsLaunchArgsRegistry.INSTANCE.read(args);
            JGems3D.launch(JGemsLaunchArgsRegistry.INSTANCE);
        }

        private static class SimpleProcessHandler extends NuAbstractProcessHandler {
            @Override
            public void onStdout(ByteBuffer buffer, boolean closed) {
                if (!closed && buffer.hasRemaining()) {
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    System.out.print(new String(bytes));
                }
            }

            @Override
            public void onStderr(ByteBuffer buffer, boolean closed) {
                if (!closed && buffer.hasRemaining()) {
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    System.err.print(new String(bytes));
                }
            }
        }
    }
}