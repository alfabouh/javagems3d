package ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2i;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.TBoxMapSys;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.ObjectTable;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.TBoxScene;

public class EditorContent implements ImGuiContent {
    public static boolean isFocusedOnSceneFrame;

    public boolean fogCheck;
    public float[] fogClr = new float[3];
    public float[] fogDensity = new float[1];
    public float[] sunClr = new float[3];
    public float[] sunBrightness = new float[1];
    public boolean skyCoveredByFog;
    public final ImString mapName = new ImString();
    public final ImInt objectInt = new ImInt();

    public final String[] objectIds;
    public final IMapObject[] iMapObjects;

    public EditorContent() {
        ObjectTable objectTable = TBoxMapSys.INSTANCE.getObjectTable();

        int i = 0;
        this.objectIds = new String[objectTable.getObjects().size()];
        this.iMapObjects = new IMapObject[objectTable.getObjects().size()];

        for (IMapObject mapObject : objectTable.getObjects()) {
            this.iMapObjects[i] = mapObject;
            this.objectIds[i] = this.iMapObjects[i].objectId();
            i += 1;
        }
    }

    private void renderScene(Vector2i dim) {
        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        isFocusedOnSceneFrame = ImGui.isWindowFocused();
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f);
        ImGui.setWindowPos(0, 20);
        ImGui.image(TBoxScene.sceneFbo.getTexturePrograms().get(0).getTextureId(), dim.x * 0.5f - 16, dim.y * 0.5f - 40, 0.0f, 1.0f, 1.0f, 0.0f);
        ImGui.end();
    }

    private void renderEdit(Vector2i dim) {
        ImGui.begin("Edit", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y - 20);
        ImGui.setWindowPos(dim.x * 0.5f, 20);

        ImGui.beginChild("actions");
        ImGui.combo("Object", this.objectInt, this.objectIds);
        ImGui.endChild();

        ImGui.end();
    }

    private void renderProperties(Vector2i dim) {
        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(dim.x * 0.5f, dim.y * 0.5f - 19);
        ImGui.setWindowPos(0, dim.y * 0.5f + 20);

        ImGui.inputText("Map Name", this.mapName);
        ImGui.newLine();
        if (ImGui.collapsingHeader("Map Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("sett1");
            if (ImGui.collapsingHeader("Sky")) {
                ImGui.sliderFloat("Sun Brightness", this.sunBrightness, 0.0f, 1.0f);
                ImGui.colorEdit3("Sun Color", this.sunClr);
            }
            if (ImGui.collapsingHeader("Fog")) {
                if (ImGui.checkbox("Enable Fog", this.fogCheck)) {
                    this.fogCheck = !this.fogCheck;
                }
                ImGui.sliderFloat("Fog Density", this.fogDensity, 0.0f, 1.0f);
                ImGui.colorEdit3("Fog Color", this.fogClr);
                if (ImGui.checkbox("Sky Covered By Fog", this.skyCoveredByFog)) {
                    this.skyCoveredByFog = !this.skyCoveredByFog;
                }
            }
            ImGui.endChild();
        }

        ImGui.end();
    }

    private void renderMenuBar() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {
            }

            if (ImGui.menuItem("Open")) {
            }

            if (ImGui.menuItem("Save")) {
            }

            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }

    public void drawContent(Vector2i dim, double partialTicks) {
        this.renderScene(dim);
        this.renderProperties(dim);
        this.renderEdit(dim);
        this.renderMenuBar();
    }
}
