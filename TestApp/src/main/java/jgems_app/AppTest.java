package jgems_app;

import api.application.JGemsApplication;
import api.application.events.IAppEventSubscriber;
import api.application.resources.IAppResources;
import api.application.workbench.manager.IAPIWBenchDataManager;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import api.scripting.JGemsAPIScriptingEngine;
import api.scripting.functions.APIScriptingFunction;
import api.system.JGemsAPI;
import api.system.JGemsAppEntry;
import api.system.JGemsAppInstance;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.items.TagRadioBoolean;
import javagems3d.mapping.tags.items.TagString;
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
    public static APIScriptingFunction scriptingFunction = JGemsAPIScriptingEngine.createJSFunction("testf", "Test fun");

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
        final Tag<TagString> tagMarkerId = new Tag<>(TagID.DEFAULT.MARKER_STRING_ID, new TagString("default"));

        //final JGemsPath sponza = new JGemsPath("assets/models/sponza/sponza.gltf");
        //final JGemsPath map04 = new JGemsPath("/assets/models/cube/cube.gltf");

        final JGemsPath cube = new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf");
        final JGemsPath trees = new JGemsPath("/assets/models/trees/trees.gltf");
        final JGemsPath flatgrass = new JGemsPath("/assets/models/flatgrass/flatgrass.gltf");
        final JGemsPath flatgrass_back = new JGemsPath("/assets/models/flatgrass/flatgrass_back.gltf");

        final JGemsPath Gate_2x4 = new JGemsPath("/assets/models/castle1/Gate_2x4.gltf");
        final JGemsPath Gate_2x4_Beveled = new JGemsPath("/assets/models/castle1/Gate_2x4_Beveled.gltf");
        final JGemsPath Gate_2x4_doorway = new JGemsPath("/assets/models/castle1/Gate_2x4_doorway.gltf");
        final JGemsPath Gate_Door = new JGemsPath("/assets/models/castle1/Gate_Door.gltf");
        final JGemsPath Roof_Cone = new JGemsPath("/assets/models/castle1/Roof_Cone.gltf");
        final JGemsPath Roof_Cube = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Roof_rectangle = new JGemsPath("/assets/models/castle1/Roof_rectangle.gltf");
        final JGemsPath Scaffle = new JGemsPath("/assets/models/castle1/Gate_2x4.gltf");
        final JGemsPath Scaffle_Ramp = new JGemsPath("/assets/models/castle1/Scaffle_Ramp.gltf");
        final JGemsPath Tower_Doorway = new JGemsPath("/assets/models/castle1/Tower_Doorway.gltf");
        final JGemsPath Tower_Mid = new JGemsPath("/assets/models/castle1/Tower_Mid.gltf");
        final JGemsPath Tower_mid_hollow = new JGemsPath("/assets/models/castle1/Tower_mid_hollow.gltf");
        final JGemsPath Tower_mid_hollow_ruined = new JGemsPath("/assets/models/castle1/Tower_mid_hollow_ruined.gltf");
        final JGemsPath Tower_top_1 = new JGemsPath("/assets/models/castle1/Tower_top_1.gltf");
        final JGemsPath Wall_2x2 = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Wall_2x2_walkway = new JGemsPath("/assets/models/castle1/Wall_2x2_walkway.gltf");
        final JGemsPath Wall_2x4 = new JGemsPath("/assets/models/castle1/Wall_2x4.gltf");
        final JGemsPath Wall_2x4_ruined = new JGemsPath("/assets/models/castle1/Wall_2x4_ruined.gltf");
        final JGemsPath Wall_2x4_walkway = new JGemsPath("/assets/models/castle1/Wall_2x4_walkway.gltf");
        final JGemsPath Wall_2x4_walkway_beveled = new JGemsPath("/assets/models/castle1/Wall_2x4_walkway_beveled.gltf");
        final JGemsPath Wall_corner_walkwayh = new JGemsPath("/assets/models/castle1/Wall_corner_walkwayh.gltf");
        final JGemsPath Window_bars = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Window_glass = new JGemsPath("/assets/models/castle1/Window_glass.gltf");

        manager.addResourceEntity("castle1", "Gate_2x4", Gate_2x4);
        manager.addResourceEntity("castle1", "Gate_2x4_Beveled", Gate_2x4_Beveled);
        manager.addResourceEntity("castle1", "Gate_2x4_doorway", Gate_2x4_doorway);
        manager.addResourceEntity("castle1", "Gate_Door", Gate_Door);
        manager.addResourceEntity("castle1", "Roof_Cone", Roof_Cone);
        manager.addResourceEntity("castle1", "Roof_Cube", Roof_Cube);
        manager.addResourceEntity("castle1", "Roof_rectangle", Roof_rectangle);
        manager.addResourceEntity("castle1", "Scaffle", Scaffle);
        manager.addResourceEntity("castle1", "Scaffle_Ramp", Scaffle_Ramp);
        manager.addResourceEntity("castle1", "Tower_Doorway", Tower_Doorway);
        manager.addResourceEntity("castle1", "Tower_Mid", Tower_Mid);
        manager.addResourceEntity("castle1", "Tower_mid_hollow", Tower_mid_hollow);
        manager.addResourceEntity("castle1", "Tower_mid_hollow_ruined", Tower_mid_hollow_ruined);
        manager.addResourceEntity("castle1", "Tower_top_1", Tower_top_1);
        manager.addResourceEntity("castle1", "Wall_2x2", Wall_2x2);
        manager.addResourceEntity("castle1", "Wall_2x2_walkway", Wall_2x2_walkway);
        manager.addResourceEntity("castle1", "Wall_2x4", Wall_2x4);
        manager.addResourceEntity("castle1", "Wall_2x4_ruined", Wall_2x4_ruined);
        manager.addResourceEntity("castle1", "Wall_2x4_walkway", Wall_2x4_walkway);
        manager.addResourceEntity("castle1", "Wall_2x4_walkway_beveled", Wall_2x4_walkway_beveled);
        manager.addResourceEntity("castle1", "Wall_corner_walkwayh", Wall_corner_walkwayh);
        manager.addResourceEntity("castle1", "Window_bars", Window_bars);
        manager.addResourceEntity("castle1", "Window_glass", Window_glass);

        manager.addResourceProp("castle1", "Gate_2x4", Gate_2x4);
        manager.addResourceProp("castle1", "Gate_2x4_Beveled", Gate_2x4_Beveled);
        manager.addResourceProp("castle1", "Gate_2x4_doorway", Gate_2x4_doorway);
        manager.addResourceProp("castle1", "Gate_Door", Gate_Door);
        manager.addResourceProp("castle1", "Roof_Cone", Roof_Cone);
        manager.addResourceProp("castle1", "Roof_Cube", Roof_Cube);
        manager.addResourceProp("castle1", "Roof_rectangle", Roof_rectangle);
        manager.addResourceProp("castle1", "Scaffle", Scaffle);
        manager.addResourceProp("castle1", "Scaffle_Ramp", Scaffle_Ramp);
        manager.addResourceProp("castle1", "Tower_Doorway", Tower_Doorway);
        manager.addResourceProp("castle1", "Tower_Mid", Tower_Mid);
        manager.addResourceProp("castle1", "Tower_mid_hollow", Tower_mid_hollow);
        manager.addResourceProp("castle1", "Tower_mid_hollow_ruined", Tower_mid_hollow_ruined);
        manager.addResourceProp("castle1", "Tower_top_1", Tower_top_1);
        manager.addResourceProp("castle1", "Wall_2x2", Wall_2x2);
        manager.addResourceProp("castle1", "Wall_2x2_walkway", Wall_2x2_walkway);
        manager.addResourceProp("castle1", "Wall_2x4", Wall_2x4);
        manager.addResourceProp("castle1", "Wall_2x4_ruined", Wall_2x4_ruined);
        manager.addResourceProp("castle1", "Wall_2x4_walkway", Wall_2x4_walkway);
        manager.addResourceProp("castle1", "Wall_2x4_walkway_beveled", Wall_2x4_walkway_beveled);
        manager.addResourceProp("castle1", "Wall_corner_walkwayh", Wall_corner_walkwayh);
        manager.addResourceProp("castle1", "Window_bars", Window_bars);
        manager.addResourceProp("castle1", "Window_glass", Window_glass);

        //WBenchRenderProperties.getDefault().setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, false)
        //JGemsRenderProperties.getDefault().setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, false)

        manager.addResourceEntity("terrain", "flatgrass", () -> new WBenchObjectData(flatgrass), () -> new JGemsEntityData(flatgrass));
        manager.addResourceEntity("testPhys", "cube", () -> new WBenchObjectData(cube).addTag(tagPhysics), () -> new JGemsEntityData(cube));

        manager.addResourceProp("terrain", "flatgrass_back", () -> new WBenchObjectData(flatgrass_back), () -> new JGemsPropData(flatgrass_back));
        manager.addResourceProp("trees", () -> new WBenchObjectData(trees), () -> new JGemsPropData(trees));
        manager.addResourceProp("testProp", "cube", () -> new WBenchObjectData(cube), () -> new JGemsPropData(cube));

        manager.addResourceMarker("player", "spawn", () -> new WBenchMarkerData(DefaultMarker.CURSOR_CONE, new Vector3f(0.0f, 3.0f, 0.0f), false).addTag(tagMarkerId));

        manager.addResourceSkyCubeMap("SkyDay1", "png", new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay"));
    }
}