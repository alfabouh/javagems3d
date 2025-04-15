package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.WBenchPointLightObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.EditorInterface;
import workbench.project.ProjectTemplates;
import workbench.project.WBenchProject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ResourcesInterfaceComponent {
    private final EditorInterface editorInterface;
    private final ImInt currentSelectedScript;
    private final ImString newScriptName = new ImString(64);

    public ResourcesInterfaceComponent(EditorInterface editorInterface) {
        this.currentSelectedScript = new ImInt(-1);
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {

    }

    public void resourcesContent() {
        final boolean flag = this.getEditorInterface().getSelectedScene().equals(SelectedScene.MAIN);
        if (flag) {
            if (ImGui.collapsingHeader("Generate Light")) {
                ImGui.treePush();
                if (ImGui.selectable("Point Light", false)) {
                    ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                    Vector3f posToSpawn = camera.getCamPosition();
                    posToSpawn.add(JGemsUtils.calcLookVector(camera.getCamRotation()).mul(3.0f));

                    WBenchPointLightObject pointLightObject = WBenchPointLightObject.create("plmarker", this.getEditorInterface().getOpenGLRenderer().getWorld());
                    pointLightObject.setPosition(posToSpawn);
                    this.getEditorInterface().addObjectInWorld(pointLightObject);
                }
                ImGui.treePop();
            }
            ImGui.separator();
            if (ImGui.collapsingHeader("Entities")) {
                this.renderObjectGroupsList(WBench.get().getProjectObjects().getEntityGroups());
            }
        }
        if (ImGui.collapsingHeader("Props")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getPropGroups());
        }
        if (ImGui.collapsingHeader("Markers")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getMarkerGroups());
        }
        if (ImGui.collapsingHeader("Scripts")) {
            ImGui.treePush();
            final WBenchProject wBenchProject = WBench.get().getProjectManager().getCurrentProject();
            final List<String> scriptPaths = wBenchProject.getScriptFiles();

            List<String> itemsList = new ArrayList<>();
            itemsList.add("+ Create new script");
            itemsList.addAll(scriptPaths);
            final String[] items = itemsList.toArray(new String[0]);

            if (ImGui.combo("##Scripts", this.currentSelectedScript, items, 6)) {
                if (this.currentSelectedScript.get() == 0) {
                    this.currentSelectedScript.set(-1);
                    ImGui.openPopup("NewScriptPopup");
                } else {
                    wBenchProject.reviseScripts();
                }
            }
            if (ImGui.beginPopup("NewScriptPopup")) {
                ImGui.text("Enter script name:");
                ImGui.inputText("##scriptName", this.newScriptName);

                if (ImGui.button("Create")) {
                    String name = this.newScriptName.get();
                    if (!name.isEmpty()) {
                        this.newScriptName.clear();
                        WBench.get().getProjectManager().getCurrentProject().createNewScript(name);
                        ImGui.closeCurrentPopup();
                    }
                }
                ImGui.sameLine();
                if (ImGui.button("Cancel")) {
                    this.newScriptName.clear();
                    ImGui.closeCurrentPopup();
                }

                ImGui.endPopup();
            }

            int realIndex = this.currentSelectedScript.get() - 1;
            if (realIndex >= 0 && realIndex < scriptPaths.size()) {
                String selectedPath = scriptPaths.get(realIndex);

                try {
                    if (ImGui.button("Open File")) {
                        Desktop.getDesktop().open(new File(wBenchProject.getScriptPathTo(selectedPath).getFullPath()));
                    }
                    if (ImGui.button("Open Folder")) {
                        Desktop.getDesktop().open(new File(wBenchProject.getScriptPathTo(selectedPath).getFullPath()).getParentFile());
                    }
                } catch (IOException e) {
                    Log.get().exception(e);
                }
                if (ImGui.button("Delete")) {
                    if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                        wBenchProject.deleteScript(realIndex);
                        this.currentSelectedScript.set(0);
                    }
                }
            }
            ImGui.treePop();
        }
        ImGui.separator();
    }

    private <T extends WBenchObjectTemplate> void renderObjectGroupsList(Map<String, ProjectTemplates.TemplatesTable<T>> tableMap) {
        for (Map.Entry<String, ProjectTemplates.TemplatesTable<T>> entry : tableMap.entrySet()) {
            String groupName = entry.getKey();
            Collection<T> objects = entry.getValue().getTemplateMap().values();

            ImGui.treePush();
            String groupNameTree = groupName != null ? groupName : "Other";
            if (ImGui.treeNode(groupNameTree)) {
                ImGui.treePush();
                for (T object : objects) {
                    boolean flag = this.getEditorInterface().getCurrentSelectedTemplate() == object;
                    if (ImGui.selectable(object.getObjectId().getNameId(), flag)) {
                        if (!flag) {
                            this.getEditorInterface().setCurrentSelectedTemplate(object);
                            this.getEditorInterface().setPreviewDistance(1.0f);
                        } else {
                            this.getEditorInterface().setCurrentSelectedTemplate(null);
                        }
                    }
                }
                ImGui.treePop();
                ImGui.treePop();
            }
            ImGui.treePop();
        }
    }


    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
