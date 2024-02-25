package ru.BouH.engine.game;

import org.joml.Vector3d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.physics.brush.WorldModeledBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.physics.liquids.Water;
import ru.BouH.engine.physics.particles.ParticleFlame;
import ru.BouH.engine.physics.particles.SimpleParticle;
import ru.BouH.engine.physics.triggers.Zone;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.fabric.models.AbstractSceneObject;
import ru.BouH.engine.render.scene.fabric.models.SceneObject;
import ru.BouH.engine.render.scene.fabric.render.RenderSceneModel;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class GameEvents {

    public static void worldUpdateEvent(World world) {
        //if (world.getTicks() % 20 == 0) {
        //    SimpleParticle simpleParticle = new ParticleFlame(world, 15.0d, new Vector3d(Game.random.nextFloat(), Game.random.nextFloat() + 0.5f, Game.random.nextFloat()).add(0, 0, 0), new Vector3d(0.0d));
        //    Game.getGame().getProxy().addItemInWorlds(simpleParticle, ResourceManager.renderDataAssets.particleFlame);
        //}
    }

    public static void addSceneModels(SceneWorld sceneWorld) {
        //Model<Format3D> knife = new Model<>(new Format3D(), ResourceManager.modelAssets.knife);
        //knife.getFormat().setScale(new Vector3d(50.0d));
        //knife.getFormat().setPosition(new Vector3d(0.0d, -54.0d, 0.0d));
        //knife.getFormat().setRotation(new Vector3d(0, Math.toRadians(20), 0));
        //AbstractSceneObject sceneObject = new SceneObject(new RenderSceneModel(), knife, ResourceManager.shaderAssets.world);
        //sceneWorld.addRenderObjectInScene(sceneObject);
//
        //Model<Format3D> house = new Model<>(new Format3D(), ResourceManager.modelAssets.house);
        //house.getFormat().setScale(new Vector3d(5.0d));
        //house.getFormat().setPosition(new Vector3d(0.0d, 5.0d, 30.0d));
        //house.getFormat().setRotation(new Vector3d(0, Math.toRadians(20), 0));
        //AbstractSceneObject sceneObject2 = new SceneObject(new RenderSceneModel(), house, ResourceManager.shaderAssets.world);
        //sceneWorld.addRenderObjectInScene(sceneObject2);
//
        //Material grass = new Material();
        //grass.setDiffuse(ResourceManager.renderAssets.tallGrass);
//
        //Model<Format3D> tallgrass = new Model<>(new Format3D(), MeshHelper.generatePlane3DMesh(new Vector3d(0.0d, 0.0d, 0.0d), new Vector3d(0.0d, 1.0d, 0.0d), new Vector3d(1.0d, 1.0d, 0.0d), new Vector3d(1.0d, 0.0d, 0.0d)));
        //tallgrass.getFormat().setScale(new Vector3d(5.0d));
        //tallgrass.getFormat().setPosition(new Vector3d(0.0d, -1.0d, 20.0d));
        //tallgrass.getFormat().setRotation(new Vector3d(0, Math.toRadians(20), 0));
        //AbstractSceneObject sceneObject3 = new SceneObject(new RenderSceneModel(), tallgrass, ResourceManager.shaderAssets.world).setOverObjectMaterial(grass);
        //sceneObject3.getModelRenderParams().setHasTransparency(true);
        //sceneObject3.getModelRenderParams().setShadowCaster(false);
        //sceneObject3.getModelRenderParams().setShadowReceiver(false);
        //sceneObject3.getModelRenderParams().invertTextureCoordinates();
        //sceneWorld.addRenderObjectInScene(sceneObject3);
    }

    public static void populate(World world) {
        GameEvents.addBrushes(world);
        GameEvents.addEntities(world);
        GameEvents.addTriggers(world);
    }

    public static void addEntities(World world) {
        //PhysEntityCube entityPropInfo = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 20.0d), new Vector3d(1, 1, 1), 1.0d, new Vector3d(0.0d, 15.0d, 10.0d), new Vector3d(0.0d));
        //Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
//
        //PhysEntityCube entityPropInfo2 = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 100.0d), new Vector3d(1, 1, 1), 50.0d, new Vector3d(0.0d, 60.0d, 200.0d), new Vector3d(0.0d));
        //Game.getGame().getProxy().addItemInWorlds(entityPropInfo2, ResourceManager.renderDataAssets.entityLargeCube);
    }

    public static void addTriggers(World world) {
       // world.createSimpleTriggerZone(new Zone(new Vector3d(50.0d, 0.0d, 0.0d), new Vector3d(5.0d, 5.0d, 5.0d)), (e) -> {
       //     if (e instanceof IPlayer) {
       //         Scene.testTrigger = true;
       //     }
       // }, (e) -> {
       //     if (e instanceof IPlayer) {
       //         Scene.testTrigger = false;
       //     }
       // });
    }

    public static void addBrushes(World world) {
        WorldModeledBrush worldModeledBrush = new WorldModeledBrush(world, ResourceManager.modelAssets.ground, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), "grass");
        Game.getGame().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground));
        worldModeledBrush.setDebugDrawing(false);

        //final int size = 300;
        //final int wallH = 30;
//
        //Plane4dBrush plane4dBrush = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(-size, 0, size), new Vector3d(size, 0, -size), new Vector3d(size, 0, size)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush, ResourceManager.renderDataAssets.planeGround);
//
        //Plane4dBrush plane4dBrush2 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size), new Vector3d(-size, 0, -size), new Vector3d(-size, wallH, -size)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush2, ResourceManager.renderDataAssets.plane);
//
        //Plane4dBrush plane4dBrush3 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size), new Vector3d(-size, wallH, -size)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush3, ResourceManager.renderDataAssets.plane);
//
        //Plane4dBrush plane4dBrush4 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush4, ResourceManager.renderDataAssets.plane);
//
        //Plane4dBrush plane4dBrush5 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush5, ResourceManager.renderDataAssets.plane);
//
//
        //Plane4dBrush plane4dBrush6 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-25.0d, 0.0d, -75.0d), new Vector3d(25.0d, 0.0d, -75.0d), new Vector3d(-25.0d, 1.6d, -75.0d), new Vector3d(25.0d, 1.6d, -75.0d)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush6, ResourceManager.renderDataAssets.test);
//
        //Plane4dBrush plane4dBrush7 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-25.0d, 0.0d, -125.0d), new Vector3d(25.0d, 0.0d, -125.0d), new Vector3d(-25.0d, 1.6d, -125.0d), new Vector3d(25.0d, 1.6d, -125.0d)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush7, ResourceManager.renderDataAssets.test);
//
        //Plane4dBrush plane4dBrush8 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-25.0d, 0.0d, -75.0d), new Vector3d(-25.0d, 0.0d, -125.0d), new Vector3d(-25.0d, 1.6d, -75.0d), new Vector3d(-25.0d, 1.6d, -125.0d)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush8, ResourceManager.renderDataAssets.test);
//
        //Plane4dBrush plane4dBrush9 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(25.0d, 0.0d, -75.0d), new Vector3d(25.0d, 0.0d, -125.0d), new Vector3d(25.0d, 1.6d, -75.0d), new Vector3d(25.0d, 1.6d, -125.0d)});
        //Game.getGame().getProxy().addItemInWorlds(plane4dBrush9, ResourceManager.renderDataAssets.test);
//
//
        //ILiquid liquid = new Water(new Zone(new Vector3d(0.0d, -5.0d, 65.0d), new Vector3d(80.0d, 5.0d, 80.0d)));
        //Game.getGame().getProxy().addLiquidInWorlds(liquid, ResourceManager.renderDataAssets.renderLiquidData);
    }
}