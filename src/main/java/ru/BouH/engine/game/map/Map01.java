package ru.BouH.engine.game.map;

import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.map.loader.IMapLoader;
import ru.BouH.engine.game.map.loader.MapInfo;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.graph.pathgen.TerrainGraphGenerator;
import ru.BouH.engine.physics.brush.WorldModeledBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.enemy.EntityManiac;
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

        WorldDoor worldDoor = new WorldDoor(world, new Vector3d(0, -1.8d, 0), new Vector3d(0.0d), "door");
        //Game.getGame().getProxy().addItemInWorlds(worldDoor, ResourceManager.renderDataAssets.door1);

        Graph graph = new Graph();
        TerrainGraphGenerator terrainGraphGenerator = new TerrainGraphGenerator(world.getDynamicsWorld(), graph);
        Graph.GVertex vertex = terrainGraphGenerator.startPos(0, 0, 0);
        terrainGraphGenerator.generate(vertex);

        world.setGraph(graph);

        entityManiac = new EntityManiac(world, new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityManiac, ResourceManager.renderDataAssets.enemy);
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
    public MapInfo levelInfo() {
        return new MapInfo(new Vector3d(0.0d, 2.0d, 0.0d), 1.0d, new Vector3d(1.0d, 0.95, 0.9d), "map01");
    }
}
