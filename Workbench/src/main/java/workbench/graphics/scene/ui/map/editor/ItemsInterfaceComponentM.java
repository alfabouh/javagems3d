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
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;

import java.util.*;

public class ItemsInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    public int sortingMode; //0 = ID; 1 = NAME; 2 = TYPE
    private boolean scrollToSelection;

    public ItemsInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.clear();
        this.sortingMode = 0;
    }

    public void clear() {
    }

    public synchronized void scrollToSelection() {
        this.scrollToSelection = true;
    }

    @SuppressWarnings("all")
    public void itemsContent() {
        boolean cloneAll = false;
        if (ProjectUIUtils.ctrlC()) {
            cloneAll = true;
        }
        final List<WBenchObject<?>> sortedSet = new ArrayList<>(this.getEditorInterface().setOfSceneObjects());
        Comparator<WBenchObject<?>> comparator;
        if (this.sortingMode == 0) {
            comparator = Comparator.comparing(WBenchObject::getListID);
        } else if (this.sortingMode == 1) {
            comparator = Comparator.<WBenchObject<?>, String>comparing(WBenchObject::getName).thenComparing(WBenchObject::getListID);
        } else {
            comparator = Comparator.<WBenchObject<?>, Integer>comparing(WBenchObject::orderInList).thenComparing(WBenchObject::getListID);
        }
        sortedSet.sort(comparator);
        for (WBenchObject<?> wBenchObject : sortedSet) {
            boolean flag = this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().contains(wBenchObject);
            float x = ImGui.getContentRegionAvailX() - 50f;
            ImGui.pushID(wBenchObject.getListID());
            Vector3f color = wBenchObject.textInMenuColor();
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, 1.0f);

            String fullText = "[" + wBenchObject.getListID() + "] " + wBenchObject.getObjectNameId().getNameId();
            String displayText = fullText;
            float textWidth = ImGui.calcTextSize(displayText).x;
            float maxWidth = Math.max(x, 0.0f);

            float ratio = textWidth / maxWidth;

            if (ratio > 1.0f) {
                int endIndex = (int) (fullText.length() / (ratio + 0.1f));
                endIndex = Math.max(endIndex, 0);
                displayText = fullText.substring(0, endIndex);
                displayText += "...";
            }
            if (ImGui.selectable(displayText, flag, ImGuiSelectableFlags.AllowItemOverlap, x, 18f)) {
                if (ProjectUIUtils.ctrl()) {
                    this.getEditorInterface().getSelectedObjectsManager().addObjectInSelection(true, wBenchObject);
                    this.getEditorInterface().getActionsContent().resetObjectPreview();
                } else {
                    if (!flag) {
                        this.getEditorInterface().getSelectedObjectsManager().setCurrentSelectedObject(true, wBenchObject);
                        this.getEditorInterface().getActionsContent().resetObjectPreview();
                    } else {
                        this.getEditorInterface().getSelectedObjectsManager().setCurrentSelectedObject(true, null);
                    }
                }
            }
            if (this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().contains(wBenchObject)) {
                if (!ImGui.isItemVisible() && this.scrollToSelection) {
                    ImGui.setScrollHereY();
                }
                this.scrollToSelection = false;
            }
            this.rightKeyContext(displayText, true);
            if (ImGui.isItemHovered() && !displayText.equals(fullText)) {
                ImGui.setTooltip(fullText);
            }
            ImGui.popStyleColor();
            ImGui.sameLine();
            if (ImGui.button("X")) {
                this.mapEditorInterface.getSelectedObjectsManager().deleteObject(true, wBenchObject);
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("Remove. id: " + wBenchObject.getListID());
            }
            ImGui.sameLine();
            if (ImGui.button("C")) {
                cloneAll = true;
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("Clone. id: " + wBenchObject.getListID());
            }
            ImGui.popID();
        }
        if (cloneAll) {
            ItemsInterfaceComponentM.cloneAllSelected(this.mapEditorInterface);
        }
    }

    public void rightKeyContext(String id, boolean common) {
        if (this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().isEmpty()) {
            return;
        }
        if (common ? ImGui.beginPopupContextItem(id) : ImGui.beginPopup(id)) {
            if (ImGui.menuItem("Remove")) {
                this.mapEditorInterface.getSelectedObjectsManager().deleteSelected();
            }
            if (ImGui.menuItem("Clone")) {
                cloneAllSelected(this.mapEditorInterface);
            }
            ImGui.separator();
            if (ImGui.menuItem("Teleport")) {
                if (this.getEditorInterface().getOldCamera() == null) {
                    ((ControlledCamera) this.getEditorInterface().getOpenGLRenderer().getCamera()).setCameraPosition(this.mapEditorInterface.getSelectedObjectsManager().center());
                }
            }
            ImGui.separator();
            final float rayDist = 8.0f;
            if (ImGui.menuItem("Snap X+")) {
                this.snap(new Vector3f(rayDist, 0.0f, 0.0f));
            }
            if (ImGui.menuItem("Snap X-")) {
                this.snap(new Vector3f(-rayDist, 0.0f, 0.0f));
            }
            if (ImGui.menuItem("Snap Z+")) {
                this.snap(new Vector3f(0.0f, 0.0f, rayDist));
            }
            if (ImGui.menuItem("Snap Z-")) {
                this.snap(new Vector3f(0.0f, 0.0f, -rayDist));
            }
            if (ImGui.menuItem("Snap Y+")) {
                this.snap( new Vector3f(0.0f, rayDist, 0.0f));
            }
            if (ImGui.menuItem("Snap Y-")) {
                this.snap(new Vector3f(0.0f, -rayDist, 0.0f));
            }
            ImGui.endPopup();
        }
    }

    public static void cloneAllSelected(MapEditorInterface mapEditorInterface) {
        Set<WBenchObject<?>> wBenchObjects = new HashSet<>(mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects());
        mapEditorInterface.getSelectedObjectsManager().setCurrentSelectedObject(false, null);
        wBenchObjects.forEach(e -> {
            cloneSelected(mapEditorInterface, e);
        });
    }

    private void snap(Vector3f direction) {
        WBenchUITrackingHelper.instantlyTrackAndPush();
        this.mapEditorInterface.getSelectedObjectsManager().getCurrentSelectedObjects().forEach(wBenchObject -> {
            CullingAABB aabb = wBenchObject.getCullingData();
            if (aabb == null) {
                return;
            }

            Vector3f center = new Vector3f();
            aabb.getAabbMin().add(aabb.getAabbMax(), center).mul(0.5f);

            Vector3f halfSize = new Vector3f();
            aabb.getAabbMax().sub(aabb.getAabbMin(), halfSize).mul(0.5f);

            Vector3f origin = new Vector3f(center);
            List<Pair<SceneObject, Vector3f>> hits = this.getEditorInterface().getSceneComponent().getIntersectedObjects(this.getEditorInterface().getWorld().getSceneObjects(), origin, direction);

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
        });
    }

    private static void cloneSelected(MapEditorInterface mapEditorInterface, WBenchObject<?> wBenchObject) {
        if (wBenchObject == null) {
            return;
        }
        WBenchObject<?> cloneObj = wBenchObject.clone();
        CullingAABB cullingAABB = cloneObj.getCullingData();

        if (cullingAABB != null) {
            Vector3f posToCopy = cloneObj.getPosition();
            posToCopy.y += (cullingAABB.getAabbMax().y - cullingAABB.getAabbMin().y) * cloneObj.getScaling().y+ 0.5f;
            cloneObj.setPosition(posToCopy);
            mapEditorInterface.addObjectInWorld(cloneObj);
            mapEditorInterface.getSelectedObjectsManager().addObjectInSelection(false, cloneObj);
            Log.get().trace("Cloned " + cloneObj);
        }
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
