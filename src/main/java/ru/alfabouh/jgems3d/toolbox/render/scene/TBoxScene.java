package ru.alfabouh.jgems3d.toolbox.render.scene;

import javafx.util.Pair;
import org.joml.*;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.render.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.proxy.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.TBoxMapSys;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.read.TBoxEditorReader;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.TBoxEditorSaver;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container.SaveContainer;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveObject;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.AbstractObjectData;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ObjectType;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.Attribute;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeIDS;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.camera.TBoxFreeCamera;
import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.toolbox.render.scene.container.SceneContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.DIMGuiRenderTBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.ImGuiContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;
import ru.alfabouh.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import javax.swing.*;
import java.io.File;
import java.lang.Math;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TBoxScene {
    public static FBOTexture2DProgram sceneFbo;
    public static FBOTexture2DProgram previewItemFbo;

    private DIMGuiRenderTBox dimGuiRenderTBox;
    private final SceneContainer sceneObjects;
    private final IWindow window;
    private final TransformationUtils transformationUtils;
    private ICamera camera;

    public TBoxScene(TransformationUtils transformationUtils, IWindow window) {
        this.sceneObjects = new SceneContainer();
        this.transformationUtils = transformationUtils;
        this.window = window;
    }

    public void resetEditor() {
        SystemLogging.get().getLogManager().log("Resetting editor...");
        this.clear();
        this.setGUIEditor();
    }

    public void tryLoadMap(File file) {
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

                MapProperties mapObjectProperties = saveContainer.getSaveMapProperties();
                if (mapObjectProperties == null) {
                    throw new JGemsException("Invalid deserialization!");
                }

                if (mapObjectProperties.getMapName() == null || mapObjectProperties.getMapName().isEmpty()) {
                    throw new JGemsException("Invalid file provided");
                }

                Set<SaveObject> saveObjects = saveContainer.getSaveObjectsSet();
                this.clear();

                if (saveObjects != null) {
                    for (SaveObject saveObject : saveObjects) {
                        Vector3d savePos = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.POSITION_XYZ, Vector3d.class);
                        Vector3d saveRot = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.ROTATION_XYZ, Vector3d.class);
                        Vector3d saveScale = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.SCALING_XYZ, Vector3d.class);
                        if (savePos == null) {
                            SystemLogging.get().getLogManager().error("Deserialized object has NULL position!!");
                            continue;
                        }
                        if (saveRot != null) {
                            saveRot = new Vector3d(Math.toRadians(saveRot.x), Math.toRadians(saveRot.y), Math.toRadians(saveRot.z));
                        }
                        Format3D format3D = new Format3D(savePos, saveRot, saveScale);
                        AbstractObjectData mapObject = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(saveObject.getObjectId());
                        MeshDataGroup meshDataGroup = mapObject.meshDataGroup();
                        TBoxObject tBoxModelObject = new TBoxObject(saveObject.getObjectId(), new TBoxObjectRenderData(mapObject.getShaderManager(), mapObject.getObjectRenderer()), new Model<>(format3D, meshDataGroup));
                        tBoxModelObject.setAttributeContainer(saveObject.getAttributeContainer());
                        this.addObject(tBoxModelObject);
                    }
                } else {
                    SystemLogging.get().getLogManager().error("Couldn't read objects file from map!");
                    LoggingManager.showExceptionDialog("Couldn't read objects file from map!");
                }

                this.getSceneContainer().setMapProperties(mapObjectProperties);
            }
        } catch (Exception e) {
            SystemLogging.get().getLogManager().exception(e);
            LoggingManager.showExceptionDialog("Found errors, while reading map!");
        }
    }

    public void prepareMapToSave(File file) {
        if (this.getMapProperties().getMapName() == null || this.getMapProperties().getMapName().isEmpty()) {
            LoggingManager.showWindowInfo("Enter map name!");
            return;
        }

        SaveContainer saveContainer = new SaveContainer(this.getSceneContainer().getMapProperties());

        for (TBoxObject tBoxObject : this.getSceneContainer().getObjectsFromContainer(TBoxObject.class)) {
            saveContainer.addSaveObject(new SaveObject(tBoxObject.getAttributeContainer(), tBoxObject.objectId()));
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
        } catch (Exception e) {
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
            this.tryLoadMap(new File(recentStrOpen));
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
        if (this.getDimGuiRenderTBox().getCurrentContentToRender() instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) this.getDimGuiRenderTBox().getCurrentContentToRender();
            this.getSceneContainer().render(deltaTime);
            Vector3d v3 = this.getMapProperties().getSkyProp().getSunPos();
            Model<Format3D> modelSun = MeshHelper.generateVector3DModel(new Vector3f(0.0f), new Vector3f((float) v3.x, (float) v3.y, (float) v3.z).mul(300.0f));
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

        ImGuiContent content = this.getDimGuiRenderTBox().getCurrentContentToRender();
        if (content instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) content;
            String s = editorContent.currentSelectedToPlaceID;
            if (s != null && TBoxScene.canRenderOnPreview(s)) {
                AbstractObjectData mapObject = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(s);
                TBoxScene.previewItemFbo.bindFBO();
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
                GL30.glEnable(GL30.GL_DEPTH_TEST);
                this.renderIsometricEditorItem(mapObject, editorContent.previewBorders[0]);
                GL30.glDisable(GL30.GL_DEPTH_TEST);
                TBoxScene.previewItemFbo.unBindFBO();
            }
        }

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

    private void renderIsometricEditorItem(AbstractObjectData mapObject, float borders) {
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

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneContainer().getSceneObjects().stream().filter(e -> e.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
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

    public static boolean canRenderOnPreview(String nameId) {
        AbstractObjectData mapObject = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(nameId);
        return mapObject.objectType().equals(ObjectType.MODEL_OBJECT) || mapObject.objectType().equals(ObjectType.PHYSICS_OBJECT);
    }

    public void placeObjectFromGUI(EditorContent editorContent, String nameId) {
        AbstractObjectData mapObject = TBoxMapSys.INSTANCE.getObjectTable().getObjects().get(nameId);
        MeshDataGroup meshDataGroup = mapObject.meshDataGroup();
        Vector3d camRot = this.getCamera().getCamRotation();
        Vector3d camPos = this.getCamera().getCamPosition();
        Vector3d camTo = MathHelper.calcLookVector(camRot).normalize();

        Format3D format3D = new Format3D();
        format3D.setPosition(new Vector3d(camPos).add(MathHelper.calcLookVector(camRot).mul(5.0f)));

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneContainer().getSceneObjects().stream().filter(obj -> obj.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
        List<Vector3d> intersections = intersectedAABBs.stream().map(obj -> obj.getLocalCollision().findClosesPointRayIntersectObjectMesh(obj.getModel().getFormat(), camPos, camTo)).filter(Objects::nonNull).filter(e -> e.distance(camPos) < 15.0f).sorted(Comparator.comparingDouble(e -> e.distance(camPos))).collect(Collectors.toList());

        if (!intersections.isEmpty()) {
            Vector3d closestObject = intersections.get(0);
            format3D.setPosition(closestObject);
        }

        TBoxObject tBoxObject = new TBoxObject(nameId, new TBoxObjectRenderData(mapObject.getShaderManager(), mapObject.getObjectRenderer()), new Model<>(format3D, meshDataGroup));
        tBoxObject.setAttributeContainer(mapObject.copyAttributeContainer());

        Attribute<Vector3d> attribute = tBoxObject.getAttributeContainer().tryGetAttributeByID(AttributeIDS.POSITION_XYZ, Vector3d.class);
        if (attribute == null) {
            throw new JGemsException("Caught attribute with NULL position!");
        }
        attribute.setValue(format3D.getPosition());

        Vector3d rot = tBoxObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.ROTATION_XYZ, Vector3d.class);
        if (rot != null) {
            format3D.setRotation(new Vector3d(Math.toRadians(rot.x), Math.toRadians(rot.y), Math.toRadians(rot.z)));
        }

        Vector3d scaling = tBoxObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.SCALING_XYZ, Vector3d.class);
        if (scaling != null) {
            format3D.setScaling(scaling);
        }

        tBoxObject.reCalcCollision();
        this.addObject(tBoxObject);
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
        this.getSceneContainer().clear();
        this.getSceneContainer().createMapProperties();
    }

    public void removeObject(TBoxScene3DObject scene3DObject) {
        this.getSceneContainer().removeObject(scene3DObject);
    }

    public void addObject(TBoxScene3DObject scene3DObject) {
        this.getSceneContainer().addObject(scene3DObject);
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }

    public void onWindowResize(Vector2i dim) {
        this.getDimGuiRenderTBox().onResize(dim);

        this.destroyFBOs();
        this.createFBOs(dim);
    }

    public MapProperties getMapProperties() {
        return this.getSceneContainer().getMapProperties();
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

    public SceneContainer getSceneContainer() {
        return this.sceneObjects;
    }
}
