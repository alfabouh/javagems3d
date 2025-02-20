/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

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

    void UIPanelActionRequest(@Nullable PanelUI panelUI);

    @NotNull Model2D getScreenModel();

    @NotNull IWorld getWorld();
    @NotNull IWindow getWindow();
}
