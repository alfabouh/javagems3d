package javagems3d.graphics.screen;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScreen {
    void createObjects(IWindow window);
    void createScreenAndContext();
    void runRenderThread();

    IWindow getWindow();

    void zeroRenderTick();
    float getRenderTicks();

    default void setIcon(@Nullable JGemsPath icon) {
        this.getWindow().setIcon(icon);
    }

    default void setTitle(@NotNull String title) {
        this.getWindow().setTitle(title);
    }
}
