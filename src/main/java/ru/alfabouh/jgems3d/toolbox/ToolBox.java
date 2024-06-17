package ru.alfabouh.jgems3d.toolbox;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.system.EngineSystem;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.logger.managers.JGemsLogging;
import ru.alfabouh.jgems3d.toolbox.render.screen.TBoxScreen;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;
import ru.alfabouh.jgems3d.toolbox.settings.TBoxSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ToolBox {
    private static ToolBox toolBox;
    private final TBoxScreen tBoxScreen;
    private boolean shouldBeClosed;
    private final TBoxSettings tBoxSettings;

    private ToolBox() throws IOException {
        SystemLogging.get().setCurrentLogging(SystemLogging.toolBoxLogging);
        this.tBoxScreen = new TBoxScreen();
        JGems.checkFilesDirectory();

        this.tBoxSettings = new TBoxSettings(new File(ToolBox.getTBoxFilesFolder().toFile(), "settings.txt"));
        this.shouldBeClosed = false;
    }

    public static void main(String[] args) throws IOException {
        ToolBox.toolBox = new ToolBox();
        ToolBox.get().startSystem();
    }

    public void closeTBox() {
        this.shouldBeClosed = true;
    }

    public void startSystem() {
        SystemLogging.get().getLogManager().log("Starting system!");
        try {
            ToolBox.get().getTBoxSettings().loadOptions();
            ToolBox.get().getScreen().buildScreen();
            ToolBox.get().getScreen().startScreenRenderProcess();
        } catch (Exception e) {
            SystemLogging.get().getLogManager().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder to find out the details.");
        } finally {
            ToolBox.get().getTBoxSettings().saveOptions();
        }
    }

    public TBoxSettings getTBoxSettings() {
        return this.tBoxSettings;
    }

    public ResourceManager getResourceManager() {
        return this.getScreen().getResourceManager();
    }

    public TBoxScreen getScreen() {
        return this.tBoxScreen;
    }

    public static ToolBox get() {
        return ToolBox.toolBox;
    }

    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public static Path getTBoxFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase() + "//tool_box";
        return Paths.get(appdataPath, folderPath);
    }


    public String toString() {
        return EngineSystem.ENG_NAME + " - [ToolBox v" + EngineSystem.ENG_VER + "]";
    }
}