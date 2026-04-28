package workbench;

import api.system.JGemsAPI;
import api.system.JGemsAPIEditorResources;
import com.google.gson.JsonSyntaxException;
import javagems3d.JGems3D;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.help.JGemsHelper;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.os.OS;
import javagems3d.system.service.os.SysOSValidation;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.SystemLogging;
import logger.managers.JGemsLogging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import workbench.controller.WBenchControllerDispatcher;
import workbench.controller.binding.WBenchBindingManager;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.game.WBenchGameProjectManager;
import workbench.project.map.WBenchMapProjectManager;
import workbench.resources.WBenchResourceManager;
import workbench.resources.frame.LoadingInterfaceSwing;
import workbench.settings.WBenchSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public final class WBench {
    public static final float MAP_SIZE = JGems3D.MAP_MAX_SIZE;

    private final OS os;
    public static final String ID = "wbench";
    public static final String VERSION = JGemsCore.ENG_VER;

    public static long rngSeed;
    public static Random random;
    private static WBench wBench;
    private boolean shouldBeClosed;

    private final WBenchScreen screen;
    private final WBenchResourceManager resourceManager;
    private WBenchSettings settings;
    private final WBenchGameProjectManager wBenchGameProject;
    private final JGemsSoundManager jGemsSoundManager;

    private static JGemsAPIEditorResources apiEditorResources;

    private WBench(@Nullable String apiAppClasspath, @NotNull JGemsLaunchArgsRegistry launchArgs) {
        try {
            SystemLogging.get().setCurrentLogging(new JGemsLogging("WorkbenchLogger"));
            JGemsAPI.INIT_JGEMS();
            WBench.apiEditorResources = JGemsAPI.get().launchAPIEditorData(apiAppClasspath, launchArgs);
            WBench.checkFilesDirectory();
        } catch (IOException | JGemsAPIException e) {
            throw new JGemsRuntimeException(e);
        }
        this.os = SysOSValidation.getCurrentOS();

        WBench.rngSeed = WBench.systemTime();
        WBench.random = new Random(WBench.rngSeed);

        try {
            this.settings = WBenchSettings.load(new JGemsPath(WBench.getFilesFolder()));
        } catch (JGemsIOException | IllegalStateException | JsonSyntaxException e) {
            Log.get().exception(e);
        }

        this.jGemsSoundManager = new JGemsSoundManager();
        this.resourceManager = new WBenchResourceManager();
        this.screen = new WBenchScreen();
        this.wBenchGameProject = new WBenchGameProjectManager();

        this.shouldBeClosed = false;
    }

    public OS getOS() {
        return this.os;
    }

    public static JGemsAPIEditorResources APIEditorResources() {
        return WBench.apiEditorResources;
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    @SuppressWarnings("all")
    public static void launch(@NotNull JGemsLaunchArgsRegistry argsRegistry) {
        if (WBench.wBench != null) {
            throw new JGemsRuntimeException("Couldn't launch ToolBox more than 1 times");
        }
        if (argsRegistry.getValue(JGemsLaunchArgsRegistry.DEFAULT_ARGS.DEBUG) == Boolean.TRUE) {
            JGems3D.DEBUG_MODE = true;
        }
        WBench.wBench = new WBench(argsRegistry.getValue(JGemsLaunchArgsRegistry.DEFAULT_ARGS.API_APP_CLASSPATH), argsRegistry);
        WBench.get().start();
    }

    public static WBench get() {
        return WBench.wBench;
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(WBench.getFilesFolder())) {
            WBench.getFilesFolder().toFile().mkdirs();
            Log.get().debug("Created system folder");
        }
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase() + File.separator + WBench.ID;
        return Paths.get(appdataPath, folderPath);
    }

    private static void start() {
        try {
            JGemsLaunchArgsRegistry.INSTANCE.printArgs();
            Log.get().debug("BEGIN");
            Log.get().info("Starting system! Date: " + JGems3D.date());
            Log.get().info(WBench.get().toString());
            Log.get().separator();
            JGemsCore.printSystemInfo();
            WBench.get().getSoundManager().createSystem();
            WBench.get().getResourceManager().initGlobalResources();
            JGemsHelper.initResourceManager(WBench.get().getResourceManager());
            WBench.get().getScreen().createScreenAndContext();
            WBench.get().getScreen().createObjects(WBench.get().getScreen().getWindow());
            WBench.get().getScreen().runRenderThread();
        } catch (Exception e) {
            Log.get().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.", e);
        } finally {
            try {
                WBenchSettings.save(WBench.get().getSettings(), new JGemsPath(WBench.getFilesFolder()));
            } catch (JGemsIOException e) {
                Log.get().exception(e);
            }
            JGemsLaunchArgsRegistry.clear();
            WBench.get().getMapProjectManager().closeMapProject(false);
            WBench.get().getResourceManager().destroy();
            WBench.get().getSoundManager().destroy();
            LoadingInterfaceSwing.dispose();
            JGemsAPI.get().close();
            Log.get().info("Cleared resources");
            Log.get().debug("END");
        }
    }

    public void openInterface(@NotNull DearUIInterface dearUIInterface) {
        ((IDearUIImp) this.getScreen().getScene().getSceneRenderer()).openUIInterface(dearUIInterface);
    }

    public void close() {
        Log.get().warn("Exit...");
        this.shouldBeClosed = true;
    }

    public WBenchSettings getSettings() {
        return this.settings;
    }

    public JGemsSoundManager getSoundManager() {
        return this.jGemsSoundManager;
    }

    public @NotNull WBenchMapProjectManager getMapProjectManager() {
        return this.getGameProjectManager().getMapProjectManager();
    }
    
    public @NotNull WBenchGameProjectManager getGameProjectManager() {
        return this.wBenchGameProject;
    }

    public WBenchBindingManager getBindingManager() {
        return (WBenchBindingManager) this.getControllerDispatcher().getCurrentController().getBindingManager();
    }

    public WBenchControllerDispatcher getControllerDispatcher() {
        return this.getScreen().getControllerDispatcher();
    }

    public WBenchResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public WBenchScreen getScreen() {
        return this.screen;
    }

    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public String toString() {
        return "Workbench v" + WBench.VERSION;
    }
}
