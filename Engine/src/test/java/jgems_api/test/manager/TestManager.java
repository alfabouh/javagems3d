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

package jgems_api.test.manager;

import javagems3d.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import javagems3d.physics.entities.player.AdvancedKinematicPlayer;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.core.player.IPlayerConstructor;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import api.app.configuration.AppConfiguration;
import api.app.manager.AppManager;
import jgems_api.test.entities.TestPlayer;
import jgems_api.test.gui.TestMainMenuPanel;
import jgems_api.test.manager.bindings.TestBindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestManager extends AppManager {
    public TestManager(@Nullable AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    @Override
    public @NotNull BindingManager createBindingManager() {
        return new TestBindings();
    }

    @Override
    public @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader) {
        return (w, p, r) -> new Pair<>(new AdvancedKinematicPlayer(w, p, r), JGemsResourceManager.globalRenderDataAssets.player);
    }

    public @NotNull PanelUI gameMainMenuPanel() {
        return new TestMainMenuPanel(null);
    }
}
