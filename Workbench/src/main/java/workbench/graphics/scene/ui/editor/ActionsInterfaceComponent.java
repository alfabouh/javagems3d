package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.items.TagItem;
import org.joml.Vector3f;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.EditorInterface;

import java.util.Collection;

public class ActionsInterfaceComponent {
    private final EditorInterface editorInterface;

    public ActionsInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {

    }

    public void actionsContent() {
        if (this.getEditorInterface().getCurrentSelectedTemplate() != null && ImGui.collapsingHeader("Preview", ImGuiTreeNodeFlags.DefaultOpen)) {
            float available = Math.min(ImGui.getContentRegionAvailX(), 256);
            ImGui.text("Preview: " + this.getEditorInterface().getCurrentSelectedTemplate().getId());
            ImGui.image(this.getEditorInterface().getScenePreview().getTextureIDByIndex(0), available, available, 0.0f, 1.0f, 1.0f, 0.0f);

            float[] scaling = new float[]{this.getEditorInterface().getPreviewDistance()};
            if (ImGui.sliderFloat("Distance", scaling, 1.0f, 10.0f)) {
                this.getEditorInterface().setPreviewDistance(scaling[0]);
            }
            if (ImGui.button("Generate")) {
                WBenchObject wBenchObject = new WBenchObject(this.getEditorInterface().getOpenGLRenderer().getWorld(), this.getEditorInterface().getCurrentSelectedTemplate());
                wBenchObject.setId(this.getEditorInterface().getOpenGLRenderer().getWorld().getSceneObjects().size());
                this.getEditorInterface().getOpenGLRenderer().getWorld().addObjectInWorld(wBenchObject);
            }
            ImGui.separator();
        }

        if (this.getEditorInterface().getCurrentSelectedObject() != null) {
            if (ImGui.collapsingHeader("Object", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.treePush();
                if (this.getEditorInterface().getCurrentSelectedObject().hasTranslationConstraints() && ImGui.treeNode("Transformation")) {
                    int objectFlagTranslate = this.getEditorInterface().getCurrentSelectedObject().getTranslationConstraints().getPositionConstraints().getFlag();
                    int objectFlagRotate = this.getEditorInterface().getCurrentSelectedObject().getTranslationConstraints().getRotationConstraints().getFlag();
                    int objectFlagScaling = this.getEditorInterface().getCurrentSelectedObject().getTranslationConstraints().getScalingConstraints().getFlag();

                    if (objectFlagTranslate != 0) {
                        if (ImGui.radioButton("Translate", (this.getEditorInterface().getCurrentOperation() & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
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

                    this.processTranslations();
                    ImGui.treePop();
                }
                Collection<Tag<? extends TagItem>> tags = this.getEditorInterface().getCurrentSelectedObject().getTagsContainer().getTagCollection();
                if (!tags.isEmpty() && ImGui.treeNode("Tags")) {
                    for (Tag<? extends TagItem> tag : tags) {
                        this.processTag(tag);
                    }
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
    }

    private void processTag(Tag<? extends TagItem> tag) {
        ImGui.text(tag.getTagID().getDescription());

        ImGui.separator();
    }

    private void processTranslations() {
        int operationFlag = this.getEditorInterface().getCurrentOperation();

        float[] posArrayX = new float[] {this.getEditorInterface().getCurrentSelectedObject().getPosition().x};
        float[] posArrayY = new float[] {this.getEditorInterface().getCurrentSelectedObject().getPosition().y};
        float[] posArrayZ = new float[] {this.getEditorInterface().getCurrentSelectedObject().getPosition().z};

        float[] rotArrayX = new float[] {this.getEditorInterface().getCurrentSelectedObject().getRotation().x};
        float[] rotArrayY = new float[] {this.getEditorInterface().getCurrentSelectedObject().getRotation().y};
        float[] rotArrayZ = new float[] {this.getEditorInterface().getCurrentSelectedObject().getRotation().z};

        float[] sclArrayX = new float[] {this.getEditorInterface().getCurrentSelectedObject().getScaling().x};
        float[] sclArrayY = new float[] {this.getEditorInterface().getCurrentSelectedObject().getScaling().y};
        float[] sclArrayZ = new float[] {this.getEditorInterface().getCurrentSelectedObject().getScaling().z};

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

        this.getEditorInterface().getCurrentSelectedObject().setPosition(new Vector3f(posArrayX[0], posArrayY[0], posArrayZ[0]));
        this.getEditorInterface().getCurrentSelectedObject().setRotation(new Vector3f(rotArrayX[0], rotArrayY[0], rotArrayZ[0]));
        this.getEditorInterface().getCurrentSelectedObject().setScaling(new Vector3f(sclArrayX[0], sclArrayY[0], sclArrayZ[0]));
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
