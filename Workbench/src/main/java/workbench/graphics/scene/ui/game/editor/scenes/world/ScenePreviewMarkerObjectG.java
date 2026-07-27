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

package workbench.graphics.scene.ui.game.editor.scenes.world;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.AssetsChooseCombo;
import workbench.project.managing.WBenchProjectResourcesManager;

import java.util.Objects;
import java.util.function.Supplier;

public class ScenePreviewMarkerObjectG<T extends GameResourceMarkerObjectAsset> {
    private final AssetsChooseCombo<GameResourceModelAsset> gameResourceModelAssetsChooseCombo;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final String tab;
    private final Supplier<T> getter;

    /*
    new ArrayList<>() {{
            add(new Pair<>("DefaultPoint", () -> new GameResourceModelAsset("DefaultPoint", "/defaults/", WBenchResourceManager.gameEditorModelAssets.markerCube)));
            add(new Pair<>("DefaultCursor", () -> new GameResourceModelAsset("DefaultCursor", "/defaults/", WBenchResourceManager.gameEditorModelAssets.markerCursor)));
            add(new Pair<>("DefaultAABB", () -> new GameResourceModelAsset("DefaultAABB", "/defaults/", WBenchResourceManager.gameEditorModelAssets.markerAabb)));
        }}
     */

    public ScenePreviewMarkerObjectG(String tab, Supplier<T> getter, ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.gameResourceModelAssetsChooseCombo = new AssetsChooseCombo<>("Model", () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder());
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.getter = getter;
        this.tab = tab;
    }

    public void render() {
        T markerObjectAsset = this.getter.get();
        if (markerObjectAsset != null) {
            ImGui.pushID("##SCENEMARKERPREVIEW_" + this.tab);
            if (ImGui.collapsingHeader(this.tab + ": " + markerObjectAsset.getID(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##prop_preview", ImGui.getColumnWidth(), 460, true);
                ImGui.beginChild("##prop_preview_INNER", ImGui.getColumnWidth(), 400, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("MARKER-Model");
                final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(markerObjectAsset.getModelAssetRelativePath());
                this.gameResourceModelAssetsChooseCombo.render(
                        () -> extractModelAsset,
                        (e) -> markerObjectAsset.setModelAssetRelativePath(e.relativePath()),
                        (e) -> markerObjectAsset.setModelAssetRelativePath(null));
                ImGui.beginDisabled(extractModelAsset == null);
                if (ImGui.button("View Model")) {
                    this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(Objects.requireNonNull(extractModelAsset)));
                }
                ImGui.endDisabled();
                ImGui.spacing();
                ScenePreviewWorldObjectG.constraintsEdit(markerObjectAsset);
                ImGui.spacing();

                {
                    ScenePreviewWorldObjectG.tagsEdit(markerObjectAsset.getTagsContainer());
                }
                {
                    ImGui.spacing();
                    ImGui.bulletText("Rendering");
                    ImGui.beginChild("##RenProps", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                    ImGui.indent();
                    {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                        ImGui.bulletText("Color");
                        ImGui.popStyleColor();
                        float[] cols = new float[] {markerObjectAsset.getColor().x, markerObjectAsset.getColor().y, markerObjectAsset.getColor().z};
                        if (ImGui.colorEdit3("##color", cols)) {
                            markerObjectAsset.getColor().set(cols);
                        }
                    }
                    {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                        ImGui.bulletText("Transparency");
                        ImGui.popStyleColor();
                        if (ImGui.checkbox("25% Alpha", markerObjectAsset.isTransparent())) {
                            markerObjectAsset.setTransparent(!markerObjectAsset.isTransparent());
                        }
                    }
                    ImGui.unindent();
                    ImGui.endChild();
                }
                ImGui.spacing();
                //Vector2i vector2i = texturePreviewAsset.getTextureAsset().getTexture2DProgram().getSize();
                //ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
                if (ImGui.button("Save Marker")) {
                    WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.MARKERS);
                }
                ImGui.endChild();
            }
            ImGui.popID();
        }
    }
}
