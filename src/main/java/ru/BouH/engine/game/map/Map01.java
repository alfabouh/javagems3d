package ru.BouH.engine.game.map;

import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.map.loader.IMapLoader;
import ru.BouH.engine.game.map.loader.MapInfo;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.inventory.items.ItemCrowbar;
import ru.BouH.engine.inventory.items.ItemEmp;
import ru.BouH.engine.inventory.items.ItemRadio;
import ru.BouH.engine.inventory.items.ItemZippo;
import ru.BouH.engine.physics.brush.WorldModeledBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.enemy.EntityManiac;
import ru.BouH.engine.physics.entities.items.EntityItem;
import ru.BouH.engine.physics.entities.prop.PhysPlank;
import ru.BouH.engine.physics.entities.prop.WorldDoor;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;

public class Map01 implements IMapLoader {
    public static EntityManiac entityManiac;

    @Override
    public void onMapUpdate(World world) {

    }

    @Override
    public void addEntities(World world) {

    }

    @Override
    public void addBrushes(World world) {
        WorldModeledBrush worldModeledBrush = new WorldModeledBrush(world, ResourceManager.modelAssets.ground, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), "grass");
        Game.getGame().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground));
        worldModeledBrush.setDebugDrawing(false);

        EntityItem entityItem1 = new EntityItem(world, new ItemZippo(), new Vector3d(6.5d, -1.26d, 9.45d), "zippo_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem1, ResourceManager.renderDataAssets.zippo_world);

        EntityItem entityItem2 = new EntityItem(world, new ItemEmp(), new Vector3d(32.0d, -1.26d, 12.0d), "emp_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem2, ResourceManager.renderDataAssets.emp_world);

        EntityItem entityItem3 = new EntityItem(world, new ItemCrowbar(), new Vector3d(26.0d, -1.26d, 12.0d), "crowbar_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem3, ResourceManager.renderDataAssets.crowbar_world);

        EntityItem entityItem4 = new EntityItem(world, new ItemRadio(), new Vector3d(24.0d, -1.26d, 12.0d), "radio_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem4, ResourceManager.renderDataAssets.radio_world);

        WorldDoor worldDoor = new WorldDoor(world, new Vector3d(0, -1.8d, 0), new Vector3d(0.0d), "door");
        Game.getGame().getProxy().addItemInWorlds(worldDoor, ResourceManager.renderDataAssets.door1);

        PhysPlank plank = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.0d, new Vector3d(0, -0.8d, 2.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank, ResourceManager.renderDataAssets.plank);
        plank.getBulletObject().disableCCD();
        //Graph graph = new Graph();
        //TerrainGraphGenerator terrainGraphGenerator = new TerrainGraphGenerator(world.getDynamicsWorld(), graph);
        //Graph.GVertex vertex = terrainGraphGenerator.startPos(0, 0, 0);
        //terrainGraphGenerator.generate(vertex);
        //Graph.saveInFile(graph, this.levelInfo().getLevelName());

        Map01.entityManiac = new EntityManiac(world, new Vector3d(100.0d));
        Game.getGame().getProxy().addItemInWorlds(entityManiac, ResourceManager.renderDataAssets.enemy);
        Game.getGame().getSoundManager().playSoundAtEntity(ResourceManager.soundAssetsLoader.saw, SoundType.WORLD_AMBIENT_SOUND, 1.5f, 3.0f, 3.0f, entityManiac);
    }

    @Override
    public void addLiquids(World world) {

    }

    @Override
    public void addTriggers(World world) {

    }

    @Override
    public void addSounds(World world) {
        Game.getGame().getSoundManager().playSoundAt(ResourceManager.soundAssetsLoader.map_ambience1, SoundType.WORLD_AMBIENT_SOUND, 1.25f, 0.5f, 0.1f, new Vector3d(100.0d, 15.0d, 30.0d));
    }

    @Override
    public void readNavMesh(World world) {
        Graph graph = Graph.readFromFile(this.levelInfo().getLevelName());
        world.setGraph(graph);
    }

    @Override
    public MapInfo levelInfo() {
        return new MapInfo(new Vector3d(0.0d, 2.0d, 0.0d), 1.0d, new Vector3d(1.0d, 0.95, 0.9d), "map01");
    }
}
