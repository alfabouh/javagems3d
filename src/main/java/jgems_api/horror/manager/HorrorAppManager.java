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

import jgems_api.horror.entities.HorrorSimplePlayer;
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
import ru.jgems3d.engine.system.inventory.items.ItemZippo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.map.loaders.tbox.placers.TBoxMapDefaultObjectsPlacer;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
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
        return (HorrorSimplePlayer::new);
    }

    public @NotNull PanelUI gameMainMenuPanel() {
        return new HorrorMainMenuPanel(null);
    }

    @Override
    public void placeTBoxEntityOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
        TBoxMapDefaultObjectsPlacer.placeObjectOnMap(sceneWorld, physicsWorld, globalGameResources, localGameResources, id, attributesContainer, userData);
    }

    @Override
    public void placeTBoxTriggerZoneOnMap(PhysicsWorld physicsWorld, Vector3f position, Vector3f size, String id, AttributesContainer attributesContainer, TUserData userData) {
        TBoxMapDefaultObjectsPlacer.placeTBoxTriggerZoneOnMap(physicsWorld, position, size, id, attributesContainer, userData);
    }

    @Override
    public void handleMarkerOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, AttributesContainer attributesContainer, TUserData userData) {
        String name = attributesContainer.tryGetValueFromAttributeByID(AttributeID.NAME, String.class);
        Vector3f pos = attributesContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
        if (name.equals("zippo_item")) {
            JGemsHelper.WORLD.addItemInWorld(new EntityCollectableItem(physicsWorld, new ItemZippoModded(), new Vector3f(pos).add(0.0f, 0.5f, 0.0f), name), JGemsResourceManager.globalRenderDataAssets.zippo_world);
        }
    }
}
