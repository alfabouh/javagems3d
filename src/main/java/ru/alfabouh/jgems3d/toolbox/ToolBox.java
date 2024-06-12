package ru.alfabouh.jgems3d.toolbox;
import ru.alfabouh.jgems3d.engine.system.EngineSystem;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.render.screen.TBoxScreen;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;

public class ToolBox {
    private static ToolBox toolBox;
    private final TBoxScreen tBoxScreen;
    private boolean shouldBeClosed;

    private ToolBox() {
        SystemLogging.get().setCurrentLogging(SystemLogging.toolBoxLogging);

        this.tBoxScreen = new TBoxScreen();
        this.shouldBeClosed = false;
    }

    public static void main(String[] args) {
        ToolBox.toolBox = new ToolBox();
        ToolBox.get().startSystem();
    }

    public void closeTBox() {
        this.shouldBeClosed = true;
    }

    public void startSystem() {
        SystemLogging.get().getLogManager().log("Starting system!");

        ToolBox.get().getScreen().buildScreen();
        ToolBox.get().getScreen().startScreenRenderProcess();
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

    public String toString() {
        return EngineSystem.ENG_NAME + " - [ToolBox v" + EngineSystem.ENG_VER + "]";
    }
}
