package ru.jgems3d.engine.graphics.opengl.rendering.scene;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.dear_imgui.DIMGuiRenderJGems;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.shadow.ShadowScene;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.textures.TextureProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.base.ISceneRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.deferred.WorldDeferredRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward.*;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.LiquidsRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.ParticlesRender;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent.WorldTransparentRender;
import ru.jgems3d.engine.graphics.opengl.screen.window.Window;
import ru.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JGemsOpenGLRendererNew implements ISceneRenderer {
    private final SceneData sceneData;
    private final SceneRenderBaseContainer sceneRenderBaseContainer;
    private final DIMGuiRenderJGems dearImGuiRender;
    private final Set<FBOTexture2DProgram> fboSet;
    private boolean wantsToTakeScreenshot;


    private final GuiRender guiRender;
    private final InventoryRender inventoryRender;
    private final WorldTransparentRender worldTransparentRender;

    public JGemsOpenGLRendererNew(Window window, SceneData sceneData) {
        this.sceneData = sceneData;
        this.sceneRenderBaseContainer = new SceneRenderBaseContainer();

        this.guiRender = new GuiRender(this);
        this.inventoryRender = new InventoryRender(this);
        this.worldTransparentRender = new WorldTransparentRender(this);

        this.createResources(window.getWindowDimensions());

        this.dearImGuiRender = new DIMGuiRenderJGems(window, JGemsResourceManager.getGlobalGameResources().getResourceCache());
        this.fboSet = new HashSet<>();
    }

    protected WorldTransparentRender getWorldTransparentRender() {
        return this.worldTransparentRender;
    }

    protected InventoryRender getInventoryRender() {
        return this.inventoryRender;
    }

    protected GuiRender getGuiRender() {
        return this.guiRender;
    }

    public void recreateResources(Vector2i windowSize) {
        this.destroyResources();
        this.createResources(windowSize);
    }

    @Override
    public void createResources(Vector2i windowSize) {

    }

    @Override
    public void destroyResources() {
        this.getFboSet().forEach(FBOTexture2DProgram::clearFBO);
    }

    private void fillScene() {
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new WorldForwardRender(this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new SkyRender(this));
        this.getSceneRenderBaseContainer().addBaseInForwardContainer(new DebugRender(this));

        this.getSceneRenderBaseContainer().addBaseInDeferredContainer(new WorldDeferredRender(this));

        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new ParticlesRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(new LiquidsRender(this));
        this.getSceneRenderBaseContainer().addBaseInTransparencyContainer(this.worldTransparentRender);


    }

    @Override
    public void onStartRender() {
        this.getSceneRenderBaseContainer().startAll();
    }

    @Override
    public void onStopRender() {
        this.getDearImGuiRender().cleanUp();
        this.getSceneRenderBaseContainer().endAll();
        this.destroyResources();
    }

    @Override
    public void onRender(Vector2i windowSize) {

        this.takeScreenShotIfNeeded(windowSize);
    }

    private void takeScreenShotIfNeeded(Vector2i windowSize) {
        if (this.wantsToTakeScreenshot) {
            JGemsHelper.getLogger().log("Took screenshot!");
            this.writeBufferInFile(windowSize);
            this.wantsToTakeScreenshot = false;
        }
    }

    @Override
    public void onWindowResize(Vector2i windowSize) {
        this.recreateResources(windowSize);
        this.getDearImGuiRender().onResize(windowSize);
    }

    public void addFBOInSet(FBOTexture2DProgram fboTexture2DProgram) {
        this.getFboSet().add(fboTexture2DProgram);
    }

    public void takeScreenShot() {
        this.wantsToTakeScreenshot = true;
    }

    private Set<FBOTexture2DProgram> getFboSet() {
        return this.fboSet;
    }

    public DIMGuiRenderJGems getDearImGuiRender() {
        return this.dearImGuiRender;
    }

    public SceneRenderBaseContainer getSceneRenderBaseContainer() {
        return this.sceneRenderBaseContainer;
    }

    @Override
    public SceneData getSceneData() {
        return this.sceneData;
    }

    private void writeBufferInFile(Vector2i windowSize) {
        int w = windowSize.x;
        int h = windowSize.y;
        int i1 = w * h;
        ByteBuffer p = ByteBuffer.allocateDirect(i1 * 4);
        GL30.glReadPixels(0, 0, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, p);
        try {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[] pArray = new int[i1];
            p.asIntBuffer().get(pArray);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = (x + (w * y)) * 4;
                    int r = p.get(i) & 0xFF;
                    int g = p.get(i + 1) & 0xFF;
                    int b = p.get(i + 2) & 0xFF;
                    int a = p.get(i + 3) & 0xFF;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, windowSize.y - y - 1, rgb);
                }
            }
            Path scrPath = Paths.get(JGems3D.getGameFilesFolder() + "/screenshots/");
            if (!Files.exists(scrPath)) {
                Files.createDirectories(scrPath);
            }
            String builder = scrPath + "/screen_" + JGems3D.systemTime() + ".png";
            ImageIO.write(image, "PNG", new File(builder));
        } catch (IOException e) {
            JGemsHelper.getLogger().warn(e.getMessage());
        }
    }

    public static final class SceneRenderBaseContainer {
        private final Set<SceneRenderBase> forwardRenderSet;
        private final Set<SceneRenderBase> deferredRenderSet;
        private final Set<SceneRenderBase> transparencyRenderSet;
        private final Set<SceneRenderBase> guiRenderSet;
        private final Set<SceneRenderBase> inventoryRenderSet;

        public SceneRenderBaseContainer() {
            this.forwardRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.deferredRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.transparencyRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.guiRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
            this.inventoryRenderSet = new TreeSet<>(Comparator.comparingInt(SceneRenderBase::getRenderOrder));
        }

        public static void renderSceneRenderSet(float partialTicks, Set<SceneRenderBase> sceneRenderBases) {
            sceneRenderBases.forEach(e -> e.onRender(partialTicks));
        }

        public void endAll() {
            this.getForwardRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStopRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStopRender);
        }

        public void startAll() {
            this.getForwardRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getDeferredRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getTransparencyRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getGuiRenderSet().forEach(SceneRenderBase::onStartRender);
            this.getInventoryRenderSet().forEach(SceneRenderBase::onStartRender);
        }

        public void addBaseInGUIContainer(SceneRenderBase base) {
            this.getGuiRenderSet().add(base);
        }

        public void addBaseInInventoryForwardContainer(SceneRenderBase base) {
            this.getInventoryRenderSet().add(base);
        }

        public void addBaseInForwardContainer(SceneRenderBase base) {
            this.getForwardRenderSet().add(base);
        }

        public void addBaseInDeferredContainer(SceneRenderBase base) {
            this.getDeferredRenderSet().add(base);
        }

        public void addBaseInTransparencyContainer(SceneRenderBase base) {
            this.getTransparencyRenderSet().add(base);
        }

        public Set<SceneRenderBase> getInventoryRenderSet() {
            return this.inventoryRenderSet;
        }

        public Set<SceneRenderBase> getGuiRenderSet() {
            return this.guiRenderSet;
        }

        public Set<SceneRenderBase> getTransparencyRenderSet() {
            return this.transparencyRenderSet;
        }

        public Set<SceneRenderBase> getDeferredRenderSet() {
            return this.deferredRenderSet;
        }

        public Set<SceneRenderBase> getForwardRenderSet() {
            return this.forwardRenderSet;
        }
    }
}