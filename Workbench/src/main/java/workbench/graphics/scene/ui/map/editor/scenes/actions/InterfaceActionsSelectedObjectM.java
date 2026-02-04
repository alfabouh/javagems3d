package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.items.TagCheckBoolean;
import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.map.MapEditorInterface;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InterfaceActionsSelectedObjectM {
    private final MapEditorInterface mapEditorInterface;
    private int currentOperation;

    public InterfaceActionsSelectedObjectM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.reset(null);
    }

    public void reset(@Nullable WBenchObject currentSelectedObject) {
        if (currentSelectedObject == null) {
            this.currentOperation = Operation.TRANSLATE;
        } else {
            this.setCurrentOperation(this.chooseDefaultGuizmoOperation(currentSelectedObject));
        }
    }

    public int chooseDefaultGuizmoOperation(WBenchObject currentSelectedObject) {
        int f1 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        if (f1 != 0) {
            return f1;
        }
        int f2 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        if (f2 != 0) {
            return f2;
        }
        return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
    }

    public int chooseGuizmoOperation(WBenchObject currentSelectedObject, boolean translation, boolean rotation, boolean scaling) {
        if (translation) {
            return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        }
        if (rotation) {
            return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        }
        return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
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

    private void processTranslations(WBenchObject wBenchObject) {
        if (wBenchObject == null) {
            return;
        }
        int operationFlag = this.getCurrentOperation();

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

    private void showItemDescription(TagID tagID) {
        if (tagID.getToolTip() != null && !tagID.getToolTip().isEmpty() && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.setTooltip(tagID.getToolTip());
            ImGui.endTooltip();
        }
    }

    private void showTags(@NotNull WBenchObject selectedObject) {
        Set<Pair<Integer, SceneObject>> wolrdObjectsToViewInList = new TreeSet<>(Comparator.comparingInt(Pair::getFirst));
        for (Map.Entry<Integer, WBenchObject> entry : this.mapEditorInterface.getOpenGLRenderer().getWorld().getIdMap().entrySet()) {
            wolrdObjectsToViewInList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        final TagsContainer tagsContainer = selectedObject.getTagsContainer();
        if (!tagsContainer.isEmpty()) {
            for (Tag<? extends TagItem> tag : tagsContainer.getTagCollection()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
                if (ImGui.collapsingHeader(tag.getTagID().getId(), ImGuiTreeNodeFlags.DefaultOpen)) {
                    ImGui.popStyleColor();
                    //ImGui.beginChild("##InsideTag_" + tag.getTagID().getId(), ImGui.getColumnWidth(), 60, true, ImGuiWindowFlags.HorizontalScrollbar);
                    {
                        tag.getTagItem().ImGuiRendering(tagsContainer, selectedObject, tag.getTagItem(), tag.getTagID(), wolrdObjectsToViewInList);
                        this.showItemDescription(tag.getTagID());
                    }
                    //ImGui.endChild();
                } else {
                    this.showItemDescription(tag.getTagID());
                    ImGui.popStyleColor();
                }
                ImGui.spacing();
            }
        } else {
            ImGui.text("<empty>");
        }
    }

    public void render() {
        WBenchObject selectedObject = this.mapEditorInterface.getCurrentSelectedObject();
        if (selectedObject != null && ImGui.collapsingHeader("Object: [" + selectedObject.getId() + "] " + selectedObject.getObjectId().getNameId(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##insideResourceObjPreview", ImGui.getColumnWidth(), 400, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            ImGui.bulletText("Transformation");
            ImGui.popStyleColor();
            ImGui.indent();
            if (selectedObject.hasTranslationConstraints()) {
                int objectFlagTranslate = selectedObject.getTranslationConstraints().getPositionConstraints().getFlag();
                int objectFlagRotate = selectedObject.getTranslationConstraints().getRotationConstraints().getFlag();
                int objectFlagScaling = selectedObject.getTranslationConstraints().getScalingConstraints().getFlag();

                if (objectFlagTranslate != 0) {
                    if (ImGui.radioButton("Translation", (this.getCurrentOperation() & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
                        this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject, true, false, false));
                    }
                }

                if (objectFlagRotate != 0) {
                    if (ImGui.radioButton("Rotation", (this.getCurrentOperation() & (Operation.ROTATE_X | Operation.ROTATE_Y | Operation.ROTATE_Z)) != 0)) {
                        this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject, false, true, false));
                    }
                }

                if (objectFlagScaling != 0) {
                    if (ImGui.radioButton("Scaling", (this.getCurrentOperation() & (Operation.SCALE_X | Operation.SCALE_Y | Operation.SCALE_Z)) != 0)) {
                        this.setCurrentOperation(this.chooseGuizmoOperation(selectedObject, false, false, true));
                    }
                }

                this.processTranslations(selectedObject);
            } else {
                ImGui.text("<empty>");
            }
            ImGui.unindent();
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            ImGui.bulletText("Tags");
            ImGui.popStyleColor();
            ImGui.indent();
            this.showTags(selectedObject);
            ImGui.unindent();
            ImGui.endChild();
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
