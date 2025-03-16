package workbench;

import api.system.JGemsAPI;
import api.system.JGemsAPIEditorResources;
import com.google.gson.JsonSyntaxException;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.dear_imgui.IDearUIImp;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.exceptions.JGemsAPIException;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.os.OS;
import javagems3d.system.service.os.SysOSValidation;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.SystemLogging;
import logger.managers.JGemsLogging;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import workbench.controller.WBenchControllerDispatcher;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.ProjectManager;
import workbench.project.ProjectObjects;
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
    public static final float MAP_SIZE = 128.0f;

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
    private final ProjectManager projectManager;

    private static JGemsAPIEditorResources apiEditorResources;

    private WBench() {
        try {
            SystemLogging.get().setCurrentLogging(new JGemsLogging("WorkbenchLogger"));
            JGemsAPI.INIT_JGEMS();
            WBench.apiEditorResources = JGemsAPI.get().launchAPIAndGetOnlyEditorData();
            WBench.checkFilesDirectory();
        } catch (IOException | JGemsAPIException e) {
            throw new JGemsRuntimeException(e);
        }
        this.os = SysOSValidation.getCurrentOS();

        WBench.rngSeed = WBench.systemTime();
        WBench.random = new Random(WBench.rngSeed);

        this.settings = new WBenchSettings();

        try {
            this.settings = WBenchSettings.load(new JGemsPath(WBench.getFilesFolder()));
        } catch (JGemsIOException | IllegalStateException | JsonSyntaxException e) {
            Log.get().exception(e);
        }

        this.resourceManager = new WBenchResourceManager();
        this.screen = new WBenchScreen();
        this.projectManager = new ProjectManager();

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
    public static void launch() {
        if (WBench.wBench != null) {
            throw new JGemsRuntimeException("Couldn't launch ToolBox more than 1 times");
        }
        WBench.wBench = new WBench();
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
            Log.get().debug("BEGIN");
            Log.get().info("Starting system! Date: " + JGems3D.date());
            Log.get().info(WBench.get().toString());
            Log.get().info("===============================================================");
            JGemsCore.printSystemInfo();

            WBench.get().getResourceManager().initGlobalResources();

            WBench.get().getScreen().createScreenAndContext();
            WBench.get().getScreen().createObjects(WBench.get().getScreen().getWindow());
            WBench.get().getScreen().runRenderThread();

        } catch (Exception e) {
            Log.get().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        } finally {
            WBench.get().getProjectManager().closeProject(true);
            WBench.get().getResourceManager().destroy();
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

    public ProjectObjects getProjectObjects() {
        return this.getProjectManager().getProjectObjects();
    }

    public ProjectManager getProjectManager() {
        return this.projectManager;
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
