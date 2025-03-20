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
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.nodes.templates.WIGluingRenderNode;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.EditorInterface;

import java.util.*;

public class SceneInterfaceComponent {
    private final EditorInterface editorInterface;

    public SceneInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {

    }

    public void sceneContent() {
        float[] view = JGemsTransformManager.INSTANCE.getCameraViewMatrix().get(new float[16]);
        float[] projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix().get(new float[16]);
        float[] model = TransformUtils.getModelMatrix(new Pose3D()).get(new float[16]);
        ImGuizmo.setAllowAxisFlip(true);
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();

        float availableX = ImGui.getContentRegionAvailX();
        float availableY = ImGui.getContentRegionAvailY();

        if (!this.getEditorInterface().getContextComponent().isCameraCheckBox()) {
            ImGuizmo.drawGrid(view, projection, model, (int) WBench.MAP_SIZE);
        }

        WIGluingRenderNode gluingRenderNode = this.getEditorInterface().getOpenGLRenderer().getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        ImGui.image(gluingRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
        int imagePosX = (int) ImGui.getItemRectMinX();
        int imagePosY = (int) ImGui.getItemRectMinY();
        int imageSizeX = (int) (ImGui.getItemRectSizeX());
        int imageSizeY = (int) (ImGui.getItemRectSizeY());
        if (EditorInterface.isCursorInsideScene) {
            if (ImGui.isMouseClicked(0)) {
                WBenchObject wBenchObject = this.tryToSelectObjectFromMouse(new Vector2i(imagePosX, imagePosY), new Vector2i(imageSizeX, imageSizeY), new Vector2i((int) ImGui.getMousePos().x, (int) ImGui.getMousePos().y));
                this.getEditorInterface().setCurrentSelectedObject(wBenchObject);
            }
        }

        if (!this.getEditorInterface().getContextComponent().isCameraCheckBox()) {
            ImVec2 imageSize = ImGui.getItemRectSize();
            ImVec2 imagePos = ImGui.getItemRectMin();

            float imGuizmoX = imageSize.x;
            float imGuizmoY = imageSize.y;
            ImGuizmo.setRect(imagePos.x, imagePos.y, imGuizmoX, imGuizmoY);

            // float windowWidth = ImGui.getWindowWidth();
            // float viewManipulateRight = ImGui.getWindowPosX() + windowWidth;
            // float viewManipulateTop = ImGui.getWindowPosY();
            // ImGuizmo.viewManipulate(view, 1f, new float[]{viewManipulateRight - 128, viewManipulateTop + 24}, new float[]{128f, 128f}, 0x10101010);

            if (this.getEditorInterface().getCurrentSelectedObject() != null) {
                CullingAABB cullingAABB = this.getEditorInterface().getCurrentSelectedObject().pickAABBDataFromMesh();
                if (cullingAABB != null) {
                    WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                    float[] modelMatrix = TransformUtils.getModelMatrix(this.getEditorInterface().getCurrentSelectedObject().getModel().getPose()).get(new float[16]);
                    ImGuizmo.manipulate(view, projection, modelMatrix, this.getEditorInterface().getCurrentOperation(), Mode.WORLD, new float[]{0.01f, 0.01f, 0.01f});

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
    }

    private WBenchObject tryToSelectObjectFromMouse(Vector2i sceneWindowPos, Vector2i sceneWindowSize, Vector2i mouseCoordinates) {
        Vector2i screenSize = this.getEditorInterface().getOpenGLRenderer().getWindowSize();
        Vector2i mousePosRelatedToWindow = mouseCoordinates.sub(sceneWindowPos);

        float ndcMouseX = ((float) (2 * mousePosRelatedToWindow.x) / sceneWindowSize.x) - 1.0f;
        float ndcMouseY = 1.0f - ((float) (2 * mousePosRelatedToWindow.y) / sceneWindowSize.y);

        System.out.println(ndcMouseX + " " + ndcMouseY);

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
            Vector3f intersection = this.findClosesPointRayIntersectMesh(object.getModel(), origin, camRay);
            if (intersection != null) {
                sceneObjects.add(new Pair<>(object, origin.distance(intersection)));
            }
        }
        sceneObjects.sort(Comparator.comparingDouble(Pair::getSecond));
        return sceneObjects.isEmpty() ? null : (WBenchObject) sceneObjects.get(0).getFirst();
    }

    public Vector3f findClosesPointRayIntersectMesh(Model3D model3D, Vector3f rayStart, Vector3f rayEnd) {
        Vector3f closestVector = null;
        MeshStructure3D<?> meshStructure3D = model3D.getMeshStructure();
        Matrix4f modelMatrix = TransformUtils.getModelMatrix(model3D.getPose());

        for (MeshNode3D<?> meshNode3D : meshStructure3D.getAllNodes()) {
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
    }

    public boolean isRayIntersectObjectAABB(CullingAABB cullingAABB, Vector3f rayStart, Vector3f rayEnd) {
        return Intersectionf.testRayAab(rayStart, rayEnd, cullingAABB.getAabbMin(), cullingAABB.getAabbMax());
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
