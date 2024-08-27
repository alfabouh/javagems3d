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

package jgems_api.horror.manager;

import jgems_api.horror.HorrorGame;
import jgems_api.horror.entities.HorrorSimplePlayer;
import jgems_api.horror.items.ItemCross;
import jgems_api.horror.items.ItemZippoModded;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.collectabes.EntityCollectableItem;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.map.loaders.tbox.placers.TBoxMapDefaultObjectsPlacer;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.collections.Pair;
import ru.jgems3d.engine_api.app.tbox.containers.TUserData;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import jgems_api.horror.gui.HorrorMainMenuPanel;
import jgems_api.horror.manager.bindings.HorrorBindings;

public class HorrorAppManager extends AppManager {
    public HorrorAppManager(@Nullable AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    @Override
    public @NotNull BindingManager createBindingManager() {
        return new HorrorBindings();
    }

    @Override
    public @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader) {
        return (w, p, r) -> new Pair<>(new HorrorSimplePlayer(w, p, r), HorrorGame.get().horrorRenderDataLoader.player);
    }

    public @NotNull PanelUI gameMainMenuPanel() {
        return new HorrorMainMenuPanel(null);
    }
}