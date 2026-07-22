package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SpotLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.ui.jgems_imgui.IJGemsUIImp;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.processing.base.IMapProcessor;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.liquids.Liquid;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.helper.MeshAABBHelper;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import javagems3d.system.resources.localisation.JGemsLocalization;
import javagems3d.system.resources.localisation.LocalizationManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.settings.JGemsSettings;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

//@SuppressWarnings("all")
public final class JGemsHelper {
    public static JGemsHelper INSTANCE = new JGemsHelper();

    public static JGemsHelper get() {
        return JGemsHelper.INSTANCE;
    }

    public static void initResourceManager(@NotNull ResourceManager resourceManager) {
        JGemsHelper.get().resourceManager = resourceManager;
    }

    private ResourceManager resourceManager;

    public static void initJGemsCore(@NotNull JGemsCore core) {
        JGemsHelper.get().core = core;
    }

    private JGemsCore core;

    private final Files files;
    private final Math math;
    private final JGemsResources resources;
    private final Render render;
    private final UI ui;
    private final Localization localization;
    private final World world;
    private final Screen screen;
    private final Map map;
    private final Controller controller;
    private final Camera camera;
    private final State state;

    private JGemsHelper() {
        this.files = new Files();
        this.math = new Math();
        this.resources = new JGemsResources();
        this.render = new Render();
        this.ui = new UI();
        this.localization = new Localization();
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

    public static JGemsResources resources() {
        return JGemsHelper.get().resources;
    }

    public static Render render() {
        return JGemsHelper.get().render;
    }

    public static UI ui() {
        return JGemsHelper.get().ui;
    }

    public static Localization localisation() {
        return JGemsHelper.get().localization;
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
            JGemsHelper.this.core.pauseGame();
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

    public static final class Controller {
        public JGemsControllerDispatcher getControllerDispatcher() {
            return JGemsHelper.screen().getScreen().getControllerDispatcher();
        }

        public IController getCurrentController() {
            return this.getControllerDispatcher().getCurrentController();
        }

        public BindingManager getBindingManager() {
            return this.getCurrentController().getBindingManager();
        }


        public void setCursorInCenter() {
            IController controller = this.getCurrentController();
            if (controller instanceof MouseKeyboardController mouseKeyboardController) {
                mouseKeyboardController.setCursorInCenter();
                return;
            }
            Log.get().warn("Couldn't find cursor. Check your controller");
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

        public JGemsPath getMapPath(String relativePath) {
            return JGemsHelper.this.core.getMapPath(relativePath);
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

        public JGemsTimedAction createTimer() {
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

        public void addProp(SceneProp sceneProp) {
            JGemsHelper.this.getSceneWorld().addObject(sceneProp);
        }

        public void removeProp(SceneProp sceneProp) {
            JGemsHelper.this.getSceneWorld().removeObject(sceneProp);
        }

        public void addWorldObject(IWorldObject worldItem) {
            JGemsHelper.this.getPhysicsWorld().addObject(worldItem);
        }

        public void removeWorldObject(IWorldObject worldItem) {
            JGemsHelper.this.getPhysicsWorld().removeObject(worldItem);
        }

        public Pair<WorldItem, SceneObject> addWorldObjectInBothWorlds(WorldItem worldItem, EntityRenderData renderData) {
            JGemsHelper.this.getPhysicsWorld().addObject(worldItem);
            SceneObject sceneObject = JGemsHelper.this.getSceneWorld().addWorldObject(worldItem, renderData);
            return new Pair<>(worldItem, sceneObject);
        }

        public void removeWorldItem(WorldItem worldItem) {
            JGemsHelper.this.getPhysicsWorld().removeObject(worldItem);
        }

        public void addLiquidInBothWorlds(Liquid liquid, LiquidRenderData liquidRenderData) {
            JGemsHelper.this.getPhysicsWorld().addObject(liquid);
            JGemsHelper.this.getSceneWorld().addLiquid(liquid, liquidRenderData);
        }

        public void removeLiquid(Liquid liquid) {
            JGemsHelper.this.getPhysicsWorld().removeObject(liquid);
        }

        public void removeLight(Light light) {
            JGemsHelper.this.getSceneWorld().removeLight(light);
        }

        public void addLight(Light light) {
            JGemsHelper.this.getSceneWorld().addLight(light, null);
        }

        public void addLight(Light light, @Nullable IObjectWithLights lighted) {
            JGemsHelper.this.getSceneWorld().addLight(light, lighted);
        }

        public void addWorldItemLight(WorldItem worldItem, ILightAttachable light) {
            JGemsHelper.this.getSceneWorld().addWorldItemLight(worldItem, light);
        }
    }

    public static final class Localization {
        public JGemsLocalization getLocalization() {
            return JGems3D.get().getLocalization();
        }

        public void readLanguageMap(LocalizationManager.Lang lang, @NotNull JGemsPathSource path) throws IOException {
            this.getLocalization().readLanguageMap(lang, path);
        }

        public LocalizationManager.Lang getCurrentLanguage() {
            return this.getLocalization().getCurrentLang();
        }

        public void setCurrentLanguage(LocalizationManager.Lang lang) {
            this.getLocalization().setCurrentLang(lang);
        }

        public String format(String key, Object... args) {
            return this.getLocalization().format(key, args);
        }
    }

    public final class UI {
        public void openMainMenu() {
            this.openPanel(JGems3D.getAPIAppData().getMainMenuPanel());
        }

        public void openPanel(PanelUI panelUI) {
            if (panelUI == null) {
                Log.get().warn("Couldn't open NULL ui");
            } else {
                ((IJGemsUIImp) JGemsHelper.this.core.getScreen().getScene().getSceneRenderer()).openUIPanel(panelUI);
            }
        }

        public void closePanel() {
            ((IJGemsUIImp) JGemsHelper.this.core.getScreen().getScene().getSceneRenderer()).openUIPanel(null);
        }
    }

    public static final class Render {
        public static final int DIFFUSE_CODE = 1 << 2;
        public static final int NORMALS_CODE = 1 << 3;
        public static final int EMISSION_CODE = 1 << 4;
        public static final int METALLIC_ROUGHNESS_CODE = 1 << 5;

        public void renderModelAABBDebug(DebugLinesDrawer debugLinesDrawer, Model3D model3D) {
            this.renderModelAABBDebug(debugLinesDrawer, model3D.getMeshStructure(), model3D.getPose());
        }

        public void renderModelAABBDebug(DebugLinesDrawer debugLinesDrawer, CullingAABB cullingAABB) {
            if (cullingAABB == null) {
                return;
            }
            debugLinesDrawer.addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
        }

        public void renderModelAABBDebug(DebugLinesDrawer debugLinesDrawer, MeshStructure3D<?> meshStructure3D, Pose3D pose3D) {
            if (meshStructure3D == null) {
                return;
            }
            final CullingAABB globalAABB = meshStructure3D.getMeshAABBData().getNormalizedAABB(pose3D);
            if (globalAABB != null) {
                debugLinesDrawer.addRequest(DebugLinesDrawer.BoxRequest(globalAABB.getAabbMin(), globalAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
            }
            this.getLocalAABBs(debugLinesDrawer, meshStructure3D, pose3D).forEach(localAABB -> {
                if (localAABB != null) {
                    localAABB = MeshBoundingBoxData.transformAABB(localAABB, pose3D);
                    debugLinesDrawer.addRequest(DebugLinesDrawer.BoxRequest(localAABB.getAabbMin(), localAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
                }
            });
        }

        public void renderModelAABBDebug(DebugLinesDrawer debugLinesDrawer, CullingAABB cullingAABB, Matrix4f matrixModel, Vector3f color) {
            final CullingAABB globalAABB = MeshBoundingBoxData.transformAABB(cullingAABB, matrixModel);
            debugLinesDrawer.addRequest(DebugLinesDrawer.BoxRequest(globalAABB.getAabbMin(), globalAABB.getAabbMax(), color, DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
        }

        private List<CullingAABB> getLocalAABBs(DebugLinesDrawer debugLinesDrawer, MeshStructure3D<?> meshStructure3D, Pose3D pose3D) {
            List<CullingAABB> aabbs = new ArrayList<>();
            for (MeshNode3D<?> meshNode : meshStructure3D.getAllNodes()) {
                if (meshNode.getMeshData().getLocalAABB() != null) {
                    aabbs.add(meshNode.getMeshData().getLocalAABB());
                }
            }
            return aabbs;
        }

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

        public void performDefaultModelMaterialOnShader(IEnvironment environment, JGemsShaderManager shaderManager, Material material, float discardAlphaLevel, int decalLayerID) {
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

            if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.DECAL_LAYER_ID))) {
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.DECAL_LAYER_ID), UniformFunctions.UINTEGER(decalLayerID));
            }

            if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.OPACITY))) {
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.OPACITY), UniformFunctions.FLOAT(material.getOpacity()));
            }

            shaderManager.disableWarns();
            if (cubeMapProgram != null) {
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
                    shaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
                }
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(true));
                }
            } else {
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(false));
                }
            }

            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.ALPHA_DISCARD), UniformFunctions.FLOAT(discardAlphaLevel));
            shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.DIFFUSE_COLOR), diffuseColor);
            shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.EMISSIVE_COLOR), emissionColor);
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.METALLIC_FACTOR), UniformFunctions.FLOAT(metallicFactor));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.ROUGHNESS_FACTOR), UniformFunctions.FLOAT(roughnessFactor));

            if (diffuseMap != null) {
                shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.DIFFUSE_MAP), diffuseMap);
            }

            if (emissionMap != null) {
                shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.EMISSIVE_MAP), emissionMap);
            }

            if (normalsMap != null) {
                shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.NORMALS_MAP), normalsMap);
            }

            if (metallicRoughnessMap != null) {
                shaderManager.performUniformSample(new UniformString(DefaultUniformDefinitions.METALLIC_ROUGHNESS_MAP), metallicRoughnessMap);
            }

            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.TEXTURING_CODE), UniformFunctions.INTEGER(this.getTexturingCodeForShader(material)));
            shaderManager.enableWarns();
        }

        public void performEmptyAnimationsInfo(@NotNull JGemsShaderManager shaderManager) {
            shaderManager.disableWarns();
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(-1));
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(-1));
            shaderManager.enableWarns();
        }

        public void performAnimationsInfo(@NotNull JGemsShaderManager shaderManager, @NotNull IAnimated animated) {
            shaderManager.disableWarns();
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffset"), UniformFunctions.INTEGER(!animated.isAnimated() ? -1 : animated.getAnimationData().getCurrentAnimationFrame().getOffset()));
            shaderManager.performUniform(new UniformString("animationData.currAnimationOffsetPrev"), UniformFunctions.INTEGER(!animated.isAnimated() ? -1 : animated.getAnimationData().getPreviousAnimationFrame().getOffset()));
            if (animated.isAnimated()) {
                shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.ANIMATIONS_MATRIX), JGemsHelper.INSTANCE.resourceManager.getAnimationMatricesTexture());
                shaderManager.performUniform(new UniformString("animationData.deltaFrame"), UniformFunctions.FLOAT(animated.getAnimationData().getAnimationFrameDelta()));
            }
            shaderManager.enableWarns();
        }

        public void performShadowsInfo(IEnvironment environment, JGemsShaderManager shaderManager) {
            shaderManager.disableWarns();
            ShadowScene shadowScene = (ShadowScene) environment.getShadowScene();
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.POS_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.NEG_EXP), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.EVSM_NEGATIVE_EXPONENT));
            for (int i = 0; i < JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES; i++) {
                SunLightShadow.Cascade cascade = shadowScene.getSunLightShadow().getCascades().get(i);
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.SUN_SHADOW_MAP.getS() + i))) {
                    shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.SUN_SHADOW_MAP.getS() + i), shadowScene.getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.CASCADE_SHADOW_SPLIT_DISTANCE.getS() + i), UniformFunctions.FLOAT(cascade.getSplitDistance()));
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.CASCADE_SHADOW_PROJECTION_VIEW.getS() + i), UniformFunctions.MAT4F(cascade.getLightProjectionViewMatrix()));
                }
            }
            for (int i = 0; i < JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS; i++) {
                PointLightShadow pointLightShadow = shadowScene.getPointLightShadows().get(i);
                shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.POINT_LIGHT_FAR_PLANE), UniformFunctions.FLOAT(pointLightShadow.farPlane())); //TODO
                if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.POINT_LIGHT_CUBE_MAP.getS() + i))) {
                    shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.POINT_LIGHT_CUBE_MAP.getS() + i), pointLightShadow.getPointLightCubeMap().getCubeMapProgram());
                }
            }
            for (int i = 0; i < JGemsConfig.SYSTEM.MAX_SPOT_LIGHTS_SHADOWS; i++) {
                SpotLightShadow spotLightShadow = shadowScene.getSpotLightShadows().get(i);
                if (spotLightShadow.getShadowProjectionView() != null) {
                    shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.SPOT_LIGHT_FAR_PLANE), UniformFunctions.FLOAT(spotLightShadow.farPlane())); //TODO
                    if (shaderManager.isUniformExist(new UniformString(DefaultUniformDefinitions.SPOT_LIGHT_SHADOW_MAP.getS() + i))) {
                        shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.SPOT_LIGHT_SHADOW_MAP.getS() + i), spotLightShadow.getSpotLightFBO().getTextureByIndex(0));
                        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.SPOT_LIGHT_SHADOW_PROJECTION_VIEW.getS() + i), UniformFunctions.MAT4F(spotLightShadow.getShadowProjectionView()));
                    }
                }
            }
            shaderManager.enableWarns();
        }
    }

    public final class JGemsResources {
        @SuppressWarnings("all")
        public static boolean createMetaData(MeshStructure3D<?> meshStructure, @Nullable MeshCollisionData.Fabric collisionFabric) {
            boolean created = false;
            created = JGemsResources.createMeshAABBData(meshStructure);
            if (DynamicsSystem.VALID) {
                if (JGemsHelper.JGemsResources.createMeshCollisionData(meshStructure, collisionFabric)) {
                    created = true;
                }
            }
            return created;
        }

        @SuppressWarnings("all")
        public static boolean createMeshAABBData(MeshStructure3D<?> meshStructure) {
            int optimalThreads = Runtime.getRuntime().availableProcessors();
            if (meshStructure != null) {
                meshStructure.setMeshAABBData(new MeshBoundingBoxData(MeshAABBHelper.createMultiThread(meshStructure, optimalThreads)));
                if (meshStructure.isAnimatedStructure()) {
                    for (java.util.Map.Entry<Animation, CullingAABB> aabbEntry : MeshAABBHelper.createAnimatedMultiThread(meshStructure, optimalThreads).entrySet()) {
                        meshStructure.setMeshAABBDataForAnimationFrame(aabbEntry.getKey(), new MeshBoundingBoxData(aabbEntry.getValue()));
                    }
                }
                return true;
            }
            return false;
        }

        @SuppressWarnings("all")
        public static boolean createMeshCollisionData(MeshStructure3D<?> meshStructure, @Nullable MeshCollisionData.Fabric collisionFabric) {
            if (meshStructure != null) {
                final MeshCollisionData meshCollisionData = new MeshCollisionData(meshStructure, collisionFabric == null ? new MeshCollisionData.DefaultFabric() : collisionFabric);
                meshStructure.setMeshCollisionData(meshCollisionData);
                return true;
            }
            return false;
        }

        public static List<Vector3f> getVertexPositionsFromMesh(IMesh mesh) {
            List<Integer> integers = mesh.getVertexIndexes();
            List<Float> floats = mesh.getVertexPositions();
            List<Vector3f> vertexes = new ArrayList<>();

            for (int i = 0; i < integers.size(); i++) {
                int i1 = mesh.getVertexIndexes().get(i) * 3;
                Vector4f v4 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f);
                vertexes.add(new Vector3f(v4.x, v4.y, v4.z));
            }

            return vertexes;
        }

        public static List<Vector3f> getVertexPositionsFromMesh(IMesh mesh, Pose3D pose) {
            List<Integer> integers = mesh.getVertexIndexes();
            List<Float> floats = mesh.getVertexPositions();
            List<Vector3f> vertexes = new ArrayList<>();
            Matrix4f modelMat = TransformUtils.getModelMatrix(pose);

            for (int i = 0; i < integers.size(); i++) {
                int i1 = mesh.getVertexIndexes().get(i) * 3;
                Vector4f v4 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMat);
                vertexes.add(new Vector3f(v4.x, v4.y, v4.z));
            }

            return vertexes;
        }

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
            //EventLauncher.pushEvent(new EventBus.ReloadResourcesEvent(JGems3D.get().getResourceManager()));
            JGems3D.get().getScreen().showGameLoadingScreen("System01");
            JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Performing settings...");
            if (JGemsHelper.map().isCurrentGameMapValid()) {
                JGems3D.get().getScreen().refreshSceneResources();
            }
            this.getResourceManager().loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTexturesData);
            JGems3D.get().getScreen().removeLoadingScreen();
        }
    }

    public static final class Math {
        public static Matrix4f getMatrixFromArray(float[] arr) {
            return new Matrix4f(arr[0], arr[1], arr[2], arr[3],
                    arr[4], arr[5], arr[6], arr[7],
                    arr[8], arr[9], arr[10], arr[11],
                    arr[12], arr[13], arr[14], arr[15]);
        }

        public static Matrix4f getMatrixFromArray(List<Float> arr) {
            return new Matrix4f(arr.get(0), arr.get(1), arr.get(2), arr.get(3),
                    arr.get(4), arr.get(5), arr.get(6), arr.get(7),
                    arr.get(8), arr.get(9), arr.get(10), arr.get(11),
                    arr.get(12), arr.get(13), arr.get(14), arr.get(15));
        }

        public static List<Integer> convertIntsList(int[] arr) {
            List<Integer> list = new ArrayList<>(arr.length);
            for (int f : arr) {
                list.add(f);
            }
            return list;
        }

        public static List<Float> convertFloatsList(float[] arr) {
            List<Float> list = new ArrayList<>(arr.length);
            for (float f : arr) {
                list.add(f);
            }
            return list;
        }

        public static int[] convertIntsArray(List<Integer> list) {
            if (list == null || list.isEmpty()) {
                return new int[] {};
            }
            return list.stream().mapToInt( v -> v).toArray();
        }

        public static double[] convertDoublesArray(List<Double> list) {
            if (list == null || list.isEmpty()) {
                return new double[] {};
            }
            return list.stream().mapToDouble( v -> v).toArray();
        }

        public static float[] convertFloatsArray(List<Float> list) {
            if (list == null || list.isEmpty()) {
                return new float[] {};
            }
            float[] a = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
        }

        public static float[] convertFloats3Array(List<Vector3f> list) {
            if (list == null || list.isEmpty()) {
                return new float[] {};
            }
            float[] a = new float[list.size() * 3];
            for (int i = 0; i < list.size(); i += 3) {
                a[i] = list.get(i).x;
                a[i + 1] = list.get(i).y;
                a[i + 2] = list.get(i).z;
            }
            return a;
        }

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

    public static final class Files {
        public int countChar(String s, char c) {
            return (int) s.chars().filter(ch -> ch == c).count();
        }

        public String getTextWithLines(String text) {
            String[] lines = text.split("\n");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                stringBuilder.append("/* (").append(i + 1).append(") */ ").append(line).append("\n");
            }
            return stringBuilder.toString();
        }

        public static <K, V, U> void putObjectInMapOrUpdate(java.util.Map<K, V> map, K key, V defaultValue, BiFunction<V, U, V> updateFunction, U updateValue) {
            map.merge(key, defaultValue, (existingValue, newValue) -> updateFunction.apply(existingValue, updateValue));
        }

        public String md5(String str) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(str.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String readTextFromFile(@NotNull JGemsPathSource path) {
            StringBuilder textBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(JGems3D.getInputStream(path), StandardCharsets.UTF_8))) {
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

        public ByteBuffer toByteBufferSized(InputStream inputStream, int size) throws IOException {
            final int BUFFER_SIZE = 8 * 1024;
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(size);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteBuffer.put(buffer, 0, bytesRead);
            }

            byteBuffer.flip();
            return byteBuffer;
        }

        public void copyDirectory(File src, File dst, @Nullable String fl_exception) {
            if (!src.exists()) {
                return;
            }
            if (!dst.exists()) {
                dst.mkdirs();
            }

            for (File file : Objects.requireNonNull(src.listFiles())) {
                File target = new File(dst, file.getName());
                if (file.isDirectory()) {
                    this.copyDirectory(file, target, fl_exception);
                } else {
                    if (fl_exception == null || !file.getName().endsWith(fl_exception)) {
                        this.copyFile(file, target);
                    }
                }
            }
        }

        public void copyFile(File src, File dst) {
            try {
                java.nio.file.Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException("Copy failed: " + src, e);
            }
        }

        public String openFolderViewChooser(@Nullable String defaultStr) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Choose folder");
            int returnValue = chooser.showDialog(null, "Choose");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = chooser.getSelectedFile();
                return selectedFolder.getAbsolutePath();
            }
            return defaultStr;
        }
    }
}
