package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.ticking.FrameTicking;

import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISceneRenderer extends IWindow.ResizeEvent {
    void onStartRender();
    void onRender(FrameTicking frameTicking);
    void onStopRender();

    @NotNull Model2D getScreenModel();

    @NotNull IWorld getWorld();
    @NotNull IWindow getWindow();
}
