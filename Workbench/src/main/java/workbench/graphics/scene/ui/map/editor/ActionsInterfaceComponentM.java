package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.actions.*;

public class ActionsInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final InterfaceActionsSelectedTemplateM interfaceActionsSelectedTemplateM;
    private final InterfaceActionsSelectedObjectM interfaceActionsSelectedObjectM;
    private final InterfaceActionsEnvFogM interfaceActionsEnvFogM;
    private final InterfaceActionsEnvSkyM interfaceActionsEnvSkyM;
    private final InterfaceActionsEnvShadowsM interfaceActionsEnvShadowsM;
    private final InterfaceActionsEnvLightingM interfaceActionsEnvLightingM;

    public ActionsInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.interfaceActionsSelectedObjectM = new InterfaceActionsSelectedObjectM(mapEditorInterface);
        this.interfaceActionsSelectedTemplateM = new InterfaceActionsSelectedTemplateM(mapEditorInterface.getScenePreview(), mapEditorInterface);
        this.interfaceActionsEnvFogM = new InterfaceActionsEnvFogM(mapEditorInterface);
        this.interfaceActionsEnvSkyM = new InterfaceActionsEnvSkyM(mapEditorInterface);
        this.interfaceActionsEnvShadowsM = new InterfaceActionsEnvShadowsM(mapEditorInterface);
        this.interfaceActionsEnvLightingM = new InterfaceActionsEnvLightingM(mapEditorInterface);
        this.clear();
    }

    public void clear() {
        this.interfaceActionsSelectedObjectM.reset(this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects());
        this.interfaceActionsSelectedTemplateM.reset();
        this.resetEnvInterfaces();
    }

    public void resetEnvInterfaces() {
        this.interfaceActionsEnvSkyM.reset();
    }

    public void resetObjectPreview() {
        this.interfaceActionsSelectedObjectM.reset(this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects());
        this.interfaceActionsSelectedTemplateM.reset();
    }

    public void resetTemplatePreview() {
        this.interfaceActionsSelectedTemplateM.reset();
    }

    public void actionsContent() {
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            if (ImGui.collapsingHeader("Global Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.popStyleColor();
                ImGui.indent();
                if (ImGui.collapsingHeader("Fog")) {
                    ImGui.beginChild("##FogContent", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceActionsEnvFogM.render();
                    ImGui.endChild();
                }
                if (ImGui.collapsingHeader("SkyBox")) {
                    ImGui.beginChild("##SkyContent", ImGui.getColumnWidth(), 280, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceActionsEnvSkyM.render();
                    ImGui.endChild();
                }
                if (ImGui.collapsingHeader("Shadows")) {
                    ImGui.beginChild("##ShadowsContent0", ImGui.getColumnWidth(), 320, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceActionsEnvShadowsM.render();
                    ImGui.endChild();
                }
                if (ImGui.collapsingHeader("Lighting##Light1")) {
                    ImGui.beginChild("##LightingContent0", ImGui.getColumnWidth(), 360, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceActionsEnvLightingM.render();
                    ImGui.endChild();
                }
                ImGui.unindent();
            } else {
                ImGui.popStyleColor();
            }
            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();
        }
        this.interfaceActionsSelectedTemplateM.render();
        this.interfaceActionsSelectedObjectM.render();
    }

    public InterfaceActionsEnvFogM getInterfaceEnvFogM() {
        return this.interfaceActionsEnvFogM;
    }

    public InterfaceActionsEnvSkyM getInterfaceEnvSkyM() {
        return this.interfaceActionsEnvSkyM;
    }

    public InterfaceActionsEnvShadowsM getInterfaceEnvShadowsM() {
        return this.interfaceActionsEnvShadowsM;
    }

    public InterfaceActionsSelectedTemplateM getInterfaceActionsSelectedTemplateM() {
        return this.interfaceActionsSelectedTemplateM;
    }

    public InterfaceActionsSelectedObjectM getInterfaceActionsSelectedObjectM() {
        return this.interfaceActionsSelectedObjectM;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
