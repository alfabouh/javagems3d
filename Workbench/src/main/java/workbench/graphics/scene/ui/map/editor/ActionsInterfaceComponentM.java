package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
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
import workbench.graphics.scene.ui.map.editor.scenes.actions.InterfaceActionsSelectedObjectM;
import workbench.graphics.scene.ui.map.editor.scenes.actions.InterfaceActionsSelectedTemplateM;
import workbench.graphics.scene.ui.map.editor.scenes.resources.environment.InterfaceEnvFogM;
import workbench.graphics.scene.ui.map.editor.scenes.resources.environment.InterfaceEnvShadowsM;
import workbench.graphics.scene.ui.map.editor.scenes.resources.environment.InterfaceEnvSkyM;

import java.util.*;

public class ActionsInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final InterfaceActionsSelectedTemplateM interfaceActionsSelectedTemplateM;
    private final InterfaceActionsSelectedObjectM interfaceActionsSelectedObjectM;
    private final InterfaceEnvFogM interfaceEnvFogM;
    private final InterfaceEnvSkyM interfaceEnvSkyM;
    private final InterfaceEnvShadowsM interfaceEnvShadowsM;

    public ActionsInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.interfaceActionsSelectedObjectM = new InterfaceActionsSelectedObjectM(mapEditorInterface);
        this.interfaceActionsSelectedTemplateM = new InterfaceActionsSelectedTemplateM(mapEditorInterface.getScenePreview(), mapEditorInterface);
        this.interfaceEnvFogM = new InterfaceEnvFogM(mapEditorInterface);
        this.interfaceEnvSkyM = new InterfaceEnvSkyM(mapEditorInterface);
        this.interfaceEnvShadowsM = new InterfaceEnvShadowsM(mapEditorInterface);
        this.clear();
    }

    public void clear() {
        this.interfaceActionsSelectedObjectM.reset(this.getEditorInterface().getCurrentSelectedObject());
        this.interfaceActionsSelectedTemplateM.reset();
        this.resetEnvInterfaces();
    }

    public void resetEnvInterfaces() {
        this.interfaceEnvSkyM.reset();
    }

    public void resetObjectPreview() {
        this.interfaceActionsSelectedObjectM.reset(this.getEditorInterface().getCurrentSelectedObject());
        this.interfaceActionsSelectedTemplateM.reset();
    }

    public void resetTemplatePreview() {
        this.interfaceActionsSelectedTemplateM.reset();
    }

    public void actionsContent() {
        {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff99ff6e);
            if (ImGui.collapsingHeader("Environment", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.popStyleColor();
                ImGui.indent();
                if (ImGui.collapsingHeader("Fog")) {
                    ImGui.beginChild("##FogContet", ImGui.getColumnWidth(), 100, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceEnvFogM.render();
                    ImGui.endChild();
                }
                if (ImGui.collapsingHeader("SkyBox")) {
                    ImGui.beginChild("##SkyContet", ImGui.getColumnWidth(), 200, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceEnvSkyM.render();
                    ImGui.endChild();
                }
                if (ImGui.collapsingHeader("Shadows")) {
                    ImGui.beginChild("##ShadowsContet", ImGui.getColumnWidth(), 200, true, ImGuiWindowFlags.HorizontalScrollbar);
                    this.interfaceEnvShadowsM.render();
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

    public InterfaceEnvFogM getInterfaceEnvFogM() {
        return this.interfaceEnvFogM;
    }

    public InterfaceEnvSkyM getInterfaceEnvSkyM() {
        return this.interfaceEnvSkyM;
    }

    public InterfaceEnvShadowsM getInterfaceEnvShadowsM() {
        return this.interfaceEnvShadowsM;
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
