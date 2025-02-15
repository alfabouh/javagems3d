/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d;

import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.Environment;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.particles.ParticlesEmitter;
import javagems3d.graphics.particles.attributes.ParticleAttributes;
import javagems3d.graphics.particles.objects.SimpleColoredParticle;
import javagems3d.graphics.particles.objects.SimpleTexturedParticle;
import javagems3d.graphics.particles.objects.base.ParticleFX;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.screen.timer.JGemsTimer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.physics.world.triggers.zones.base.ITriggerZone;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.controller.objects.IController;
import javagems3d.system.controller.objects.MouseKeyboardController;
import javagems3d.system.graph.Graph;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.map.navigation.pathgen.MapNavGraphGenerator;
import javagems3d.system.resources.assets.materials.Material;

import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import javagems3d.system.resources.assets.models.mesh.udata.MeshCollisionData;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import javagems3d.system.resources.localisation.JGemsLocalisation;
import javagems3d.system.resources.localisation.Lang;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.system.settings.JGemsSettings;
import logger.SystemLogging;
import logger.managers.LoggingManager;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Using the JGemsHelper class, you can conveniently access most of the most important functions for managing the state of the engine. This utility class is divided into sections for easier navigation.
 *
 * @see javagems3d.physics.world.thread.dynamics.DynamicsUtils
 * @see javagems3d.system.resources.assets.models.helper.MeshHelper
 */

@SuppressWarnings("all")
public abstract class JGemsHelper {
    public static JGemsTimer createTimer() {
        return JGemsHelper.getScreen().getTimerPool().createTimer();
    }

    public static IPlayer getCurrentPlayer() {
        return JGemsHelper.getMainObject().getPlayer();
    }

    public static JGemsScreen getScreen() {
        return JGemsHelper.getMainObject().getScreen();
    }

    public static SceneWorld getSceneWorld() {
        return JGemsHelper.getMainObject().getCore().getScreen().getSceneWorld();
    }

    public static PhysicsWorld getPhysicsWorld() {
        return JGemsHelper.getMainObject().getPhysics().getPhysicsProcessor().getPhysicsWorld();
    }

    public static JGems3D getMainObject() {
        return JGems3D.get();
    }

    public static JGemsSoundManager getSoundManager() {
        return JGemsHelper.getMainObject().getSoundManager();
    }

    public static LoggingManager getLogger() {
        return SystemLogging.get().getLogManager();
    }

    public static abstract class RENDERING {

        public static int getMaxTextureUnits() {
            return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
        }

        public static Model2D createScreenModel() {
            return MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(JGemsHelper.getScreen().getWindowDimensions()), 0);
        }

        public static void renderMeshNode(RenderMesh renderMesh) {
            GL46.glBindVertexArray(renderMesh.getVao());
            renderMesh.enableAllMeshAttributes();
            GL46.glDrawElements(GL46.GL_TRIANGLES, renderMesh.getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            renderMesh.disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }

        public static void renderModel2D(Model2D model2D, int code) {
            RENDERING.renderMeshList2D(model2D.getMeshStructure().getNodes(), code);
        }
        
        public static void renderMeshList2D(List<MeshNode2D> list, int code) {
            for (MeshNode2D meshNode2D : list) {
                RENDERING.renderMeshNode(meshNode2D.getMeshData());
            }
        }

        public static void renderModel3D(Model3D model3D, int layer, int code) {
            RENDERING.renderMeshList3D(model3D.<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer), code);
        }

        public static void renderMeshList3D(List<MeshNode3D<RenderMesh>> list, int code) {
            for (MeshNode3D<RenderMesh> meshNode3D : list) {
                RENDERING.renderMeshNode(meshNode3D.getMeshData());
            }
        }

        public static int getLightingCodeForShader(RenderAttributes configuration) {
            int code = 0;
            if (configuration.isDefaultBrightLighted()) {
                code |= 1 << 2;
            }
            return code;
        }

        public static int getTexturingCodeForShader(Material material) {
            int code = 0;
            if (material.getDiffuse() instanceof ImageBasedTexture) {
                code |= 1 << 2;
            }
            if (material.getNormalsMap() instanceof ImageBasedTexture) {
                code |= 1 << 3;
            }
            if (material.getEmissionMap() instanceof ImageBasedTexture) {
                code |= 1 << 4;
            }
            if (material.getSpecularMap() instanceof ImageBasedTexture) {
                code |= 1 << 5;
            }
            if (material.getMetallicMap() instanceof ImageBasedTexture) {
                code |= 1 << 6;
            }
            return code;
        }
    }

    public static abstract class ANIMATION {
        public static IAnimated getAnimatedObject(WorldItem worldItem) {
            return JGemsHelper.getSceneWorld().getAnimatedObject(worldItem);
        }
    }

    public static abstract class RESOURCES {
        public static GameResources getGlobalResources() {
            return JGemsResourceManager.getGlobalGameResources();
        }

        public static GameResources getLocalResources() {
            return JGemsResourceManager.getLocalGameResources();
        }

        public static JGemsResourceManager getJGemsResourceManager() {
            return JGems3D.get().getResourceManager();
        }

        public static void reloadResources() {
            JGems3D.get().reloadResources();
        }
    }

    public static abstract class CAMERA {
        public static ICamera getCurrentCamera() {
            return JGemsHelper.getScreen().getCamera();
        }

        public static void setCurrentCamera(ICamera camera) {
            JGemsHelper.getScreen().getScene().setCamera(camera);
        }

        public static void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
            JGemsHelper.getScreen().getScene().setCamera(new ControlledCamera(controller, pos, rot));
        }

        public static void enableAttachedCamera(WorldItem worldItem) {
            JGemsHelper.getScreen().getScene().setCamera(JGemsHelper.getSceneWorld().createAttachedCamera(worldItem));
        }

        public static void enableAttachedCamera(SceneEntity abstractSceneEntity) {
            JGemsHelper.getScreen().getScene().setCamera(JGemsHelper.getSceneWorld().createAttachedCamera(abstractSceneEntity));
        }
    }

    public static abstract class LOCALISATION {
        public static Lang createLocalisation(String langName, JGemsPath path) {
            return JGemsLocalisation.createLocalisation(langName, path);
        }

        public static void setLangLocalisationPath(Lang lang, JGemsPath path) {
            JGemsLocalisation.setLangLocalisationPath(lang, path);
        }

        public static JGemsLocalisation getLocalisation() {
            return JGems3D.get().getLocalisation();
        }
    }

    public static abstract class PARTICLES {
        public static SimpleTexturedParticle createSimpleTexturedParticle(ParticleAttributes particleAttributes, ParticleTexturesPack particleTexturesPack, Vector3f pos, Vector2f scaling) {
            return ParticlesEmitter.createSimpleTexturedParticle(JGemsHelper.getSceneWorld(), particleAttributes, particleTexturesPack, pos, scaling);
        }

        public static SimpleColoredParticle createSimpleColoredParticle(ParticleAttributes particleAttributes, Vector3f color, Vector3f pos, Vector2f scaling) {
            return ParticlesEmitter.createSimpleColoredParticle(JGemsHelper.getSceneWorld(), particleAttributes, color, pos, scaling);
        }

        public static ParticleFX emitParticle(ParticleFX particleFX) {
            JGemsHelper.PARTICLES.getParticlesEmitter().emitParticle(particleFX);
            return particleFX;
        }

        public static ParticlesEmitter getParticlesEmitter() {
            return JGemsHelper.getScreen().getScene().getSceneWorld().getParticlesEmitter();
        }
    }

    public static abstract class ENVIRONMENT {
        public static SkyBox getSky() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox();
        }

        public static FogManager getFog() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getFog();
        }

        public static Environment getWorldEnvironment() {
            return JGemsHelper.getSceneWorld().getEnvironment();
        }
    }

    public static abstract class CONTROLLER {
        public static boolean setCursorInCenter() {
            IController controller = getCurrentController();
            if (controller instanceof MouseKeyboardController) {
                MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
                mouseKeyboardController.setCursorInCenter();
                return true;
            }
            JGemsHelper.getLogger().warn("Couldn't find cursor. Check your controller");
            return false;
        }

        public static void attachControllerTo(IController controller, IControllable remoteController) {
            JGemsHelper.CONTROLLER.getControllerDispatcher().attachControllerTo(controller, remoteController);
        }

        public static JGemsControllerDispatcher getControllerDispatcher() {
            return JGemsHelper.getScreen().getControllerDispatcher();
        }

        public static IController getCurrentController() {
            return JGemsHelper.CONTROLLER.getControllerDispatcher().getCurrentController();
        }

        public static void detachController() {
            JGemsHelper.CONTROLLER.getControllerDispatcher().detachController();
        }

        public static BindingManager bindingManager() {
            return JGemsControllerDispatcher.bindingManager();
        }
    }

    public static abstract class GAME {
        public static void killItems() {
            JGemsHelper.getPhysicsWorld().killItems();
        }

        public static void zeroRenderTick() {
            JGems3D.get().getScreen().zeroRenderTick();
        }

        public static void lockController() {
            JGems3D.get().lockController();
        }

        public static void unLockController() {
            JGems3D.get().unLockController();
        }

        public static void pauseGameAndLockUnPausing(boolean pauseSounds) {
            JGems3D.get().pauseGameAndLockUnPausing(pauseSounds);
        }

        public static IMapLoader getCurrentMap() {
            return JGems3D.get().getCore().getMapLoader();
        }

        public static void unPauseGameAndUnLockUnPausing() {
            JGems3D.get().unPauseGameAndUnLockUnPausing();
        }

        public static void pauseGame(boolean pauseSounds) {
            JGems3D.get().pauseGame(pauseSounds);
        }

        public static void unPauseGame() {
            JGems3D.get().unPauseGame();
        }

        public static void loadMap(IMapLoader mapLoader) {
            JGems3D.get().loadMap(mapLoader);
        }

        public static void destroyMap() {
            JGems3D.get().destroyMap();
        }

        public static void destroyGame() {
            JGems3D.get().destroyGame();
        }

        public static JGemsSettings getGameSettings() {
            return JGems3D.get().getGameSettings();
        }
    }

    public static abstract class WINDOW {
        public static void setWindowFocus(boolean focus) {
            JGemsHelper.getScreen().getWindow().setInFocus(focus);
        }

        public static boolean isWindowActive() {
            return JGemsHelper.getScreen().getWindow().isWindowActive();
        }
    }

    public static abstract class UI {
        public static void closeUIPanel() {
            JGems3D.get().closeUIPanel();
        }

        public static void openUIPanel(PanelUI ui) {
            JGems3D.get().openUIPanel(ui);
        }
    }

    public static abstract class WORLD {
        public static Graph genSimpleMapGraphFromStartPoint(Vector3f start) {
            return MapNavGraphGenerator.createGraphWithStartPoint(JGems3D.get().getPhysics().getPhysicsProcessor().getDynamicsSystem(), DynamicsUtils.convertV3F_JME(start));
        }

        public static void removePropFromScene(SceneProp sceneProp) {
            JGems3D.get().getScreen().getScene().getSceneWorld().removeObjectFromWorld(sceneProp);
        }

        public static void addPropInScene(SceneProp sceneProp) {
            JGems3D.get().getScreen().getScene().getSceneWorld().addObjectInWorld(sceneProp);
        }

        public static void removeItemFromWorld(WorldItem worldItem) {
            worldItem.setDead();
        }

        public static void addItemInWorld(WorldItem worldItem, EntityRenderData renderData) {
            JGemsHelper.getPhysicsWorld().addItem(worldItem);
            JGemsHelper.getSceneWorld().addItem(worldItem, renderData);
        }

        public static void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(worldItem, light);
            JGemsHelper.ENVIRONMENT.getWorldEnvironment().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addPointLight(PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(light);
            JGemsHelper.ENVIRONMENT.getWorldEnvironment().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addLiquid(Liquid liquid, LiquidRenderData liquidRenderData) {
            JGemsHelper.getPhysicsWorld().addItem(liquid);
            JGemsHelper.getSceneWorld().addLiquid(liquid, liquidRenderData);
        }

        public static void addTriggerZone(ITriggerZone triggerZone) {
            JGemsHelper.getPhysicsWorld().addItem(triggerZone);
        }

        public static void addLight(WorldItem worldItem, Light light) {
            JGemsHelper.getSceneWorld().addWorldItemLight(worldItem, light);
        }

        public static void addLight(Light light) {
            light.on();
            JGemsHelper.ENVIRONMENT.getWorldEnvironment().getLightManager().addLight(light);
        }
    }

    public static abstract class MATH {

        public static float lerp(float a, float b, float f) {
            return a + f * (b - a);
        }

        public static int clamp(int d1, int d2, int d3) {
            return d1 < d2 ? d2 : (int) Math.min(d1, d3);
        }

        public static float clamp(float d1, float d2, float d3) {
            return d1 < d2 ? d2 : Math.min(d1, d3);
        }

        public static double clamp(double d1, double d2, double d3) {
            return d1 < d2 ? d2 : Math.min(d1, d3);
        }

        public static void clampVectorToZeroThreshold(Vector3f in, float threshold) {
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

    public static abstract class UTILS {
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
                return null;
            }
            return list.stream().mapToInt( v -> (Integer) v).toArray();
        }

        public static double[] convertDoublesArray(List<Double> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.stream().mapToDouble( v -> (Double) v).toArray();
        }

        public static float[] convertFloatsArray(List<Float> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            float[] a = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
        }

        public static float[] convertFloats3Array(List<Vector3f> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            float[] a = new float[list.size() * 3];
            for (int i = 0; i < list.size(); i += 3) {
                a[i] = list.get(i).x;
                a[i + 1] = list.get(i).y;
                a[i + 2] = list.get(i).z;
            }
            return a;
        }

        public static Vector3f convertV3DV3F(Vector3f vector3f) {
            return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
        }

        public static Vector3f convertV3FV3D(Vector3f vector3f) {
            return new Vector3f(vector3f);
        }

        public static Vector3f calcLookVector(Vector3f rotations) {
            float x = rotations.x;
            float y = rotations.y;
            float lX = Math.sin(y) * Math.cos(x);
            float lY = -Math.sin(x);
            float lZ = -Math.cos(y) * Math.cos(x);
            return new Vector3f(lX, lY, lZ);
        }

        public static void createMeshCollisionData(MeshStructure3D... m) {
            for (MeshStructure3D<?> o : m) {
                JGemsHelper.UTILS.createMeshCollisionData(o);
            }
        }

        public static void createMeshAABBData(MeshStructure3D<?>... m) {
            for (MeshStructure3D<?> o : m) {
                JGemsHelper.UTILS.createMeshAABBData(o);
            }
        }

        @SuppressWarnings("all")
        public static boolean createMeshAABBData(MeshStructure3D<?> meshStructure) {
            if (meshStructure != null && meshStructure.getMeshUserData(MeshStructure3D.MESH_AABB_UD) == null) {
                meshStructure.setMeshUserData(MeshStructure3D.MESH_AABB_UD, MeshAABBData.create(meshStructure));
                return true;
            }
            return false;
        }

        @SuppressWarnings("all")
        public static boolean createMeshCollisionData(MeshStructure3D<?> meshStructure) {
            if (meshStructure != null && meshStructure.getMeshUserData(MeshStructure3D.MESH_COLLISION_UD) == null) {
                meshStructure.setMeshUserData(MeshStructure3D.MESH_COLLISION_UD, new MeshCollisionData(meshStructure));
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

        public static <K, V, U> void putObjectInMapOrUpdate(Map<K, V> map, K key, V defaultValue, BiFunction<V, U, V> updateFunction, U updateValue) {
            map.merge(key, defaultValue, (existingValue, newValue) -> updateFunction.apply(existingValue, updateValue));
        }
    }
}
