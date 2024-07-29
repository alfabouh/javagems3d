package ru.jgems3d.engine_api.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.default_panels.DefaultMainMenuPanel;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

public abstract class AppManager {
    private final AppConfiguration appConfiguration;

    public AppManager(@Nullable AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration == null ? AppConfiguration.createDefaultAppConfiguration() : appConfiguration;
    }

    public abstract void putIncomingObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributeContainer attributeContainer, TRenderContainer renderContainer);
    public abstract @NotNull IPlayerConstructor createPlayer(String mapName);

    public AppConfiguration getAppConfiguration() {
        return this.appConfiguration;
    }

    public @NotNull PanelUI openMainMenu() {
        return new DefaultMainMenuPanel(null);
    }
}
