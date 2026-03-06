package jgems_app;

import api.application.JGemsApplication;
import api.application.events.IAppEventSubscriber;
import api.application.resources.IAppResources;
import api.application.workbench.manager.IAPIWBenchDataManager;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import api.scripting.legacy.JGemsAPIScriptingEngine;
import api.scripting.legacy.functions.APIScriptingFunction;
import api.system.JGemsAPI;
import api.system.JGemsAppEntry;
import api.system.JGemsAppInstance;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.Window;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.binding.DefaultBindings;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import jgems_app.events.TestEvents;
import jgems_app.gui.TestMainMenuPanel;
import jgems_app.resources.ModelInitializer;

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
        JGemsAPI.getAPIScriptingCore().test();
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
        //final JGemsPath sponza = new JGemsPath("assets/models/sponza/sponza.gltf");
        //final JGemsPath map04 = new JGemsPath("/assets/models/cube/cube.gltf");

        final JGemsPathSource trees =                    new JGemsPathSource(new JGemsPath("/assets/models/trees/trees.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource flatgrass =                new JGemsPathSource(new JGemsPath("/assets/models/flatgrass/flatgrass.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource flatgrass_back =           new JGemsPathSource(new JGemsPath("/assets/models/flatgrass/flatgrass_back.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Gate_2x4 =                 new JGemsPathSource(new JGemsPath("/assets/models/castle1/Gate_2x4.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Gate_2x4_Beveled =         new JGemsPathSource(new JGemsPath("/assets/models/castle1/Gate_2x4_Beveled.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Gate_2x4_doorway =         new JGemsPathSource(new JGemsPath("/assets/models/castle1/Gate_2x4_doorway.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Gate_Door =                new JGemsPathSource(new JGemsPath("/assets/models/castle1/Gate_Door.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Roof_Cone =                new JGemsPathSource(new JGemsPath("/assets/models/castle1/Roof_Cone.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Roof_Cube =                new JGemsPathSource(new JGemsPath("/assets/models/castle1/Roof_Cube.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Roof_rectangle =           new JGemsPathSource(new JGemsPath("/assets/models/castle1/Roof_rectangle.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Scaffle =                  new JGemsPathSource(new JGemsPath("/assets/models/castle1/Gate_2x4.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Scaffle_Ramp =             new JGemsPathSource(new JGemsPath("/assets/models/castle1/Scaffle_Ramp.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Tower_Doorway =            new JGemsPathSource(new JGemsPath("/assets/models/castle1/Tower_Doorway.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Tower_Mid =                new JGemsPathSource(new JGemsPath("/assets/models/castle1/Tower_Mid.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Tower_mid_hollow =         new JGemsPathSource(new JGemsPath("/assets/models/castle1/Tower_mid_hollow.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Tower_mid_hollow_ruined =  new JGemsPathSource(new JGemsPath("/assets/models/castle1/Tower_mid_hollow_ruined.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Tower_top_1 =              new JGemsPathSource(new JGemsPath("/assets/models/castle1/Tower_top_1.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x2 =                 new JGemsPathSource(new JGemsPath("/assets/models/castle1/Roof_Cube.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x2_walkway =         new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_2x2_walkway.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x4 =                 new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_2x4.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x4_ruined =          new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_2x4_ruined.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x4_walkway =         new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_2x4_walkway.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_2x4_walkway_beveled = new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_2x4_walkway_beveled.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Wall_corner_walkwayh =     new JGemsPathSource(new JGemsPath("/assets/models/castle1/Wall_corner_walkwayh.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Window_bars =              new JGemsPathSource(new JGemsPath("/assets/models/castle1/Roof_Cube.gltf"), ISource.Source.INSIDE_JAR);
        final JGemsPathSource Window_glass =             new JGemsPathSource(new JGemsPath("/assets/models/castle1/Window_glass.gltf"), ISource.Source.INSIDE_JAR);

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
        manager.addResourceProp("terrain", "flatgrass_back", () -> new WBenchObjectData(flatgrass_back), () -> new JGemsPropData(flatgrass_back));
        manager.addResourceProp("trees", () -> new WBenchObjectData(trees), () -> new JGemsPropData(trees));

        manager.SET_DEFAULTS();
    }
}