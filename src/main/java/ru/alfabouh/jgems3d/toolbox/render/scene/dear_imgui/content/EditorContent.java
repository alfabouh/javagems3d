package ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiComboFlags;
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
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ObjectType;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.Attribute;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeContainer;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeType;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;
import ru.alfabouh.jgems3d.toolbox.controller.binding.TBoxBindingManager;
import ru.alfabouh.jgems3d.toolbox.render.scene.TBoxScene;
import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.screen.TBoxScreen;

import java.io.File;
import java.util.Map;

public class EditorContent implements ImGuiContent {
    public static boolean sceneShowLight = true;
    public static boolean sceneShowFog = true;
    public static boolean isFocusedOnSceneFrame;

    private final TBoxScene tBoxScene;

    private final Vector2d sceneFrameCenter;
    private final Vector2d sceneFrameMin;
    private final Vector2d sceneFrameMax;

    public final ImInt objectSelectionInt = new ImInt();

    public String currentSelectedToPlaceID;

    public float[] previewBorders = new float[] {1.0f};

    public TBoxScene3DObject prevSelectedItem;
    public TBoxScene3DObject currentSelectedObject;

    public EditorContent(TBoxScene tBoxScene) {
        this.sceneFrameCenter = new Vector2d(0.0d);
        this.sceneFrameMin = new Vector2d(0.0d);
        this.sceneFrameMax = new Vector2d(0.0d);

        this.tBoxScene = tBoxScene;
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
            ImGui.columns(2, "SEdit", true);

            ObjectTable boxMapSys = TBoxMapSys.INSTANCE.getObjectTable();

            String[] physicNames = boxMapSys.getObjects().entrySet().stream().filter(e -> e.getValue().objectType().equals(ObjectType.PHYSICS_OBJECT)).map(Map.Entry::getKey).toArray(String[]::new);
            String[] modelNames = boxMapSys.getObjects().entrySet().stream().filter(e -> e.getValue().objectType().equals(ObjectType.MODEL_OBJECT)).map(Map.Entry::getKey).toArray(String[]::new);
            String[] specialNames = boxMapSys.getObjects().entrySet().stream().filter(e -> e.getValue().objectType().equals(ObjectType.SPECIAL_OBJECT)).map(Map.Entry::getKey).toArray(String[]::new);

            ImGui.showDemoWindow();
            ImGui.newLine();

            if (ImGui.beginCombo("Physics Objects", "Preview1", ImGuiComboFlags.NoPreview)) {
                for (String s : physicNames) {
                    if (ImGui.selectable(s)) {
                        this.currentSelectedToPlaceID = s;
                    }
                }
                ImGui.endCombo();
            }

            if (ImGui.beginCombo("Models", "Preview2", ImGuiComboFlags.NoPreview)) {
                for (String s : modelNames) {
                    if (ImGui.selectable(s)) {
                        this.currentSelectedToPlaceID = s;
                    }
                }
                ImGui.endCombo();
            }

            if (ImGui.beginCombo("Markers", "Preview3", ImGuiComboFlags.NoPreview)) {
                for (String s : specialNames) {
                    if (ImGui.selectable(s)) {
                        this.currentSelectedToPlaceID = s;
                    }
                }
                ImGui.endCombo();
            }

            if (this.currentSelectedToPlaceID != null) {
                ImGui.newLine();
                ImGui.text("Selected: " + this.currentSelectedToPlaceID);
                if (ImGui.button("Place object")) {
                    this.getTBoxScene().placeObjectFromGUI(this, this.currentSelectedToPlaceID);
                    this.removeSelection();
                }
                if (TBoxScene.canRenderOnPreview(this.currentSelectedToPlaceID)) {
                    ImGui.nextColumn();
                    ImGui.text("Object preview");
                    ImGui.image(TBoxScene.previewItemFbo.getTexturePrograms().get(0).getTextureId(), 240, 240, 0.0f, 1.0f, 1.0f, 0.0f);
                    ImGui.newLine();
                    ImGui.sliderFloat("Preview Scale", this.previewBorders, 0.1f, 10.0f);
                }
            }

            ImGui.columns(1);
        }

        ImGui.newLine();

        this.prevSelectedItem = this.currentSelectedObject;
        this.currentSelectedObject = null;
        if (ImGui.collapsingHeader("Scene Objects", ImGuiTreeNodeFlags.DefaultOpen)) {
            String[] strings = this.getTBoxScene().getSceneContainer().getSceneObjects().stream().map(TBoxScene3DObject::toString).toArray(String[]::new);
            ImGui.columns(2, "SObj", true);

            ImGui.beginChild("list");
            ImGui.listBox("Object List" + "(" + strings.length + ")", this.objectSelectionInt, strings, 20);
            if (ImGui.button("Remove Selection")) {
                this.removeSelection();
            }
            ImGui.endChild();

            if (!this.getTBoxScene().getSceneContainer().getSceneObjects().isEmpty()) {
                Object[] objects = this.getTBoxScene().getSceneContainer().getSceneObjects().toArray();
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

                ImGui.text("Selected:" + this.currentSelectedObject);
                if (ImGui.button("Destroy Object")) {
                    this.getTBoxScene().removeObject(this.currentSelectedObject);
                    if (this.objectSelectionInt.get() >= this.getTBoxScene().getSceneContainer().getSceneObjects().size()) {
                        this.objectSelectionInt.set(this.getTBoxScene().getSceneContainer().getSceneObjects().size() - 1);
                    }
                }

                ImGui.newLine();
                ImGui.text("Transform:");

                TBoxBindingManager tBoxBindingManager = TBoxControllerDispatcher.bindingManager();

                float speed = tBoxBindingManager.keyCtrl.isPressed() ? 0.001f : 0.01f;
                if (this.currentSelectedObject.canEditPosition()) {
                    float[] positionX = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getPosition().x};
                    float[] positionY = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getPosition().y};
                    float[] positionZ = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getPosition().z};

                    if (ImGui.dragFloat("Position X", positionX, speed) || ImGui.dragFloat("Position Y", positionY, speed) || ImGui.dragFloat("Position Z", positionZ, speed)) {
                        format3D.setPosition(new Vector3d(positionX[0], positionY[0], positionZ[0]));
                        this.currentSelectedObject.getLocalCollision().calcAABB(format3D);
                    }
                }
                if (this.currentSelectedObject.canEditRotation()) {
                    ImGui.separator();
                    float[] rotationX = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getRotation().x};
                    float[] rotationY = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getRotation().y};
                    float[] rotationZ = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getRotation().z};

                    if (ImGui.dragFloat("Rotation X", rotationX, speed) || ImGui.dragFloat("Rotation Y", rotationY, speed) || ImGui.dragFloat("Rotation Z", rotationZ, speed)) {
                        format3D.setRotation(new Vector3d(rotationX[0], rotationY[0], rotationZ[0]));
                        this.currentSelectedObject.getLocalCollision().calcAABB(format3D);
                    }
                }
                if (this.currentSelectedObject.canEditScaling()) {
                    ImGui.separator();
                    float[] scalingX = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getScaling().x};
                    float[] scalingY = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getScaling().y};
                    float[] scalingZ = new float[] {(float) this.currentSelectedObject.getModel().getFormat().getScaling().z};

                    if (ImGui.dragFloat("Scaling X", scalingX, speed) || ImGui.dragFloat("Scaling Y", scalingY, speed) || ImGui.dragFloat("Scaling Z", scalingZ, speed)) {
                        format3D.setScaling(new Vector3d(scalingX[0], scalingY[0], scalingZ[0]));
                        this.currentSelectedObject.getLocalCollision().calcAABB(format3D);
                    }
                }

                ImGui.newLine();
                ImGui.beginChild("obj_prop");
                if (this.currentSelectedObject.hasAttributes()) {
                    AttributeContainer attributeContainer = this.currentSelectedObject.getAttributeContainer();
                    for (Attribute<?> attribute : attributeContainer.getAttributeSet().values()) {
                        if (attribute.getAttributeType().equals(AttributeType.COLOR3)) {
                            Object value = attribute.getValue();
                            if (value instanceof Vector3d) {
                                Vector3d vector3d = (Vector3d) value;
                                float[] f1 = new float[] {(float) vector3d.x, (float) vector3d.y, (float) vector3d.z};
                                if (ImGui.colorEdit3(attribute.getDescription(), f1)) {
                                    vector3d.set(f1);
                                }
                            }
                        }
                    }
                }
                ImGui.endChild();
            }
            ImGui.endChild();

            ImGui.columns(1);
        }

        ImGui.end();
    }

    public boolean trySelectObject(TBoxScene3DObject boxScene3DObject) {
        Object[] objects = this.getTBoxScene().getSceneContainer().getSceneObjects().toArray();
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

    private void renderProperties(Vector2i dim) {
        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f - 19);
        ImGui.setWindowPos(0, dim.y * 0.5f + 20);

        MapProperties mapProperties = this.getTBoxScene().getMapProperties();
        ImString string = new ImString(mapProperties.getMapName());
        if (ImGui.inputText("Map Name", string)) {
            mapProperties.setMapName(string.get());
        }
        if (ImGui.collapsingHeader("Map Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("sett1");
            if (ImGui.collapsingHeader("Sky")) {
                float[] f1 = new float[] {mapProperties.getSkyProp().getSunBrightness()};
                if (ImGui.sliderFloat("Sun Brightness", f1, 0.0f, 1.0f)) {
                    mapProperties.getSkyProp().setSunBrightness(f1[0]);
                }
                float[] c1 = new float[] {(float) mapProperties.getSkyProp().getSunColor().x, (float) mapProperties.getSkyProp().getSunColor().y, (float) mapProperties.getSkyProp().getSunColor().z};
                if (ImGui.colorEdit3("Sun Color", c1)) {
                    mapProperties.getSkyProp().setSunColor(new Vector3d(c1));
                }
                Vector3d v3 = new Vector3d(0.0f, 1.0f, 0.0f);
                float[] a1 = new float[] {(float) mapProperties.getSkyProp().getSunPos().x};
                float[] a3 = new float[] {(float) mapProperties.getSkyProp().getSunPos().z};
                if (ImGui.sliderAngle("Sun Angle X", a1) || ImGui.sliderAngle("Sun Angle Z", a3)) {
                    mapProperties.getSkyProp().setSunPos(new Vector3d(a1[0], (float) mapProperties.getSkyProp().getSunPos().y, a3[0]));
                }
            }
            if (ImGui.collapsingHeader("Fog")) {
                if (ImGui.checkbox("Enable Fog", mapProperties.getFogProp().isFogEnabled())) {
                    mapProperties.getFogProp().setFogEnabled(!mapProperties.getFogProp().isFogEnabled());
                }

                ImGui.beginDisabled(!mapProperties.getFogProp().isFogEnabled());
                float [] f1 = new float[] {mapProperties.getFogProp().getFogDensity()};
                if (ImGui.sliderFloat("Fog Density", f1, 0.0f, 1.0f)) {
                    mapProperties.getFogProp().setFogDensity(f1[0]);
                }
                float[] c1 = new float[] {(float) mapProperties.getFogProp().getFogColor().x, (float) mapProperties.getFogProp().getFogColor().y, (float) mapProperties.getFogProp().getFogColor().z};
                ImGui.colorEdit3("Fog Color", c1);
                if (ImGui.checkbox("Sky Covered By Fog", mapProperties.getFogProp().isSkyCoveredByFog())) {
                    mapProperties.getFogProp().setSkyCoveredByFog(!mapProperties.getFogProp().isSkyCoveredByFog());
                }
                ImGui.endDisabled();
            }
            ImGui.endChild();
        }

        ImGui.end();
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
                if (this.getTBoxScene().getMapProperties().getMapName().isEmpty()) {
                    LoggingManager.showWindowInfo("Enter map name!");
                } else {
                    this.getTBoxScene().prepareMapToSave(null);
                }
            }

            if (ImGui.menuItem("Load Map")) {
                this.getTBoxScene().tryLoadMap(null);
            }

            String recentStrSave = ToolBox.get().getTBoxSettings().recentPathSave.getValue();
            if (!recentStrSave.isEmpty()) {
                if (ImGui.menuItem("Save Recent: " + recentStrSave)) {
                    this.getTBoxScene().prepareMapToSave(new File(recentStrSave));
                }
            }
            String recentStrOpen = ToolBox.get().getTBoxSettings().recentPathOpen.getValue();
            if (!recentStrOpen.isEmpty()) {
                if (ImGui.menuItem("Load Recent: " + recentStrOpen)) {
                    this.getTBoxScene().tryLoadMap( new File(recentStrOpen));
                }
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Scene")) {
            if (ImGui.checkbox("Light", this.sceneShowLight)) {
                EditorContent.sceneShowLight = !EditorContent.sceneShowLight;
            }
            if (ImGui.checkbox("Fog", this.sceneShowFog)) {
                EditorContent.sceneShowFog = !EditorContent.sceneShowFog;
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
                if (this.objectSelectionInt.get() >= this.getTBoxScene().getSceneContainer().getSceneObjects().size()) {
                    this.objectSelectionInt.set(this.getTBoxScene().getSceneContainer().getSceneObjects().size() - 1);
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
