package ru.alfabouh.engine.game.map;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.map.loader.IMapLoader;
import ru.alfabouh.engine.game.map.loader.MapInfo;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.graph.Graph;
import ru.alfabouh.engine.graph.pathgen.TerrainGraphGenerator;
import ru.alfabouh.engine.inventory.items.ItemCrowbar;
import ru.alfabouh.engine.inventory.items.ItemEmp;
import ru.alfabouh.engine.inventory.items.ItemRadio;
import ru.alfabouh.engine.inventory.items.ItemZippo;
import ru.alfabouh.engine.physics.brush.WorldModeledBrush;
import ru.alfabouh.engine.physics.entities.Materials;
import ru.alfabouh.engine.physics.entities.enemy.EntityManiac;
import ru.alfabouh.engine.physics.entities.items.EntityCassetteItem;
import ru.alfabouh.engine.physics.entities.items.EntityCdItem;
import ru.alfabouh.engine.physics.entities.items.EntityItem;
import ru.alfabouh.engine.physics.entities.items.EntitySodaItem;
import ru.alfabouh.engine.physics.entities.prop.PhysCube;
import ru.alfabouh.engine.physics.entities.prop.PhysLightCube;
import ru.alfabouh.engine.physics.entities.prop.PhysPlank;
import ru.alfabouh.engine.physics.entities.prop.WorldDoor;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.liquids.Water;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.render.environment.light.PointLight;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;

import java.util.ArrayList;
import java.util.List;

public class Map01 implements IMapLoader {
    public static EntityManiac entityManiac;
    private final List<Vector3d> randomCassetteSpawnPoints;
    private int spawnEnemyCd;

    public Map01() {
        this.spawnEnemyCd = 600;

        this.randomCassetteSpawnPoints = new ArrayList<Vector3d>() {{
            add(new Vector3d(10.85, -9.91, -17.24));
            add(new Vector3d(-13.95, -9.9, 2.14));
            add(new Vector3d(-8.65, -15.5, -20.1));
            add(new Vector3d(21.53, -15.5, -37.33));
            add(new Vector3d(26.72, -9.9, -33.23));
            add(new Vector3d(-0.77, -9.9, -27.91));
            add(new Vector3d(-26.36, -9.9, -8.74));
            add(new Vector3d(-23.54, -9.9, -38.26));
            add(new Vector3d(9.92, -9.9, -59.84));
            add(new Vector3d(-12.39, -9.11, -27.74));
            add(new Vector3d(8.87, -9.9, -20.93));
            add(new Vector3d(1.22, -11.5, 21.92));
            add(new Vector3d(10.64, -15.5, -14.81));
        }};
    }

    @Override
    public void onMapUpdate(World world) {
        if (this.spawnEnemyCd-- == 0) {
            Map01.entityManiac = new EntityManiac(world, new Vector3d(-21.0d, -9.7d, -44.0d));
            Map01.entityManiac.getNavigationAI().setActive(true);
            Game.getGame().getProxy().addItemInWorlds(Map01.entityManiac, ResourceManager.renderDataAssets.enemy);
            Game.getGame().getSoundManager().playSoundAtEntity(ResourceManager.soundAssetsLoader.en_steps, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 2.0f, 3.0f, Map01.entityManiac);
        }
    }

    @Override
    public void addEntities(World world) {
        Map01.entityManiac = null;

        EntityCdItem entityCdItem1 = new EntityCdItem(world, new Vector3d(0.45, -18.2f, -26.92), "cd_world");
        Game.getGame().getProxy().addItemInWorlds(entityCdItem1, ResourceManager.renderDataAssets.cd_world);

        EntityCdItem entityCdItem2 = new EntityCdItem(world, new Vector3d(41.0d, -15.1f, -58.5d), "cd_world");
        Game.getGame().getProxy().addItemInWorlds(entityCdItem2, ResourceManager.renderDataAssets.cd_world);

        EntityCdItem entityCdItem3 = new EntityCdItem(world, new Vector3d(-42.5, -9.9, -27.94), "cd_world");
        Game.getGame().getProxy().addItemInWorlds(entityCdItem3, ResourceManager.renderDataAssets.cd_world);

        int maxI = 8;
        for (int i = 0; i < 7; i++) {
            int r = Game.random.nextInt(maxI--);
            Vector3d v3 = this.randomCassetteSpawnPoints.get(r);
            this.randomCassetteSpawnPoints.remove(r);
            EntityCassetteItem entityCassette = new EntityCassetteItem(world, v3, "cassette_world");
            Game.getGame().getProxy().addItemInWorlds(entityCassette, ResourceManager.renderDataAssets.cassette_world);
        }

        EntityItem entityItem1 = new EntityItem(world, new ItemZippo(), new Vector3d(29.0d, -8.3f, -10.65d), "zippo_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem1, ResourceManager.renderDataAssets.zippo_world);

        EntityItem entityItem2 = new EntityItem(world, new ItemEmp(), new Vector3d(new Vector3d(-7.7d, -9.1f, -28.0d)), "emp_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem2, ResourceManager.renderDataAssets.emp_world);

        EntityItem entityItem3 = new EntityItem(world, new ItemCrowbar(), new Vector3d(-2.2d, -18.2f, -22.0d), "crowbar_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem3, ResourceManager.renderDataAssets.crowbar_world);

        EntityItem entityItem4 = new EntityItem(world, new ItemRadio(), new Vector3d(-5.18, -18.61, -54.62), "radio_world");
        Game.getGame().getProxy().addItemInWorlds(entityItem4, ResourceManager.renderDataAssets.radio_world);

        EntitySodaItem entitySodaItem2 = new EntitySodaItem(world, new Vector3d(-24.75d, -15.5f, -45.4d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem2, ResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem3 = new EntitySodaItem(world, new Vector3d(-2.1d, -15.5f, -69.5d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem3, ResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem4 = new EntitySodaItem(world, new Vector3d(25.48d, -9.9f, -46.2d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem4, ResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem5 = new EntitySodaItem(world, new Vector3d(-2.0d, -9.9f, 2.5d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem5, ResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem6 = new EntitySodaItem(world, new Vector3d(-23.6d, -9.9f, -47.5d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem6, ResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem8 = new EntitySodaItem(world, new Vector3d(-22.45d, -9.9f, -31.8d), "soda_world");
        Game.getGame().getProxy().addItemInWorlds(entitySodaItem8, ResourceManager.renderDataAssets.soda_world);

        WorldDoor worldDoor = new WorldDoor(world, new Vector3d(9.3f, -19.2d, -52.3f), new Vector3d(0.0f, Math.toRadians(90.0f), 0.0f), "door", true);
        Game.getGame().getProxy().addItemInWorlds(worldDoor, ResourceManager.renderDataAssets.door1);

        WorldDoor worldDoor2 = new WorldDoor(world, new Vector3d(-11.4f, -19.2d, -52.3f), new Vector3d(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        Game.getGame().getProxy().addItemInWorlds(worldDoor2, ResourceManager.renderDataAssets.door1);

        WorldDoor worldDoor3 = new WorldDoor(world, new Vector3d(8.4f, -19.2d, -24.5f), new Vector3d(0.0f, Math.toRadians(90.0f), 0.0f), "door", true);
        Game.getGame().getProxy().addItemInWorlds(worldDoor3, ResourceManager.renderDataAssets.door1);

        WorldDoor worldDoor4 = new WorldDoor(world, new Vector3d(-11.6f, -19.2d, -24.5f), new Vector3d(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        Game.getGame().getProxy().addItemInWorlds(worldDoor4, ResourceManager.renderDataAssets.door1);

        WorldDoor worldDoor5 = new WorldDoor(world, new Vector3d(37.5f, -8.8d, -10.7f), new Vector3d(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        Game.getGame().getProxy().addItemInWorlds(worldDoor5, ResourceManager.renderDataAssets.door1);

        PhysPlank plank = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(-11.275f, -18.05f, -52.5f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0f));
        Game.getGame().getProxy().addItemInWorlds(plank, ResourceManager.renderDataAssets.plank);

        PhysPlank plank1 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(-11.275f, -18.45f, -52.3f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank1, ResourceManager.renderDataAssets.plank);

        PhysPlank plank3 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(9.175f, -18.05f, -52.5f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0f));
        Game.getGame().getProxy().addItemInWorlds(plank3, ResourceManager.renderDataAssets.plank);

        PhysPlank plank4 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(9.175f, -18.45f, -52.3f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank4, ResourceManager.renderDataAssets.plank);

        PhysPlank plank6 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(-41.48f, -9.66f, -26.75f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank6, ResourceManager.renderDataAssets.plank);

        PhysPlank plank7 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(-41.48f, -9.26f, -26.55f), new Vector3d(0.0d, Math.toRadians(90.0f), 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank7, ResourceManager.renderDataAssets.plank);

        PhysPlank plank8 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(0.65f, -18.15f, -25.96f), new Vector3d(0.0d, 0.0d, 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank8, ResourceManager.renderDataAssets.plank);

        PhysPlank plank9 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3d(0.575f, -18.55f, -25.96f), new Vector3d(0.0d, 0.0d, 0.0d));
        Game.getGame().getProxy().addItemInWorlds(plank9, ResourceManager.renderDataAssets.plank);
    }

    @Override
    public void addBrushes(World world) {
        WorldModeledBrush worldModeledBrush = new WorldModeledBrush(world, ResourceManager.modelAssets.ground, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), "grass");
        Game.getGame().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground));
        worldModeledBrush.setRotation(new Vector3d(-Math.toRadians(90.0f), Math.toRadians(180.0f), 0.0f));
        worldModeledBrush.setScale(0.025d);
        worldModeledBrush.setDebugDrawing(false);

        PhysCube entityPropInfo = new PhysLightCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 1.0d), new Vector3d(1.0d), 0.1d, new Vector3d(33.0f, -12.71f, -56.8f), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityLamp);
        entityPropInfo.getBulletObject().makeStatic();
        PointLight pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(1.0f, 0.0f, 0.0f));
        pointLight.setBrightness(2.0f);
        Game.getGame().getProxy().addLight(entityPropInfo, pointLight);

        PointLight pointLight2 = (PointLight) new PointLight(new Vector3d(38.25f, -6.1f, -10.65f)).setLightColor(new Vector3d(1.0f, 0.0f, 0.0f));
        pointLight2.setBrightness(3.0f);
        Game.getGame().getProxy().addLight(pointLight2);

        PointLight pointLight3 = (PointLight) new PointLight(new Vector3d(-40.0f, -7.7f, -26.5f)).setLightColor(new Vector3d(1.0f, 0.0f, 0.0f));
        pointLight3.setBrightness(3.0f);
        Game.getGame().getProxy().addLight(pointLight3);

        //Graph graph = new Graph();
        //TerrainGraphGenerator terrainGraphGenerator = new TerrainGraphGenerator(world.getDynamicsWorld(), graph);
        //Graph.GVertex vertex = terrainGraphGenerator.startPos(42.0d, 7.5d, -9.5d);
        //terrainGraphGenerator.generate(vertex);
        //Graph.saveInFile(graph, this.levelInfo().getLevelName());
    }

    @Override
    public void addLiquids(World world) {
        Water water = new Water(new Zone(new Vector3d(-1.5d, -19.0d, -23.0d), new Vector3d(new Vector3d(35.0d, 1.0d, 35.0d))));
        Game.getGame().getProxy().addLiquidInWorlds(water, ResourceManager.renderDataAssets.water);
    }

    @Override
    public void addTriggers(World world) {
    }

    @Override
    public void addSounds(World world) {
        Game.getGame().getSoundManager().playSoundAt(ResourceManager.soundAssetsLoader.drips, SoundType.WORLD_AMBIENT_SOUND, 1.0f, 5.0f, 4.0f, new Vector3d(-1.5d, -19.0d, -23.0d));
        Game.getGame().getSoundManager().playSoundAt(ResourceManager.soundAssetsLoader.map_ambience1, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 3.0f, 3.0f, new Vector3d(20.5d, 10.0d, -25.0d));
    }

    @Override
    public void readNavMesh(World world) {
        Graph graph = Graph.readFromFile(this.levelInfo().getLevelName());
        world.setGraph(graph);
    }

    @Override
    public MapInfo levelInfo() {
        return new MapInfo(new Vector4d(0.0d, 0.0d, 0.0d, 0.1375d), false, new Vector3d(43.0d, -7.5d, -10.0d), 0.15f, new Vector3f(0.5f, 0.5f, 1.0f), "map01");
    }
}
