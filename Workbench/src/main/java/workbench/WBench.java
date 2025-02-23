package workbench;

import javagems3d.JGems3D;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.os.OS;
import javagems3d.system.os.SysOSValidation;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.Log;
import logger.SystemLogging;
import logger.managers.JGemsLogging;
import org.lwjgl.glfw.GLFW;
import workbench.controller.WBenchControllerDispatcher;
import workbench.graphics.screen.WBenchScreen;
import workbench.resources.WBenchResourceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public final class WBench {
    private final OS os;
    public static final String ID = "wbench";
    public static final String VERSION = "0.20a";

    public static long rngSeed;
    public static Random random;
    private static WBench wBench;
    private boolean shouldBeClosed;

    private final WBenchScreen wBenchScreen;
    private final WBenchResourceManager resourceManager;

    private WBench() {
        try {
            SystemLogging.get().setCurrentLogging(new JGemsLogging("WorkbenchLogger"));
            WBench.checkFilesDirectory();
        } catch (IOException e) {
            throw new JGemsRuntimeException(e);
        }
        this.os = SysOSValidation.getCurrentOS();

        WBench.rngSeed = WBench.systemTime();
        WBench.random = new Random(WBench.rngSeed);

        this.resourceManager = new WBenchResourceManager();
        this.wBenchScreen = new WBenchScreen();
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
            WBench.get().getResourceManager().destroy();
            Log.get().info("Cleared resources!");
            Log.get().debug("END");
        }
    }

    public void close() {
        this.shouldBeClosed = true;
    }

    public WBenchControllerDispatcher getControllerDispatcher() {
        return this.getScreen().getControllerDispatcher();
    }

    public WBenchResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public WBenchScreen getScreen() {
        return this.wBenchScreen;
    }

    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public String toString() {
        return "Workbench v" + WBench.VERSION;
    }
}
