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

import javagems3d.graphics.opengl.environment.skybox.SkyBox;
import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.Mesh;
import javagems3d.system.resources.assets.models.mesh.attributes.VertexAttribute;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;
import org.joml.*;
import javagems3d.audio.SoundManager;
import javagems3d.graphics.opengl.camera.FreeControlledCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.environment.Environment;
import javagems3d.graphics.opengl.environment.fog.Fog;
import javagems3d.graphics.opengl.environment.light.Light;
import javagems3d.graphics.opengl.environment.light.PointLight;
import javagems3d.graphics.opengl.particles.ParticlesEmitter;
import javagems3d.graphics.opengl.particles.attributes.ParticleAttributes;
import javagems3d.graphics.opengl.particles.objects.SimpleColoredParticle;
import javagems3d.graphics.opengl.particles.objects.SimpleTexturedParticle;
import javagems3d.graphics.opengl.particles.objects.base.ParticleFX;
import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import javagems3d.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.graphics.opengl.rendering.items.props.SceneProp;
import javagems3d.graphics.opengl.screen.JGemsScreen;
import javagems3d.graphics.opengl.screen.timer.JGemsTimer;
import javagems3d.graphics.opengl.world.SceneWorld;
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
import javagems3d.system.resources.assets.material.samples.packs.ParticleTexturePack;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.localisation.Lang;
import javagems3d.system.resources.localisation.Localisation;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.system.settings.JGemsSettings;
import logger.SystemLogging;
import logger.managers.LoggingManager;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

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
        return JGems3D.get().getPlayer();
    }

    public static JGemsScreen getScreen() {
        return JGems3D.get().getScreen();
    }

    public static SceneWorld getSceneWorld() {
        return JGems3D.get().getSceneWorld();
    }

    public static PhysicsWorld getPhysicsWorld() {
        return JGems3D.get().getPhysicsWorld();
    }

    public static JGems3D getCoreObject() {
        return JGems3D.get();
    }

    public static SoundManager getSoundManager() {
        return JGems3D.get().getSoundManager();
    }

    public static LoggingManager getLogger() {
        return SystemLogging.get().getLogManager();
    }

    // section Resources
    public static abstract class RESOURCES {
        public static GameResources getGlobalResources() {
            return JGemsHelper.RESOURCES.getJGemsResourceManager().getGlobalResources();
        }

        public static GameResources getLocalResources() {
            return JGemsHelper.RESOURCES.getJGemsResourceManager().getLocalResources();
        }

        public static JGemsResourceManager getJGemsResourceManager() {
            return JGems3D.get().getResourceManager();
        }

        public static void reloadResources() {
            JGems3D.get().reloadResources();
        }
    }

    // section Camera
    public static abstract class CAMERA {
        public static ICamera getCurrentCamera() {
            return JGemsHelper.getScreen().getCamera();
        }

        public static void setCurrentCamera(ICamera camera) {
            JGemsHelper.getScreen().getScene().setCamera(camera);
        }

        public static void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
            JGemsHelper.getScreen().getScene().setCamera(new FreeControlledCamera(controller, pos, rot));
        }

        public static void enableAttachedCamera(WorldItem worldItem) {
            JGemsHelper.getScreen().getScene().setCamera(JGemsHelper.getSceneWorld().createAttachedCamera(worldItem));
        }

        public static void enableAttachedCamera(AbstractSceneEntity abstractSceneEntity) {
            JGemsHelper.getScreen().getScene().enableAttachedCamera(abstractSceneEntity);
        }
    }

    // section Localisation
    public static abstract class LOCALISATION {
        public static Lang createLocalisation(String langName, JGemsPath path) {
            return Localisation.createLocalisation(langName, path);
        }

        public static void setLangLocalisationPath(Lang lang, JGemsPath path) {
            Localisation.setLangLocalisationPath(lang, path);
        }

        public static Localisation getLocalisation() {
            return JGems3D.get().getLocalisation();
        }
    }

    // section Particles
    public static abstract class PARTICLES {
        public static SimpleTexturedParticle createSimpleTexturedParticle(ParticleAttributes particleAttributes, ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
            return ParticlesEmitter.createSimpleTexturedParticle(JGemsHelper.getSceneWorld(), particleAttributes, particleTexturePack, pos, scaling);
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

    // section Controller
    public static abstract class ENVIRONMENT {
        public static SkyBox getSky() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox();
        }

        public static Fog getFog() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getFog();
        }

        public static Environment getWorldEnvironment() {
            return JGemsHelper.getSceneWorld().getEnvironment();
        }
    }

    // section Controller
    public static abstract class CONTROLLER {
        public static boolean setCursorInCenter() {
            IController controller = getCurrentController();
            if (controller instanceof MouseKeyboardController) {
                MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
                mouseKeyboardController.setCursorInCenter();
                return true;
            }
            JGemsHelper.getLogger().warn("Couldn't find cursor. Check your controller!");
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

    // section Game
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
            return JGems3D.get().getEngineSystem().getMapLoader();
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

    // section Window
    public static abstract class WINDOW {
        public static void setWindowFocus(boolean focus) {
            JGemsHelper.getScreen().getWindow().setInFocus(focus);
        }

        public static boolean isWindowActive() {
            return JGemsHelper.getScreen().getWindow().isWindowActive();
        }
    }

    // section UI
    public static abstract class UI {
        public static void removeUIPanel() {
            JGems3D.get().removeUIPanel();
        }

        public static void openUIPanel(PanelUI ui) {
            JGems3D.get().openUIPanel(ui);
        }
    }

    // section World
    public static abstract class WORLD {
        public static Graph genSimpleMapGraphFromStartPoint(Vector3f start) {
            return MapNavGraphGenerator.createGraphWithStartPoint(JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getDynamicsSystem(), DynamicsUtils.convertV3F_JME(start));
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

        public static void addItemInWorld(WorldItem worldItem, RenderEntityData renderData) {
            JGemsHelper.getPhysicsWorld().addItem(worldItem);
            JGemsHelper.getSceneWorld().addItem(worldItem, renderData);
        }

        public static void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(worldItem, light);
            JGemsHelper.getScreen().getScene().getSceneRenderer().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addPointLight(PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(light);
            JGemsHelper.getScreen().getScene().getSceneRenderer().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addLiquid(Liquid liquid, RenderLiquidData renderLiquidData) {
            JGemsHelper.getPhysicsWorld().addItem(liquid);
            JGemsHelper.getSceneWorld().addLiquid(liquid, renderLiquidData);
        }

        public static void addTriggerZone(ITriggerZone triggerZone) {
            JGemsHelper.getPhysicsWorld().addItem(triggerZone);
        }

        public static void addLight(WorldItem worldItem, Light light) {
            JGemsHelper.getSceneWorld().addWorldItemLight(worldItem, light);
        }

        public static void addLight(Light light) {
            light.start();
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
    }

    //section Utils
    public static abstract class UTILS {
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

        public static float calcDistanceToMostFarPoint(MeshGroup meshGroup, Vector3f scaling) {
            return JGemsHelper.UTILS.calcDistanceToMostFarPoint(meshGroup, scaling, DefaultPointers.POSITIONS.getIndex());
        }

        public static float calcDistanceToMostFarPoint(MeshGroup meshGroup, Vector3f scaling, int positionsAttributeIndex) {
            float max = Float.MIN_VALUE;

            for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
                List<Float> floats = meshNode.getMesh().tryGetValuesFromAttributeByIndex(positionsAttributeIndex);
                for (int i = 0; i < floats.size(); i += 3) {
                    float i1 = floats.get(i);
                    float i2 = floats.get(i + 1);
                    float i3 = floats.get(i + 2);

                    float scaledX = i1 * scaling.x;
                    float scaledY = i2 * scaling.y;
                    float scaledZ = i3 * scaling.z;

                    float length = (float) Math.sqrt(scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ);

                    if (length > max) {
                        max = length;
                    }
                }
            }
            return max;
        }

        public static int[] convertIntsArray(List<Integer> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            int[] a = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
        }

        public static double[] convertDoublesArray(List<Double> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            double[] a = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
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

        public static boolean createMeshCollisionData(MeshGroup meshGroup) {
            return JGemsHelper.UTILS.createMeshCollisionData(meshGroup, DefaultPointers.POSITIONS.getIndex());
        }

        @SuppressWarnings("all")
        public static boolean createMeshCollisionData(MeshGroup meshGroup, int positionsAttributeIndex) {
            if (meshGroup != null && meshGroup.getMeshUserData(MeshGroup.MESH_COLLISION_UD) == null) {
                meshGroup.setMeshUserData(MeshGroup.MESH_COLLISION_UD, new MeshCollisionData(meshGroup, positionsAttributeIndex));
                return true;
            }
            return false;
        }

        public static boolean createMeshRenderAABBData(MeshGroup meshGroup) {
            return JGemsHelper.UTILS.createMeshRenderAABBData(meshGroup, DefaultPointers.POSITIONS.getIndex());
        }

        @SuppressWarnings("all")
        public static boolean createMeshRenderAABBData(MeshGroup meshGroup, int positionsAttributeIndex) {
            if (meshGroup != null && meshGroup.getMeshUserData(MeshGroup.MESH_RENDER_AABB_UD) == null) {
                Vector3f min = new Vector3f(Float.MAX_VALUE);
                Vector3f max = new Vector3f(Float.MIN_VALUE);
                for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
                    List<Float> positions = meshNode.getMesh().tryGetValuesFromAttributeByIndex(positionsAttributeIndex);
                    List<Integer> indices = meshNode.getMesh().getVertexIndexes();
                    for (int index : indices) {
                        int i1 = index * 3;
                        Vector4f Vector4f = new Vector4f(positions.get(i1), positions.get(i1 + 1), positions.get(i1 + 2), 1.0f);
                        Vector3f vector3f = new Vector3f(Vector4f.x, Vector4f.y, Vector4f.z);
                        min.min(vector3f);
                        max.max(vector3f);
                    }
                }
                meshGroup.setMeshUserData(MeshGroup.MESH_RENDER_AABB_UD, new ICulled.RenderAABB(min.add(new Vector3f(-0.05f)), max.add(new Vector3f(0.05f))));
                return true;
            }
            return false;
        }

        public static ICulled.RenderAABB calcRenderAABBWithTransforms(Model<Format3D> model) {
            if (model.getMeshDataGroup().getMeshUserData(MeshGroup.MESH_RENDER_AABB_UD) == null) {
                return null;
            }

            Vector3f min = new Vector3f(model.getMeshDataGroup().<ICulled.RenderAABB>getUnSafeMeshUserData(MeshGroup.MESH_RENDER_AABB_UD).getMin());
            Vector3f max = new Vector3f(model.getMeshDataGroup().<ICulled.RenderAABB>getUnSafeMeshUserData(MeshGroup.MESH_RENDER_AABB_UD).getMax());

            Matrix4f modelMatrix = Transformation.getModelMatrix(model.getFormat());

            Vector3f[] vertices = new Vector3f[8];
            vertices[0] = new Vector3f(min.x, min.y, min.z);
            vertices[1] = new Vector3f(max.x, min.y, min.z);
            vertices[2] = new Vector3f(min.x, max.y, min.z);
            vertices[3] = new Vector3f(max.x, max.y, min.z);
            vertices[4] = new Vector3f(min.x, min.y, max.z);
            vertices[5] = new Vector3f(max.x, min.y, max.z);
            vertices[6] = new Vector3f(min.x, max.y, max.z);
            vertices[7] = new Vector3f(max.x, max.y, max.z);

            Vector3f transformedMin = new Vector3f(Float.MAX_VALUE);
            Vector3f transformedMax = new Vector3f(-Float.MAX_VALUE);

            for (Vector3f vertex : vertices) {
                Vector4f transformedVertex = new Vector4f(vertex, 1.0f).mul(modelMatrix);
                transformedMin.x = Math.min(transformedMin.x, transformedVertex.x);
                transformedMin.y = Math.min(transformedMin.y, transformedVertex.y);
                transformedMin.z = Math.min(transformedMin.z, transformedVertex.z);
                transformedMax.x = Math.max(transformedMax.x, transformedVertex.x);
                transformedMax.y = Math.max(transformedMax.y, transformedVertex.y);
                transformedMax.z = Math.max(transformedMax.z, transformedVertex.z);
            }

            return new ICulled.RenderAABB(transformedMin, transformedMax);
        }

        public static List<Vector3f> getVertexPositionsFromMesh(Mesh mesh, Format3D format3D) {
            return JGemsHelper.UTILS.getVertexPositionsFromMesh(mesh, format3D, DefaultPointers.POSITIONS.getIndex());
        }

        public static List<Vector3f> getVertexPositionsFromMesh(Mesh mesh, Format3D format3D, int positionsAttributeIndex) {
            List<Integer> integers = mesh.getVertexIndexes();
            List<Float> floats = mesh.tryGetValuesFromAttributeByIndex(positionsAttributeIndex);
            List<Vector3f> vertexes = new ArrayList<>();
            Matrix4f modelMat = Transformation.getModelMatrix(format3D);

            for (int i = 0; i < integers.size(); i++) {
                int i1 = mesh.getVertexIndexes().get(i) * 3;
                Vector4f v4 = new Vector4f(floats.get(i1), floats.get(i1 + 1), floats.get(i1 + 2), 1.0f).mul(modelMat);
                vertexes.add(new Vector3f(v4.x, v4.y, v4.z));
            }

            return vertexes;
        }
    }
}
