package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiSelectableFlags;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.joml.Vector3f;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.map.MapEditorInterface;

import java.util.HashSet;
import java.util.List;

public class ItemsInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;

    public ItemsInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.clear();
    }

    public void clear() {
    }

    public void itemsContent() {
        if (ProjectUIUtils.ctrlC()) {
            this.cloneSelected(this.getEditorInterface().getCurrentSelectedObject());
        }

        for (WBenchObject wBenchObject : new HashSet<>(this.getEditorInterface().setOfSceneObjects())) {
            boolean flag = this.getEditorInterface().getCurrentSelectedObject() == wBenchObject;
            float x = ImGui.getContentRegionAvailX() - 50f;
            ImGui.pushID(wBenchObject.getId());
            Vector3f color = wBenchObject.textInMenuColor();
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, 1.0f);

            String fullText = wBenchObject.toString(false);
            String displayText = fullText;
            float textWidth = ImGui.calcTextSize(displayText).x;
            float maxWidth = Math.max(x, 0.0f);

            float ratio = textWidth / maxWidth;

            if (ratio > 1.0f) {
                int endIndex = (int) (fullText.length() / (ratio + 0.1f));
                endIndex = Math.max(endIndex, 0);
                displayText = fullText.substring(0, endIndex);
            }
            if (ImGui.selectable(displayText, flag, ImGuiSelectableFlags.AllowItemOverlap, x, 18f)) {
                if (!flag) {
                    this.getEditorInterface().setCurrentSelectedObject(wBenchObject);
                    this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseDefaultGuizmoOperation());
                } else {
                    this.getEditorInterface().setCurrentSelectedObject(null);
                }
            }
            if (ImGui.beginPopupContextItem(displayText)) {
                if (ImGui.menuItem("Remove")) {
                    this.deleteSelected(wBenchObject);
                }
                if (ImGui.menuItem("Clone")) {
                    this.cloneSelected(wBenchObject);
                }
                ImGui.separator();
                if (ImGui.menuItem("Teleport")) {
                    if (this.getEditorInterface().getOldCamera() == null) {
                        ((ControlledCamera) this.getEditorInterface().getOpenGLRenderer().getCamera()).setCameraPosition(wBenchObject.getPosition());
                    }
                }
                ImGui.separator();
                final float rayDist = 8.0f;
                if (ImGui.menuItem("Snap X+")) {
                    this.snap(wBenchObject, new Vector3f(rayDist, 0.0f, 0.0f));
                }
                if (ImGui.menuItem("Snap X-")) {
                    this.snap(wBenchObject, new Vector3f(-rayDist, 0.0f, 0.0f));
                }
                if (ImGui.menuItem("Snap Z+")) {
                    this.snap(wBenchObject, new Vector3f(0.0f, 0.0f, rayDist));
                }
                if (ImGui.menuItem("Snap Z-")) {
                    this.snap(wBenchObject, new Vector3f(0.0f, 0.0f, -rayDist));
                }
                if (ImGui.menuItem("Snap Y+")) {
                    this.snap(wBenchObject, new Vector3f(0.0f, rayDist, 0.0f));
                }
                if (ImGui.menuItem("Snap Y-")) {
                    this.snap(wBenchObject, new Vector3f(0.0f, -rayDist, 0.0f));
                }
                ImGui.endPopup();
            }
            if (ImGui.isItemHovered() && !displayText.equals(fullText)) {
                ImGui.setTooltip(fullText);
            }
            ImGui.popStyleColor();
            ImGui.sameLine();
            if (ImGui.button("X")) {
                this.deleteSelected(wBenchObject);
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("Remove. id: " + wBenchObject.getId());
            }
            ImGui.sameLine();
            if (ImGui.button("C")) {
                this.cloneSelected(wBenchObject);
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("Clone. id: " + wBenchObject.getId());
            }
            ImGui.popID();
        }
    }

    private void snap(WBenchObject wBenchObject, Vector3f direction) {
        CullingAABB aabb = wBenchObject.getCullingData();
        if (aabb == null) {
            return;
        }

        Vector3f center = new Vector3f();
        aabb.getAabbMin().add(aabb.getAabbMax(), center).mul(0.5f);

        Vector3f halfSize = new Vector3f();
        aabb.getAabbMax().sub(aabb.getAabbMin(), halfSize).mul(0.5f);

        Vector3f origin = new Vector3f(center);
        List<Pair<SceneObject, Vector3f>> hits = this.getEditorInterface().getSceneComponent().getIntersectedObjects(this.getEditorInterface().getVisibleObjects(), origin, direction);

        if (hits.isEmpty()) {
            return;
        }

        Vector3f hitPoint = new Vector3f(hits.get(0).getSecond());

        if (direction.x > 0) {
            hitPoint.sub(halfSize.x, 0, 0);
        } else if (direction.x < 0) {
            hitPoint.add(halfSize.x, 0, 0);
        }

        if (direction.y > 0) {
            hitPoint.sub(0, halfSize.y, 0);
        } else if (direction.y < 0) {
            hitPoint.add(0, halfSize.y, 0);
        }

        if (direction.z > 0) {
            hitPoint.sub(0, 0, halfSize.z);
        } else if (direction.z < 0) {
            hitPoint.add(0, 0, halfSize.z);
        }

        Vector3f aabbOffset = new Vector3f();
        wBenchObject.getPosition().sub(center, aabbOffset);

        hitPoint.add(aabbOffset);
        wBenchObject.setPosition(hitPoint);
    }

    private void deleteSelected(WBenchObject wBenchObject) {
        if (wBenchObject.equals(this.getEditorInterface().getCurrentSelectedObject())) {
            this.getEditorInterface().setCurrentSelectedObject(null);
        }
        this.getEditorInterface().removeObjectFromWorld(wBenchObject);
    }

    private void cloneSelected(WBenchObject wBenchObject) {
        if (wBenchObject == null) {
            return;
        }
        WBenchObject cloneObj = wBenchObject.clone();
        CullingAABB cullingAABB = cloneObj.getCullingData();

        if (cullingAABB != null) {
            Vector3f posToCopy = cloneObj.getPosition();
            posToCopy.y += (cullingAABB.getAabbMax().y - cullingAABB.getAabbMin().y) * cloneObj.getScaling().y+ 0.5f;
            this.getEditorInterface().getActionsContent().spawnInWorld(cloneObj, posToCopy);
            this.getEditorInterface().setCurrentSelectedObject(cloneObj);
            Log.get().trace("Cloned " + cloneObj);
        }
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
