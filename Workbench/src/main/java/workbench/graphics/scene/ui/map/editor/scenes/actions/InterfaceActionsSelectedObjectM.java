package workbench.graphics.scene.ui.map.editor.scenes.actions;

import api.application.workbench.resources.data.wbench.WBenchData;
import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import imgui.ImGui;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.*;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagFloat;
import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.external.mapping.tags.items.TagVector;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4i;
import workbench.WBench;
import workbench.controller.binding.WBenchBindingManager;
import workbench.graphics.objects.WBenchMarkerObject;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewWorldObjectG;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.SceneInterfaceComponentM;

import java.util.*;
import java.util.function.Function;

public class InterfaceActionsSelectedObjectM {
    private boolean lockObjectAngles;
    private final MapEditorInterface mapEditorInterface;
    private int currentOperation;
    public static Vector4i scalingFlags = new Vector4i();

    public InterfaceActionsSelectedObjectM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.reset(null);
    }

    public void reset(@Nullable Set<WBenchObject<?>> currentSelectedObjects) {
        if (currentSelectedObjects != null && currentSelectedObjects.size() == 1) {
            WBenchObject<?> wBenchObject = currentSelectedObjects.stream().findFirst().get();
            this.setCurrentOperation(this.chooseDefaultGuizmoOperation(wBenchObject));
            return;
        }
        this.currentOperation = Operation.TRANSLATE;
    }

    private int chooseDefaultGuizmoOperation(WBenchObject<?> currentSelectedObject) {
        int f1 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().positionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        if (f1 != 0) {
            return f1;
        }
        int f2 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().rotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        if (f2 != 0) {
            return f2;
        }
        return this.getOperationMask(currentSelectedObject.getTranslationConstraints().scalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
    }

    private int chooseGuizmoOperation(TranslationConstraints translationConstraints, boolean translation, boolean rotation, boolean scaling) {
        int i = 0;
        if (translation) {
            i |= this.getOperationMask(translationConstraints.positionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        }
        if (rotation) {
            i |= this.getOperationMask(translationConstraints.rotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        }
        if (scaling) {
            i |= this.getOperationMask(translationConstraints.scalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
        }
        return i;
    }

    private int getOperationMask(int flag, int xOp, int yOp, int zOp) {
        if (flag == 0) {
            return 0;
        }
        int result = 0;
        if ((flag & 1) != 0) {
            result |= xOp;
        }
        if ((flag & 2) != 0) {
            result |= yOp;
        }
        if ((flag & 4) != 0) {
            result |= zOp;
        }
        return result;
    }

    public boolean isLockObjectAngles() {
        WBenchBindingManager wBenchBindingManager = (WBenchBindingManager) WBench.get().getControllerDispatcher().getCurrentController().getBindingManager();
        return wBenchBindingManager.keyAlt.isPressed() || this.lockObjectAngles;
    }

    public boolean isScaleTranslation() {
        return this.mapEditorInterface.getSelectedObjectsManager().isScaleTranslator();
    }

    public boolean isOneDirScaling() {
        WBenchBindingManager wBenchBindingManager = (WBenchBindingManager) WBench.get().getControllerDispatcher().getCurrentController().getBindingManager();
        return wBenchBindingManager.keyAlt.isPressed() || this.mapEditorInterface.getSelectedObjectsManager().isOneDirScaling();
    }

    private boolean rotationQuickButtons(String id, float[] arr) {
        if (ImGui.button("+45##rotationQuickButtons_1" + id)) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            arr[0] += (float) (Math.PI / 4.0f);
            return true;
        }
        ImGui.sameLine();
        if (ImGui.button("-45##rotationQuickButtons_2" + id)) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            arr[0] -= (float) (Math.PI / 4.0f);
            return true;
        }
        ImGui.sameLine();
        if (ImGui.button("+90##rotationQuickButtons_3" + id)) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            arr[0] += (float) (Math.PI / 2.0f);
            return true;
        }
        ImGui.sameLine();
        if (ImGui.button("-90##rotationQuickButtons_4" + id)) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            arr[0] -= (float) (Math.PI / 2.0f);
            return true;
        }
        return false;
    }

    private void processTranslations(String id, Collection<WBenchObject<?>> wBenchObjects, int operationFlag, boolean textInfoIfCannotBeTransformed) {
        boolean wideBorder = (operationFlag & Operation.ROTATE) != 0;
        ImGui.beginChild(id, ImGui.getColumnWidth(), textInfoIfCannotBeTransformed ? 330 : (wideBorder ? 190 : 140), true);
        if (!wBenchObjects.isEmpty()) {
            final boolean many = wBenchObjects.size() > 1;
            WBenchObject<?> getFirst = wBenchObjects.stream().findFirst().get();
            Vector3f Pos = many ? this.mapEditorInterface.getSelectedObjectsManager().getGroupPosition() : getFirst.getPosition();
            Vector3f Rot = many ? this.mapEditorInterface.getSelectedObjectsManager().getGroupRotation() : getFirst.getRotation();
            Vector3f Scale = many ? this.mapEditorInterface.getSelectedObjectsManager().getGroupScaling() : getFirst.getScaling();

            boolean captTranslate = false;
            boolean captRotate = false;
            boolean captScale = false;

            float[] posArrayX = new float[]{Pos.x};
            float[] posArrayY = new float[]{Pos.y};
            float[] posArrayZ = new float[]{Pos.z};

            float[] rotArrayX = new float[]{Rot.x};
            float[] rotArrayY = new float[]{Rot.y};
            float[] rotArrayZ = new float[]{Rot.z};

            float[] sclArrayX = new float[]{Scale.x};
            float[] sclArrayY = new float[]{Scale.y};
            float[] sclArrayZ = new float[]{Scale.z};

            boolean showTranslX = (operationFlag & Operation.TRANSLATE_X) != 0;
            boolean showTranslY = (operationFlag & Operation.TRANSLATE_Y) != 0;
            boolean showTranslZ = (operationFlag & Operation.TRANSLATE_Z) != 0;

            boolean showRotX = (operationFlag & Operation.ROTATE_X) != 0;
            boolean showRotY = (operationFlag & Operation.ROTATE_Y) != 0;
            boolean showRotZ = (operationFlag & Operation.ROTATE_Z) != 0;

            boolean showScaleX = (operationFlag & Operation.SCALE_X) != 0;
            boolean showScaleY = (operationFlag & Operation.SCALE_Y) != 0;
            boolean showScaleZ = (operationFlag & Operation.SCALE_Z) != 0;

            if (showTranslX || showTranslY || showTranslZ) {
                ImGui.bulletText("Translation");
                ImGui.beginDisabled(!showTranslX);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_TRANSLATE_X", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("X##posArrayX", posArrayX, 0.01f)) {
                            captTranslate = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showTranslY);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_TRANSLATE_Y", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("Y##posArrayY", posArrayY, 0.01f)) {
                            captTranslate = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showTranslZ);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffff4444);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_TRANSLATE_Z", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("Z##posArrayZ", posArrayZ, 0.01f)) {
                            captTranslate = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();
            } else if (textInfoIfCannotBeTransformed) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8c8c);
                ImGui.bulletText("Can't be Translated");
                ImGui.popStyleColor();
            }

            boolean forceDesyncAngles = false;
            if (showRotX || showRotY || showRotZ) {
                ImGui.bulletText("Rotation");
                ImGui.beginDisabled(!showRotX);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_ROTATE_X", WBenchUITrackingHelper::INSTANCE)) {
                        float[] rotDeg = new float[1];
                        rotDeg[0] = (float) Math.toDegrees(rotArrayX[0]);
                        if (ImGui.dragFloat("X##rotArrayX", rotDeg, 0.1f)) {
                            if (ImGui.getIO().getWantTextInput()) {
                                forceDesyncAngles = true;
                            }
                            captRotate = true;
                            rotArrayX[0] = (float) Math.toRadians(rotDeg[0]);
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    if (this.rotationQuickButtons("X", rotArrayX)) {
                        captRotate = true;
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showRotY);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_ROTATE_Y", WBenchUITrackingHelper::INSTANCE)) {
                        float[] rotDeg = new float[1];
                        rotDeg[0] = (float) Math.toDegrees(rotArrayY[0]);
                        if (ImGui.dragFloat("Y##rotArrayY", rotDeg, 0.1f)) {
                            if (ImGui.getIO().getWantTextInput()) {
                                forceDesyncAngles = true;
                            }
                            captRotate = true;
                            rotArrayY[0] = (float) Math.toRadians(rotDeg[0]);
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    if (this.rotationQuickButtons("Y", rotArrayY)) {
                        captRotate = true;
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showRotZ);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffff4444);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_ROTATE_Z", WBenchUITrackingHelper::INSTANCE)) {
                        float[] rotDeg = new float[1];
                        rotDeg[0] = (float) Math.toDegrees(rotArrayZ[0]);
                        if (ImGui.dragFloat("Z##rotArrayZ", rotDeg, 0.1f)) {
                            if (ImGui.getIO().getWantTextInput()) {
                                forceDesyncAngles = true;
                            }
                            captRotate = true;
                            rotArrayZ[0] = (float) Math.toRadians(rotDeg[0]);
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                    }
                    if (this.rotationQuickButtons("Z", rotArrayZ)) {
                        captRotate = true;
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();
                if (!many) {
                    if (ImGui.checkbox("Desync Angle-Rotation", this.isLockObjectAngles())) {
                        this.lockObjectAngles = !this.isLockObjectAngles();
                    }
                }
            } else if (textInfoIfCannotBeTransformed) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8c8c);
                ImGui.bulletText("Can't be Rotated");
                ImGui.popStyleColor();
            }

            boolean scalingActive = false;
            if (showScaleX || showScaleY || showScaleZ) {
                ImGui.bulletText("Scaling");
                ImGui.beginDisabled(!showScaleX);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff0000ff);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_SCALE_X", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("X##sclArrayX", sclArrayX, 0.01f, -1000.0f, 1000.0f)) {
                            captScale = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                        if (ImGui.isItemActive()) {
                            scalingActive = true;
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showScaleY);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_SCALE_Y", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("Y##sclArrayY", sclArrayY, 0.01f, -1000.0f, 1000.0f)) {
                            captScale = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                        if (ImGui.isItemActive()) {
                            scalingActive = true;
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();

                ImGui.beginDisabled(!showScaleZ);
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0xffff4444);
                    try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_operationFlag_SCALE_Z", WBenchUITrackingHelper::INSTANCE)) {
                        if (ImGui.dragFloat("Z##sclArrayZ", sclArrayZ, 0.01f, -1000.0f, 1000.0f)) {
                            captScale = true;
                            if (uiTrackingHelper.saveSnapshot()) {
                            }
                        }
                        if (ImGui.isItemActive()) {
                            scalingActive = true;
                        }
                    }
                    ImGui.popStyleColor();
                }
                ImGui.endDisabled();
                if (many) {
                    if (ImGui.checkbox("Scale-Translation", this.isScaleTranslation())) {
                        this.mapEditorInterface.getSelectedObjectsManager().setScaleTranslator(!this.isScaleTranslation());
                    }
                }
                if (!many || !this.isScaleTranslation()) {
                    if (ImGui.checkbox("One-Dir Scaling", this.isOneDirScaling())) {
                        this.mapEditorInterface.getSelectedObjectsManager().setOneDirScaling(!this.isOneDirScaling());
                    }
                }
            } else if (textInfoIfCannotBeTransformed) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8c8c);
                ImGui.bulletText("Can't be Scaled");
                ImGui.popStyleColor();
            }

            final Vector3f newPos = new Vector3f(posArrayX[0], posArrayY[0], posArrayZ[0]);
            final Vector3f newRot = new Vector3f(rotArrayX[0], rotArrayY[0], rotArrayZ[0]);
            final Vector3f newScale = new Vector3f(sclArrayX[0], sclArrayY[0], sclArrayZ[0]);

            if (!scalingActive) {
                InterfaceActionsSelectedObjectM.scalingFlags.w = 0;
            }

            if (many) {
                if (captTranslate) {
                    this.mapEditorInterface.getSelectedObjectsManager().setGroupPosition(newPos);
                }
                if (captRotate) {
                    this.mapEditorInterface.getSelectedObjectsManager().setGroupRotation(newRot);
                }
                if (captScale) {
                    Vector3f oldScale = new Vector3f(this.mapEditorInterface.getSelectedObjectsManager().getGroupScaling());
                    final Vector3f delta = new Vector3f(oldScale).sub(newScale);
                    this.mapEditorInterface.getSelectedObjectsManager().setGroupScaling(new Vector3f(Math.max(newScale.x, 0.001f), Math.max(newScale.y, 0.001f), Math.max(newScale.z, 0.001f)));
                }
            } else {
                if (captTranslate) {
                    getFirst.setPosition(newPos);
                }
                if (captRotate) {
                    if (this.lockObjectAngles || forceDesyncAngles) {
                        getFirst.setRotation(newRot);
                    } else {
                        Vector3f angle = new Vector3f(getFirst.getRotation()).sub(newRot);
                        Matrix4f model = TransformUtils.getModelMatrix(getFirst.getModel().getPose());
                        Matrix4f worldRot = new Matrix4f().identity().rotateXYZ(angle);
                        worldRot.mul(model, model);
                        Quaternionf q = model.getUnnormalizedRotation(new Quaternionf());
                        Vector3f euler = q.getEulerAnglesXYZ(new Vector3f()).negate();
                        getFirst.setRotation(euler);
                    }
                }
                if (captScale) {
                    if (this.isOneDirScaling()) {
                        SceneInterfaceComponentM.oneDirScaling(Mode.LOCAL, null, InterfaceActionsSelectedObjectM.scalingFlags, getFirst.getRotation(), getFirst.getPosition(), getFirst, newScale, getFirst.getScaling());
                    } else {
                        getFirst.setScaling(new Vector3f(Math.max(newScale.x, 0.001f), Math.max(newScale.y, 0.001f), Math.max(newScale.z, 0.001f)));
                    }
                }
            }
        }
        ImGui.endChild();
    }

    private void showItemDescription(TagID tagID) {
        if (tagID.getToolTip() != null && !tagID.getToolTip().isEmpty() && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.setTooltip(tagID.getToolTip());
            ImGui.endTooltip();
        }
    }

    private void showTags(@NotNull WBenchObject<?> selectedObject) {
        Set<Pair<Integer, SceneObject>> worldObjectsToViewInList = new TreeSet<>(Comparator.comparingInt(Pair::first));
        for (Map.Entry<Integer, WBenchObject<?>> entry : this.mapEditorInterface.getOpenGLRenderer().getWorld().getIdMap().entrySet()) {
            if (!entry.getValue().objectType().equals(WBenchData.ObjectType.MARKER)) {
                worldObjectsToViewInList.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }

        final TagsContainer tagsContainer = selectedObject.getTagsContainer();
        if (!tagsContainer.isEmpty()) {
            UITrackingHelper.startTrackingRowOfUITrackers();
            for (Tag<? extends TagItem> tag : tagsContainer.getTagCollection()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                if (ImGui.collapsingHeader(tag.getTagID().getNormalName() == null ? tag.getTagID().getId() : tag.getTagID().getNormalName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                    this.showItemDescription(tag.getTagID());
                    ImGui.popStyleColor();
                    //ImGui.beginChild("##InsideTag_" + tag.getTagID().getId(), ImGui.getColumnWidth(), 60, true, ImGuiWindowFlags.HorizontalScrollbar);
                    {
                        tag.getTagItem().ImGuiRendering(tagsContainer, selectedObject, tag.getTagItem(), tag.getTagID(), worldObjectsToViewInList, WBenchUITrackingHelper::INSTANCE,
                                MapEditorInterface.gameResourcesSet());
                        ImGui.separator();
                        //this.showItemDescription(tag.getTagID());
                    }
                    //ImGui.endChild();
                } else {
                    this.showItemDescription(tag.getTagID());
                    ImGui.popStyleColor();
                }
                ImGui.spacing();
            }
            if (UITrackingHelper.stopTrackingRowOfUITrackersAndGetResult()) {
                selectedObject.onTagsContainerAnyTagModified(tagsContainer, selectedObject);
            }
        } else {
            ImGui.text("<empty>");
        }
    }

    private void forSelectedObject(WBenchObject<?> selectedObject, boolean manyObjects) {
        if (selectedObject != null && ImGui.collapsingHeader("Object: [" + selectedObject.getListID() + "] " + selectedObject.getObjectNameId().nameId(), manyObjects ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
            ImGui.pushID(this.getClass().getSimpleName() + "_" + selectedObject.getListID());
            ImGui.beginChild("##insideResourceObjPreview", ImGui.getColumnWidth(), Math.min(400 + selectedObject.getTagsContainer().tags().size() * 20, 1000), true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            ImGui.bulletText("Transformation");
            ImGui.popStyleColor();
          //  ImGui.indent();
            if (manyObjects) {
                if (selectedObject.hasTranslationConstraints()) {
                    int objectFlagTranslate = selectedObject.getTranslationConstraints().positionConstraints().getFlag();
                    int objectFlagRotate = selectedObject.getTranslationConstraints().rotationConstraints().getFlag();
                    int objectFlagScaling = selectedObject.getTranslationConstraints().scalingConstraints().getFlag();

                    if (objectFlagTranslate != 0) {
                        if (ImGui.radioButton("Translation", (this.getCurrentOperation() & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
                            WBenchUITrackingHelper.instantlyTrackAndPush();
                            this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject.getTranslationConstraints(), true, false, false));
                            this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                            this.mapEditorInterface.getSelectedObjectsManager().reset();
                        }
                    }

                    if (objectFlagRotate != 0) {
                        if (ImGui.radioButton("Rotation", (this.getCurrentOperation() & (Operation.ROTATE_X | Operation.ROTATE_Y | Operation.ROTATE_Z)) != 0)) {
                            WBenchUITrackingHelper.instantlyTrackAndPush();
                            this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject.getTranslationConstraints(), false, true, false));
                            this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                            this.mapEditorInterface.getSelectedObjectsManager().reset();
                        }
                    }

                    if (objectFlagScaling != 0) {
                        if (ImGui.radioButton("Scaling", (this.getCurrentOperation() & (Operation.SCALE_X | Operation.SCALE_Y | Operation.SCALE_Z)) != 0)) {
                            WBenchUITrackingHelper.instantlyTrackAndPush();
                            this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject.getTranslationConstraints(), false, false, true));
                            this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                            this.mapEditorInterface.getSelectedObjectsManager().reset();
                        }
                    }

                    this.processTranslations("##transl_sngobj", Collections.singletonList(selectedObject), this.getCurrentOperation(), false);
                } else {
                    ImGui.text("<empty>");
                }
            } else {
                this.processTranslations("##transl_sngobj", Collections.singletonList(selectedObject), this.chooseGuizmoOperation(selectedObject.getTranslationConstraints(), true, true, true), true);
            }
           // ImGui.unindent();
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            ImGui.bulletText("Tags");
            ImGui.popStyleColor();
            {
               // ImGui.indent();
                ImGui.beginChild("##ObjTags", ImGui.getColumnWidth(), 0, true, ImGuiWindowFlags.HorizontalScrollbar);
                this.showTags(selectedObject);
                ImGui.endChild();
               // ImGui.unindent();
            }
            if (!(selectedObject instanceof WBenchMarkerObject)) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
                ImGui.bulletText("Rendering");
                ImGui.popStyleColor();
                {
                    ImGui.beginChild("##RenProps", ImGui.getColumnWidth(), 220, true, ImGuiWindowFlags.HorizontalScrollbar);
                    if (selectedObject.getRenderAttributes().getProperties() == null) {
                        selectedObject.getRenderAttributes().setRenderProperties(new WBenchRenderProperties());
                        Log.get().debug("Null renderProp. Created");
                    }
                   // ImGui.indent();
                    ScenePreviewWorldObjectG.renderPropertiesEdit(selectedObject.getRenderAttributes().getProperties(), true);
                  //  ImGui.unindent();
                    ImGui.endChild();
                }
            }
            ImGui.endChild();
            ImGui.popID();
        }
    }

    private boolean anyMatch(Set<WBenchObject<?>> wBenchObjects, Function<WBenchObject<?>, AxisConstraints> axisConstraints, int flag) {
        return wBenchObjects.stream().anyMatch(e -> ((axisConstraints.apply(e).getFlag() & flag) != 0));
    }

    public void render() {
        if (this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().size() <= 1) {
            this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().forEach(e -> this.forSelectedObject(e, true));
        } else {
            if (ImGui.collapsingHeader("Multiple Objects (" + this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().size() + ")", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.indent();
                final boolean anyTranslateX = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().positionConstraints(), AxisConstraints.AXIS_X.getFlag());
                final boolean anyTranslateY = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().positionConstraints(), AxisConstraints.AXIS_Y.getFlag());
                final boolean anyTranslateZ = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().positionConstraints(), AxisConstraints.AXIS_Z.getFlag());

                final boolean anyRotateX = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().rotationConstraints(), AxisConstraints.AXIS_X.getFlag());
                final boolean anyRotateY = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().rotationConstraints(), AxisConstraints.AXIS_Y.getFlag());
                final boolean anyRotateZ = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().rotationConstraints(), AxisConstraints.AXIS_Z.getFlag());

                final boolean anyScaleX = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().scalingConstraints(), AxisConstraints.AXIS_X.getFlag());
                final boolean anyScaleY = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().scalingConstraints(), AxisConstraints.AXIS_Y.getFlag());
                final boolean anyScaleZ = this.anyMatch(this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), (e) -> e.getTranslationConstraints().scalingConstraints(), AxisConstraints.AXIS_Z.getFlag());

                final TranslationConstraints translationConstraints = new TranslationConstraints(
                        AxisConstraints.GET(anyTranslateX, anyTranslateY, anyTranslateZ),
                        AxisConstraints.GET(anyRotateX, anyRotateY, anyRotateZ),
                        AxisConstraints.GET(anyScaleX, anyScaleY, anyScaleZ));

                ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
                ImGui.bulletText("Group Data");
                ImGui.popStyleColor();
                if (ImGui.radioButton("Translation", (this.getCurrentOperation() & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    this.setCurrentOperation(this.chooseGuizmoOperation(translationConstraints, true, false, false));
                    this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                    this.mapEditorInterface.getSelectedObjectsManager().reset();
                }

                if (ImGui.radioButton("Rotation", (this.getCurrentOperation() & (Operation.ROTATE_X | Operation.ROTATE_Y | Operation.ROTATE_Z)) != 0)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    this.setCurrentOperation(this.chooseGuizmoOperation(translationConstraints, false, true, false));
                    this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                    this.mapEditorInterface.getSelectedObjectsManager().reset();
                }

                if (ImGui.radioButton("Scaling", (this.getCurrentOperation() & (Operation.SCALE_X | Operation.SCALE_Y | Operation.SCALE_Z)) != 0)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    this.setCurrentOperation(this.chooseGuizmoOperation(translationConstraints, false, false, true));
                    this.mapEditorInterface.getSelectedObjectsManager().setDefaultMetaMods();
                    this.mapEditorInterface.getSelectedObjectsManager().reset();
                }

                this.processTranslations("##transl_mltobj", this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects(), this.getCurrentOperation(), false);

                ImGui.spacing();
                this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().forEach(e -> this.forSelectedObject(e, false));
                ImGui.unindent();
            }
        }
    }

    public int getCurrentOperation() {
        return this.currentOperation;
    }

    public InterfaceActionsSelectedObjectM setCurrentOperation(int currentOperation) {
        this.currentOperation = currentOperation;
        return this;
    }
}
