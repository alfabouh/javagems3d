package ru.alfabouh.jgems3d.engine.system;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels.MainMenuPanel;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.zones.base.ITriggerZone;
import ru.alfabouh.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.data.MeshCollisionData;

import java.util.List;

public abstract class JGemsHelper {
    public static void pauseGameAndLockUnPausing(boolean pauseSounds) {
        JGems.get().pauseGameAndLockUnPausing(pauseSounds);
    }

    public static void unPauseGameAndUnLockUnPausing() {
        JGems.get().unPauseGameAndUnLockUnPausing();
    }

    public static void pauseGame(boolean pauseSounds) {
        JGems.get().pauseGame(pauseSounds);
    }

    public static void unPauseGame() {
        JGems.get().unPauseGame();
    }

    public static void loadMap(String mapName) {
        JGems.get().loadMap(mapName);
    }

    public static void loadMap(IMapLoader mapLoader) {
        JGems.get().loadMap(mapLoader);
    }

    public static void destroyMap() {
        JGems.get().destroyMap();
    }

    public static void destroyGame() {
        JGems.get().destroyGame();
    }

    public static void openMainMenu() {
        JGems.get().setUIPanel(new MainMenuPanel(null));
    }

    public static void removeUIPanel() {
        JGems.get().removeUIPanel();
    }

    public static void setUIPanel(PanelUI ui) {
        JGems.get().setUIPanel(ui);
    }

    public static void addPropInScene(SceneProp sceneProp) {
        JGems.get().getScreen().getScene().getSceneWorld().addObjectInWorld(sceneProp);
    }

    public static void addItemInWorlds(WorldItem worldItem, RenderObjectData renderData) {
        JGems.get().getProxy().addItemInWorlds(worldItem, renderData);
    }

    public static void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
        JGems.get().getProxy().addPointLight(worldItem, light, attachShadowScene);
    }

    public static void addPointLight(PointLight light, int attachShadowScene) {
        JGems.get().getProxy().addPointLight(light, attachShadowScene);
    }

    public static void addLiquidInWorlds(Liquid liquid, RenderLiquidData renderLiquidData) {
        JGems.get().getProxy().addLiquidInWorlds(liquid, renderLiquidData);
    }

    public static void addTriggerZone(ITriggerZone triggerZone) {
        JGems.get().getProxy().addTriggerZone(triggerZone);
    }

    public static void addLight(WorldItem worldItem, Light light) {
        JGems.get().getProxy().addLight(worldItem, light);
    }

    public static void addLight(Light light) {
        JGems.get().getProxy().addLight(light);
    }

    public static boolean tryCreateMeshCollisionData(MeshDataGroup meshDataGroup) {
        if (meshDataGroup.getMeshDataContainer() == null) {
            meshDataGroup.setMeshDataContainer(new MeshCollisionData(meshDataGroup));
            return true;
        }
        return false;
    }

    public static float calcDistanceToMostFarPoint(MeshDataGroup meshDataGroup, Vector3f scaling) {
        float max = Float.MIN_VALUE;

        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            List<Float> floats = modelNode.getMesh().getAttributePositions();
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
}
