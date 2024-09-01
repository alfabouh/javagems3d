/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.graphics.opengl.camera.FreeCamera;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.service.collections.Pair;
import logger.managers.LoggingManager;
import toolbox.ToolBox;
import toolbox.controller.TBoxControllerDispatcher;
import toolbox.controller.binding.TBoxBindingManager;
import toolbox.map_sys.save.objects.MapProperties;
import toolbox.map_sys.save.objects.object_attributes.Attribute;
import toolbox.map_sys.save.objects.object_attributes.AttributeTarget;
import toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import toolbox.map_table.ObjectsTable;
import toolbox.map_table.TBoxMapTable;
import toolbox.map_table.object.ObjectCategory;
import toolbox.render.scene.TBoxScene;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.screen.TBoxScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditorContent implements ImGuiContent {
    public static boolean sceneShowLight = true;
    public static boolean sceneShowFog = true;
    public static boolean isFocusedOnSceneFrame;
    public static float alphaDiscard = 0.5f;

    private final TBoxScene tBoxScene;
    private final Vector2f sceneFrameCenter;
    private final Vector2f sceneFrameMin;
    private final Vector2f sceneFrameMax;
    public String currentSelectedToPlaceID;

    public float[] previewBorders = new float[]{1.0f};

    public TBoxAbstractObject currentSelectedObject;

    private boolean sortObjectsByName;
    private boolean sortObjectsById = true;
    private String objectsFilter = "";

    public EditorContent(TBoxScene tBoxScene) {
        this.sceneFrameCenter = new Vector2f(0.0f);
        this.sceneFrameMin = new Vector2f(0.0f);
        this.sceneFrameMax = new Vector2f(0.0f);

        this.tBoxScene = tBoxScene;
    }

    private void renderScene(Vector2i dim) {
        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        EditorContent.isFocusedOnSceneFrame = ImGui.isWindowFocused();
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f);
        ImGui.setWindowPos(0, 20);

        ImGui.image(TBoxScene.sceneFbo.getTexturePrograms().get(0).getTextureId(), dim.x * 0.5f - 16, dim.y * 0.5f - 40, 0.0f, 1.0f, 1.0f, 0.0f);

        if (ImGui.beginPopup("context_obj")) {
            this.showObjectContext(this.getTBoxScene().getSceneContainer().getSceneObjects().size());
            ImGui.endPopup();
        }
        if (ImGui.isItemClicked(1)) {
            TBoxAbstractObject old = this.currentSelectedObject;
            this.getTBoxScene().tryGrabObject(this);
            if (old != null && this.currentSelectedObject == old) {
                ImGui.openPopup("context_obj");
            }
        }

        ImVec2 min = new ImVec2();
        ImVec2 max = new ImVec2();

        ImGui.getItemRectMin(min);
        ImGui.getItemRectMax(max);
        this.getSceneFrameMin().set(min.x, min.y);
        this.getSceneFrameMax().set(max.x, max.y);
        this.getSceneFrameCenter().set(min.x + max.x / 2.0f, min.y + max.y / 2.0f);

        Vector3f pos = this.getTBoxScene().getCamera().getCamPosition();
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
        ImGui.setWindowSize(dim.x * 0.5f, dim.y - 20 - 200);
        ImGui.setWindowPos(dim.x * 0.5f, 20);

        if (ImGui.collapsingHeader("Create Object", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.columns(2, "SEdit", true);

            ObjectsTable boxMapSys = TBoxMapTable.INSTANCE.getObjectTable();

            List<Pair<String, String[]>> set = new ArrayList<>();
            for (ObjectCategory objectCategory : ObjectCategory.values) {
                String[] sA = boxMapSys.getObjects().entrySet().stream().filter(e -> e.getValue().objectType().equals(objectCategory)).map(Map.Entry::getKey).toArray(String[]::new);
                if (sA.length == 0) {
                    continue;
                }
                set.add(new Pair<>(objectCategory.getGroupName(), sA));
            }

            ImGui.newLine();

            int i = 0;
            for (Pair<String, String[]> pairs : set) {
                if (ImGui.beginCombo(pairs.getFirst(), "Preview" + i++, ImGuiComboFlags.NoPreview)) {
                    for (String s : pairs.getSecond()) {
                        if (ImGui.selectable(s)) {
                            this.currentSelectedToPlaceID = s;
                            this.previewBorders[0] = 1.0f;
                        }
                    }
                    ImGui.endCombo();
                }
            }

            if (this.currentSelectedToPlaceID != null) {
                ImGui.newLine();
                ImGui.text("Selected: " + this.currentSelectedToPlaceID);
                if (ImGui.button("Place object")) {
                    this.getTBoxScene().placeObjectFromGUI(this, this.currentSelectedToPlaceID);
                    this.removeSelection();
                }
                ImGui.nextColumn();
                ImGui.text("Object preview");
                ImGui.image(TBoxScene.previewItemFbo.getTexturePrograms().get(0).getTextureId(), 240, 240, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.newLine();
                ImGui.sliderFloat("Preview Scale", this.previewBorders, 0.1f, 10.0f);
            }

            ImGui.columns(1);
        }

        ImGui.newLine();

        if (ImGui.collapsingHeader("Scene Objects", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.columns(2, "SObj", true);

            ImGui.beginChild("list");
            List<TBoxAbstractObject> sortedList = this.getTBoxScene().getSceneContainer().getSceneObjects()
                    .stream()
                    .filter(e -> e.toString().startsWith(this.objectsFilter))
                    .sorted(this.sortObjectsByName ? Comparator.comparing(Object::toString) : Comparator.comparingInt(TBoxAbstractObject::getId))
                    .collect(Collectors.toList());

            ImGui.beginChild("list_cont_c");
            if (ImGui.beginListBox("Objects(" + sortedList.size() + ")", 200, 300)) {
                if (ImGui.beginPopup("context_obj_2")) {
                    this.showObjectContext(this.getTBoxScene().getSceneContainer().getSceneObjects().size());
                    ImGui.endPopup();
                }
                for (TBoxAbstractObject boxAbstractObject : sortedList) {
                    if (ImGui.selectable(boxAbstractObject.toString(), boxAbstractObject.isSelected())) {
                        this.setSingleObjectSelection(boxAbstractObject);
                    }

                    if (ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                        this.setSingleObjectSelection(boxAbstractObject);
                        ImGui.openPopup("context_obj_2");
                    }
                }
                ImGui.endListBox();
            }
            if (ImGui.treeNode("List Settings")) {
                ImString stringF = new ImString();
                stringF.set(this.objectsFilter);
                if (ImGui.inputText("Filter", stringF)) {
                    this.objectsFilter = stringF.get();
                }

                if (ImGui.radioButton("Sort By Name", this.sortObjectsByName)) {
                    this.sortObjectsByName = true;
                    this.sortObjectsById = false;
                }
                if (ImGui.radioButton("Sort By ID", this.sortObjectsById)) {
                    this.sortObjectsByName = false;
                    this.sortObjectsById = true;
                }
                ImGui.treePop();
            }

            if (ImGui.treeNode("Object Actions")) {
                this.showObjectContext(sortedList.size());
                ImGui.treePop();
            }

            ImGui.endChild();
            ImGui.endChild();

            ImGui.nextColumn();
            ImGui.beginChild("list_editor");

            if (ImGui.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                this.removeSelection();
            }

            if (this.currentSelectedObject != null) {
                ImGui.beginChild("obj_prop");

                float speed = ImGui.getIO().getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) ? 0.001f : 0.01f;
                ImGui.text("Selected:" + this.currentSelectedObject);
                ImGui.separator();

                if (this.currentSelectedObject.hasAttributes()) {
                    AttributesContainer attributesContainer = this.currentSelectedObject.getAttributeContainer();
                    for (Attribute<?> attribute : attributesContainer.getAttributeSet().values()) {
                        if (attribute.getAttributeType().equals(AttributeTarget.STATIC_NO_EDIT)) {
                            continue;
                        }
                        Format3D format3D = this.currentSelectedObject.getModel().getFormat();
                        Object value = attribute.getValue();
                        switch (attribute.getAttributeType()) {
                            case POSITION_X:
                            case POSITION_Y:
                            case POSITION_Z:
                            case POSITION_XYZ: {
                                if (value instanceof Vector3f) {
                                    Integer i = attribute.getAttributeType().checkAndGet(Integer.class, 0);
                                    if (i != null) {
                                        Vector3f vector3f = (Vector3f) value;
                                        ImGui.text(attribute.getDescription());
                                        float[] f1 = new float[]{vector3f.x, vector3f.y, vector3f.z};
                                        this.transformPosition(format3D, f1, i, speed);
                                        attribute.setValueWithCast(new Vector3f(f1));
                                    }
                                }
                                break;
                            }
                            case ROTATION_X:
                            case ROTATION_Y:
                            case ROTATION_Z:
                            case ROTATION_XYZ: {
                                if (value instanceof Vector3f) {
                                    Integer i = attribute.getAttributeType().checkAndGet(Integer.class, 0);
                                    if (i != null) {
                                        Vector3f vector3f = (Vector3f) value;
                                        ImGui.text(attribute.getDescription());
                                        float[] f1 = new float[]{vector3f.x, vector3f.y, vector3f.z};
                                        this.transformRotation(format3D, f1, i, speed);
                                        attribute.setValueWithCast(new Vector3f(f1));
                                    }
                                }
                                break;
                            }
                            case SCALING_X:
                            case SCALING_Y:
                            case SCALING_Z:
                            case SCALING_XYZ: {
                                if (value instanceof Vector3f) {
                                    Integer i = attribute.getAttributeType().checkAndGet(Integer.class, 0);
                                    if (i != null) {
                                        Vector3f vector3f = (Vector3f) value;
                                        ImGui.text(attribute.getDescription());
                                        float[] f1 = new float[]{vector3f.x, vector3f.y, vector3f.z};
                                        this.transformScaling(format3D, f1, i, speed);
                                        attribute.setValueWithCast(new Vector3f(f1));
                                    }
                                }
                                break;
                            }
                            case COLOR3: {
                                if (value instanceof Vector3f) {
                                    Vector3f vector3f = (Vector3f) value;
                                    float[] f1 = new float[]{vector3f.x, vector3f.y, vector3f.z};
                                    if (ImGui.colorEdit3(attribute.getDescription(), f1)) {
                                        vector3f.set(f1);
                                    }
                                }
                                break;
                            }
                            case BOOL: {
                                if (value instanceof Boolean) {
                                    Boolean aBoolean = (Boolean) value;
                                    if (ImGui.checkbox(attribute.getDescription(), aBoolean)) {
                                        attribute.setValueWithCast(!aBoolean);
                                    }
                                }
                                break;
                            }
                            case FLOAT_0_50:
                            case FLOAT_0_1: {
                                if (value instanceof Float) {
                                    Float i = attribute.getAttributeType().checkAndGet(Float.class, 0);
                                    Float i2 = attribute.getAttributeType().checkAndGet(Float.class, 1);
                                    if (i != null && i2 != null) {
                                        Float aFloat = (Float) value;
                                        float[] f1 = new float[]{aFloat};
                                        if (ImGui.dragFloat(attribute.getDescription(), f1, 0.01f, i, i2)) {
                                            attribute.setValueWithCast(f1[0]);
                                        }
                                    }
                                }
                                break;
                            }
                            case STRING: {
                                if (value instanceof String) {
                                    String aString = (String) value;
                                    ImString string = new ImString();
                                    string.set(aString);
                                    if (ImGui.inputText(attribute.getDescription(), string)) {
                                        attribute.setValueWithCast(string.get());
                                    }
                                }
                                break;
                            }
                        }
                        ImGui.separator();
                    }
                }
                ImGui.endChild();
            }
            ImGui.endChild();

            ImGui.columns(1);
        }

        ImGui.end();
    }

    private void showObjectContext(int totalObjects) {
        ImGui.text("Selected:" + this.currentSelectedObject);
        if (this.currentSelectedObject != null) {
            if (ImGui.button("Move Camera -> Object")) {
                ((FreeCamera) this.getTBoxScene().getCamera()).setCameraPos(new Vector3f(this.currentSelectedObject.getModel().getFormat().getPosition()).add(0.0f, 1.0f, 0.0f));
            }
            if (ImGui.button("Move Object -> CameraDir")) {
                Vector3f whereLook = this.getTBoxScene().findPointWhereCamLooks(15.0f);

                Vector3f camRot = this.getTBoxScene().getCamera().getCamRotation();
                Vector3f camPos = this.getTBoxScene().getCamera().getCamPosition();

                Vector3f where = new Vector3f(camPos).add(JGemsHelper.UTILS.calcLookVector(camRot).mul(5.0f));

                if (whereLook != null) {
                    where = whereLook;
                }

                this.currentSelectedObject.setPositionWithAttribute(where);
            }

            ImGui.separator();
            if (ImGui.button("Clone")) {
                TBoxAbstractObject object = this.currentSelectedObject.copy();
                this.getTBoxScene().addObject(object);
                this.setSingleObjectSelection(object);
            }
            if (ImGui.button("Delete")) {
                this.getTBoxScene().removeObject(this.currentSelectedObject);
                this.setSingleObjectSelection(null);
            }
        }
    }

    private void transformPosition(Format3D currentObjFormat, float[] values, int transformFlag, float dragSpeed) {
        boolean flag = false;
        float[] positionX = new float[]{0.0f};
        float[] positionY = new float[]{0.0f};
        float[] positionZ = new float[]{0.0f};

        if ((transformFlag & 1) != 0) {
            positionX[0] = values[0];
            if (ImGui.dragFloat("Position X", positionX, dragSpeed)) {
                values[0] = positionX[0];
                flag = true;
            }
        }

        if ((transformFlag & 2) != 0) {
            positionY[0] = values[1];
            if (ImGui.dragFloat("Position Y", positionY, dragSpeed)) {
                values[1] = positionY[0];
                flag = true;
            }
        }

        if ((transformFlag & 4) != 0) {
            positionZ[0] = values[2];
            if (ImGui.dragFloat("Position Z", positionZ, dragSpeed)) {
                values[2] = positionZ[0];
                flag = true;
            }
        }

        if (flag) {
            currentObjFormat.setPosition(new Vector3f(positionX[0], positionY[0], positionZ[0]));
            this.currentSelectedObject.getLocalCollision().calcAABB(currentObjFormat);
        }
    }

    private void transformRotation(Format3D currentObjFormat, float[] values, int transformFlag, float dragSpeed) {
        boolean flag = false;
        float[] rotationX = new float[]{0.0f};
        float[] rotationY = new float[]{0.0f};
        float[] rotationZ = new float[]{0.0f};

        if ((transformFlag & 1) != 0) {
            rotationX[0] = (float) Math.toDegrees(values[0]);
            if (ImGui.dragFloat("Rotation X", rotationX, dragSpeed)) {
                values[0] = (float) Math.toRadians(rotationX[0]);
                flag = true;
            }
        }

        if ((transformFlag & 2) != 0) {
            rotationY[0] = (float) Math.toDegrees(values[1]);
            if (ImGui.dragFloat("Rotation Y", rotationY, dragSpeed)) {
                values[1] = (float) Math.toRadians(rotationY[0]);
                flag = true;
            }
        }

        if ((transformFlag & 4) != 0) {
            rotationZ[0] = (float) Math.toDegrees(values[2]);
            if (ImGui.dragFloat("Rotation Z", rotationZ, dragSpeed)) {
                values[2] = (float) Math.toRadians(rotationZ[0]);
                flag = true;
            }
        }

        if (flag) {
            currentObjFormat.setRotation(new Vector3f(values[0], values[1], values[2]));
            this.currentSelectedObject.getLocalCollision().calcAABB(currentObjFormat);
        }
    }

    private void transformScaling(Format3D currentObjFormat, float[] values, int transformFlag, float dragSpeed) {
        boolean flag = false;
        float[] scalingX = new float[]{0.0f};
        float[] scalingY = new float[]{0.0f};
        float[] scalingZ = new float[]{0.0f};

        if ((transformFlag & 1) != 0) {
            scalingX[0] = values[0];
            if (ImGui.dragFloat("Scaling X", scalingX, dragSpeed)) {
                values[0] = scalingX[0];
                flag = true;
            }
        }

        if ((transformFlag & 2) != 0) {
            scalingY[0] = values[1];
            if (ImGui.dragFloat("Scaling Y", scalingY, dragSpeed)) {
                values[1] = scalingY[0];
                flag = true;
            }
        }

        if ((transformFlag & 4) != 0) {
            scalingZ[0] = values[2];
            if (ImGui.dragFloat("Scaling Z", scalingZ, dragSpeed)) {
                values[2] = scalingZ[0];
                flag = true;
            }
        }

        if (flag) {
            currentObjFormat.setScaling(new Vector3f(scalingX[0], scalingY[0], scalingZ[0]));
            this.currentSelectedObject.getLocalCollision().calcAABB(currentObjFormat);
        }
    }

    public void setSingleObjectSelection(TBoxAbstractObject boxScene3DObject) {
        for (TBoxAbstractObject boxAbstractObject : this.getTBoxScene().getSceneContainer().getSceneObjects()) {
            boxAbstractObject.setSelected(false);
        }
        this.currentSelectedObject = boxScene3DObject;
        if (boxScene3DObject != null) {
            this.currentSelectedObject.setSelected(true);
        }
    }

    public void trySelectObject(TBoxAbstractObject boxScene3DObject) {
        this.setSingleObjectSelection(boxScene3DObject);
    }

    public void removeSelection() {
        this.setSingleObjectSelection(null);
    }

    private void renderProperties(Vector2i dim) {
        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f - 19 - 200);
        ImGui.setWindowPos(0, dim.y * 0.5f + 20);

        MapProperties mapProperties = this.getTBoxScene().getMapProperties();

        ImString imString1 = new ImString();
        imString1.set(mapProperties.getMapName());
        if (ImGui.inputText("Map Name", imString1)) {
            mapProperties.setMapName(imString1.get());
        }

        if (ImGui.collapsingHeader("Map Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("sett1");
            if (ImGui.collapsingHeader("Sky")) {

                ImString imString2 = new ImString();
                imString2.set(mapProperties.getSkyProp().getSkyBoxPath());
                if (ImGui.inputText("Skybox Texture Path", imString2)) {
                    mapProperties.getSkyProp().setSkyBoxPath(imString2.get());
                }

                float[] f1 = new float[]{mapProperties.getSkyProp().getSunBrightness()};
                if (ImGui.dragFloat("Sun Brightness", f1, 0.01f, 0.0f, 1.0f)) {
                    mapProperties.getSkyProp().setSunBrightness(f1[0]);
                }
                float[] c1 = new float[]{mapProperties.getSkyProp().getSunColor().x, mapProperties.getSkyProp().getSunColor().y, mapProperties.getSkyProp().getSunColor().z};
                if (ImGui.colorEdit3("Sun Color", c1)) {
                    mapProperties.getSkyProp().setSunColor(new Vector3f(c1));
                }
                boolean flag = false;
                float[] a1 = new float[]{mapProperties.getSkyProp().getSunPos().x};
                float[] a2 = new float[]{mapProperties.getSkyProp().getSunPos().y};
                float[] a3 = new float[]{mapProperties.getSkyProp().getSunPos().z};
                if (ImGui.dragFloat("Sun Position X", a1, 0.01f, -1.0f, 1.0f)) {
                    flag = true;
                }
                if (ImGui.dragFloat("Sun Position Y", a2, 0.01f, -1.0f, 1.0f)) {
                    flag = true;
                }
                if (ImGui.dragFloat("Sun Position Z", a3, 0.01f, -1.0f, 1.0f)) {
                    flag = true;
                }
                if (flag) {
                    mapProperties.getSkyProp().setSunPos(new Vector3f(a1[0], a2[0], a3[0]));
                }
            }
            if (ImGui.collapsingHeader("Fog")) {
                if (ImGui.checkbox("Enable Fog", mapProperties.getFogProp().isFogEnabled())) {
                    mapProperties.getFogProp().setFogEnabled(!mapProperties.getFogProp().isFogEnabled());
                }

                ImGui.beginDisabled(!mapProperties.getFogProp().isFogEnabled());
                float[] f1 = new float[]{mapProperties.getFogProp().getFogDensity()};
                if (ImGui.dragFloat("Fog Density", f1, 0.001f, 0.0f, 1.0f)) {
                    mapProperties.getFogProp().setFogDensity(f1[0]);
                }
                float[] c1 = new float[]{mapProperties.getFogProp().getFogColor().x, mapProperties.getFogProp().getFogColor().y, mapProperties.getFogProp().getFogColor().z};
                if (ImGui.colorEdit3("Fog Color", c1)) {
                    mapProperties.getFogProp().setFogColor(new Vector3f(c1));
                }
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
                    this.getTBoxScene().tryLoadMap(new File(recentStrOpen));
                }
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Scene")) {
            if (ImGui.button("Teleport In Center")) {
                ((FreeCamera) this.getTBoxScene().getCamera()).setCameraPos(new Vector3f(0.0f));
            }
            if (ImGui.checkbox("Light", EditorContent.sceneShowLight)) {
                EditorContent.sceneShowLight = !EditorContent.sceneShowLight;
            }
            if (ImGui.checkbox("Fog", EditorContent.sceneShowFog)) {
                EditorContent.sceneShowFog = !EditorContent.sceneShowFog;
            }
            float[] f1 = new float[]{EditorContent.alphaDiscard};
            if (ImGui.dragFloat("Alpha Discard", f1, 0.01f, 0.0f, 0.9f)) {
                EditorContent.alphaDiscard = f1[0];
            }
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }

    public void drawContent(Vector2i dim, float partialTicks) {
        this.renderScene(dim);
        this.renderProperties(dim);
        this.renderEdit(dim);
        this.renderMenuBar();

        ImGui.setNextWindowPos(0.0f, (float) dim.y - 200, ImGuiCond.Always);
        ImGui.setNextWindowSize(dim.x, 200);
        ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
        ImGui.begin("Output", ImGuiWindowFlags.AlwaysVerticalScrollbar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoBringToFrontOnFocus);
        String[] textLines = LoggingManager.consoleText().split("\n");
        for (String s : textLines) {
            if (s.isEmpty()) {
                continue;
            }
            ImGui.textWrapped(s);
        }
        if (LoggingManager.markConsoleDirty) {
            ImGui.setScrollHereY(1.0f);
            LoggingManager.markConsoleDirty = false;
        }
        ImGui.end();

        if (this.currentSelectedObject != null) {
            TBoxBindingManager tBoxBindingManager = TBoxControllerDispatcher.bindingManager();
            if (tBoxBindingManager.keyDelete.isClicked()) {
                this.getTBoxScene().removeObject(this.currentSelectedObject);
                this.setSingleObjectSelection(null);
            }
        }
    }

    public Vector2f getSceneFrameMax() {
        return this.sceneFrameMax;
    }

    public Vector2f getSceneFrameMin() {
        return this.sceneFrameMin;
    }

    public Vector2f getSceneFrameCenter() {
        return this.sceneFrameCenter;
    }

    public TBoxScene getTBoxScene() {
        return this.tBoxScene;
    }
}
