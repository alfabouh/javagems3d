package workbench.graphics.scene.ui.game.editor.scenes.world;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;
import javagems3d.system.external.gaming.def.world.IWBenchAssetWithTranslationConstraints;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.service.collections.Pair;
import logger.Log;
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
                    ImGui.beginChild("##RenProps", ImGui.getColumnWidth(), 180, true);
                    if (worldObjectAsset.getRenderProperties() == null) {
                        worldObjectAsset.setRenderProperties(new WBenchRenderProperties());
                        Log.get().debug("Null renderProp. Created");
                    }
                    ImGui.indent();
                    ScenePreviewWorldObjectG.renderPropertiesEdit(worldObjectAsset.getRenderProperties());
                    ImGui.unindent();
                    ImGui.endChild();
                }
                ImGui.spacing();
                //Vector2i vector2i = texturePreviewAsset.getTextureAsset().getTexture2DProgram().getSize();
                //ImGui.textWrapped("Size: " + vector2i.x + " x " + vector2i.y);
                ImGui.unindent();
                ImGui.endChild();
                if (ImGui.button("Save")) {
                    if (worldObjectAsset instanceof GameResourcePropObjectAsset) {
                        WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.PROPS);
                    } else {
                        WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchGameResourcesManager.AssetsTarget.ENTITIES);
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
                ImGui.textWrapped(tag.getTagID().getId() + " (" + tag.getTagID().getDescription() + ")");
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

    public static void renderPropertiesEdit(RenderProperties renderProperties) {
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff982cff);
            ImGui.bulletText("Properties");
            ImGui.popStyleColor();
            if (renderProperties.propertiesMap == null || renderProperties.propertiesMap.isEmpty()) {
                ImGui.text("<Error: Empty>!");
            } else {
                if (renderProperties.propertiesMap.containsKey(JGemsRenderProperties.KEY_RENDER_DISTANCE)) {
                    float[] f1 = new float[]{(float) renderProperties.getFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE)};
                    ImGui.text(JGemsRenderProperties.KEY_RENDER_DISTANCE);
                    ImGui.sameLine();
                    ImGui.setNextItemWidth(80);
                    ImGui.dragFloat("##" + JGemsRenderProperties.KEY_RENDER_DISTANCE, f1, 0.5f, -1.0f, 1024.0f);
                    renderProperties.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, f1[0]);
                }
                if (renderProperties.propertiesMap.containsKey(JGemsRenderProperties.KEY_ALPHA_DISCARD)) {
                    float[] f1 = new float[]{(float) renderProperties.getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD)};
                    ImGui.text(JGemsRenderProperties.KEY_ALPHA_DISCARD);
                    ImGui.sameLine();
                    ImGui.setNextItemWidth(80);
                    ImGui.dragFloat("##" + JGemsRenderProperties.KEY_ALPHA_DISCARD, f1, 0.0001f, 0.0f, 1.0f);
                    renderProperties.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, f1[0]);
                }
                if (renderProperties.propertiesMap.containsKey(JGemsRenderProperties.KEY_SHADOW_CASTER)) {
                    boolean res = renderProperties.getBool(JGemsRenderProperties.KEY_SHADOW_CASTER);
                    ImGui.text(JGemsRenderProperties.KEY_SHADOW_CASTER);
                    ImGui.sameLine();
                    if (ImGui.checkbox("##" + JGemsRenderProperties.KEY_SHADOW_CASTER, res)) {
                        renderProperties.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, !res);
                    }
                }
                if (renderProperties.propertiesMap.containsKey(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION)) {
                    boolean res = renderProperties.getBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION);
                    ImGui.text(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION);
                    ImGui.sameLine();
                    if (ImGui.checkbox("##" + JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, res)) {
                        renderProperties.setValueBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, !res);
                    }
                }
            }
        }
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff982cff);
            ImGui.bulletText("Culling");
            ImGui.popStyleColor();
            {
                boolean res = renderProperties.getCullingRules().isIgnoreDistanceCulling();
                ImGui.text("Ignore Distance Culling");
                ImGui.sameLine();
                if (ImGui.checkbox("## Ignore Distance Culling", res)) {
                    renderProperties.getCullingRules().setIgnoreDistanceCulling(!res);
                }
            }
            {
                boolean res = renderProperties.getCullingRules().isIgnoreFrustumCulling();
                ImGui.text("Ignore Frustum Culling");
                ImGui.sameLine();
                if (ImGui.checkbox("## Ignore Frustum Culling", res)) {
                    renderProperties.getCullingRules().setIgnoreFrustumCulling(!res);
                }
            }
        }
    }
}
