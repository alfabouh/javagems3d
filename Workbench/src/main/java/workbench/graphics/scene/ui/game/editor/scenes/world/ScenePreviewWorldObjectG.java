package workbench.graphics.scene.ui.game.editor.scenes.world;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import javagems3d.system.external.gaming.def.world.IWBenchAssetWithTranslationConstraints;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import workbench.WBench;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.utils.AssetsChooseCombo;
import workbench.project.managing.WBenchProjectResourcesManager;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceObjectTagData;
import javagems3d.system.external.gaming.def.world.GameResourceWorldObjectAsset;

import java.util.*;
import java.util.function.Supplier;

public class ScenePreviewWorldObjectG <T extends GameResourceWorldObjectAsset> {
    private final AssetsChooseCombo<GameResourceModelAsset> gameResourceModelAssetsChooseCombo;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final String tab;
    private final Supplier<T> getter;

    public ScenePreviewWorldObjectG(String tab, Supplier<T> getter, ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.gameResourceModelAssetsChooseCombo = new AssetsChooseCombo<>("Model", () -> WBench.get().getGameProjectManager().getGameResourcesManager().getModelAssetsFolder());
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.getter = getter;
        this.tab = tab;
    }

    public void render() {
        T worldObjectAsset = this.getter.get();
        if (worldObjectAsset != null) {
            ImGui.pushID("##SCENEPREVIEW_" + this.tab);
            if (ImGui.collapsingHeader(this.tab + ": " + worldObjectAsset.getID(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.beginChild("##prop_preview", ImGui.getColumnWidth(), 500, true);
                ImGui.beginChild("##prop_preview_INNER", ImGui.getColumnWidth(), 460, true);
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
                ScenePreviewWorldObjectG.constraintsEdit(worldObjectAsset);
                ImGui.spacing();

                {
                    ScenePreviewWorldObjectG.tagsEdit(worldObjectAsset.getTagsContainer());
                }
                {
                    ImGui.spacing();
                    ImGui.bulletText("Rendering");
                    ImGui.beginChild("##RenProps", ImGui.getColumnWidth(), 180, true, ImGuiWindowFlags.HorizontalScrollbar);
                    if (worldObjectAsset.getRenderProperties() == null) {
                        worldObjectAsset.setRenderProperties(new WBenchRenderProperties());
                        Log.get().debug("Null renderProp. Created");
                    }
                    ImGui.indent();
                    ScenePreviewWorldObjectG.renderPropertiesEdit(worldObjectAsset.getRenderProperties(), false);
                    ImGui.unindent();
                    ImGui.endChild();
                }
                ImGui.spacing();
                //Vector2i vector2i = texturePreviewAsset.getTextureAsset().getTexture2DProgram().getSize();
                //ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
                if (ImGui.button("Save Object")) {
                    if (worldObjectAsset instanceof GameResourcePropObjectAsset) {
                        WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.PROPS);
                    } else {
                        WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.ENTITIES);
                    }
                }
                ImGui.endChild();
            }
            ImGui.popID();
        }
    }

    public static void tagsEdit(TagsContainer tagsContainer) {
        ImGui.bulletText("Tags");
        final List<Pair<String, GameResourceObjectTagData>> allTagsAsset = new ArrayList<>();
        AssetsChooseCombo.parseTreeS(WBench.get().getGameProjectManager().getGameResourcesManager().getTagAssetsFolder(), allTagsAsset);
        ResourcesInterfaceComponentG.DEFAULT_TAGS().forEach(e -> {
            allTagsAsset.add(new Pair<>(e.name(), e));
        });
        String[] listForTagCombo = allTagsAsset.stream().map(Pair::first).toList().toArray(new String[]{});
        ImInt imInt = new ImInt(-1);
        if (ImGui.combo("+ Tags", imInt, listForTagCombo)) {
            tagsContainer.copyTagsFrom(allTagsAsset.get(imInt.get()).second().getTagContainer());
        }
        if (tagsContainer.tags().isEmpty()) {
            ImGui.text("<Empty>!");
        } else {
            ImGui.beginChild("##tags_inc_there", ImGui.getColumnWidth(), 150, true);
            Iterator<Tag<?>> tagIterator = tagsContainer.tags().values().iterator();
            while (tagIterator.hasNext()) {
                Tag<?> tag = tagIterator.next();
                ImGui.pushID("##view_tags_" + tag.getTagID().getId());
                if (ImGui.button("-")) {
                    tagIterator.remove();
                }
                ImGui.sameLine();
                //if (ImGui.button("View")) {
                //}
                //ImGui.sameLine();
                ImGui.textWrapped(tag.getTagID().getId() + " (" + tag.getTagID().getNormalName() + ")");
                ImGui.popID();
            }
            ImGui.endChild();
        }
    }

    public static void constraintsEdit(IWBenchAssetWithTranslationConstraints assetWithTranslationConstraints) {
        final List<Pair<String, AxisConstraints>> constraintsPos = new ArrayList<>();
        constraintsPos.add(new Pair<>(assetWithTranslationConstraints.getAxisConstraints().positionConstraints().name(), null));
        final List<Pair<String, AxisConstraints>> constraintsRot = new ArrayList<>();
        constraintsRot.add(new Pair<>(assetWithTranslationConstraints.getAxisConstraints().rotationConstraints().name(), null));
        final List<Pair<String, AxisConstraints>> constraintsScale = new ArrayList<>();
        constraintsScale.add(new Pair<>(assetWithTranslationConstraints.getAxisConstraints().scalingConstraints().name(), null));
        for (AxisConstraints axisConstraints : AxisConstraints.values()) {
            constraintsPos.add(new Pair<>(axisConstraints.name(), axisConstraints));
            constraintsRot.add(new Pair<>(axisConstraints.name(), axisConstraints));
            constraintsScale.add(new Pair<>(axisConstraints.name(), axisConstraints));
        }
        String[] constraintsPosS = constraintsPos.stream().map(Pair::first).toList().toArray(new String[]{});
        String[] constraintsRotS = constraintsRot.stream().map(Pair::first).toList().toArray(new String[]{});
        String[] constraintsScaleS = constraintsScale.stream().map(Pair::first).toList().toArray(new String[]{});
        ImInt selectInt = new ImInt(0);
        ImGui.bulletText("Translation Constraints");
        if (ImGui.combo("Position", selectInt, constraintsPosS)) {
            if (selectInt.get() > 0) {
                assetWithTranslationConstraints.setAxisConstraints(
                        new TranslationConstraints(constraintsPos.get(selectInt.get()).second(), assetWithTranslationConstraints.getAxisConstraints().rotationConstraints(), assetWithTranslationConstraints.getAxisConstraints().scalingConstraints())
                );
            }
        }
        if (ImGui.combo("Rotation", selectInt, constraintsRotS)) {
            if (selectInt.get() > 0) {
                assetWithTranslationConstraints.setAxisConstraints(
                        new TranslationConstraints(
                                assetWithTranslationConstraints.getAxisConstraints().positionConstraints(),
                                constraintsRot.get(selectInt.get()).second(),
                                assetWithTranslationConstraints.getAxisConstraints().scalingConstraints())
                );
            }
        }
        if (ImGui.combo("Scaling", selectInt, constraintsScaleS)) {
            if (selectInt.get() > 0) {
                assetWithTranslationConstraints.setAxisConstraints(
                        new TranslationConstraints(
                                assetWithTranslationConstraints.getAxisConstraints().positionConstraints(),
                                assetWithTranslationConstraints.getAxisConstraints().rotationConstraints(),
                                constraintsScale.get(selectInt.get()).second())
                );
            }
        }
    }

    public static void renderPropertiesEdit(RenderProperties renderProperties, boolean withSnapshots) {
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff982cff);
            ImGui.bulletText("Properties");
            ImGui.popStyleColor();
            if (renderProperties.getPropertiesMap() == null || renderProperties.getPropertiesMap().isEmpty()) {
                ImGui.text("<Error: Empty>!");
            } else {
                renderProperties.getPropertiesMap().forEach((k, v) -> {
                    if (k.startsWith("KEY_")) {
                        k = k.substring(4);
                    }
                    if (v instanceof RenderProperties.FloatValue f) {
                        float[] value = new float[]{f.value};
                        ImGui.text(k);
                        ImGui.sameLine();
                        ImGui.setNextItemWidth(60.0f);
                        boolean changed = ImGui.dragFloat("##" + k, value, 0.01f, f.min, f.max);
                        if (withSnapshots) {
                            try (UITrackingHelper ignored = UITrackingHelper.create("TRACK_operationFlag_" + k, WBenchUITrackingHelper::INSTANCE)) {
                                if (changed) {
                                    ignored.saveSnapshot();
                                }
                            }
                        }
                        if (changed) {
                            f.value = value[0];
                        }
                    } else if (v instanceof RenderProperties.IntValue i) {
                        int[] value = new int[]{i.value};
                        ImGui.text(k);
                        ImGui.sameLine();
                        ImGui.setNextItemWidth(60.0f);
                        boolean changed = ImGui.dragInt("##" + k, value, 0.2f, i.min, i.max);
                        if (withSnapshots) {
                            try (UITrackingHelper ignored = UITrackingHelper.create("TRACK_operationFlag_" + k, WBenchUITrackingHelper::INSTANCE)) {
                                if (changed) {
                                    ignored.saveSnapshot();
                                }
                            }
                        }
                        if (changed) {
                            i.value = value[0];
                        }
                    } else if (v instanceof RenderProperties.BoolValue b) {
                        ImGui.text(k);
                        ImGui.sameLine();
                        boolean changed = ImGui.checkbox("##" + k, b.value);
                        if (changed) {
                            if (withSnapshots) {
                                WBenchUITrackingHelper.instantlyTrackAndPush();
                            }
                            b.value = !b.value;
                        }
                    }
                });
            }
        }
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff982cff);
            ImGui.bulletText("Culling");
            ImGui.popStyleColor();

            {
                ImGui.text("Ignore Distance Culling");
                ImGui.sameLine();
                if (ImGui.checkbox("##Ignore Distance Culling", renderProperties.getCullingRules().isIgnoreDistanceCulling())) {
                    renderProperties.getCullingRules().setIgnoreDistanceCulling(!renderProperties.getCullingRules().isIgnoreDistanceCulling());
                }
            }

            {
                ImGui.text("Ignore Frustum Culling");
                ImGui.sameLine();
                if (ImGui.checkbox("##Ignore Frustum Culling", renderProperties.getCullingRules().isIgnoreFrustumCulling())) {
                    renderProperties.getCullingRules().setIgnoreFrustumCulling(!renderProperties.getCullingRules().isIgnoreFrustumCulling());
                }
            }
        }
    }
}
