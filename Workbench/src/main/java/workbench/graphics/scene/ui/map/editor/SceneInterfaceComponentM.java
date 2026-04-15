package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IPostFXRenderNode;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.help.JGemsUtils;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import workbench.WBench;
import workbench.controller.binding.WBenchBindingManager;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.actions.InterfaceActionsSelectedObjectM;

import java.lang.Math;
import java.lang.Runtime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SceneInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final AtomicBoolean isThreadInProcess;
    private boolean wasGuizmoUsed;
    private final Vector3f guizmoPrevTranlate;
    private final Vector3f guizmoPrevRotate;
    private final Vector3f guizmoPrevScale;
    private boolean rightMouseDown = false;
    private boolean rightMouseDragged = false;
    private final float DRAG_THRESHOLD = 3.0f; // пиксели
    private float dragStartX;
    private float dragStartY;
    public static boolean scalingFlag;

    public SceneInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.isThreadInProcess = new AtomicBoolean();
        this.guizmoPrevTranlate = new Vector3f();
        this.guizmoPrevRotate = new Vector3f();
        this.guizmoPrevScale = new Vector3f();
        this.clear();
    }

    public void resetFrame() {
        this.guizmoPrevTranlate.set(0.0f);
        this.guizmoPrevRotate.set(0.0f);
        this.guizmoPrevScale.set(0.0f);
    }

    public void clear() {
        this.isThreadInProcess.set(false);
    }

    public void sceneContent() {
        final float[] view = JGemsTransformManager.INSTANCE.getCameraViewMatrix().get(new float[16]);
        final float[] projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix().get(new float[16]);
        ImGuizmo.setAllowAxisFlip(true);
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();

        final float availableX = ImGui.getContentRegionAvailX();
        final float availableY = ImGui.getContentRegionAvailY();

        final IDeferredRenderNode deferredRenderNode = this.getEditorInterface().getOpenGLRenderer().getRenderNodeByPass(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        IPostFXRenderNode postFXRenderNode = this.getEditorInterface().getOpenGLRenderer().getRenderNodeByPass(WBenchOpenGLRenderer.POST_FX_RENDER_PASS);
        ImGui.image(postFXRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
        //ImGui.image(deferredRenderNode.getOutGBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
        int imagePosX = (int) ImGui.getItemRectMinX();
        int imagePosY = (int) ImGui.getItemRectMinY();
        int imageSizeX = (int) (ImGui.getItemRectSizeX());
        int imageSizeY = (int) (ImGui.getItemRectSizeY());

        if (MapEditorInterface.isCursorInsideScene) {
            if (!this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().isEmpty()) {
                this.getEditorInterface().getItemsComponent().rightKeyContext("sceneSelectedContext", false);
                if (ImGui.isMouseClicked(1)) {
                    this.rightMouseDown = true;
                    this.rightMouseDragged = false;
                    this.dragStartX = ImGui.getMousePosX();
                    this.dragStartY = ImGui.getMousePosY();
                }

                if (this.rightMouseDown && ImGui.isMouseDown(1)) {
                    float dx = ImGui.getMousePosX() - this.dragStartX;
                    float dy = ImGui.getMousePosY() - this.dragStartY;
                    if (Math.abs(dx) > this.DRAG_THRESHOLD || Math.abs(dy) > this.DRAG_THRESHOLD) {
                        this.rightMouseDragged = true;
                    }
                }

                if (this.rightMouseDown && ImGui.isMouseReleased(1)) {
                    if (!this.rightMouseDragged) {
                        ImGui.openPopup("sceneSelectedContext");
                    }

                    this.rightMouseDown = false;
                }
            }
        }

        ImGuizmo.setRect(imagePosX, imagePosY, imageSizeX, imageSizeY);
        if (!this.getEditorInterface().getActionsContent().getInterfaceEnvSkyM().isCameraCheckBox()) {
            if (!ImGuizmo.isUsing() && MapEditorInterface.isCursorInsideSceneAndFocused) {
                if (ImGui.isMouseReleased(0)) {
                    ImGui.closeCurrentPopup();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    if (!this.isThreadInProcess.get()) {
                        executor.execute(() -> {
                            try {
                                this.isThreadInProcess.set(true);
                                WBenchObject<?> wBenchObject = this.tryToSelectObjectFromMouse(new Vector2i(imagePosX, imagePosY), new Vector2i(imageSizeX, imageSizeY), new Vector2i((int) ImGui.getMousePos().x, (int) ImGui.getMousePos().y));
                                if (ProjectUIUtils.ctrl()) {
                                    if (wBenchObject != null) {
                                        if (this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().contains(wBenchObject)) {
                                            this.getEditorInterface().getSelectedObjectsManager().removeObjectFromSelection(true, wBenchObject);
                                        } else {
                                            this.getEditorInterface().getSelectedObjectsManager().addObjectInSelection(true, wBenchObject);
                                        }
                                    }
                                } else {
                                    this.getEditorInterface().getSelectedObjectsManager().setCurrentSelectedObject(true, wBenchObject);
                                }
                                this.getEditorInterface().getItemsComponent().scrollToSelection();
                                if (wBenchObject != null) {
                                    this.getEditorInterface().getActionsContent().resetObjectPreview();
                                }
                            } finally {
                                this.isThreadInProcess.set(false);
                                executor.shutdown();
                            }
                        });
                    }
                }
            }

            if (this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().size() == 1) {
                this.renderForSingleObject(view, projection, this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().stream().findFirst().get());
            } else if (this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects().size() > 1) {
                this.renderForMultipleObjects(view, projection, this.getEditorInterface().getSelectedObjectsManager().getCurrentSelectedObjects());
            }
        }

        WBench.get().getScreen().getWindow().setFocus(ImGui.isWindowFocused());
    }

    public boolean isOneDirScaling() {
        WBenchBindingManager wBenchBindingManager = (WBenchBindingManager) WBench.get().getControllerDispatcher().getCurrentController().getBindingManager();
        return wBenchBindingManager.keyAlt.isPressed() || this.mapEditorInterface.getSelectedObjectsManager().isOneDirScaling();
    }

    private static boolean reversedStick(Vector3f stick, ICamera camera) {
        Vector3f axisWorld = new Vector3f(stick);
        Vector3f camDir = JGemsHelper.math().calcLookVector(camera.getCamRotation());
        float dot = axisWorld.dot(camDir);
        return dot > 0.0f;
    }

    private void renderForSingleObject(float[] view, float[] projection, WBenchObject<?> wBenchObject) {
        boolean UsedImGuizmo = ImGuizmo.isUsing();
        CullingAABB cullingAABB = wBenchObject.pickAABBDataFromMesh();
        if (cullingAABB != null) {
            WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
            Pose3D pose3D = new Pose3D();
            pose3D.setPosition(this.guizmoPrevTranlate.length() > 0.0f ? this.guizmoPrevTranlate : wBenchObject.getModel().getPose().getPosition());
            pose3D.setRotation(wBenchObject.getModel().getPose().getRotation());
            pose3D.setScaling(wBenchObject.getModel().getPose().getScaling());
            float[] modelMatrix = TransformUtils.getModelMatrix(pose3D).get(new float[16]);
            float[] deltaMatrix = new float[16];
            int currentOperation = this.getEditorInterface().getActionsContent().getInterfaceActionsSelectedObjectM().getCurrentOperation();

            ImGuizmo.manipulate(view, projection, modelMatrix, deltaMatrix, currentOperation, Mode.WORLD, new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f});

            if (!this.wasGuizmoUsed && UsedImGuizmo) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
            }
            if (UsedImGuizmo && ImGui.isItemHovered()) {
                Vector3f position = new Vector3f();
                Vector3f rotation = new Vector3f();
                Vector3f scaling = new Vector3f();
                Matrix4f newMatrix = JGemsUtils.getMatrixFromArray(modelMatrix);

                newMatrix.getTranslation(position);
                newMatrix.getScale(scaling);
                newMatrix.getUnnormalizedRotation(new Quaternionf()).getEulerAnglesXYZ(rotation);

                final Vector3f newPos = position;
                final Vector3f newRot = rotation.negate();
                final Vector3f newScale = scaling;

                if ((currentOperation & Operation.TRANSLATE) != 0) {
                    wBenchObject.setPosition(newPos);
                }
                if ((currentOperation & Operation.ROTATE) != 0) {
                    wBenchObject.setRotation(newRot);
                }
                if ((currentOperation & Operation.SCALE) != 0) {
                    if (this.isOneDirScaling()) {
                        Vector3f oldScale = new Vector3f(wBenchObject.getScaling());
                        final Vector3f delta = new Vector3f(oldScale).sub(newScale);
                        if (!SceneInterfaceComponentM.scalingFlag && (delta.x != 0 || delta.y != 0 || delta.z != 0)) {
                            this.guizmoPrevTranlate.set(wBenchObject.getPosition());
                            if (delta.x != 0) {
                                this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(1.0f, 0.0f, 0.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                            } else if (delta.y != 0) {
                                this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(0.0f, 1.0f, 0.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                            } else {
                                this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(0.0f, 0.0f, 1.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                            }
                            SceneInterfaceComponentM.scalingFlag = true;
                        }

                        Vector3f scaleRatio = new Vector3f(newScale).div(oldScale);
                        CullingAABB aabb = wBenchObject.getCullingData();
                        Vector3f pivot = new Vector3f(this.mapEditorInterface.getSelectedObjectsManager().isScalingOneDirMaxPlane() ? aabb.getAabbMax() : aabb.getAabbMin());
                        Vector3f oldPos = new Vector3f(wBenchObject.getPosition());
                        Vector3f newPosScaled = new Vector3f(oldPos).sub(pivot).mul(scaleRatio).add(pivot);
                        if (newPosScaled.isFinite()) {
                            wBenchObject.setPosition(newPosScaled);
                        }
                    }
                    wBenchObject.setScaling(newScale);
                } else {
                    SceneInterfaceComponentM.scalingFlag = false;
                }
            } else {
                SceneInterfaceComponentM.scalingFlag = false;
                this.guizmoPrevTranlate.set(0.0f);
            }
        }
        this.wasGuizmoUsed = UsedImGuizmo;
    }

    private void renderForMultipleObjects(float[] view, float[] projection, Set<WBenchObject<?>> wBenchObjects) {
        final Vector3f min = new Vector3f(Float.MAX_VALUE);
        final Vector3f max = new Vector3f(-Float.MAX_VALUE);
        boolean UsedImGuizmo = ImGuizmo.isUsing();

        for (WBenchObject<?> wBenchObject : wBenchObjects) {
            CullingAABB cullingAABB = wBenchObject.pickAABBDataFromMesh();
            if (cullingAABB != null) {
                WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.Depth(), DebugLinesDrawer.Depth()));
                min.min(cullingAABB.getAabbMin());
                max.max(cullingAABB.getAabbMax());
            }
        }

        Pose3D pose3D = new Pose3D();
        pose3D.setPosition(this.guizmoPrevTranlate.length() > 0.0f ? this.guizmoPrevTranlate : this.mapEditorInterface.getSelectedObjectsManager().getGroupPosition());
        pose3D.setRotation(this.mapEditorInterface.getSelectedObjectsManager().getGroupRotation().negate());
        pose3D.setScaling(this.mapEditorInterface.getSelectedObjectsManager().getGroupScaling());
        float[] modelMatrix = TransformUtils.getModelMatrix(pose3D).get(new float[16]);
        float[] deltaMatrix = new float[16];
        int currentOperation = this.getEditorInterface().getActionsContent().getInterfaceActionsSelectedObjectM().getCurrentOperation();
        ImGuizmo.manipulate(view, projection, modelMatrix, deltaMatrix, currentOperation, Mode.WORLD, new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f});
        if (UsedImGuizmo && ImGui.isItemHovered()) {
            if (!this.wasGuizmoUsed) {
                //this.getEditorInterface().getSelectedObjectsManager().beginGroupTransform();
                WBenchUITrackingHelper.instantlyTrackAndPush();
            }
            Matrix4f newMatrix = JGemsUtils.getMatrixFromArray(modelMatrix);
            Matrix4f deltaMatrix2 = JGemsUtils.getMatrixFromArray(deltaMatrix);
            final Vector3f newPos = newMatrix.getTranslation(new Vector3f());
            final Vector3f newRot = deltaMatrix2.getEulerAnglesXYZ(new Vector3f());
            final Vector3f newScale = newMatrix.getScale(new Vector3f());

            if ((currentOperation & Operation.TRANSLATE) != 0) {
                this.mapEditorInterface.getSelectedObjectsManager().setGroupPosition(newPos);
            }
            if ((currentOperation & Operation.ROTATE) != 0) {
                Vector3f deltaRot = this.mapEditorInterface.getSelectedObjectsManager().getGroupRotation().add(newRot);
                {
                    if (deltaRot.x < -Math.PI) {
                        deltaRot.x = (float) Math.PI;
                    } else if (deltaRot.x > Math.PI) {
                        deltaRot.x = (float) -Math.PI;
                    }
                }
                {
                    if (deltaRot.y < -Math.PI) {
                        deltaRot.y = (float) Math.PI;
                    } else if (deltaRot.y > Math.PI) {
                        deltaRot.y = (float) -Math.PI;
                    }
                }
                {
                    if (deltaRot.z < -Math.PI) {
                        deltaRot.z = (float) Math.PI;
                    } else if (deltaRot.z > Math.PI) {
                        deltaRot.z = (float) -Math.PI;
                    }
                }
                this.mapEditorInterface.getSelectedObjectsManager().setGroupRotation(deltaRot);
            }
            if ((currentOperation & Operation.SCALE) != 0) {
                if (this.isOneDirScaling()) {
                    Vector3f oldScale = new Vector3f(this.mapEditorInterface.getSelectedObjectsManager().getGroupScaling());
                    final Vector3f delta = new Vector3f(oldScale).sub(newScale);
                    if (!SceneInterfaceComponentM.scalingFlag && (delta.x != 0 || delta.y != 0 || delta.z != 0)) {
                        this.guizmoPrevTranlate.set(this.mapEditorInterface.getSelectedObjectsManager().getGroupPosition());
                        if (delta.x != 0) {
                            this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(1.0f, 0.0f, 0.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                        } else if (delta.y != 0) {
                            this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(0.0f, 1.0f, 0.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                        } else {
                            this.mapEditorInterface.getSelectedObjectsManager().setScalingOneDirMaxPlane(SceneInterfaceComponentM.reversedStick(new Vector3f(0.0f, 0.0f, 1.0f), this.mapEditorInterface.getOpenGLRenderer().getCamera()));
                        }
                        SceneInterfaceComponentM.scalingFlag = true;
                    }
                }
                this.mapEditorInterface.getSelectedObjectsManager().setGroupScaling(new Vector3f(newScale));
            } else {
                SceneInterfaceComponentM.scalingFlag = false;
            }

        } else {
            SceneInterfaceComponentM.scalingFlag = false;
            this.guizmoPrevTranlate.set(0.0f);
            this.resetFrame();
        }
        this.wasGuizmoUsed = UsedImGuizmo;

        WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(new Vector3f(min.x - 0.1f, min.y - 0.1f, min.z - 0.1f), new Vector3f(max.x + 0.1f, max.y + 0.1f, max.z + 0.1f), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
    }

    private WBenchObject<?> tryToSelectObjectFromMouse(Vector2i sceneWindowPos, Vector2i sceneWindowSize, Vector2i mouseCoordinates) {
        Vector2i mousePosRelatedToWindow = mouseCoordinates.sub(sceneWindowPos);

        float ndcMouseX = ((float) (2 * mousePosRelatedToWindow.x) / sceneWindowSize.x) - 1.0f;
        float ndcMouseY = 1.0f - ((float) (2 * mousePosRelatedToWindow.y) / sceneWindowSize.y);

        Matrix4f viewMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
        Matrix4f projectionMatrix = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();

        Vector4f ray = new Vector4f(ndcMouseX, ndcMouseY, -1.0f, 1.0f);
        Vector4f getRayInView = ray.mul(projectionMatrix.invert(new Matrix4f()));
        getRayInView.z = -1.0f;
        getRayInView.w = 0.0f;

        Vector4f getRayInWorld = getRayInView.mul(viewMatrix.invert(new Matrix4f()));
        Vector3f camRay = new Vector3f(getRayInWorld.x, getRayInWorld.y, getRayInWorld.z);
        camRay.normalize();

        Vector3f origin = this.getEditorInterface().getOpenGLRenderer().getCamera().getCamPosition();
        List<Pair<SceneObject, Vector3f>> sceneObjects = this.getIntersectedObjectsRayCenter(this.getEditorInterface().getWorld().getEndFrameVisibleObjects(), origin, camRay);
        return sceneObjects.isEmpty() ? null : (WBenchObject<?>) sceneObjects.getFirst().first();
    }

    public @Nullable Vector3f resolveMovementWithCollision(SceneObject objectOriginal, Collection<? extends SceneObject> objects, int steps, Vector3f direction) {
        final CullingAABB baseAABB = objectOriginal.pickAABBDataFromMesh();
        if (baseAABB == null) {
            return null;
        }
        Vector3f step = new Vector3f();
        final float xExt = baseAABB.getAabbMax().x - baseAABB.getAabbMin().x;
        final float yExt = baseAABB.getAabbMax().y - baseAABB.getAabbMin().y;
        final float zExt = baseAABB.getAabbMax().z - baseAABB.getAabbMin().z;
        if (direction.x != 0) {
            step = new Vector3f(xExt * 0.5f * direction.x, 0.0f, 0.0f);
        } else if (direction.y != 0) {
            step = new Vector3f(0.0f, yExt * 0.5f * direction.y, 0.0f);
        } else if (direction.z != 0) {
            step = new Vector3f(0.0f, 0.0f, zExt * 0.5f * direction.z);
        }
        Vector3f currentOffset = new Vector3f();
        for (int i = 0; i < steps; i++) {
            Vector3f nextOffset = new Vector3f(currentOffset).add(step);
            Vector3f minA = new Vector3f(baseAABB.getAabbMin()).add(nextOffset);
            Vector3f maxA = new Vector3f(baseAABB.getAabbMax()).add(nextOffset);
            for (SceneObject obj : objects) {
                if (obj == objectOriginal) {
                    continue;
                }
                CullingAABB other = obj.pickAABBDataFromMesh();
                if (other == null) {
                    continue;
                }
                if (!Intersectionf.testAabAab(minA, maxA, other.getAabbMin(), other.getAabbMax())) {
                    continue;
                }
                Model3D modelA = new Model3D(objectOriginal.getModel(), objectOriginal.getModel().getPose().copy().setPosition(nextOffset));
                Model3D modelB = obj.getModel();
                Vector3f hit = this.findEdgeTriangleIntersect(modelA, modelB, Runtime.getRuntime().availableProcessors() - 1);
                if (hit != null) {
                    return currentOffset;
                }
            }
            currentOffset.set(nextOffset);
        }
        return null;
    }

    public List<Pair<SceneObject, Vector3f>> getIntersectedObjectsRayCenter(Collection<? extends SceneObject> objects, Vector3f origin, Vector3f ray) {
        Set<SceneObject> intersectedAabbs = new HashSet<>();
        for (SceneObject sceneObject : objects) {
            CullingAABB cullingAABB = sceneObject.pickAABBDataFromMesh();
            if (cullingAABB != null) {
                Vector2f vector2f = new Vector2f();
                if (Intersectionf.intersectRayAab(origin, ray, cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), vector2f)) {
                    intersectedAabbs.add(sceneObject);
                }
            }
        }
        List<Pair<SceneObject, Vector3f>> sceneObjects = new ArrayList<>();
        for (SceneObject object : intersectedAabbs) {
            if (object.getAnimationData() != null) {
                CullingAABB cullingAABB = object.pickAABBDataFromMesh();
                if (cullingAABB != null) {
                    Vector2f intersection = new Vector2f();
                    Intersectionf.intersectRayAab(origin, ray, cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), intersection);
                    sceneObjects.add(new Pair<>(object, new Vector3f(origin).add(new Vector3f(ray).mul(intersection.x))));
                }
            } else {
                Vector3f intersection = this.findClosesPointRayIntersectMesh(object.getModel(), origin, ray, Runtime.getRuntime().availableProcessors() - 1);
                if (intersection != null) {
                    sceneObjects.add(new Pair<>(object, intersection));
                }
            }
        }
        sceneObjects.sort(Comparator.comparingDouble(e -> e.second().distance(origin)));
        return sceneObjects;
    }

    public Vector3f findEdgeTriangleIntersect(Model3D modelA, Model3D modelB, int threads) {
        Matrix4f matA = TransformUtils.getModelMatrix(modelA.getPose());
        Matrix4f matB = TransformUtils.getModelMatrix(modelB.getPose());
        List<? extends MeshNode3D<?>> nodesA = modelA.getMeshStructure().getAllNodes();
        List<? extends MeshNode3D<?>> nodesB = modelB.getMeshStructure().getAllNodes();
        int total = nodesA.size();
        int effectiveThreads = Math.min(total, threads);
        int chunk = total / effectiveThreads;
        int rest = total % effectiveThreads;
        ExecutorService executor = Executors.newFixedThreadPool(effectiveThreads);
        List<Future<Vector3f>> futures = new ArrayList<>();
        for (int t = 0; t < effectiveThreads; t++) {
            int start = t * chunk;
            int end = start + chunk + (t == effectiveThreads - 1 ? rest : 0);
            futures.add(executor.submit(() -> {
                Vector3f closest = null;
                float bestDist = Float.MAX_VALUE;
                Vector3f intersection = new Vector3f();
                for (int i = start; i < end; i++) {
                    MeshNode3D<?> nodeA = nodesA.get(i);
                    List<Float> vA = nodeA.getMeshData().getVertexPositions();
                    List<Integer> idxA = nodeA.getMeshData().getVertexIndexes();
                    for (int j = 0; j < idxA.size(); j += 3) {
                        int i0 = idxA.get(j) * 3;
                        int i1 = idxA.get(j + 1) * 3;
                        int i2 = idxA.get(j + 2) * 3;
                        Vector3f v0 = new Vector3f(vA.get(i0), vA.get(i0 + 1), vA.get(i0 + 2)).mulPosition(matA);
                        Vector3f v1 = new Vector3f(vA.get(i1), vA.get(i1 + 1), vA.get(i1 + 2)).mulPosition(matA);
                        Vector3f v2 = new Vector3f(vA.get(i2), vA.get(i2 + 1), vA.get(i2 + 2)).mulPosition(matA);
                        Vector3f[][] edges = {{v0, v1}, {v1, v2}, {v2, v0}};
                        for (Vector3f[] edge : edges) {
                            Vector3f p0 = edge[0];
                            Vector3f p1 = edge[1];
                            for (MeshNode3D<?> nodeB : nodesB) {
                                List<Float> vB = nodeB.getMeshData().getVertexPositions();
                                List<Integer> idxB = nodeB.getMeshData().getVertexIndexes();
                                for (int k = 0; k < idxB.size(); k += 3) {
                                    int j0 = idxB.get(k) * 3;
                                    int j1 = idxB.get(k + 1) * 3;
                                    int j2 = idxB.get(k + 2) * 3;
                                    Vector3f t0 = new Vector3f(vB.get(j0), vB.get(j0 + 1), vB.get(j0 + 2)).mulPosition(matB);
                                    Vector3f t1 = new Vector3f(vB.get(j1), vB.get(j1 + 1), vB.get(j1 + 2)).mulPosition(matB);
                                    Vector3f t2 = new Vector3f(vB.get(j2), vB.get(j2 + 1), vB.get(j2 + 2)).mulPosition(matB);
                                    if (Intersectionf.intersectLineSegmentTriangle(p0, p1, t0, t1, t2, 1e-5f, intersection)) {
                                        float dist = p0.distance(intersection);
                                        if (dist < bestDist) {
                                            bestDist = dist;
                                            closest = new Vector3f(intersection);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return closest;
            }));
        }
        executor.shutdown();
        Vector3f result = null;
        float best = Float.MAX_VALUE;
        try {
            for (Future<Vector3f> f : futures) {
                Vector3f r = f.get();
                if (r != null) {
                    float d = r.length();
                    if (d < best) {
                        best = d;
                        result = r;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Vector3f findClosesPointRayIntersectMesh(Model3D model3D, Vector3f rayStart, Vector3f rayEnd, int threads) {
        MeshStructure3D<?> meshStructure3D = model3D.getMeshStructure();
        Matrix4f modelMatrix = TransformUtils.getModelMatrix(model3D.getPose());

        List<? extends MeshNode3D<?>> allNodes = meshStructure3D.getAllNodes();

        int totalNodes = allNodes.size();
        int effectiveThreads = Math.min(totalNodes, threads);
        int nodesInGroup = allNodes.size() / effectiveThreads;
        int restNodesInGroup = allNodes.size() % effectiveThreads;
        List<Future<Vector3f>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(effectiveThreads);
        try {
            for (int z = 0; z < effectiveThreads; z++) {
                int start = z * (nodesInGroup);
                int end = z * (nodesInGroup) + nodesInGroup + ((z == effectiveThreads - 1) ? restNodesInGroup : 0);
                futures.add(executorService.submit(() -> {
                    Vector3f closestVector = null;
                    for (int k = start; k < end; k++) {
                        MeshNode3D<?> meshNode3D = allNodes.get(k);
                        List<Float> floats = meshNode3D.getMeshData().getVertexPositions();
                        for (int i = 0; i < meshNode3D.getMeshData().numVertexIndexes(); i += 3) {
                            int i1 = meshNode3D.getMeshData().getVertexIndexes().get(i) * 3;
                            int i2 = meshNode3D.getMeshData().getVertexIndexes().get(i + 1) * 3;
                            int i3 = meshNode3D.getMeshData().getVertexIndexes().get(i + 2) * 3;
                            Vector4f Vector4f1 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMatrix);
                            Vector4f Vector4f2 = new Vector4f(floats.get(i2), floats.get(i2 + 1), floats.get(i2 + 2), 1.0f).mul(modelMatrix);
                            Vector4f Vector4f3 = new Vector4f(floats.get(i3), floats.get(i3 + 1), floats.get(i3 + 2), 1.0f).mul(modelMatrix);

                            Vector3f vertex1 = new Vector3f(Vector4f1.x, Vector4f1.y, Vector4f1.z);
                            Vector3f vertex2 = new Vector3f(Vector4f2.x, Vector4f2.y, Vector4f2.z);
                            Vector3f vertex3 = new Vector3f(Vector4f3.x, Vector4f3.y, Vector4f3.z);

                            float d = Intersectionf.intersectRayTriangleFront(rayStart, rayEnd, vertex1, vertex2, vertex3, 1.0e-4f);
                            if (d > 0.0f) {
                                Vector3f vector3f = new Vector3f(rayStart).add(new Vector3f(rayEnd).mul(d));
                                if (closestVector == null || rayStart.distance(vector3f) < rayStart.distance(closestVector)) {
                                    closestVector = vector3f;
                                }
                            }
                        }
                    }
                    return closestVector;
                }));
            }
            Vector3f finalClosestVector = null;
            for (Future<Vector3f> future : futures) {
                try {
                    Vector3f vector3f = future.get();
                    if (vector3f != null) {
                        if (finalClosestVector == null || vector3f.distance(rayStart) < finalClosestVector.distance(rayStart)) {
                            finalClosestVector = vector3f;
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            return finalClosestVector;
        } finally {
            executorService.shutdown();
        }
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
