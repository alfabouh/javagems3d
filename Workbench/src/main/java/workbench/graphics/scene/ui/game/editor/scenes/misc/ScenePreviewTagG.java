package workbench.graphics.scene.ui.game.editor.scenes.misc;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.items.*;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ObjectTagPreview;
import workbench.graphics.scene.ui.game.editor.utils.TagItemCreatingInstancesG;
import workbench.project.managing.WBenchProjectResourcesManager;

import java.util.*;

public class ScenePreviewTagG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private Map<String, TagItemCreatingInstancesG.TagItemClassResolver<?>> tagItemRows;

    private ImString uniqueID;
    private ImString description;
    private ImString tip;
    private ImInt comboBoxSelection;

    private ImString edit_description;
    private ImString edit_tip;

    public ScenePreviewTagG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.reset();
    }

    private Map<String, TagItemCreatingInstancesG.TagItemClassResolver<?>> initTagItemRows() {
        final Map<String, TagItemCreatingInstancesG.TagItemClassResolver<?>> map = new LinkedHashMap<>();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagCheckBoolean> tagCheckBooleanTagItemClassResolver = TagItemCreatingInstancesG.booleanCheckTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagRadioBoolean> radioBooleanTagItemClassResolver = TagItemCreatingInstancesG.radioBooleanTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagFloat> tagFloatTagItemClassResolver = TagItemCreatingInstancesG.floatTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagInt> intTagItemClassResolver = TagItemCreatingInstancesG.intTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagString> stringTagItemClassResolver = TagItemCreatingInstancesG.stringTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagColor> colorTagItemClassResolver = TagItemCreatingInstancesG.colorTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagVector> vectorTagItemClassResolver = TagItemCreatingInstancesG.vectorTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagObjectsList> objectListTagItemClassResolver = TagItemCreatingInstancesG.objectListTagItemResolver.get();
        final TagItemCreatingInstancesG.TagItemClassResolver<TagGameResourcesList> gameResourcesListTagItemClassResolver = TagItemCreatingInstancesG.gameResourcesListTagItemResolver.get();

        {
            map.put("None", null);
            map.put("Checkbox", tagCheckBooleanTagItemClassResolver);
            map.put("RadioFlags", radioBooleanTagItemClassResolver);
            map.put("Float Input", tagFloatTagItemClassResolver);
            map.put("Integer Input", intTagItemClassResolver);
            map.put("Text Input", stringTagItemClassResolver);
            map.put("Color", colorTagItemClassResolver);
            map.put("Vector", vectorTagItemClassResolver);
            map.put("Scene Objects List", objectListTagItemClassResolver);
            map.put("Game Resources List", gameResourcesListTagItemClassResolver);
        }
        return map;
    }

    public void reset() {
        this.tagItemRows = this.initTagItemRows();
        this.uniqueID = new ImString();
        this.description = new ImString();
        this.tip = new ImString();
        this.edit_description = new ImString();
        this.edit_tip = new ImString();
        this.comboBoxSelection = new ImInt(0);
    }

    public void render() {
        ObjectTagPreview tagPreview = this.resourcesInterfaceComponentG.getTagResourceTreeDrawer().getPreviewWrapperObject();
        if (tagPreview != null) {
            if (ImGui.collapsingHeader("Tags Container: " + tagPreview.getAsset().name(), ImGuiTreeNodeFlags.DefaultOpen)) {
                final TagsContainer tagsContainer = tagPreview.getAsset().getTagContainer();

                ImGui.indent();
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                if (ImGui.treeNodeEx("Tag Creator", ImGuiTreeNodeFlags.DefaultOpen)) {
                    ImGui.popStyleColor();

                    ImGui.beginChild("##TagID", ImGui.getColumnWidth(), 105, true);
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c76ff);
                    ImGui.bulletText("TagID");
                    ImGui.popStyleColor();

                    ImGui.spacing();
                    ImGui.inputText("Unique ID", this.uniqueID);
                    {
                        ImGui.pushStyleColor(ImGuiCol.Text, 0xff8a8a8a);
                        ImGui.inputText("Info", this.description);
                        ImGui.inputText("Tip", this.tip);
                        ImGui.popStyleColor();
                    }
                    ImGui.spacing();
                    ImGui.endChild();

                    ImGui.beginChild("##TagData", ImGui.getColumnWidth(), 200, true);
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c76ff);
                    ImGui.bulletText("TagData");
                    ImGui.popStyleColor();
                    ImGui.spacing();

                    TagItemCreatingInstancesG.TagItemClassResolver<?> tagItemClassResolver = null;
                    {
                        String[] comboGet = this.tagItemRows.keySet().toArray(new String[0]);
                        ImGui.combo("Tag Type", this.comboBoxSelection, comboGet);
                        {
                            final int selected = this.comboBoxSelection.get();
                            if (selected < 0 || selected > comboGet.length) {
                                ImGui.text("...");
                            } else {
                                tagItemClassResolver = this.tagItemRows.get(comboGet[selected]);
                                if (tagItemClassResolver != null) {
                                    tagItemClassResolver.renderUI(this.resourcesInterfaceComponentG);
                                }
                            }
                        }
                    }
                    ImGui.endChild();

                    ImGui.spacing();
                    {
                        ImGui.beginDisabled(this.uniqueID.isEmpty() || tagsContainer.tags().containsKey(new TagID(this.uniqueID.get())) || tagItemClassResolver == null);
                        if (ImGui.button("+ Create")) {
                            tagsContainer.addTag(new Tag<>(new TagID(this.uniqueID.get(), this.description.get(), this.tip.get()), Objects.requireNonNull(tagItemClassResolver).create()));
                            this.reset();
                            WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
                        }
                        ImGui.endDisabled();
                    }
                    ImGui.treePop();
                } else {
                    ImGui.popStyleColor();
                }
                {
                    ImGui.spacing();
                }
                ImGui.pushStyleColor(ImGuiCol.Text, 0xffff8c7a);
                if (ImGui.treeNodeEx("Tags inside:", ImGuiTreeNodeFlags.DefaultOpen)) {
                    ImGui.popStyleColor();
                    ImGui.beginChild("##tags_inside_child", ImGui.getColumnWidth(), ImGui.getWindowHeight() * 0.5f, true);
                    if (tagsContainer.tags().isEmpty()) {
                        ImGui.text("<Empty!>");
                    } else {
                        Iterator<Map.Entry<TagID, Tag<? extends TagItem>>> iterator = tagsContainer.tags().entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<TagID, Tag<? extends TagItem>> tagItem = iterator.next();
                            ImGui.beginChild("##tagPreview_" + tagItem.getKey().getId(), ImGui.getColumnWidth(), 228, true, ImGuiWindowFlags.HorizontalScrollbar);
                            ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c76ff);
                            ImGui.bulletText("TagID");
                            ImGui.popStyleColor();

                            {
                                ImGui.indent();
                                ImGui.textWrapped("Unique ID: " + tagItem.getKey().getId());
                                {
                                    {
                                        ImGui.text("Description:");
                                        ImGui.sameLine();
                                        this.edit_description.set(tagItem.getKey().getNormalName());
                                        if (ImGui.inputTextMultiline("##desc1", this.edit_description, ImGui.getColumnWidth(), 18)) {
                                            tagItem.getKey().setNormalName(this.edit_description.get());
                                        }
                                    }
                                    {
                                        ImGui.text("Tooltip:");
                                        ImGui.sameLine();
                                        this.edit_tip.set(tagItem.getKey().getToolTip());
                                        if (ImGui.inputTextMultiline("##desc2", this.edit_tip, ImGui.getColumnWidth(), 18)) {
                                            tagItem.getKey().setToolTip(this.edit_tip.get());
                                        }
                                    }
                                }
                                {
                                    if (ImGui.button("Save Metadata")) {
                                        WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
                                    }
                                }
                                ImGui.spacing();
                                ImGui.unindent();
                            }

                            ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c76ff);
                            ImGui.bulletText("TagData");
                            ImGui.popStyleColor();

                            {
                                ImGui.indent();
                                ImGui.textWrapped("Type = " + tagItem.getValue().getTagItem().getTypeString());
                                ImGui.textWrapped("Contains: " + tagItem.getValue().getTagItem().toString());
                                ImGui.unindent();
                            }
                            ImGui.spacing();
                            ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                            ImGui.indent();
                            if (ImGui.button("- Delete Tag")) {
                                WBench.get().getGameProjectManager().saveResourceObjectFiles(WBenchProjectResourcesManager.AssetsTarget.TAGS);
                                iterator.remove();
                            }
                            ImGui.unindent();
                            ImGui.popStyleColor();
                            ImGui.endChild();
                        }
                    }
                    ImGui.unindent();
                    ImGui.endChild();
                    ImGui.treePop();
                } else {
                    ImGui.popStyleColor();
                }
            }
        }
    }
}
