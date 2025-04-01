package jgems_app;

import api.application.JGemsApplication;
import api.application.events.IAppEventSubscriber;
import api.application.resources.IAppResources;
import api.application.workbench.manager.IAPIWBenchDataManager;
import api.application.workbench.resources.data.JGemsEntityData;
import api.application.workbench.resources.data.WBenchObjectData;
import api.system.JGemsAppEntry;
import api.system.JGemsAppInstance;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.items.TagRadioBoolean;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.binding.DefaultBindings;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import jgems_app.events.TestEvents;
import jgems_app.gui.TestMainMenuPanel;
import jgems_app.resources.ModelInitializer;
import org.joml.Vector3f;

@JGemsAppEntry(id = "DefaultGame")
public class AppTest extends JGemsApplication {
    @JGemsAppInstance
    public static AppTest appTest;

    public AppTest() {
        JGems3D.DEBUG_MODE = true;
    }

    @Override
    public void initEvents(@NotNull IAppEventSubscriber appEventSubscriber) {
        appEventSubscriber.addClassWithEvents(TestEvents.class);
    }

    @Override
    public void initResources(@NotNull IAppResources appResources) {
        appResources.putGlobalAssetsInitializer(new ModelInitializer());
    }

    @Override
    public @NotNull BindingManager getBindingManager() {
        return new DefaultBindings();
    }

    @Override
    public @NotNull PanelUI getMainMenuPanel() {
        return new TestMainMenuPanel(null);
    }

    @Override
    public @NotNull Window.WindowProperties getWindowProperties() {
        return new Window.WindowProperties("DefaultGame", new JGemsPath(Window.DEFAULT_ICON));
    }

    @Override
    public void setupEditorResources(IAPIWBenchDataManager manager) {
        final Tag<TagRadioBoolean> tagPhysics = new Tag<>(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Is Static", true), new TagRadioBoolean.Info("Is Dynamic", false)));

        final JGemsPath sponza = new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "sponza/sponza.gltf");
        final JGemsPath cube = new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf");

        manager.addResourceEntity("sponza",
                () -> new WBenchObjectData(sponza),
                () -> new JGemsEntityData(false)
        );
        manager.addResourceEntity("test", "cube",
                () -> new WBenchObjectData(cube).addTag(tagPhysics),
                () -> new JGemsEntityData(false)
        );

        manager.addResourceSkyCubeMap("SkyDay1", "png", new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay"));
    }
}