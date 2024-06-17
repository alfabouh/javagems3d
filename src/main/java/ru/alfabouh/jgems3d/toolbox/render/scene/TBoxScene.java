package ru.alfabouh.jgems3d.toolbox.render.scene;

import javafx.util.Pair;
import org.joml.*;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.render.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.TBoxMapSys;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.read.TBoxEditorReader;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.TBoxEditorSaver;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container.SaveContainer;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveMapProperties;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveModeledObject;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.map_prop.SkyProp;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.base.IMapObject;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.camera.TBoxFreeCamera;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.DIMGuiRenderTBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.ImGuiContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxModelObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.Object3DRenderer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TBoxScene {
    public static FBOTexture2DProgram sceneFbo;
    public static FBOTexture2DProgram previewItemFbo;

    private DIMGuiRenderTBox dimGuiRenderTBox;
    private final Set<TBoxScene3DObject> sceneObjects;
    private final IWindow window;
    private final TransformationUtils transformationUtils;
    private ICamera camera;

    public TBoxScene(TransformationUtils transformationUtils, IWindow window) {
        this.sceneObjects = new TreeSet<>(Comparator.comparingInt(TBoxScene3DObject::getId));
        this.transformationUtils = transformationUtils;
        this.window = window;
    }

    public void resetEditor() {
        SystemLogging.get().getLogManager().log("Resetting editor...");
        this.clear();
        this.setGUIEditor();
    }

    public void tryLoadMap(EditorContent editorContent, File file) {
        SaveContainer saveContainer;
        try {
            if (file == null || !Files.exists(file.toPath())) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Read");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int userSelection = jFileChooser.showOpenDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    file = jFileChooser.getSelectedFile();
                }
            }
            if (file != null) {
                saveContainer = TBoxEditorReader.readMapFolder(file);
                ToolBox.get().getTBoxSettings().recentPathOpen.setValue(new File(file.toString()).getAbsolutePath());

                SaveMapProperties mapObjectProperties = saveContainer.getSaveMapProperties();
                if (mapObjectProperties.getMapName() == null || mapObjectProperties.getMapName().isEmpty()) {
                    throw new JGemsException("Wrong file provided");
                }

                Set<SaveModeledObject> saveObjectSet = saveContainer.getSaveObjectSet();
                this.clear();

                for (SaveModeledObject saveObject : saveObjectSet) {
                    Format3D format3D = new Format3D();
                    MeshDataGroup meshDataGroup = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(saveObject.getObjectId()).meshDataGroup();
                    format3D.setPosition(saveObject.getPosition());
                    format3D.setRotation(saveObject.getRotation());
                    format3D.setScale(saveObject.getScaling());
                    TBoxModelObject tBoxModelObject = new TBoxModelObject(saveObject.getObjectId(), new TBoxObjectRenderData(ResourceManager.shaderAssets.world_object, new Object3DRenderer(editorContent)), new Model<>(format3D, meshDataGroup));
                    tBoxModelObject.setMapObjectProperties(saveObject.getMapObjectProperties());
                    this.addObject(tBoxModelObject);
                }

                editorContent.mapName.set(mapObjectProperties.getMapName());
                if (mapObjectProperties.getSkyProp() != null) {
                    editorContent.sunBrightness[0] = mapObjectProperties.getSkyProp().sunBrightness;
                    if (mapObjectProperties.getSkyProp().sunColor != null) {
                        editorContent.sunClr[0] = (float) mapObjectProperties.getSkyProp().sunColor.x;
                        editorContent.sunClr[1] = (float) mapObjectProperties.getSkyProp().sunColor.y;
                        editorContent.sunClr[2] = (float) mapObjectProperties.getSkyProp().sunColor.z;
                    }
                    if (mapObjectProperties.getSkyProp().sunPos != null) {
                        editorContent.sunPosX[0] = (float) mapObjectProperties.getSkyProp().sunPos.x;
                        editorContent.sunPosY[0] = (float) mapObjectProperties.getSkyProp().sunPos.y;
                        editorContent.sunPosZ[0] = (float) mapObjectProperties.getSkyProp().sunPos.z;
                    }
                } else {
                    throw new JGemsException("Wrong file provided");
                }
                if (mapObjectProperties.getFogProp() != null) {
                    if (mapObjectProperties.getFogProp().fogColor != null) {
                        editorContent.fogClr[0] = (float) mapObjectProperties.getFogProp().fogColor.x;
                        editorContent.fogClr[1] = (float) mapObjectProperties.getFogProp().fogColor.y;
                        editorContent.fogClr[2] = (float) mapObjectProperties.getFogProp().fogColor.z;
                    }
                } else {
                    throw new JGemsException("Wrong file provided");
                }
                editorContent.fogCheck = mapObjectProperties.getFogProp().fogEnabled;
                editorContent.skyCoveredByFog = mapObjectProperties.getFogProp().skyCoveredByFog;
            }
        } catch (IOException | ClassNotFoundException| JGemsException e) {
            SystemLogging.get().getLogManager().exception(e);
            LoggingManager.showExceptionDialog("Found errors, while reading map!");
        }
    }

    public void prepareMapToSave(EditorContent editorContent, File file) {
        SkyProp skyProp = new SkyProp(new Vector3d(editorContent.getSunPos()), new Vector3d(editorContent.sunClr), editorContent.sunBrightness[0]);
        FogProp fogProp = new FogProp(new Vector3d(editorContent.fogClr), editorContent.fogDensity[0], editorContent.fogCheck, editorContent.skyCoveredByFog);
        SaveContainer saveContainer = new SaveContainer(new SaveMapProperties(editorContent.mapName.get(), skyProp, fogProp));

        for (TBoxScene3DObject boxScene3DObject : this.getSceneObjects()) {
            if (boxScene3DObject instanceof TBoxModelObject) {
                TBoxModelObject boxModelObject = (TBoxModelObject) boxScene3DObject;
                saveContainer.addSaveObject(SaveModeledObject.constructSaveContainer(boxModelObject.getMapObjectProperties(), boxModelObject.objectId(), boxModelObject.getModel().getFormat().getPosition(), boxModelObject.getModel().getFormat().getRotation(), boxModelObject.getModel().getFormat().getScale()));
            }
        }
        try {
            if (file == null || !Files.exists(file.toPath())) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Save");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int userSelection = jFileChooser.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    file = jFileChooser.getSelectedFile();
                }
            }
            if (file != null) {
                TBoxEditorSaver.saveEditorToJSON(saveContainer, file);
                String path = new File(file.toString()).getAbsolutePath();
                ToolBox.get().getTBoxSettings().recentPathSave.setValue(path);
            }
        } catch (IOException e) {
            SystemLogging.get().getLogManager().exception(e);
            LoggingManager.showExceptionDialog("Found errors, while saving map!");
        }
    }

    public void preRender() {
        this.createFBOs(this.getWindow().getWindowDimensions());
        this.camera = new TBoxFreeCamera(ToolBox.get().getScreen().getControllerDispatcher().getMouseKeyboardController(), new Vector3d(-0.0f), new Vector3d(0.0d));
        this.setGUIEditor();
        SystemLogging.get().getLogManager().log("Pre-Scene Render");

        String recentStrOpen = ToolBox.get().getTBoxSettings().recentPathOpen.getValue();
        if (!recentStrOpen.isEmpty()) {
            ImGuiContent content = this.getDimGuiRenderTBox().getCurrentContentToRender();
            if (content instanceof EditorContent) {
                EditorContent editorContent = (EditorContent) content;
                this.tryLoadMap(editorContent, new File(recentStrOpen));
            }
        }
    }

    public void postRender() {
        SystemLogging.get().getLogManager().log("Post-Scene Render");
        this.clear();
        this.destroyFBOs();
        this.getDimGuiRenderTBox().cleanUp();
        ToolBox.get().getScreen().getResourceManager().destroy();
    }

    public void render(double deltaTime) {
        if (!this.isActiveScene()) {
            return;
        }
        this.getCamera().updateCamera(deltaTime);
        TBoxScene.sceneFbo.bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.getSceneObjects().forEach(e -> e.getRenderData().getObjectRenderer().onRender(e, deltaTime));
        if (this.getDimGuiRenderTBox().getCurrentContentToRender() instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) this.getDimGuiRenderTBox().getCurrentContentToRender();
            Model<Format3D> modelSun = MeshHelper.generateVector3DModel(new Vector3f(0.0f), editorContent.getSunPos().mul(300.0f));
            ResourceManager.shaderAssets.world_lines.bind();
            ResourceManager.shaderAssets.world_lines.getUtils().performPerspectiveMatrix();
            ResourceManager.shaderAssets.world_lines.getUtils().performViewMatrix(TBoxSceneUtils.getMainCameraViewMatrix());
            ResourceManager.shaderAssets.world_lines.performUniform("colour", new Vector4d(1.0f, 1.0f, 0.0f, 1.0f));
            TBoxSceneUtils.renderModel(modelSun, GL30.GL_LINES);
            ResourceManager.shaderAssets.world_lines.unBind();
            modelSun.clean();
            if (editorContent.currentSelectedObject != null) {
                Model<Format3D> model = MeshHelper.generateWirebox3DModel(MathHelper.convertV3DV3F(editorContent.currentSelectedObject.getLocalCollision().getAabb().getMin()), MathHelper.convertV3DV3F(editorContent.currentSelectedObject.getLocalCollision().getAabb().getMax()));
                ResourceManager.shaderAssets.world_lines.bind();
                ResourceManager.shaderAssets.world_lines.getUtils().performPerspectiveMatrix();
                ResourceManager.shaderAssets.world_lines.getUtils().performViewMatrix(TBoxSceneUtils.getMainCameraViewMatrix());
                ResourceManager.shaderAssets.world_lines.performUniform("colour", new Vector4d(0.0f, 1.0f, 0.0f, 1.0f));
                TBoxSceneUtils.renderModel(model, GL30.GL_LINES);
                ResourceManager.shaderAssets.world_lines.unBind();
                model.clean();
            }
        }
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        this.showXYZ();
        TBoxScene.sceneFbo.unBindFBO();

        TBoxScene.previewItemFbo.bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        ImGuiContent content = this.getDimGuiRenderTBox().getCurrentContentToRender();
        if (content instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) content;
            this.renderIsometricEditorItem(TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(editorContent.objectIds[editorContent.objectInt.get()]), editorContent.previewBorders[0]);
        }
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        TBoxScene.previewItemFbo.unBindFBO();

        this.getDimGuiRenderTBox().render(deltaTime);
    }

    private void showXYZ() {
        GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        Format3D format3D = new Format3D();
        Model<Format3D> model = new Model<>(format3D, ToolBox.get().getResourceManager().getModelResources().xyz);
        Quaterniond quaterniond = new Quaterniond();
        Matrix4d matrix4d = TBoxSceneUtils.getMainCameraViewMatrix();
        matrix4d.getNormalizedRotation(quaterniond);
        Matrix4d inversedView = new Matrix4d().identity().rotate(quaterniond);
        ResourceManager.shaderAssets.world_xyz.bind();
        ResourceManager.shaderAssets.world_xyz.getUtils().performOrthographicMatrix(this.getWindow().getWindowDimensions().x / (float) this.getWindow().getWindowDimensions().y, 36.0f);
        ResourceManager.shaderAssets.world_xyz.getUtils().performModel3DMatrix(model);
        ResourceManager.shaderAssets.world_xyz.performUniform("view_inversed", inversedView);
        TBoxSceneUtils.renderModelTextured(ResourceManager.shaderAssets.world_xyz, model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.world_xyz.unBind();
        GL30.glDisable(GL30.GL_DEPTH_TEST);
    }

    private void renderIsometricEditorItem(IMapObject mapObject, float borders) {
        GL30.glViewport(0, 0, 400, 400);
        TBoxShaderManager shaderManager = ResourceManager.shaderAssets.world_isometric_object;
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(1.0f, borders);
        shaderManager.getUtils().performModel3DMatrix(new Matrix4d().identity().lookAt(new Vector3d(1.0d, 1.0d, 1.0d), new Vector3d(0.0d), new Vector3d(0.0d, 1.0d, 0.0d)));
        TBoxSceneUtils.renderModelTextured(shaderManager, mapObject.meshDataGroup(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
        ToolBox.get().getScreen().normalizeViewPort();
    }

    public boolean tryGrabObject(EditorContent editorContent) {
        Vector2d sceneFrameMin = editorContent.getSceneFrameMin();
        Vector2d sceneFrameMax = editorContent.getSceneFrameMax();
        Vector2d mouseCVector = new Vector2d(ToolBox.get().getScreen().getControllerDispatcher().getMouseKeyboardController().getMouseAndKeyboard().getCursorCoordinates());

        Vector3d camTo = this.getMouseRay(mouseCVector, sceneFrameMin, sceneFrameMax).mul(100.0f);
        Vector3d camPos = this.getCamera().getCamPosition();

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneObjects().stream().filter(e -> e.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
        List<Pair<Vector3d, TBoxScene3DObject>> intersections = intersectedAABBs.stream().map(obj -> new Pair<>(obj.getLocalCollision().findClosesPointRayIntersectObjectMesh(obj.getModel().getFormat(), camPos, camTo), obj)).filter(pair -> pair.getKey() != null).sorted(Comparator.comparingDouble(pair -> pair.getKey().distance(camPos))).collect(Collectors.toList());

        if (intersections.isEmpty()) {
            editorContent.removeSelection();
            return false;
        }

        TBoxScene3DObject closestObject = intersections.get(0).getValue();
        if (!editorContent.trySelectObject(closestObject)) {
            throw new JGemsException("Occurred error while trying to select NULL object");
        }

        return true;
    }

    public void placeObjectFromGUI(EditorContent editorContent, String nameId) {
        MeshDataGroup meshDataGroup = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(nameId).meshDataGroup();
        Vector3d camRot = this.getCamera().getCamRotation();
        Vector3d camPos = this.getCamera().getCamPosition();
        Vector3d camTo = MathHelper.calcLookVector(camRot).normalize();

        Format3D format3D = new Format3D();
        format3D.setPosition(new Vector3d(camPos).add(MathHelper.calcLookVector(camRot).mul(5.0f)));

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneObjects().stream().filter(obj -> obj.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
        List<Vector3d> intersections = intersectedAABBs.stream().map(obj -> obj.getLocalCollision().findClosesPointRayIntersectObjectMesh(obj.getModel().getFormat(), camPos, camTo)).filter(Objects::nonNull).filter(e -> e.distance(camPos) < 15.0f).sorted(Comparator.comparingDouble(e -> e.distance(camPos))).collect(Collectors.toList());

        if (!intersections.isEmpty()) {
            Vector3d closestObject = intersections.get(0);
            format3D.setPosition(closestObject);
        }

        this.addObject(new TBoxModelObject(nameId, new TBoxObjectRenderData(ResourceManager.shaderAssets.world_object, new Object3DRenderer(editorContent)), new Model<>(format3D, meshDataGroup)));
    }

    public Vector3d getMouseRay(Vector2d mouseCoordinates, Vector2d sceneFrameMin, Vector2d sceneFrameMax) {
        Matrix4d projectionMatrix = TBoxSceneUtils.getMainPerspectiveMatrix();
        Matrix4d viewMatrix = TBoxSceneUtils.getMainCameraViewMatrix();

        double normalizedX = 2.0 * (mouseCoordinates.x - sceneFrameMin.x) / (sceneFrameMax.x - sceneFrameMin.x) - 1.0;
        double normalizedY = 1.0 - 2.0 * (mouseCoordinates.y - sceneFrameMin.y) / (sceneFrameMax.y - sceneFrameMin.y);

        Vector4d clipCoordinates = new Vector4d(normalizedX, normalizedY, -1.0, 1.0);

        Matrix4d invertedProjection = projectionMatrix.invert();
        Vector4d eyeCoordinates = invertedProjection.transform(clipCoordinates);
        eyeCoordinates.z = -1.0;
        eyeCoordinates.w = 0.0;

        Matrix4d invertedView = viewMatrix.invert();
        Vector4d worldCoordinates = invertedView.transform(eyeCoordinates);

        Vector3d mouseRay = new Vector3d(worldCoordinates.x, worldCoordinates.y, worldCoordinates.z);
        mouseRay.normalize();

        return mouseRay;
    }

    public boolean isActiveScene() {
        return this.getWindow().isActive();
    }

    public void createGUI() {
        this.dimGuiRenderTBox = new DIMGuiRenderTBox(this.getWindow(), ToolBox.get().getResourceManager().getCache());
    }

    private void setGUIEditor() {
        this.getDimGuiRenderTBox().setCurrentContentToRender(new EditorContent(this));
    }

    private void createFBOs(Vector2i dim) {
        FBOTexture2DProgram.FBOTextureInfo[] psxFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA)
                };

        TBoxScene.sceneFbo = new FBOTexture2DProgram(true);
        TBoxScene.previewItemFbo = new FBOTexture2DProgram(true);
        TBoxScene.sceneFbo.createFrameBuffer2DTexture(dim, psxFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
        TBoxScene.previewItemFbo.createFrameBuffer2DTexture(new Vector2i(400, 400), psxFBOs, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
    }

    private void destroyFBOs() {
        TBoxScene.sceneFbo.clearFBO();
        TBoxScene.previewItemFbo.clearFBO();
    }

    private void clear() {
        this.getSceneObjects().forEach(this::objectPostRender);
        this.getSceneObjects().clear();
    }

    public void removeObject(TBoxScene3DObject scene3DObject) {
        this.getSceneObjects().remove(scene3DObject);
        this.objectPostRender(scene3DObject);
    }

    public void addObject(TBoxScene3DObject scene3DObject) {
        this.getSceneObjects().add(scene3DObject);
        this.objectPreRender(scene3DObject);
    }

    private void objectPreRender(TBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Pre-Render!");
    }

    private void objectPostRender(TBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Post-Render!");
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }

    public void onWindowResize(Vector2i dim) {
        this.getDimGuiRenderTBox().onResize(dim);

        this.destroyFBOs();
        this.createFBOs(dim);
    }

    public DIMGuiRenderTBox getDimGuiRenderTBox() {
        return this.dimGuiRenderTBox;
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public TransformationUtils getTransformationUtils() {
        return this.transformationUtils;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public Set<TBoxScene3DObject> getSceneObjects() {
        return this.sceneObjects;
    }
}