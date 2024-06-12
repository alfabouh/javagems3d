package ru.alfabouh.jgems3d.toolbox.render.scene;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.FBOTexture2DProgram;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.render.transformation.TransformationUtils;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.camera.TBoxFreeCamera;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.DIMGuiRenderTBox;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.ITBoxScene3DObject;

import java.util.HashSet;
import java.util.Set;

public class TBoxScene {
    public static FBOTexture2DProgram sceneFbo;

    private DIMGuiRenderTBox dimGuiRenderTBox;
    private final Set<ITBoxScene3DObject> sceneObjects;
    private final IWindow window;
    private final TransformationUtils transformationUtils;
    private ICamera camera;

    public TBoxScene(TransformationUtils transformationUtils, IWindow window) {
        this.sceneObjects = new HashSet<>();
        this.transformationUtils = transformationUtils;
        this.window = window;
    }

    public void render(double deltaTime) {
        this.getCamera().updateCamera(deltaTime);

        TBoxScene.sceneFbo.bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.getSceneObjects().forEach(e -> e.getRenderData().getObjectRenderer().onRender(e, deltaTime));
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        TBoxScene.sceneFbo.unBindFBO();

        this.getDimGuiRenderTBox().render(deltaTime);
    }

    public void createGUI() {
        this.dimGuiRenderTBox = new DIMGuiRenderTBox(this.getWindow(), ToolBox.get().getResourceManager().getCache());
    }

    public void preRender() {
        this.createFBOs(this.getWindow().getWindowDimensions());
        this.camera = new TBoxFreeCamera(ToolBox.get().getScreen().getControllerDispatcher().getMouseKeyboardController(), new Vector3d(-0.0f), new Vector3d(0.0d));
        this.getDimGuiRenderTBox().setCurrentContentToRender(new EditorContent());
        SystemLogging.get().getLogManager().log("Pre-Scene Render");
    }

    public void postRender() {
        SystemLogging.get().getLogManager().log("Post-Scene Render");
        this.clear();
        this.destroyFBOs();
        this.getDimGuiRenderTBox().cleanUp();
        ToolBox.get().getScreen().getResourceManager().destroy();
    }

    private void createFBOs(Vector2i dim) {
        TBoxScene.sceneFbo = new FBOTexture2DProgram(true);
        FBOTexture2DProgram.FBOTextureInfo[] psxFBOs = new FBOTexture2DProgram.FBOTextureInfo[]
                {
                        new FBOTexture2DProgram.FBOTextureInfo(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA)
                };
        TBoxScene.sceneFbo.createFrameBuffer2DTexture(dim, psxFBOs, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
    }

    private void destroyFBOs() {
        TBoxScene.sceneFbo.clearFBO();
    }

    private void clear() {
        this.getSceneObjects().forEach(this::objectPostRender);
        this.getSceneObjects().clear();
    }

    public void removeObject(ITBoxScene3DObject scene3DObject) {
        this.getSceneObjects().remove(scene3DObject);
        this.objectPostRender(scene3DObject);
    }

    public void addObject(ITBoxScene3DObject scene3DObject) {
        this.getSceneObjects().add(scene3DObject);
        this.objectPreRender(scene3DObject);
    }

    private void objectPreRender(ITBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject.getStringID() + " - Pre-Render!");
    }

    private void objectPostRender(ITBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject.getStringID() + " - Post-Render!");
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

    public Set<ITBoxScene3DObject> getSceneObjects() {
        return this.sceneObjects;
    }
}
