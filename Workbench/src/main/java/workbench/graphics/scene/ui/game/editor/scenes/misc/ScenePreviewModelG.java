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

package workbench.graphics.scene.ui.game.editor.scenes.misc;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.service.collections.Pair;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import javagems3d.system.service.files.VirtualObjectsFolder;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;

import java.util.ArrayList;
import java.util.List;

public class ScenePreviewModelG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    public float modelPreviewScaling;
    private boolean showAABB;
    private boolean showChessTerrain;
    private boolean flipModel;

    public ScenePreviewModelG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.showChessTerrain = true;
    }

    private void parseModelsTree(VirtualObjectsFolder<GameResourceModelAsset> folder, boolean root, List<Pair<String, GameResourceModelAsset>> allModelsAsset) {
        for (GameResourceModelAsset asset : folder.getObjectsThere()) {
            allModelsAsset.add(new Pair<>(asset.relativePath(), asset));
        }
        for (VirtualObjectsFolder<GameResourceModelAsset> child : folder.getFoldersThere()) {
            this.parseModelsTree(child, false, allModelsAsset);
        }
    }

    public void render() {
        ModelAssetPreview modelAssetPreview = this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().getPreviewWrapperObject();
        if (modelAssetPreview != null) {
            modelAssetPreview.updateAnimation();
            if (ImGui.collapsingHeader("Model: " + modelAssetPreview.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##model_preview", ImGui.getColumnWidth(), 300, true);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Nodes");
                int totalVertexes = 0;
                int totalTriangles = 0;
                for (MeshNode<?> node : modelAssetPreview.getAsset().meshGroup().getAllNodes()) {
                    totalVertexes += node.getMeshData().totalVertexes();
                    totalTriangles += node.getMeshData().numVertexIndexes() / 3;
                }
                ImGui.textWrapped("Total Nodes: " + modelAssetPreview.getAsset().meshGroup().getAllNodes().size());
                ImGui.textWrapped("Solid Nodes: " + modelAssetPreview.getAsset().meshGroup().getSolidNodes().size());
                ImGui.textWrapped("Transparent Nodes: " + modelAssetPreview.getAsset().meshGroup().getBlendedTransparencyNodes().size());
                ImGui.textWrapped("Total Vertexes: " + totalVertexes);
                ImGui.textWrapped("Total Triangles: " + totalTriangles);
                if (!modelAssetPreview.getAsset().meshGroup().getAnimationsList().isEmpty()) {
                    boolean hasAnimation = modelAssetPreview.getAnimationData() != null && modelAssetPreview.getAnimationData().getCurrentAnimation() != null;

                    ImGui.separator();
                    ImGui.bullet();
                    ImGui.text("Animations");
                    ImGui.textWrapped("Total Animations: " + modelAssetPreview.getAsset().meshGroup().getAnimationsNum());
                    List<String> animations = new ArrayList<>();
                    if (hasAnimation) {
                        animations.add("(*) " + modelAssetPreview.getAnimationData().getCurrentAnimation().name());
                        animations.add("* None");
                    } else {
                        animations.add("Select...");
                        animations.add("* None");
                    }
                    for (Animation animation : modelAssetPreview.getAsset().meshGroup().getAnimationsList()) {
                        animations.add(animation.name());
                    }
                    ImInt currentAnimation = new ImInt(0);
                    if (ImGui.combo("Animation", currentAnimation, animations.toArray(new String[]{}))) {
                        modelAssetPreview.setAnimationByID(currentAnimation.get() - 2);
                    } else {
                        if (hasAnimation) {
                            ImGui.indent();
                            ImGui.text("FPS: " + modelAssetPreview.getAnimationData().getFps());
                            ImGui.text("Duration: " + modelAssetPreview.getAnimationData().getCurrentAnimation().duration());
                            ImGui.text("Frame Count: " + modelAssetPreview.getAnimationData().getCurrentAnimation().getFrameCount());
                            ImGui.unindent();
                        }
                    }
                }
                ImGui.separator();
                ImGui.text("Preview Tools:");
                float[] f1 = new float[] {this.modelPreviewScaling};
                if (ImGui.dragFloat("Distance", f1, 0.1f)) {
                    this.modelPreviewScaling = f1[0];
                }
                if (ImGui.checkbox("Show AABB", this.showAABB)) {
                    this.showAABB = !this.showAABB;
                }
                if (ImGui.checkbox("Show Chess Terrain", this.showChessTerrain)) {
                    this.showChessTerrain = !this.showChessTerrain;
                }
                //if (ImGui.checkbox("Flip Model", this.flipModel)) {
                //    this.flipModel = !this.flipModel;
                //}
                ImGui.unindent();
                ImGui.endChild();
            }
        }
    }
    public boolean isFlipModel() {
        return this.flipModel;
    }

    public float getModelPreviewScaling() {
        return this.modelPreviewScaling;
    }

    public boolean isShowChessTerrain() {
        return this.showChessTerrain;
    }

    public boolean isShowAABB() {
        return this.showAABB;
    }
}
