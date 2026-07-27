/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.ui.map.editor.scenes.resources;

import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.def.misc.GameResourceScriptAsset;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.game.editor.instances.scripting.ScriptAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.CreatableResourcesTreeDrawerG;
import workbench.graphics.scene.ui.game.editor.utils.ScriptEditorDrawerG;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.SelectedScene;
import workbench.graphics.scene.ui.map.editor.utils.CreatableObjectsTreeDrawerM;

import java.util.ArrayList;
import java.util.Objects;

public class InterfaceResourcesObjectsM {
    private final MapEditorInterface mapEditorInterface;
    private final CreatableObjectsTreeDrawerM<WBenchObjectTemplate> props;
    private final CreatableObjectsTreeDrawerM<WBenchObjectTemplate> entities;
    private final CreatableObjectsTreeDrawerM<WBenchMarkerTemplate> markers;
    private final CreatableResourcesTreeDrawerG<GameResourceScriptAsset, ScriptAssetPreview> scriptResourceTreeDrawer;
    private final ScriptEditorDrawerG scriptEditorDrawerG;
    private final ImBoolean isPreviewOpen;

    public InterfaceResourcesObjectsM(@NotNull MapEditorInterface mapEditorInterface) {
        this.isPreviewOpen = new ImBoolean(true);
        this.mapEditorInterface = mapEditorInterface;
        this.props = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), "Props");
        this.entities = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), "Entities");
        this.markers = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getMarkers(), "Markers");
        this.scriptEditorDrawerG = new ScriptEditorDrawerG(JGemsAPI.getAPIScriptingCore().getLocalMapContext().getApiCodeEnvironmentController(), false);

        this.scriptResourceTreeDrawer = new CreatableResourcesTreeDrawerG<>(
                "Map Scripting",
                () -> Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).getScriptFiles(),
                new ArrayList<>() {{
                    add(new CreatableResourcesTreeDrawerG.PopupConstructorData("File's Name", "^(?!\\.)[a-zA-Z\\d_-]+$", "Only letters, digits, underscores (_) and dashes (-) allowed. Spaces and special symbols are not allowed, name cannot start with a dot."));
                }},
                (e) -> e.first().getFoldersThereMap().containsKey(e.second().getInputStrings().getFirst().get()),
                (e) -> {
                    final String sampleText = JGemsAPI.getAPIScriptingCore().getLocalMapContext().getApiCodeEnvironmentController().getEntryPointClass().sampleCode().toString();
                    final String name = e.second().getInputStrings().getFirst().get() + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.JS_SCRIPT_FILE;
                    final GameResourceScriptAsset gameResourceScriptAsset = new GameResourceScriptAsset(name, e.first().getHierarchy() + "/" + name, sampleText);
                    gameResourceScriptAsset.save(Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).getPathToScripts(), sampleText);
                    e.first().putObjectThere(gameResourceScriptAsset);
                    return gameResourceScriptAsset;
                },
                ScriptAssetPreview::new
        ).setAfterAssetDeleted((e) -> {
            final JGemsPath absPath = new JGemsPath(Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).getPathToScripts(), e.first().getHierarchy());
            if (absPath.toFile().exists()) {
                absPath.toFile().delete();
            }
        }).setAfterFolderCreated((e) -> {
            final JGemsPath absPath = new JGemsPath(Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).getPathToScripts(), e.getHierarchy());
            absPath.toFile().mkdirs();
        }).setAfterFolderDeleted((e) -> {
            new JGemsPath(Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).getPathToScripts(), e.getHierarchy()).recursiveDelete();
        }).setOnRefreshButton((e) -> {
            Objects.requireNonNull(WBench.get().getMapProjectManager().getCurrentMapProject()).refreshScriptFiles();
        }).setOnItemSelection((e) -> {
            if (this.getScriptResourceTreeDrawer().getPreviewWrapperObject() != null) {
                this.scriptEditorDrawerG.save();
            }
            if (e != null) {
                this.isPreviewOpen.set(true);
                this.scriptEditorDrawerG.setScriptPreviewObject(e);
            }
        });
    }

    public void reset() {
    }

    private void renderScriptCode(ScriptAssetPreview scriptAssetPreview) {
        this.scriptEditorDrawerG.render(scriptAssetPreview);
    }

    public void render() {
        if (this.isPreviewOpen.get()) {
            if (this.scriptResourceTreeDrawer.getPreviewWrapperObject() != null) {
                final float w = this.getEditorInterface().getOpenGLRenderer().getWindowSize().x() / 2.0f;
                final float h = this.getEditorInterface().getOpenGLRenderer().getWindowSize().y() / 2.0f;
                ImGui.setNextWindowPos(w / 2.0f, h / 2.0f, ImGuiCond.Appearing);
                ImGui.setNextWindowSize(w, h, ImGuiCond.Appearing);

                ImGui.begin("Map Script Preview", this.isPreviewOpen);
                this.renderScriptCode(this.scriptResourceTreeDrawer.getPreviewWrapperObject());
                ImGui.end();
            }
        } else {
            this.scriptResourceTreeDrawer.setPreviewWrapperObject(null);
        }

        final boolean flag = this.getEditorInterface().getSelectedScene().equals(SelectedScene.MAIN);
        if (flag) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xffc48aff);
            if (ImGui.collapsingHeader("Misc", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.popStyleColor();
                ImGui.treePush();
                this.getScriptResourceTreeDrawer().render();
                ImGui.treePop();
            } else {
                ImGui.popStyleColor();
            }
            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();
            this.markers.render(null);
            this.entities.render(null);
        }
        this.props.render(null);
        //this.markers.render((e) -> {
        //    if (!flag) {
        //        return e.isCanBeUsedInBackgroundSkyBox();
        //    }
        //    return true;
        //});
    }

    public CreatableResourcesTreeDrawerG<GameResourceScriptAsset, ScriptAssetPreview> getScriptResourceTreeDrawer() {
        return this.scriptResourceTreeDrawer;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
