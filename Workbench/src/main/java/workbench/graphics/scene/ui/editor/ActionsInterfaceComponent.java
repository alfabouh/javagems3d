package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.help.JGemsMathHelper;
import javagems3d.help.JGemsUtils;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.base.Colors;
import javagems3d.mapping.tags.items.*;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
            ImGui.beginDisabled(ImGui.getIO().getKeyCtrl());
            if (ImGui.sliderFloat("Distance", scaling, 0.1f, 10.0f)) {
                this.getEditorInterface().setPreviewDistance(scaling[0]);
            }
            ImGui.endDisabled();
            if (ImGui.button("Generate")) {
                WBenchObject wBenchObject = this.getEditorInterface().getCurrentSelectedTemplate().createObject(this.getEditorInterface().getOpenGLRenderer().getWorld());
                wBenchObject.setId(this.getEditorInterface().getOpenGLRenderer().getWorld().getSceneObjects().size());

                CullingAABB cullingAABB = wBenchObject.getCullingData();
                if (cullingAABB != null) {
                    ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                    float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
                    Vector3f posToSpawn = camera.getCamPosition();
                    posToSpawn.add(JGemsUtils.calcLookVector(camera.getCamRotation()).mul(diagonal + 1.0f));
                    wBenchObject.setPosition(posToSpawn);
                    this.getEditorInterface().getOpenGLRenderer().getWorld().addObjectInWorld(wBenchObject);
                }
            }
            ImGui.separator();
        }

        WBenchObject currentSelectedObject = this.getEditorInterface().getCurrentSelectedObject();
        if (currentSelectedObject != null) {
            if (ImGui.collapsingHeader("Object: " + currentSelectedObject.getName() + "(" + currentSelectedObject.getId() + ")", ImGuiTreeNodeFlags.DefaultOpen)) {
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

                        this.processTranslations();
                        ImGui.treePop();
                    }
                    ImGui.separator();
                }
                Collection<Tag<? extends TagItem>> tags = currentSelectedObject.getTagsContainer().getTagCollection();
                if (!tags.isEmpty() && ImGui.treeNodeEx("Tags", ImGuiTreeNodeFlags.DefaultOpen)) {
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
        final TagID tagID = tag.getTagID();
        TagItem tagItem = tag.getTagItem();

        if (ImGui.treeNodeEx(tagID.getDescription(), ImGuiTreeNodeFlags.DefaultOpen)) {
            if (tagItem instanceof TagRadioBoolean) {
                TagRadioBoolean tagRadioBoolean = (TagRadioBoolean) tagItem;
                TagRadioBoolean.Info[] infos = tagRadioBoolean.getValues();
                for (int i = 0; i < infos.length; i++) {
                    boolean selected = infos[i].isFlag();
                    if (ImGui.radioButton(infos[i].getName(), selected)) {
                        for (int j = 0; j < infos.length; j++) {
                            infos[j].setFlag(j == i);
                        }
                    }
                }
            }

            if (tagItem instanceof TagCheckBoolean) {
                TagCheckBoolean tagCheckBoolean = (TagCheckBoolean) tagItem;
                boolean value = tagCheckBoolean.isFlag();
                if (ImGui.checkbox("##" + tagID.getDescription(), value)) {
                    tagCheckBoolean.setFlag(!value);
                }
            }

            if (tagItem instanceof TagColor) {
                TagColor tagColor = (TagColor) tagItem;
                Vector4f color = tagColor.getColorVector();
                Colors colorMode = tagColor.getColorMode();
                if (colorMode == Colors.COLOR3) {
                    float[] colorArray = new float[]{color.x, color.y, color.z};
                    if (ImGui.colorEdit3("##" + tagID.getDescription(), colorArray)) {
                        tagColor.setColor(new Vector4f(colorArray[0], colorArray[1], colorArray[2], color.w));
                    }
                } else {
                    float[] colorArray = new float[]{color.x, color.y, color.z, color.w};
                    if (ImGui.colorEdit4("##" + tagID.getDescription(), colorArray)) {
                        tagColor.setColor(new Vector4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
                    }
                }
            }

            if (tagItem instanceof TagFloat) {
                TagFloat tagFloat = (TagFloat) tagItem;
                float[] value = new float[] {tagFloat.getValue()};
                if (ImGui.dragFloat("##" + tagID.getDescription(), value, 0.1f, tagFloat.getMin(), tagFloat.getMax())) {
                    tagFloat.setValue(JGemsMathHelper.clamp(value[0], tagFloat.getMin(), tagFloat.getMax()));
                }
            }

            if (tagItem instanceof TagInt) {
                TagInt tagInt = (TagInt) tagItem;
                int[] value = new int[] {tagInt.getValue()};
                if (ImGui.dragInt("##" + tagID.getDescription(), value, 1, tagInt.getMin(), tagInt.getMax())) {
                    tagInt.setValue(JGemsMathHelper.clamp(value[0], tagInt.getMin(), tagInt.getMax()));
                }
            }

            if (tagItem instanceof TagString) {
                TagString tagString = (TagString) tagItem;
                ImString value = new ImString(tagString.getText());
                if (ImGui.inputText("##" + tagID.getDescription(), value)) {
                    tagString.setText(value.get());
                }
            }

            ImGui.treePop();
        }

        if (tagID.getToolTip() != null && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.setTooltip(tagID.getToolTip());
            ImGui.endTooltip();
        }
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
