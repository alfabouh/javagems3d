package ru.BouH.engine.game;

import org.joml.Vector3d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.fabric.models.RenderSceneModel;
import ru.BouH.engine.render.scene.fabric.models.base.RenderSceneObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class GameEvents {

    public static void addSceneModels(SceneWorld sceneWorld) {
        Model<Format3D> knife = new Model<>(new Format3D(), ResourceManager.modelAssets.knife);
        knife.getFormat().setScale(new Vector3d(50.0d));
        knife.getFormat().setPosition(new Vector3d(0.0d, -54.0d, 0.0d));
        knife.getFormat().setRotation(new Vector3d(0, 20, 0));
        RenderSceneObject renderSceneObject = new RenderSceneModel(knife, ResourceManager.shaderAssets.world);
        sceneWorld.addModelToRender(renderSceneObject);

        Model<Format3D> house = new Model<>(new Format3D(), ResourceManager.modelAssets.house);
        house.getFormat().setScale(new Vector3d(5.0d));
        house.getFormat().setPosition(new Vector3d(0.0d, 5.0d, 30.0d));
        house.getFormat().setRotation(new Vector3d(0, 20, 0));
        RenderSceneObject renderSceneObject2 = new RenderSceneModel(house, ResourceManager.shaderAssets.world);
        sceneWorld.addModelToRender(renderSceneObject2);
    }

    public static void populate(World world) {
        GameEvents.addBrushes(world);
        GameEvents.addEntities(world);
        GameEvents.addTriggers(world);
    }

    public static void addEntities(World world) {
        PhysEntityCube entityPropInfo = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 20.0d), new Vector3d(1, 1, 1), 1.0d, new Vector3d(0.0d, 15.0d, 10.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);

        PhysEntityCube entityPropInfo2 = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 100.0d), new Vector3d(1, 1, 1), 50.0d, new Vector3d(0.0d, 120.0d, 200.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo2, ResourceManager.renderDataAssets.entityLargeCube);
    }

    public static void addTriggers(World world) {
        world.createSimpleTriggerZone(new ITriggerZone.Zone(new Vector3d(350.0d, 0.0d, 0.0d), new Vector3d(5.0d, 5.0d, 5.0d)), (e) -> {
            if (e instanceof EntityPlayerSP) {
                Scene.testTrigger = true;
            }
        }, (e) -> {
            if (e instanceof EntityPlayerSP) {
                Scene.testTrigger = false;
            }
        });
    }

    public static void addBrushes(World world) {
        final int size = 300;
        final int wallH = 30;

        Plane4dBrush plane4dBrush = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(-size, 0, size), new Vector3d(size, 0, -size), new Vector3d(size, 0, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush, ResourceManager.renderDataAssets.planeGround);

        Plane4dBrush plane4dBrush2 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size), new Vector3d(-size, 0, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush2, ResourceManager.renderDataAssets.plane);

        Plane4dBrush plane4dBrush3 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush3, ResourceManager.renderDataAssets.plane);

        Plane4dBrush plane4dBrush4 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush4, ResourceManager.renderDataAssets.plane);

        Plane4dBrush plane4dBrush5 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush5, ResourceManager.renderDataAssets.plane);
    }
}