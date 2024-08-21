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

import jgems_api.test.TestTBoxApp;
import jgems_api.test.entities.TestPlayer;
import jgems_api.test.gui.TestMainMenuPanel;
import jgems_api.test.manager.bindings.TestBindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.BtDynamicMeshBody;
import ru.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.map.loaders.tbox.TBoxMapDefaultObjectsPlacer;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

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
        return (TestPlayer::new);
    }

    public @NotNull PanelUI gameMainMenuPanel() {
        return new TestMainMenuPanel(null);
    }

    @Override
    public void placeObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources globalGameResources, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributesContainer attributesContainer, TRenderContainer renderContainer) {
        TBoxMapDefaultObjectsPlacer.placeObjectOnMap(sceneWorld, physicsWorld, globalGameResources, localGameResources, id, objectCategory, attributesContainer, renderContainer);
    }
}