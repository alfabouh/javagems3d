package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.collections.Pair;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.nodes.templates.WIGluingRenderNode;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.EditorInterface;

import java.lang.Math;
import java.lang.Runtime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SceneInterfaceComponent {
    private final EditorInterface editorInterface;
    private AtomicBoolean isThreadInProcess;
    private boolean usedGuizmoLastFrame;
    private boolean wasWindowFocused;

    public SceneInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.isThreadInProcess = new AtomicBoolean();
        this.clear();
    }

    public void clear() {
        this.isThreadInProcess.set(false);
        this.wasWindowFocused = false;
        this.usedGuizmoLastFrame = false;
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

        WIGluingRenderNode gluingRenderNode = this.getEditorInterface().getOpenGLRenderer().getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        ImGui.image(gluingRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
        int imagePosX = (int) ImGui.getItemRectMinX();
        int imagePosY = (int) ImGui.getItemRectMinY();
        int imageSizeX = (int) (ImGui.getItemRectSizeX());
        int imageSizeY = (int) (ImGui.getItemRectSizeY());

        ImGuizmo.setRect(imagePosX, imagePosY, imageSizeX, imageSizeY);

        if (!this.wasWindowFocused && !this.getEditorInterface().getContextComponent().isCameraCheckBox()) {
            if (!this.usedGuizmoLastFrame && EditorInterface.isCursorInsideScene) {
                if (ImGui.isMouseReleased(0)) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    if (!this.isThreadInProcess.get()) {
                        executor.execute(() -> {
                            try {
                                this.isThreadInProcess.set(true);
                                WBenchObject wBenchObject = this.tryToSelectObjectFromMouse(new Vector2i(imagePosX, imagePosY), new Vector2i(imageSizeX, imageSizeY), new Vector2i((int) ImGui.getMousePos().x, (int) ImGui.getMousePos().y));
                                this.getEditorInterface().setCurrentSelectedObject(wBenchObject);
                            } finally {
                                this.isThreadInProcess.set(false);
                                executor.shutdown();
                            }
                        });
                    }
                }
            }
        }
        this.usedGuizmoLastFrame = false;
        if (!this.getEditorInterface().getContextComponent().isCameraCheckBox()) {
            if (this.getEditorInterface().getCurrentSelectedObject() != null) {
                CullingAABB cullingAABB = this.getEditorInterface().getCurrentSelectedObject().pickAABBDataFromMesh();
                if (cullingAABB != null) {
                    WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                    float[] modelMatrix = TransformUtils.getModelMatrix(this.getEditorInterface().getCurrentSelectedObject().getModel().getPose()).get(new float[16]);
                    float[] oldMatrix = new float[16];
                    System.arraycopy(modelMatrix, 0, oldMatrix, 0, 16);
                    ImGuizmo.manipulate(view, projection, modelMatrix, this.getEditorInterface().getCurrentOperation(), Mode.WORLD, new float[]{0.01f, 0.01f, 0.01f});
                    if (!Arrays.equals(oldMatrix, modelMatrix)) {
                        this.usedGuizmoLastFrame = true;
                    }

                    Vector3f position = new Vector3f();
                    Vector3f rotation = new Vector3f();
                    Vector3f scaling = new Vector3f();
                    Matrix4f newMatrix = this.getEditorInterface().getMatrixFromArray(modelMatrix);
                    newMatrix.getTranslation(position);
                    newMatrix.getScale(scaling);
                    newMatrix.getUnnormalizedRotation(new Quaternionf()).getEulerAnglesXYZ(rotation);

                    this.getEditorInterface().getCurrentSelectedObject().setPosition(position);
                    this.getEditorInterface().getCurrentSelectedObject().setRotation(rotation.negate());
                    this.getEditorInterface().getCurrentSelectedObject().setScaling(scaling);
                }
            }
        }

        if (ImGui.isWindowFocused()) {
            if (ImGui.isMouseReleased(0)) {
                this.wasWindowFocused = false;
            }
            WBench.get().getScreen().getWindow().setInFocus(true);
        } else {
            this.wasWindowFocused = true;
            WBench.get().getScreen().getWindow().setInFocus(false);
        }
    }

    private WBenchObject tryToSelectObjectFromMouse(Vector2i sceneWindowPos, Vector2i sceneWindowSize, Vector2i mouseCoordinates) {
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
        Set<SceneObject> intersectedAabbs = new HashSet<>();

        Vector3f origin = this.getEditorInterface().getOpenGLRenderer().getCamera().getCamPosition();

        if (this.getEditorInterface().getVisibleObjects() != null) {
            for (SceneObject sceneObject : this.getEditorInterface().getVisibleObjects()) {
                CullingAABB cullingAABB = sceneObject.pickAABBDataFromMesh();
                if (cullingAABB != null) {
                    Vector2f vector2f = new Vector2f();
                    if (Intersectionf.intersectRayAab(origin, camRay, cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), vector2f)) {
                        intersectedAabbs.add(sceneObject);
                    }
                }
            }
        }

        List<Pair<SceneObject, Float>> sceneObjects = new ArrayList<>();
        for (SceneObject object : intersectedAabbs) {
            int optimalThreads = Runtime.getRuntime().availableProcessors();
            Vector3f intersection = this.findClosesPointRayIntersectMesh(object.getModel(), origin, camRay, optimalThreads);
            if (intersection != null) {
                sceneObjects.add(new Pair<>(object, origin.distance(intersection)));
            }
        }
        sceneObjects.sort(Comparator.comparingDouble(Pair::getSecond));
        return sceneObjects.isEmpty() ? null : (WBenchObject) sceneObjects.get(0).getFirst();
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

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
