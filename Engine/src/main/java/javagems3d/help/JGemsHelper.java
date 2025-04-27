package javagems3d.help;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.JGems3D;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.IJGemsUIImp;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.screen.timer.JGemsTimer;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import javagems3d.system.resources.localisation.JGemsLocalisation;
import javagems3d.system.resources.localisation.Lang;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.system.settings.JGemsSettings;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

//@SuppressWarnings("all")
public final class JGemsHelper {
    public static JGemsHelper INSTANCE = new JGemsHelper();

    public static JGemsHelper get() {
        return JGemsHelper.INSTANCE;
    }

    public static void init(@NotNull JGemsCore core) {
        JGemsHelper.get().core = core;
    }

    private JGemsCore core;

    private final Files files;
    private final Math math;
    private final Resources resources;
    private final Render render;
    private final UI ui;
    private final Localisation localisation;
    private final World world;
    private final Screen screen;
    private final Map map;
    private final Controller controller;
    private final Camera camera;
    private final State state;

    private JGemsHelper() {
        this.files = new Files();
        this.math = new Math();
        this.resources = new Resources();
        this.render = new Render();
        this.ui = new UI();
        this.localisation = new Localisation();
        this.world = new World();
        this.screen = new Screen();
        this.map = new Map();
        this.controller = new Controller();
        this.camera = new Camera();
        this.state = new State();
    }

    public static Math math() {
        return JGemsHelper.get().math;
    }

    public static Files files() {
        return JGemsHelper.get().files;
    }

    public static Resources resources() {
        return JGemsHelper.get().resources;
    }

    public static Render render() {
        return JGemsHelper.get().render;
    }

    public static UI ui() {
        return JGemsHelper.get().ui;
    }

    public static Localisation localisation() {
        return JGemsHelper.get().localisation;
    }

    public static World world() {
        return JGemsHelper.get().world;
    }

    public static Screen screen() {
        return JGemsHelper.get().screen;
    }

    public static Map map() {
        return JGemsHelper.get().map;
    }

    public static Controller controller() {
        return JGemsHelper.get().controller;
    }

    public static Camera camera() {
        return JGemsHelper.get().camera;
    }

    public static State state() {
        return JGemsHelper.get().state;
    }


    public JGemsSettings getGameSettings() {
        return JGems3D.get().getGameSettings();
    }

    public SceneWorld getSceneWorld() {
        return (SceneWorld) this.core.getScreen().getSceneWorld();
    }

    public PhysicsWorld getPhysicsWorld() {
        return this.core.getPhysics().getPhysicsWorld();
    }

    public JGemsSoundManager getSoundManager() {
        return this.core.getSoundManager();
    }

    public final class State {
        public void pauseGameAndLockResume(boolean pauseSounds) {
            this.pauseGame(pauseSounds);
            JGemsHelper.this.core.setLockedResume(true);
        }

        public void unPauseGameAndUnLockUnPausing() {
            this.resumeGame();
            JGemsHelper.this.core.setLockedResume(false);
        }

        public void pauseGame(boolean pauseSounds) {
            this.pauseGame(pauseSounds);
            if (pauseSounds) {
                JGemsHelper.this.getSoundManager().pauseAllSounds();
            }
        }

        public void resumeGame() {
            JGemsHelper.this.core.resumeGame();
            if (!JGemsHelper.this.core.isLockedResuming()) {
                JGemsHelper.this.getSoundManager().resumeAllSounds();
            }
        }
    }

    public final class Camera {
        public ICamera getCurrentCamera() {
            return JGemsHelper.screen().getScreen().getCamera();
        }


        public void setCurrentCamera(ICamera camera) {
            JGemsHelper.screen().getScreen().getScene().setCamera(camera);
        }

        public void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
            JGemsHelper.screen().getScreen().getScene().setCamera(new ControlledCamera(controller, pos, rot));
        }

        public void enableAttachedCamera(WorldItem worldItem) {
            JGemsHelper.screen().getScreen().getScene().setCamera(JGemsHelper.this.getSceneWorld().createAttachedCamera(worldItem));
        }

        public void enableAttachedCamera(SceneEntity abstractSceneEntity) {
            JGemsHelper.screen().getScreen().getScene().setCamera(JGemsHelper.this.getSceneWorld().createAttachedCamera(abstractSceneEntity));
        }
    }

    public final class Controller {
        public JGemsControllerDispatcher getControllerDispatcher() {
            return JGemsHelper.screen().getScreen().getControllerDispatcher();
        }

        public IController getCurrentController() {
            return this.getControllerDispatcher().getCurrentController();
        }

        public BindingManager getBindingManager() {
            return this.getCurrentController().getBindingManager();
        }


        public boolean setCursorInCenter() {
            IController controller = this.getCurrentController();
            if (controller instanceof MouseKeyboardController) {
                MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
                mouseKeyboardController.setCursorInCenter();
                return true;
            }
            Log.get().warn("Couldn't find cursor. Check your controller");
            return false;
        }

        public void attachControllerTo(IController controller, IControllable remoteController) {
            this.getControllerDispatcher().attachControllerTo(controller, remoteController);
        }

        public void detachController() {
            this.getControllerDispatcher().detachController();
        }

        public void lockController() {
            this.getControllerDispatcher().setLock(true);
        }

        public void unLockController() {
            this.getControllerDispatcher().setLock(false);
        }
    }

    public final class Map {
        public IGameMap getCurrentGameMap() {
            return JGemsHelper.this.core.getCurrentGameMap();
        }

        public boolean isCurrentGameMapPlayerValid() {
            return JGemsHelper.this.core.isCurrentGameMapPlayerValid();
        }

        public boolean isCurrentGameMapValid() {
            return JGemsHelper.this.core.isCurrentGameMapValid();
        }

        public IPlayer getCurrentGameMapPlayer() {
            return JGemsHelper.this.core.getCurrentGameMapPlayer();
        }


        public void loadMap(@NotNull IMapProcessor mapProcessor) {
            JGemsHelper.this.core.loadMap(mapProcessor);
        }

        public void exitMap() {
            JGemsHelper.this.core.exitMap();
        }
    }

    public final class Screen {
        public JGemsScreen getScreen() {
            return JGemsHelper.this.core.getScreen();
        }


        public void zeroRenderTick() {
            this.getScreen().zeroRenderTick();
        }

        public TimerPool getTimerPool() {
            return this.getScreen().getTimerPool();
        }

        public JGemsTimer createTimer() {
            return this.getScreen().getTimerPool().createTimer();
        }

        public void setWindowFocus(boolean focus) {
            this.getScreen().getWindow().setFocus(focus);
        }

        public boolean isWindowActive() {
            return this.getScreen().getWindow().isWindowActive();
        }
    }

    public final class World {
        public ISkyBackground getSkyBackGround() {
            return this.getEnvironment().getSkyBox().getBackground();
        }

        public ISkyBox getSky() {
            return this.getEnvironment().getSkyBox();
        }

        public IFogScene getFog() {
            return this.getEnvironment().getFogScene();
        }

        public IEnvironment getEnvironment() {
            return JGemsHelper.this.getSceneWorld().getEnvironment();
        }


        public void killItems() {
            JGemsHelper.this.getPhysicsWorld().killItems();
        }

        public void bindPointLightShadow(int sceneId, PointLight pointLight) {
            ((ShadowScene) this.getEnvironment().getShadowScene()).bindPointLightToShadowScene(sceneId, pointLight);
        }

        public void addProp(SceneProp sceneProp) {
            JGemsHelper.this.getSceneWorld().addObject(sceneProp);
        }

        public void removeProp(SceneProp sceneProp) {
            JGemsHelper.this.getSceneWorld().removeObject(sceneProp);
        }

        public void addWorldItem(WorldItem worldItem, EntityRenderData renderData) {
            JGemsHelper.this.getPhysicsWorld().addObject(worldItem);
            JGemsHelper.this.getSceneWorld().addWorldItem(worldItem, renderData);
        }

        public void removeWorldItem(WorldItem worldItem) {
            JGemsHelper.this.getPhysicsWorld().removeItem(worldItem);
        }

        public void addLiquid(Liquid liquid, LiquidRenderData liquidRenderData) {
            JGemsHelper.this.getPhysicsWorld().addObject(liquid);
            JGemsHelper.this.getSceneWorld().addLiquid(liquid, liquidRenderData);
        }

        public void removeLiquid(Liquid liquid) {
            JGemsHelper.this.getPhysicsWorld().removeItem(liquid);
        }

        public void removeLight(Light light) {
            JGemsHelper.this.getSceneWorld().removeLight(light);
        }

        public void addLight(Light light) {
            JGemsHelper.this.getSceneWorld().addLight(light, null);
        }

        public void addLight(Light light, @Nullable ILighted lighted) {
            JGemsHelper.this.getSceneWorld().addLight(light, lighted);
        }

        public void addWorldItemLight(WorldItem worldItem, ILightAttached light) {
            JGemsHelper.this.getSceneWorld().addWorldItemLight(worldItem, light);
        }
    }

    public final class Localisation {
        public JGemsLocalisation getLocalisation() {
            return JGems3D.get().getLocalisation();
        }


        public Lang createLocalisation(String langName, JGemsPath path) {
            return JGemsLocalisation.createLocalisation(langName, path);
        }

        public void setLangLocalisationPath(Lang lang, JGemsPath path) {
            JGemsLocalisation.setLangLocalisationPath(lang, path);
        }
    }

    public final class UI {
        public void openMainMenu() {
            this.openPanel(JGems3D.getAPIAppData().getMainMenuPanel());
        }

        public void openPanel(PanelUI panelUI) {
            ((IJGemsUIImp) JGemsHelper.this.core.getScreen().getScene().getSceneRenderer()).openUIPanel(panelUI);
        }

        public void closePanel() {
            ((IJGemsUIImp) JGemsHelper.this.core.getScreen().getScene().getSceneRenderer()).openUIPanel(null);
        }
    }

    public final class Render {
        public static final int DIFFUSE_CODE = 1 << 2;
        public static final int NORMALS_CODE = 1 << 3;
        public static final int EMISSION_CODE = 1 << 4;
        public static final int METALLIC_ROUGHNESS_CODE = 1 << 5;

        public int getMaxTextureUnits() {
            return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
        }

        public void renderMeshNode(RenderMesh renderMesh) {
            GL46.glBindVertexArray(renderMesh.getVao());
            renderMesh.enableAllMeshAttributes();
            GL46.glDrawElements(GL46.GL_TRIANGLES, renderMesh.getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            renderMesh.disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }

        public void renderModel2D(Model2D model2D, int code) {
            this.renderMeshList2D(model2D.getMeshStructure().getNodes(), code);
        }

        public void renderMeshList2D(List<MeshNode2D> list, int code) {
            for (MeshNode2D meshNode2D : list) {
                this.renderMeshNode(meshNode2D.getMeshData());
            }
        }

        public void renderModel3D(Model3D model3D, int layer, int code) {
            this.renderMeshList3D(model3D.<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer), code);
        }

        public void renderMeshList3D(List<MeshNode3D<RenderMesh>> list, int code) {
            for (MeshNode3D<RenderMesh> meshNode3D : list) {
                this.renderMeshNode(meshNode3D.getMeshData());
            }
        }

        public int getTexturingCodeForShader(Material material) {
            int code = 0;
            if (material.getDiffuseMap() != null) {
                code |= Render.DIFFUSE_CODE;
            }
            if (material.getNormalsMap() != null) {
                code |= Render.NORMALS_CODE;
            }
            if (material.getEmissionMap() != null) {
                code |= Render.EMISSION_CODE;
            }
            if (material.getMetallicRoughnessMap() != null) {
                code |= Render.METALLIC_ROUGHNESS_CODE;
            }
            return code;
        }

        public void performModelMaterialOnShader(IEnvironment environment, JGemsShaderManager shaderManager, Material material) {
            if (material == null) {
                return;
            }

            ITexture2DProgram diffuseMap = material.getDiffuseMap();
            ISampleColor4 diffuseColor = material.getDiffuseColor();

            ITexture2DProgram emissionMap = material.getEmissionMap();
            ISampleColor3 emissionColor = material.getEmissionColor();

            ITexture2DProgram metallicRoughnessMap = material.getEmissionMap();
            ITexture2DProgram normalsMap = material.getNormalsMap();

            float metallicFactor = material.getMetallicFactor();
            float roughnessFactor = material.getRoughnessFactor();

            ICubeMapProgram cubeMapProgram = environment.getSkyBox().getTexture();

            shaderManager.disableWarns();
            if (cubeMapProgram != null) {
                if (shaderManager.isUniformExist(new UniformString("ambient_cubemap"))) {
                    shaderManager.performUniformTextureBindless(new UniformString("ambient_cubemap"), cubeMapProgram);
                }
                if (shaderManager.isUniformExist(new UniformString("useCubeMap"))) {
                    shaderManager.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(true));
                }
            } else {
                if (shaderManager.isUniformExist(new UniformString("useCubeMap"))) {
                    shaderManager.performUniform(new UniformString("useCubeMap"), UniformFunctions.BOOLEAN(false));
                }
            }

            shaderManager.performUniformSample(new UniformString("diffuse_color"), diffuseColor);
            shaderManager.performUniformSample(new UniformString("emission_color"), emissionColor);
            shaderManager.performUniform(new UniformString("metallic_factor"), UniformFunctions.FLOAT(metallicFactor));
            shaderManager.performUniform(new UniformString("roughness_factor"), UniformFunctions.FLOAT(roughnessFactor));

            if (diffuseMap != null) {
                shaderManager.performUniformSample(new UniformString("diffuse_map"), diffuseMap);
            }

            if (emissionMap != null) {
                shaderManager.performUniformSample(new UniformString("emission_map"), emissionMap);
            }

            if (normalsMap != null) {
                shaderManager.performUniformSample(new UniformString("normals_map"), normalsMap);
            }

            if (metallicRoughnessMap != null) {
                shaderManager.performUniformSample(new UniformString("metallicRoughnessMap"), metallicRoughnessMap);
            }

            shaderManager.performUniform(new UniformString("texturing_code"), UniformFunctions.INTEGER(this.getTexturingCodeForShader(material)));
            shaderManager.enableWarns();
        }

        public boolean performAnimationsInfo(JGemsShaderManager shaderManager, IAnimated animated) {
            shaderManager.disableWarns();
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getCurrentAnimationFrame().getOffset()));
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(!animated.hasAnimationData() ? -1 : animated.getAnimationData().getPreviousAnimationFrame().getOffset()));
            if (animated.hasAnimationData()) {
                shaderManager.performUniformTexture(new UniformString("animations_matrix"), JGemsHelper.this.resources().getAnimationsTextureBuffer());
                shaderManager.performUniform(new UniformString("animationData.deltaFrame"), UniformFunctions.FLOAT(animated.getAnimationData().getAnimationFrameDelta()));
            }
            shaderManager.enableWarns();
            return false;
        }

        public void performShadowsInfo(IEnvironment environment, JGemsShaderManager shaderManager) {
            shaderManager.disableWarns();
            ShadowScene shadowScene = (ShadowScene) environment.getShadowScene();
            for (int i = 0; i < JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES; i++) {
                SunLightShadow.Cascade cascade = shadowScene.getSunLightShadow().getCascades().get(i);
                if (shaderManager.isUniformExist(new UniformString("sun_shadow_map", i))) {
                    shaderManager.performUniformTexture(new UniformString("sun_shadow_map", i), shadowScene.getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                    shaderManager.performUniform(new UniformString("cascade_shadow", ".split_distance", i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                    shaderManager.performUniform(new UniformString("cascade_shadow", ".projection_view", i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                    shaderManager.performUniform(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
                    shaderManager.performUniform(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_NEGATIVE_EXPONENT));
                }
            }
            for (int i = 0; i < JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = shadowScene.getPointLightShadows().get(i);
                shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                if (shaderManager.isUniformExist(new UniformString("point_light_cubemap", i))) {
                    shaderManager.performUniformTexture(new UniformString("point_light_cubemap", i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
                }
            }
            shaderManager.enableWarns();
        }
    }

    public final class Resources {
        public SystemResources getLocalGameResources() {
            return this.getResourceManager().getLocalResources();
        }

        public SystemResources getGlobalGameResources() {
            return this.getResourceManager().getGlobalResources();
        }

        public ITexture2DProgram getAnimationsTextureBuffer() {
            return this.getResourceManager().getAnimationMatricesTexture();
        }

        public JGemsResourceManager getResourceManager() {
            return JGemsHelper.this.core.getResourceManager();
        }


        public void reloadResources() {
            EventLauncher.pushEvent(new EventBus.ReloadResourcesEvent(JGems3D.get().getResourceManager()));
            JGems3D.get().getScreen().showGameLoadingScreen("System01");
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Performing settings...");
            JGems3D.get().getResourceManager().recreateTexturesInAllCaches();
            JGems3D.get().getScreen().refreshSceneResources();
            JGems3D.get().getLocalisation().setLanguage(JGemsHelper.this.getGameSettings().language.getCurrentLanguage());
            this.getResourceManager().loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTexturesData);
            JGems3D.get().getScreen().removeLoadingScreen();
        }
    }

    public final class Math {
        public float interpolate(float a, float b, float f) {
            return a + f * (b - a);
        }

        public int clamp(int d1, int d2, int d3) {
            return d1 < d2 ? d2 : org.joml.Math.min(d1, d3);
        }

        public float clamp(float d1, float d2, float d3) {
            return d1 < d2 ? d2 : org.joml.Math.min(d1, d3);
        }

        public double clamp(double d1, double d2, double d3) {
            return d1 < d2 ? d2 : org.joml.Math.min(d1, d3);
        }

        public Vector3f calcLookVector(Vector3f rotations) {
            float x = rotations.x;
            float y = rotations.y;
            float lX = org.joml.Math.sin(y) * org.joml.Math.cos(x);
            float lY = -org.joml.Math.sin(x);
            float lZ = -org.joml.Math.cos(y) * org.joml.Math.cos(x);
            return new Vector3f(lX, lY, lZ);
        }

        public void clampVectorToZeroThreshold(Vector3f in, float threshold) {
            if (in.x > -threshold && in.x < threshold) {
                in.x = 0.0f;
            }
            if (in.y > -threshold && in.y < threshold) {
                in.y = 0.0f;
            }
            if (in.z > -threshold && in.z < threshold) {
                in.z = 0.0f;
            }
        }
    }

    public final class Files {
        public String readTextFromFileInJar(JGemsPath path) {
            StringBuilder textBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(JGems3D.loadFileFromJar(path), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    textBuilder.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
            return textBuilder.toString();
        }

        public String readTextFromFileOutsideJar(JGemsPath path) {
            StringBuilder textBuilder = new StringBuilder();
            try (BufferedReader reader = java.nio.file.Files.newBufferedReader(path.toFile().toPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    textBuilder.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new JGemsIOException(e);
            }
            return textBuilder.toString();
        }

        public byte[] toByteArray(InputStream inputStream) throws IOException {
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] data = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                return buffer.toByteArray();
            }
        }

        public ByteBuffer toByteBuffer(InputStream inputStream) throws IOException {
            final int BUFFER_SIZE = 8 * 1024;
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (byteBuffer.remaining() < bytesRead) {
                    ByteBuffer newBuffer = MemoryUtil.memAlloc(byteBuffer.capacity() * 2);
                    byteBuffer.flip();
                    newBuffer.put(byteBuffer);
                    MemoryUtil.memFree(byteBuffer);
                    byteBuffer = newBuffer;
                }
                byteBuffer.put(buffer, 0, bytesRead);
            }

            byteBuffer.flip();
            return byteBuffer;
        }

        public String openFolderViewer(@Nullable String defaultStr) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Choose folder");
            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = chooser.getSelectedFile();
                return selectedFolder.getAbsolutePath();
            }
            return defaultStr;
        }
    }
}
