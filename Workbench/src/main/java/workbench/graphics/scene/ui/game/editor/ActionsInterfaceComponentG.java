package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewMapG;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewSkyBoxG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewModelG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTagG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTextureG;
import workbench.graphics.scene.ui.game.editor.scenes.scripting.ScenePreviewScriptG;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewMarkerObjectG;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewWorldObjectG;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import workbench.project.game.settings.GameProjectSettings;

public class ActionsInterfaceComponentG {
    private final ScenePreviewMapG scenePreviewMapG;
    private final ScenePreviewModelG scenePreviewModelG;
    private final ScenePreviewWorldObjectG<GameResourcePropObjectAsset> scenePreviewPropObjectG;
    private final ScenePreviewWorldObjectG<GameResourceEntityObjectAsset> scenePreviewEntityObjectG;
    private final ScenePreviewMarkerObjectG<GameResourceMarkerObjectAsset> scenePreviewMarkerObjectG;
    private final ScenePreviewTextureG scenePreviewTextureG;
    private final ScenePreviewTagG scenePreviewTagG;
    private final ScenePreviewSkyBoxG scenePreviewSkyBoxG;
    private final ScenePreviewScriptG scenePreviewScriptG;

    public ActionsInterfaceComponentG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.scenePreviewMapG = new ScenePreviewMapG(resourcesInterfaceComponentG);
        this.scenePreviewModelG = new ScenePreviewModelG(resourcesInterfaceComponentG);
        this.scenePreviewPropObjectG = new ScenePreviewWorldObjectG<>("Prop", () -> resourcesInterfaceComponentG.getPropResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewEntityObjectG = new ScenePreviewWorldObjectG<>("Entity", () -> resourcesInterfaceComponentG.getEntityResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewMarkerObjectG = new ScenePreviewMarkerObjectG<>("Marker", () -> resourcesInterfaceComponentG.getMarkerResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewTextureG = new ScenePreviewTextureG(resourcesInterfaceComponentG);
        this.scenePreviewTagG = new ScenePreviewTagG(resourcesInterfaceComponentG);
        this.scenePreviewSkyBoxG = new ScenePreviewSkyBoxG(resourcesInterfaceComponentG);
        this.scenePreviewScriptG = new ScenePreviewScriptG(resourcesInterfaceComponentG);
    }

    private void projSettings() {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
        if (ImGui.collapsingHeader("Editor Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            {
                ImGui.bullet();
                if (ImGui.button("Save")) {
                    WBench.get().getGameProjectManager().createOrSaveTempProjFile(WBench.get().getGameProjectManager().getGameProject());
                }
            }
            ImGui.popStyleColor();
            ImGui.indent();
            if (ImGui.collapsingHeader("Compile")) {
                ImGui.beginChild("##FogContent", ImGui.getColumnWidth(), 140, true, ImGuiWindowFlags.HorizontalScrollbar);
                {
                    ImGui.bulletText("Core .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath);
                    if (ImGui.button("Browse ##1")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath);
                    }
                    ImGui.separator();
                }
                {
                    ImGui.bulletText("Launcher .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath);
                    if (ImGui.button("Browse ##2")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath);
                    }
                    ImGui.separator();
                }
                {
                    ImGui.bulletText("Workbench .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath);
                    if (ImGui.button("Browse ##3")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath);
                    }
                }
                ImGui.endChild();
            }
            if (ImGui.collapsingHeader("Map Editing")) {
                ImGui.beginChild("##SkyContent", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                boolean autoSavePerSec = WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode == GameProjectSettings.MapProjectAutoSaveMode.TIMER;
                ImGui.bulletText("AutoSave Mode");
                if (ImGui.radioButton("On Timer", autoSavePerSec)) {
                    WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode = GameProjectSettings.MapProjectAutoSaveMode.TIMER;
                }
                if (ImGui.radioButton("On Steps", !autoSavePerSec)) {
                    WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode = GameProjectSettings.MapProjectAutoSaveMode.STEPS;
                }
                if (!autoSavePerSec) {
                    ImInt step = new ImInt(WBench.get().getGameProjectManager().gameProjectSettings.saveEachStep);
                    ImGui.setNextItemWidth(100);
                    ImGui.inputInt("Steps to save", step, 1, 100);
                    WBench.get().getGameProjectManager().gameProjectSettings.saveEachStep = step.get();
                } else {
                    ImInt sec = new ImInt((int) WBench.get().getGameProjectManager().gameProjectSettings.savePerSecond);
                    ImGui.setNextItemWidth(100);
                    ImGui.inputInt("Seconds to save", sec, 1, 360);
                    WBench.get().getGameProjectManager().gameProjectSettings.savePerSecond = sec.get();
                }
                ImGui.endChild();
            }

            ImGui.unindent();
        } else {
            ImGui.popStyleColor();
        }
    }

    public void actionsContent() {
        this.projSettings();
        this.scenePreviewMapG.render();
        this.scenePreviewScriptG.render();
        this.scenePreviewModelG.render();
        this.scenePreviewTagG.render();
        this.scenePreviewPropObjectG.render();
        this.scenePreviewEntityObjectG.render();
        this.scenePreviewMarkerObjectG.render();
        this.scenePreviewTextureG.render();
        this.scenePreviewSkyBoxG.render();
        ImGui.dummy(0.0f, 20.0f);
    }

    public ScenePreviewScriptG getScenePreviewScriptG() {
        return this.scenePreviewScriptG;
    }

    public ScenePreviewSkyBoxG getScenePreviewSkyBoxG() {
        return this.scenePreviewSkyBoxG;
    }

    public ScenePreviewTagG getScenePreviewTagG() {
        return this.scenePreviewTagG;
    }

    public ScenePreviewMapG getScenePreviewMapG() {
        return this.scenePreviewMapG;
    }

    public ScenePreviewModelG getScenePreviewModelG() {
        return this.scenePreviewModelG;
    }

    public ScenePreviewWorldObjectG<GameResourcePropObjectAsset> getScenePreviewPropObjectG() {
        return this.scenePreviewPropObjectG;
    }

    public ScenePreviewWorldObjectG<GameResourceEntityObjectAsset> getScenePreviewEntityObjectG() {
        return this.scenePreviewEntityObjectG;
    }

    public ScenePreviewTextureG getScenePreviewTextureG() {
        return this.scenePreviewTextureG;
    }
}
