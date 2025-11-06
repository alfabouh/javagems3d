package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.help.JGemsHelper;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.items.*;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.map.MapEditorInterface;

import java.util.*;

public class ActionsInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final float[] coordsToGen;

    public ActionsInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.coordsToGen = new float[3];
        this.clear();
    }

    public void clear() {

    }

    public void actionsContent() {
        if (this.getEditorInterface().getCurrentSelectedTemplate() != null && ImGui.collapsingHeader("Preview", ImGuiTreeNodeFlags.DefaultOpen)) {
            float available = Math.min(ImGui.getContentRegionAvailX(), 256);
            ImGui.text("Preview: " + this.getEditorInterface().getCurrentSelectedTemplate().getObjectId().getNameId());
            ImGui.image(this.getEditorInterface().getScenePreview().getTextureIDByIndex(0), available, available, 0.0f, 1.0f, 1.0f, 0.0f);

            float[] scaling = new float[]{this.getEditorInterface().getPreviewDistance()};
            ImGui.beginDisabled(ImGui.getIO().getKeyCtrl());
            if (ImGui.sliderFloat("Distance", scaling, 0.1f, 10.0f)) {
                this.getEditorInterface().setPreviewDistance(scaling[0]);
            }
            ImGui.endDisabled();
            if (ImGui.button("Generate")) {
                WBenchObject wBenchObject = this.getEditorInterface().getCurrentSelectedTemplate().createObject(this.getEditorInterface().getOpenGLRenderer().getWorld(), null);
                this.spawnInWorld(wBenchObject, null);
            }
            ImGui.sameLine();
            if (ImGui.button("Generate At...")) {
                ImGui.openPopup("Position");
            }
            if (ImGui.beginPopup("Position")) {
                ImGui.inputFloat3("##coords", this.coordsToGen);
                if (ImGui.button("Confirm")) {
                    WBenchObject wBenchObject = this.getEditorInterface().getCurrentSelectedTemplate().createObject(this.getEditorInterface().getOpenGLRenderer().getWorld(), null);
                    this.spawnInWorld(wBenchObject, new Vector3f(this.coordsToGen));
                }
                ImGui.sameLine();
                if (ImGui.button("Cancel")) {
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
            ImGui.separator();
        }

        WBenchObject currentSelectedObject = this.getEditorInterface().getCurrentSelectedObject();
        if (currentSelectedObject != null) {
            if (ImGui.collapsingHeader("Object [" + currentSelectedObject.getId() + "]", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.treePush();
                if (currentSelectedObject.hasTranslationConstraints()) {
                    if (ImGui.treeNodeEx("Transformation", ImGuiTreeNodeFlags.DefaultOpen)) {
                        int objectFlagTranslate = currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag();
                        int objectFlagRotate = currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag();
                        int objectFlagScaling = currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag();

                        if (objectFlagTranslate != 0) {
                            if (ImGui.radioButton("Translation", (this.getEditorInterface().getCurrentOperation() & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
                                this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseGuizmoOperation(true, false, false));
                            }
                        }

                        if (objectFlagRotate != 0) {
                            if (ImGui.radioButton("Rotation", (this.getEditorInterface().getCurrentOperation() & (Operation.ROTATE_X | Operation.ROTATE_Y | Operation.ROTATE_Z)) != 0)) {
                                this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseGuizmoOperation(false, true, false));
                            }
                        }

                        if (objectFlagScaling != 0) {
                            if (ImGui.radioButton("Scaling", (this.getEditorInterface().getCurrentOperation() & (Operation.SCALE_X | Operation.SCALE_Y | Operation.SCALE_Z)) != 0)) {
                                this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseGuizmoOperation(false, false, true));
                            }
                        }

                        this.processTranslations(currentSelectedObject);
                        ImGui.treePop();
                    }
                    ImGui.separator();
                }
                Collection<Tag<? extends TagItem>> tags = currentSelectedObject.getTagsContainer().getTagCollection();
                if (!tags.isEmpty() && ImGui.treeNodeEx("Tags", ImGuiTreeNodeFlags.DefaultOpen)) {
                    for (Tag<? extends TagItem> tag : tags) {
                        this.processTag(currentSelectedObject.getTagsContainer(), tag);
                    }
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
    }

    public void spawnInWorld(WBenchObject wBenchObject, @Nullable Vector3f pos) {
        CullingAABB cullingAABB = wBenchObject.getCullingData();
        if (cullingAABB != null) {
            if (pos != null) {
                wBenchObject.setPosition(pos);
            } else {
                ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
                Vector3f posToSpawn = camera.getCamPosition();
                posToSpawn.add(JGemsHelper.math().calcLookVector(camera.getCamRotation()).mul((diagonal / 2.0f) + 1.0f));
                wBenchObject.setPosition(posToSpawn);
            }
            this.getEditorInterface().addObjectInWorld(wBenchObject);
        }
    }

    private void processTag(TagsContainer tagsContainer, Tag<? extends TagItem> tag) {
        final TagID tagID = tag.getTagID();
        TagItem tagItem = tag.getTagItem();

        Set<Pair<Integer, SceneObject>> pairSet = new TreeSet<>(Comparator.comparingInt(Pair::getFirst));
        for (Map.Entry<Integer, WBenchObject> entry : this.getEditorInterface().getOpenGLRenderer().getWorld().getIdMap().entrySet()) {
            pairSet.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        if (ImGui.treeNodeEx(tagID.getDescription(), ImGuiTreeNodeFlags.DefaultOpen)) {
            this.showItemDescription(tagID);
            tagItem.ImGuiRendering(tagsContainer, this.getEditorInterface().getCurrentSelectedObject(), tagItem, tagID, pairSet);
            ImGui.treePop();
        } else {
            this.showItemDescription(tagID);
        }
    }

    private void showItemDescription(TagID tagID) {
        if (tagID.getToolTip() != null && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.setTooltip(tagID.getToolTip());
            ImGui.endTooltip();
        }
    }

    private void processTranslations(WBenchObject wBenchObject) {
        if (wBenchObject == null) {
            return;
        }
        int operationFlag = this.getEditorInterface().getCurrentOperation();

        float[] posArrayX = new float[] {wBenchObject.getPosition().x};
        float[] posArrayY = new float[] {wBenchObject.getPosition().y};
        float[] posArrayZ = new float[] {wBenchObject.getPosition().z};

        float[] rotArrayX = new float[] {wBenchObject.getRotation().x};
        float[] rotArrayY = new float[] {wBenchObject.getRotation().y};
        float[] rotArrayZ = new float[] {wBenchObject.getRotation().z};

        float[] sclArrayX = new float[] {wBenchObject.getScaling().x};
        float[] sclArrayY = new float[] {wBenchObject.getScaling().y};
        float[] sclArrayZ = new float[] {wBenchObject.getScaling().z};

        if ((operationFlag & Operation.TRANSLATE_X) != 0) {
            ImGui.dragFloat("X", posArrayX, 0.01f);
        }
        if ((operationFlag & Operation.TRANSLATE_Y) != 0) {
            ImGui.dragFloat("Y", posArrayY, 0.01f);
        }
        if ((operationFlag & Operation.TRANSLATE_Z) != 0) {
            ImGui.dragFloat("Z", posArrayZ, 0.01f);
        }

        if ((operationFlag & Operation.ROTATE_X) != 0) {
            ImGui.sliderAngle("X", rotArrayX, -180.0f, 180.0f);
        }
        if ((operationFlag & Operation.ROTATE_Y) != 0) {
            ImGui.sliderAngle("Y", rotArrayY, -180.0f, 180.0f);
        }
        if ((operationFlag & Operation.ROTATE_Z) != 0) {
            ImGui.sliderAngle("Z", rotArrayZ, -180.0f, 180.0f);
        }

        if ((operationFlag & Operation.SCALE_X) != 0) {
            ImGui.dragFloat("X", sclArrayX, 0.01f, 0.001f, 1000.0f);
        }
        if ((operationFlag & Operation.SCALE_Y) != 0) {
            ImGui.dragFloat("Y", sclArrayY, 0.01f, 0.001f, 1000.0f);
        }
        if ((operationFlag & Operation.SCALE_Z) != 0) {
            ImGui.dragFloat("Z", sclArrayZ, 0.01f, 0.001f, 1000.0f);
        }

        wBenchObject.setPosition(new Vector3f(posArrayX[0], posArrayY[0], posArrayZ[0]));
        wBenchObject.setRotation(new Vector3f(rotArrayX[0], rotArrayY[0], rotArrayZ[0]));
        wBenchObject.setScaling(new Vector3f(sclArrayX[0], sclArrayY[0], sclArrayZ[0]));
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
