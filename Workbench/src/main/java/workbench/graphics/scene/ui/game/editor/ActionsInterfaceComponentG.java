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
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewSoundG;
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
    private final ScenePreviewSoundG scenePreviewSoundG;
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
        this.scenePreviewSoundG = new ScenePreviewSoundG(resourcesInterfaceComponentG);
        this.scenePreviewTagG = new ScenePreviewTagG(resourcesInterfaceComponentG);
        this.scenePreviewSkyBoxG = new ScenePreviewSkyBoxG(resourcesInterfaceComponentG);
        this.scenePreviewScriptG = new ScenePreviewScriptG(resourcesInterfaceComponentG);
    }

    private void projSettings() {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
        if (ImGui.collapsingHeader("Editor Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##editor_settings_window", ImGui.getColumnWidth(), 320, true);
            boolean save = false;
            ImGui.popStyleColor();
            //ImGui.indent();
            if (ImGui.treeNodeEx("Compilation", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##CompileEditCont", ImGui.getColumnWidth(), 200, true);
                {
                    ImGui.textWrapped("Core .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath);
                    if (ImGui.button("Browse ##1")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath);
                        save = true;
                    }
                    ImGui.separator();
                }
                {
                    ImGui.textWrapped("Launcher .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath);
                    if (ImGui.button("Browse ##2")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath);
                        save = true;
                    }
                    ImGui.separator();
                }
                {
                    ImGui.textWrapped("Workbench .jar: " + WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath);
                    if (ImGui.button("Browse ##3")) {
                        WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath = JGemsHelper.files().openFolderViewChooser(WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath);
                        save = true;
                    }
                }
                ImGui.endChild();
                ImGui.treePop();
            }
            if (ImGui.treeNodeEx("Map Edit Autosave", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##MapEditCont", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                boolean autoSavePerSec = WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode == GameProjectSettings.MapProjectAutoSaveMode.TIMER;
                if (ImGui.radioButton("On Timer", autoSavePerSec)) {
                    WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode = GameProjectSettings.MapProjectAutoSaveMode.TIMER;
                    save = true;
                }
                if (ImGui.radioButton("On Steps", !autoSavePerSec)) {
                    WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode = GameProjectSettings.MapProjectAutoSaveMode.STEPS;
                    save = true;
                }
                if (!autoSavePerSec) {
                    ImInt step = new ImInt(WBench.get().getGameProjectManager().gameProjectSettings.saveEachStep);
                    ImGui.setNextItemWidth(100);
                    if (ImGui.inputInt("Steps to save", step, 1, 100)) {
                        save = true;
                    }
                    WBench.get().getGameProjectManager().gameProjectSettings.saveEachStep = step.get();
                } else {
                    ImInt sec = new ImInt((int) WBench.get().getGameProjectManager().gameProjectSettings.savePerSecond);
                    ImGui.setNextItemWidth(100);
                    if (ImGui.inputInt("Seconds to save", sec, 1, 360)) {
                        save = true;
                    }
                    WBench.get().getGameProjectManager().gameProjectSettings.savePerSecond = sec.get();
                }
                ImGui.endChild();
                ImGui.treePop();
            }

            if (save) {
                WBench.get().getGameProjectManager().createOrSaveTempProjFile(WBench.get().getGameProjectManager().getGameProject());
            }

            //ImGui.unindent();
            ImGui.endChild();
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
        this.scenePreviewSoundG.render();
        ImGui.dummy(0.0f, 20.0f);
    }

    public ScenePreviewSoundG getScenePreviewSoundG() {
        return this.scenePreviewSoundG;
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
