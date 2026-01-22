package workbench.graphics.scene.ui.game.editor.scenes;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.collections.Pair;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.ModelAssetPreview;
import workbench.project.managing.instances.GameResourceModelAsset;
import workbench.project.managing.instances.GameResourcePropObjectAsset;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScenePreviewPropG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;

    public ScenePreviewPropG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
    }

    private void parseModelsTree(GameResourceAssetsFolder<GameResourceModelAsset> folder, boolean root, List<Pair<String, GameResourceModelAsset>> allModelsAsset) {
        for (GameResourceModelAsset asset : folder.getAssetsThere()) {
            allModelsAsset.add(new Pair<>(asset.getRelativePath(), asset));
        }
        for (GameResourceAssetsFolder<GameResourceModelAsset> child : folder.getFoldersThere()) {
            this.parseModelsTree(child, false, allModelsAsset);
        }
    }

    public void render() {
        GameResourcePropObjectAsset propObjectAsset = this.resourcesInterfaceComponentG.getPropResourceTreeDrawer().getCurrentSelectedAsset();
        if (propObjectAsset != null) {
            if (ImGui.collapsingHeader("Prop: " + propObjectAsset.getID(), ImGuiTreeNodeFlags.DefaultOpen)) {
                GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(propObjectAsset.getModelAssetRelativePath());
                boolean hasModel = extractModelAsset != null;

                final List<Pair<String, GameResourceModelAsset>> allModelsAsset = new ArrayList<>();
                final List<Pair<String, AxisConstraints>> constraintsPos = new ArrayList<>();
                constraintsPos.add(new Pair<>(propObjectAsset.getAxisConstraints().getPositionConstraints().name(), null));
                final List<Pair<String, AxisConstraints>> constraintsRot = new ArrayList<>();
                constraintsRot.add(new Pair<>(propObjectAsset.getAxisConstraints().getRotationConstraints().name(), null));
                final List<Pair<String, AxisConstraints>> constraintsScale = new ArrayList<>();
                constraintsScale.add(new Pair<>(propObjectAsset.getAxisConstraints().getScalingConstraints().name(), null));


                if (hasModel) {
                    allModelsAsset.add(new Pair<>("(*) " + extractModelAsset.getName(), null));
                    allModelsAsset.add(new Pair<>("None", null));
                } else {
                    allModelsAsset.add(new Pair<>("Select...", null));
                }
                this.parseModelsTree(WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder(), true, allModelsAsset);
                String[] listForCombo = allModelsAsset.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(new String[]{});

                for (AxisConstraints axisConstraints : AxisConstraints.values()) {
                    constraintsPos.add(new Pair<>(axisConstraints.name(), axisConstraints));
                    constraintsRot.add(new Pair<>(axisConstraints.name(), axisConstraints));
                    constraintsScale.add(new Pair<>(axisConstraints.name(), axisConstraints));
                }
                String[] constraintsPosS = constraintsPos.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(new String[]{});
                String[] constraintsRotS = constraintsRot.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(new String[]{});
                String[] constraintsScaleS = constraintsScale.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(new String[]{});

                ImInt selectInt = new ImInt(0);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Model");
                if (ImGui.combo("Select", selectInt, listForCombo)) {
                    GameResourceModelAsset modelAsset = allModelsAsset.get(selectInt.get()).getSecond();
                    if (modelAsset != null) {
                        propObjectAsset.setModelAssetRelativePath(modelAsset.getRelativePath());
                    } else {
                        propObjectAsset.setModelAssetRelativePath(null);
                    }
                }
                ImGui.beginDisabled(!hasModel);
                if (ImGui.button("View Model")) {
                    this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(Objects.requireNonNull(extractModelAsset)));
                }
                ImGui.endDisabled();
                ImGui.spacing();
                ImGui.bullet();
                ImGui.text("Translation Constraints");
                if (ImGui.combo("Position", selectInt, constraintsPosS)) {
                    if (selectInt.get() > 0) {
                        propObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        constraintsPos.get(selectInt.get()).getSecond(),
                                        propObjectAsset.getAxisConstraints().getRotationConstraints(),
                                        propObjectAsset.getAxisConstraints().getScalingConstraints())
                        );
                    }
                }
                if (ImGui.combo("Rotation", selectInt, constraintsRotS)) {
                    if (selectInt.get() > 0) {
                        propObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        propObjectAsset.getAxisConstraints().getPositionConstraints(),
                                        constraintsRot.get(selectInt.get()).getSecond(),
                                        propObjectAsset.getAxisConstraints().getScalingConstraints())
                        );
                    }
                }
                if (ImGui.combo("Scaling", selectInt, constraintsScaleS)) {
                    if (selectInt.get() > 0) {
                        propObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        propObjectAsset.getAxisConstraints().getPositionConstraints(),
                                        propObjectAsset.getAxisConstraints().getRotationConstraints(),
                                        constraintsScale.get(selectInt.get()).getSecond())
                        );
                    }
                }
                //ImGui.combo("Allowed Translation")

                ImGui.spacing();
                if (ImGui.button("Save")) {
                    WBench.get().getGameProjectManager().saveResourceObjectFiles();
                }
                //Vector2i vector2i = texturePreviewAsset.getTextureAsset().getTexture2DProgram().getSize();
                //ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
            }
        }
    }
}
