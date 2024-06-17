package ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.proxy.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.TBoxMapSys;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.ObjectTable;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;
import ru.alfabouh.jgems3d.toolbox.controller.binding.TBoxBindingManager;
import ru.alfabouh.jgems3d.toolbox.render.scene.TBoxScene;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.screen.TBoxScreen;

import java.io.File;

public class EditorContent implements ImGuiContent {
    private final Vector2d sceneFrameCenter;
    private final Vector2d sceneFrameMin;
    private final Vector2d sceneFrameMax;

    private final TBoxScene tBoxScene;

    public static boolean isFocusedOnSceneFrame;

    public boolean fogCheck;
    public float[] sunPosX = new float[] {0.f};
    public float[] sunPosY = new float[] {0.f};
    public float[] sunPosZ = new float[] {0.f};
    public float[] fogClr = new float[] {1.f, 1.f, 1.f};
    public float[] fogDensity = new float[1];
    public float[] sunClr = new float[] {1.f, 1.f, 1.f};
    public float[] sunBrightness = new float[] {1.f};
    public boolean skyCoveredByFog;
    public final ImInt objectInt = new ImInt();
    public final ImInt objectSelectionInt = new ImInt();

    public final String[] objectIds;

    public float[] previewBorders = new float[] {1.0f};

    private final float[] positionX = new float[1];
    private final float[] positionY = new float[1];
    private final float[] positionZ = new float[1];
    private final float[] rotationX = new float[1];
    private final float[] rotationY = new float[1];
    private final float[] rotationZ = new float[1];
    private final float[] scalingX = new float[1];
    private final float[] scalingY = new float[1];
    private final float[] scalingZ = new float[1];

    public ImString mapName = new ImString();

    public TBoxScene3DObject prevSelectedItem;
    public TBoxScene3DObject currentSelectedObject;

    public EditorContent(TBoxScene tBoxScene) {
        this.sceneFrameCenter = new Vector2d(0.0d);
        this.sceneFrameMin = new Vector2d(0.0d);
        this.sceneFrameMax = new Vector2d(0.0d);

        this.tBoxScene = tBoxScene;
        ObjectTable objectTable = TBoxMapSys.INSTANCE.getObjectTable();

        this.objectIds = objectTable.getObjects().keySet().toArray(new String[0]);
    }

    private void renderScene(Vector2i dim) {
        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        isFocusedOnSceneFrame = ImGui.isWindowFocused();
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f);
        ImGui.setWindowPos(0, 20);
        ImGui.image(TBoxScene.sceneFbo.getTexturePrograms().get(0).getTextureId(), dim.x * 0.5f - 16, dim.y * 0.5f - 40, 0.0f, 1.0f, 1.0f, 0.0f);

        if (ImGui.isItemClicked(1)) {
            this.getTBoxScene().tryGrabObject(this);
        }

        ImVec2 min = new ImVec2();
        ImVec2 max = new ImVec2();

        ImGui.getItemRectMin(min);
        ImGui.getItemRectMax(max);
        this.getSceneFrameMin().set(min.x, min.y);
        this.getSceneFrameMax().set(max.x, max.y);
        this.getSceneFrameCenter().set(min.x + max.x / 2.0f, min.y + max.y / 2.0f);

        Vector3d pos = this.getTBoxScene().getCamera().getCamPosition();
        String camPos = String.format("Pos: %.3f %.3f %.3f", pos.x, pos.y, pos.z);
        String fpsText = "FPS:" + TBoxScreen.FPS;
        ImGui.setCursorPos(20, 30);
        ImGui.text(fpsText);
        ImGui.setCursorPos(20, 50);
        ImGui.text(camPos);
        ImGui.end();
    }

    private void renderEdit(Vector2i dim) {
        //ImGui.showDemoWindow();

        ImGui.begin("Edit", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y - 20);
        ImGui.setWindowPos(dim.x * 0.5f, 20);

        if (ImGui.collapsingHeader("Create Object", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.combo("Object", this.objectInt, this.objectIds);
            ImGui.newLine();
            ImGui.text("Object preview");
            ImGui.image(TBoxScene.previewItemFbo.getTexturePrograms().get(0).getTextureId(), 240, 240, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.sliderFloat("Preview Scale", this.previewBorders, 0.1f, 10.0f);

            if (ImGui.button("Place object")) {
                String nameId = this.objectIds[this.objectInt.get()];
                this.getTBoxScene().placeObjectFromGUI(this, nameId);
                this.removeSelection();
            }
        }

        ImGui.newLine();

        this.prevSelectedItem = this.currentSelectedObject;
        this.currentSelectedObject = null;
        if (ImGui.collapsingHeader("Scene Objects", ImGuiTreeNodeFlags.DefaultOpen)) {
            String[] strings = this.getTBoxScene().getSceneObjects().stream().map(TBoxScene3DObject::toString).toArray(String[]::new);
            ImGui.columns(2, "SObj", true);

            ImGui.beginChild("list");
            ImGui.listBox("Object List" + "(" + strings.length + ")", this.objectSelectionInt, strings, 20);
            if (ImGui.button("Remove Selection")) {
                this.removeSelection();
            }
            ImGui.endChild();

            if (!this.getTBoxScene().getSceneObjects().isEmpty()) {
                Object[] objects = this.getTBoxScene().getSceneObjects().toArray();
                for (int i = 0; i < objects.length; i++) {
                    TBoxScene3DObject object = (TBoxScene3DObject) objects[i];
                    object.setSelected(false);
                    if (i == this.objectSelectionInt.get()) {
                        this.currentSelectedObject = object;
                        this.currentSelectedObject.setSelected(true);
                    }
                }
            }

            ImGui.nextColumn();
            ImGui.beginChild("list_editor");
            if (this.currentSelectedObject != null) {
                Format3D format3D = this.currentSelectedObject.getModel().getFormat();

                if (this.prevSelectedItem == null || this.currentSelectedObject != this.prevSelectedItem) {
                    this.resetTransform(format3D);
                }

                ImGui.text("Selected:" + this.currentSelectedObject);
                if (ImGui.button("Destroy Object")) {
                    this.getTBoxScene().removeObject(this.currentSelectedObject);
                    if (this.objectSelectionInt.get() >= this.getTBoxScene().getSceneObjects().size()) {
                        this.objectSelectionInt.set(this.getTBoxScene().getSceneObjects().size() - 1);
                    }
                }

                ImGui.newLine();
                ImGui.text("Transform:");
                boolean flag = false;

                TBoxBindingManager tBoxBindingManager = TBoxControllerDispatcher.bindingManager();

                float speed = tBoxBindingManager.keyDown.isPressed() ? 0.1f : 0.01f;
                if (ImGui.dragFloat("PositionX", this.positionX, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("PositionY", this.positionY, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("PositionZ", this.positionZ, speed)) {
                    flag = true;
                }
                ImGui.separator();
                if (ImGui.dragFloat("RotationX", this.rotationX, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("RotationY", this.rotationY, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("RotationZ", this.rotationZ,  speed)) {
                    flag = true;
                }
                ImGui.separator();
                if (ImGui.dragFloat("ScalingX", this.scalingX, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("ScalingY", this.scalingY, speed)) {
                    flag = true;
                }
                if (ImGui.dragFloat("ScalingZ", this.scalingZ, speed)) {
                    flag = true;
                }

                if (flag) {
                    this.performSettingsOnObject(this.currentSelectedObject, format3D);
                }

                ImGui.newLine();
                ImGui.beginChild("obj_prop");
                if (ImGui.collapsingHeader("Object Properties")) {
                    if (ImGui.checkbox("Physics Object", this.currentSelectedObject.getMapObjectProperties().isPhysicsObject())) {
                        this.currentSelectedObject.getMapObjectProperties().setPhysicsObject(!this.currentSelectedObject.getMapObjectProperties().isPhysicsObject());
                    }
                    ImGui.beginDisabled(!this.currentSelectedObject.getMapObjectProperties().isPhysicsObject());
                    if (ImGui.checkbox("Is Static", this.currentSelectedObject.getMapObjectProperties().isStatic())) {
                        this.currentSelectedObject.getMapObjectProperties().setStatic(!this.currentSelectedObject.getMapObjectProperties().isStatic());
                    }
                    if (ImGui.checkbox("Use Mesh Collision", this.currentSelectedObject.getMapObjectProperties().isGenerateMeshCollision())) {
                        this.currentSelectedObject.getMapObjectProperties().setGenerateMeshCollision(!this.currentSelectedObject.getMapObjectProperties().isGenerateMeshCollision());
                    }
                    ImGui.endDisabled();
                }
                ImGui.endChild();
            }
            ImGui.endChild();

            ImGui.columns(1);
        }

        ImGui.end();
    }

    public boolean trySelectObject(TBoxScene3DObject boxScene3DObject) {
        Object[] objects = this.getTBoxScene().getSceneObjects().toArray();
        for (int i = 0; i < objects.length; i++) {
            TBoxScene3DObject object = (TBoxScene3DObject) objects[i];
            if (object.equals(boxScene3DObject)) {
                this.objectSelectionInt.set(i);
                return true;
            }
        }
        return false;
    }

    public void removeSelection() {
        this.currentSelectedObject = null;
        this.objectSelectionInt.set(-1);
    }

    private void performSettingsOnObject(TBoxScene3DObject boxScene3DObject, Format3D format3D) {
        format3D.setPosition(new Vector3d(this.positionX[0], this.positionY[0], this.positionZ[0]));
        format3D.setRotation(new Vector3d(Math.toRadians(this.rotationX[0]), Math.toRadians(this.rotationY[0]), Math.toRadians(this.rotationZ[0])));
        format3D.setScale(new Vector3d(this.scalingX[0], this.scalingY[0], this.scalingZ[0]));
        boxScene3DObject.getLocalCollision().calcAABB(format3D);
    }

    private void resetTransform(Format3D format3D) {
        this.positionX[0] = ((float) format3D.getPosition().x);
        this.positionY[0] = ((float) format3D.getPosition().y);
        this.positionZ[0] = ((float) format3D.getPosition().z);

        this.rotationX[0] = ((float) Math.toDegrees(format3D.getRotation().x));
        this.rotationY[0] = ((float) Math.toDegrees(format3D.getRotation().y));
        this.rotationZ[0] = ((float) Math.toDegrees(format3D.getRotation().z));

        this.scalingX[0] = ((float) format3D.getScale().x);
        this.scalingY[0] = ((float) format3D.getScale().y);
        this.scalingZ[0] = ((float) format3D.getScale().z);
    }

    private void renderProperties(Vector2i dim) {
        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f - 19);
        ImGui.setWindowPos(0, dim.y * 0.5f + 20);

        ImGui.inputText("Map Name", this.mapName);
        if (ImGui.collapsingHeader("Map Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("sett1");
            if (ImGui.collapsingHeader("Sky")) {
                ImGui.sliderFloat("Sun Brightness", this.sunBrightness, 0.0f, 1.0f);
                ImGui.colorEdit3("Sun Color", this.sunClr);
                ImGui.sliderAngle("Sun Angle X", this.sunPosX);
                ImGui.sliderAngle("Sun Angle Y", this.sunPosY);
                ImGui.sliderAngle("Sun Angle Z", this.sunPosZ);
            }
            if (ImGui.collapsingHeader("Fog")) {
                if (ImGui.checkbox("Enable Fog", this.fogCheck)) {
                    this.fogCheck = !this.fogCheck;
                }

                ImGui.beginDisabled(!this.fogCheck);
                ImGui.sliderFloat("Fog Density", this.fogDensity, 0.0f, 1.0f);
                ImGui.colorEdit3("Fog Color", this.fogClr);
                if (ImGui.checkbox("Sky Covered By Fog", this.skyCoveredByFog)) {
                    this.skyCoveredByFog = !this.skyCoveredByFog;
                }
                ImGui.endDisabled();
            }
            ImGui.endChild();
        }

        ImGui.end();
    }

    public Vector3f getSunPos() {
        return new Vector3f(0.0f, 1.0f, 0.0f).rotateX(this.sunPosX[0]).rotateY(this.sunPosY[0]).rotateZ(this.sunPosZ[0]).normalize();
    }

    private void renderMenuBar() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {
                if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                    this.getTBoxScene().resetEditor();
                }
            }

            if (ImGui.menuItem("Save Map")) {
                if (this.mapName.isEmpty()) {
                    LoggingManager.showWindowInfo("Enter map name!");
                } else {
                    this.getTBoxScene().prepareMapToSave(this, null);
                }
            }

            if (ImGui.menuItem("Load Map")) {
                this.getTBoxScene().tryLoadMap(this, null);
            }

            String recentStrSave = ToolBox.get().getTBoxSettings().recentPathSave.getValue();
            if (!recentStrSave.isEmpty()) {
                if (ImGui.menuItem("Save Recent: " + recentStrSave)) {
                    this.getTBoxScene().prepareMapToSave(this, new File(recentStrSave));
                }
            }
            String recentStrOpen = ToolBox.get().getTBoxSettings().recentPathOpen.getValue();
            if (!recentStrOpen.isEmpty()) {
                if (ImGui.menuItem("Load Recent: " + recentStrOpen)) {
                    this.getTBoxScene().tryLoadMap(this, new File(recentStrOpen));
                }
            }
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }

    public void drawContent(Vector2i dim, double partialTicks) {
        this.renderProperties(dim);
        this.renderEdit(dim);
        this.renderScene(dim);
        this.renderMenuBar();

        if (this.currentSelectedObject != null) {
            TBoxBindingManager tBoxBindingManager = TBoxControllerDispatcher.bindingManager();
            if (tBoxBindingManager.keyDelete.isClicked()) {
                this.getTBoxScene().removeObject(this.currentSelectedObject);
                this.getTBoxScene().removeObject(this.currentSelectedObject);
                if (this.objectSelectionInt.get() >= this.getTBoxScene().getSceneObjects().size()) {
                    this.objectSelectionInt.set(this.getTBoxScene().getSceneObjects().size() - 1);
                }
            }
        }
    }

    public Vector2d getSceneFrameMax() {
        return this.sceneFrameMax;
    }

    public Vector2d getSceneFrameMin() {
        return this.sceneFrameMin;
    }

    public Vector2d getSceneFrameCenter() {
        return this.sceneFrameCenter;
    }

    public TBoxScene getTBoxScene() {
        return this.tBoxScene;
    }
}
