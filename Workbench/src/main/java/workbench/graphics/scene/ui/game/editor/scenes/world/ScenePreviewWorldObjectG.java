package workbench.graphics.scene.ui.game.editor.scenes.world;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.collections.Pair;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.AssetsChooseCombo;
import workbench.project.managing.WBenchGameResourcesManager;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceObjectTagData;
import javagems3d.system.external.gaming.def.world.GameResourceWorldObjectAsset;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ScenePreviewWorldObjectG <T extends GameResourceWorldObjectAsset> {
    private final AssetsChooseCombo<GameResourceModelAsset> gameResourceModelAssetsChooseCombo;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final String tab;
    private final Supplier<T> getter;

    public ScenePreviewWorldObjectG(String tab, Supplier<T> getter, ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.gameResourceModelAssetsChooseCombo = new AssetsChooseCombo<>("Select Model", () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder());
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.getter = getter;
        this.tab = tab;
    }

    public void render() {
        T worldObjectAsset = this.getter.get();
        if (worldObjectAsset != null) {
            ImGui.pushID("##SCENEPREVIEW_" + this.tab);
            if (ImGui.collapsingHeader(this.tab + ": " + worldObjectAsset.getID(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##prop_preview", ImGui.getColumnWidth(), 400, true);
                final List<Pair<String, AxisConstraints>> constraintsPos = new ArrayList<>();
                constraintsPos.add(new Pair<>(worldObjectAsset.getAxisConstraints().positionConstraints().name(), null));
                final List<Pair<String, AxisConstraints>> constraintsRot = new ArrayList<>();
                constraintsRot.add(new Pair<>(worldObjectAsset.getAxisConstraints().rotationConstraints().name(), null));
                final List<Pair<String, AxisConstraints>> constraintsScale = new ArrayList<>();
                constraintsScale.add(new Pair<>(worldObjectAsset.getAxisConstraints().scalingConstraints().name(), null));
                for (AxisConstraints axisConstraints : AxisConstraints.values()) {
                    constraintsPos.add(new Pair<>(axisConstraints.name(), axisConstraints));
                    constraintsRot.add(new Pair<>(axisConstraints.name(), axisConstraints));
                    constraintsScale.add(new Pair<>(axisConstraints.name(), axisConstraints));
                }
                String[] constraintsPosS = constraintsPos.stream().map(Pair::first).collect(Collectors.toList()).toArray(new String[]{});
                String[] constraintsRotS = constraintsRot.stream().map(Pair::first).collect(Collectors.toList()).toArray(new String[]{});
                String[] constraintsScaleS = constraintsScale.stream().map(Pair::first).collect(Collectors.toList()).toArray(new String[]{});

                ImInt selectInt = new ImInt(0);
                ImGui.indent();
                ImGui.bullet();
                ImGui.text("Model");
                final GameResourceModelAsset extractModelAsset = WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheModel(worldObjectAsset.getModelAssetRelativePath());
                this.gameResourceModelAssetsChooseCombo.render(
                        () -> extractModelAsset,
                        (e) -> worldObjectAsset.setModelAssetRelativePath(e.relativePath()),
                        (e) -> worldObjectAsset.setModelAssetRelativePath(null));
                ImGui.beginDisabled(extractModelAsset == null);
                if (ImGui.button("View Model")) {
                    this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().setPreviewWrapperObject(new ModelAssetPreview(Objects.requireNonNull(extractModelAsset)));
                }
                ImGui.endDisabled();
                ImGui.spacing();
                ImGui.bullet();
                ImGui.text("Translation Constraints");
                if (ImGui.combo("Position", selectInt, constraintsPosS)) {
                    if (selectInt.get() > 0) {
                        worldObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        constraintsPos.get(selectInt.get()).second(),
                                        worldObjectAsset.getAxisConstraints().rotationConstraints(),
                                        worldObjectAsset.getAxisConstraints().scalingConstraints())
                        );
                    }
                }
                if (ImGui.combo("Rotation", selectInt, constraintsRotS)) {
                    if (selectInt.get() > 0) {
                        worldObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        worldObjectAsset.getAxisConstraints().positionConstraints(),
                                        constraintsRot.get(selectInt.get()).second(),
                                        worldObjectAsset.getAxisConstraints().scalingConstraints())
                        );
                    }
                }
                if (ImGui.combo("Scaling", selectInt, constraintsScaleS)) {
                    if (selectInt.get() > 0) {
                        worldObjectAsset.setAxisConstraints(
                                new TranslationConstraints(
                                        worldObjectAsset.getAxisConstraints().positionConstraints(),
                                        worldObjectAsset.getAxisConstraints().rotationConstraints(),
                                        constraintsScale.get(selectInt.get()).second())
                        );
                    }
                }
                //ImGui.combo("Allowed Translation")

                ImGui.spacing();

                {
                    ImGui.bullet();
                    ImGui.text("Tags");
                    final List<Pair<String, GameResourceObjectTagData>> allTagsAsset = new ArrayList<>();
                    AssetsChooseCombo.parseTreeS(WBench.get().getGameProjectManager().getGameResourcesManager().getTagAssetsFolder(), allTagsAsset);
                    String[] listForTagCombo = allTagsAsset.stream().map(Pair::first).collect(Collectors.toList()).toArray(new String[]{});
                    ImInt imInt = new ImInt(-1);
                    if (ImGui.combo("+ Tags", imInt, listForTagCombo)) {
                        worldObjectAsset.getTagsContainer().copyTagsFrom(allTagsAsset.get(imInt.get()).second().getTagContainer());
                    }
                    ImGui.text("Tags Included:");
                    if (worldObjectAsset.getTagsContainer().tags().isEmpty()) {
                        ImGui.text("<Empty>!");
                    } else {
                        ImGui.beginChild("##tags_inc_there", ImGui.getColumnWidth(), 150, true);
                        Iterator<Tag<?>> tagIterator = worldObjectAsset.getTagsContainer().tags().values().iterator();
                        while (tagIterator.hasNext()) {
                            Tag<?> tag = tagIterator.next();
                            ImGui.pushID("##view_tags_" + tag.getTagID().getId());
                            if (ImGui.button("-")) {
                                tagIterator.remove();
                            }
                            ImGui.sameLine();
                            //if (ImGui.button("View")) {
                            //    WBench.get().getGameProjectManager().getGameResourcesManager().getTagAssetsFolder().find("/root/env1/eblan.txt");
                            //}
                            //ImGui.sameLine();
                            ImGui.textWrapped(tag.getTagID().getId() + " (" + tag.getTagID().getDescription() + ")");
                            ImGui.popID();
                        }
                        ImGui.endChild();
                    }
                }

                ImGui.spacing();
                if (ImGui.button("Save")) {
                    WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
                    WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
                }
                //Vector2i vector2i = texturePreviewAsset.getTextureAsset().getTexture2DProgram().getSize();
                //ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
            }
            ImGui.popID();
        }
    }
}
