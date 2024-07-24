package ru.alfabouh.jgems3d.engine.system.map.loaders.custom;

import ru.alfabouh.jgems3d.engine.physics.entities.enemies.EntityManiac;

public class Map01 {
    public static EntityManiac entityManiac;
    /*
    public static EntityManiac entityManiac;
    private final List<Vector3f> randomCassetteSpawnPoints;
    private int spawnEnemyCd;

    public Map01() {
        this.spawnEnemyCd = 600;

        this.randomCassetteSpawnPoints = new ArrayList<Vector3f>() {{
            add(new Vector3f(10.85, -9.91, -17.24));
            add(new Vector3f(-13.95, -9.9, 2.14));
            add(new Vector3f(-8.65, -15.5, -20.1));
            add(new Vector3f(21.53, -15.5, -37.33));
            add(new Vector3f(26.72, -9.9, -33.23));
            add(new Vector3f(-0.77, -9.9, -27.91));
            add(new Vector3f(-26.36, -9.9, -8.74));
            add(new Vector3f(-23.54, -9.9, -38.26));
            add(new Vector3f(9.92, -9.9, -59.84));
            add(new Vector3f(-12.39, -9.11, -27.74));
            add(new Vector3f(8.87, -9.9, -20.93));
            add(new Vector3f(1.22, -11.5, 21.92));
            add(new Vector3f(10.64, -15.5, -14.81));
        }};
    }

    @Override
    public void onMapUpdate(World world) {
        if (this.spawnEnemyCd-- == 0) {
            Map01.entityManiac = new EntityManiac(world, new Vector3f(-21.0d, -9.7d, -44.0d));
            Map01.entityManiac.getNavigationAI().setActive(true);
            JGems.get().getProxy().addItemInWorlds(Map01.entityManiac, JGemsResourceManager.renderDataAssets.enemy);
            JGems.get().getSoundManager().playSoundAtEntity(JGemsResourceManager.soundAssetsLoader.en_steps, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 2.0f, 3.0f, Map01.entityManiac);
        }
    }

    @Override
    public void addEntities(World world) {
        Map01.entityManiac = null;

        EntityCdItem entityCdItem1 = new EntityCdItem(world, new Vector3f(0.45, -18.2f, -26.92), "cd_world");
        JGems.get().getProxy().addItemInWorlds(entityCdItem1, JGemsResourceManager.renderDataAssets.cd_world);

        EntityCdItem entityCdItem2 = new EntityCdItem(world, new Vector3f(41.0d, -15.1f, -58.5d), "cd_world");
        JGems.get().getProxy().addItemInWorlds(entityCdItem2, JGemsResourceManager.renderDataAssets.cd_world);

        EntityCdItem entityCdItem3 = new EntityCdItem(world, new Vector3f(-42.5, -9.9, -27.94), "cd_world");
        JGems.get().getProxy().addItemInWorlds(entityCdItem3, JGemsResourceManager.renderDataAssets.cd_world);

        int maxI = 8;
        for (int i = 0; i < 7; i++) {
            int r = JGems.random.nextInt(maxI--);
            Vector3f v3 = this.randomCassetteSpawnPoints.get(r);
            this.randomCassetteSpawnPoints.remove(r);
            EntityCassetteItem entityCassette = new EntityCassetteItem(world, v3, "cassette_world");
            JGems.get().getProxy().addItemInWorlds(entityCassette, JGemsResourceManager.renderDataAssets.cassette_world);
        }

        EntityItem entityItem1 = new EntityItem(world, new ItemZippo(), new Vector3f(29.0d, -8.3f, -10.65d), "zippo_world");
        JGems.get().getProxy().addItemInWorlds(entityItem1, JGemsResourceManager.renderDataAssets.zippo_world);

        EntityItem entityItem2 = new EntityItem(world, new ItemEmp(), new Vector3f(new Vector3f(-7.7d, -9.1f, -28.0d)), "emp_world");
        JGems.get().getProxy().addItemInWorlds(entityItem2, JGemsResourceManager.renderDataAssets.emp_world);

        EntityItem entityItem3 = new EntityItem(world, new ItemCrowbar(), new Vector3f(-2.2d, -18.2f, -22.0d), "crowbar_world");
        JGems.get().getProxy().addItemInWorlds(entityItem3, JGemsResourceManager.renderDataAssets.crowbar_world);

        EntityItem entityItem4 = new EntityItem(world, new ItemRadio(), new Vector3f(-5.18, -18.61, -54.62), "radio_world");
        JGems.get().getProxy().addItemInWorlds(entityItem4, JGemsResourceManager.renderDataAssets.radio_world);

        EntitySodaItem entitySodaItem2 = new EntitySodaItem(world, new Vector3f(-24.75d, -15.5f, -45.4d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem2, JGemsResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem3 = new EntitySodaItem(world, new Vector3f(-2.1d, -15.5f, -69.5d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem3, JGemsResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem4 = new EntitySodaItem(world, new Vector3f(25.48d, -9.9f, -46.2d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem4, JGemsResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem5 = new EntitySodaItem(world, new Vector3f(-2.0d, -9.9f, 2.5d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem5, JGemsResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem6 = new EntitySodaItem(world, new Vector3f(-23.6d, -9.9f, -47.5d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem6, JGemsResourceManager.renderDataAssets.soda_world);

        EntitySodaItem entitySodaItem8 = new EntitySodaItem(world, new Vector3f(-22.45d, -9.9f, -31.8d), "soda_world");
        JGems.get().getProxy().addItemInWorlds(entitySodaItem8, JGemsResourceManager.renderDataAssets.soda_world);

        PhysDoor worldDoor = new PhysDoor(world, new Vector3f(9.3f, -19.2d, -52.3f), new Vector3f(0.0f, Math.toRadians(90.0f), 0.0f), "door", true);
        JGems.get().getProxy().addItemInWorlds(worldDoor, JGemsResourceManager.renderDataAssets.door1);

        PhysDoor worldDoor2 = new PhysDoor(world, new Vector3f(-11.4f, -19.2d, -52.3f), new Vector3f(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        JGems.get().getProxy().addItemInWorlds(worldDoor2, JGemsResourceManager.renderDataAssets.door1);

        PhysDoor worldDoor3 = new PhysDoor(world, new Vector3f(8.4f, -19.2d, -24.5f), new Vector3f(0.0f, Math.toRadians(90.0f), 0.0f), "door", true);
        JGems.get().getProxy().addItemInWorlds(worldDoor3, JGemsResourceManager.renderDataAssets.door1);

        PhysDoor worldDoor4 = new PhysDoor(world, new Vector3f(-11.6f, -19.2d, -24.5f), new Vector3f(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        JGems.get().getProxy().addItemInWorlds(worldDoor4, JGemsResourceManager.renderDataAssets.door1);

        PhysDoor worldDoor5 = new PhysDoor(world, new Vector3f(37.5f, -8.8d, -10.7f), new Vector3f(0.0f, Math.toRadians(90.0f), 0.0f), "door", false);
        JGems.get().getProxy().addItemInWorlds(worldDoor5, JGemsResourceManager.renderDataAssets.door1);

        PhysPlank plank = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(-11.275f, -18.05f, -52.5f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0f));
        JGems.get().getProxy().addItemInWorlds(plank, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank1 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(-11.275f, -18.45f, -52.3f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank1, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank3 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(9.175f, -18.05f, -52.5f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0f));
        JGems.get().getProxy().addItemInWorlds(plank3, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank4 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(9.175f, -18.45f, -52.3f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank4, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank6 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(-41.48f, -9.66f, -26.75f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank6, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank7 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(-41.48f, -9.26f, -26.55f), new Vector3f(0.0d, Math.toRadians(90.0f), 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank7, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank8 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(0.65f, -18.15f, -25.96f), new Vector3f(0.0d, 0.0d, 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank8, JGemsResourceManager.renderDataAssets.plank);

        PhysPlank plank9 = new PhysPlank(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, true, 5.0d), 1.5d, new Vector3f(0.575f, -18.55f, -25.96f), new Vector3f(0.0d, 0.0d, 0.0d));
        JGems.get().getProxy().addItemInWorlds(plank9, JGemsResourceManager.renderDataAssets.plank);
    }

    @Override
    public void addBrushes(World world) {
        WorldModeledBrush worldModeledBrush = new WorldModeledBrush(world, JGemsResourceManager.modelAssets.ground, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), "grass");
        JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(JGemsResourceManager.renderDataAssets.ground, JGemsResourceManager.modelAssets.ground));
        worldModeledBrush.setRotation(new Vector3f(-Math.toRadians(90.0f), Math.toRadians(180.0f), 0.0f));
        worldModeledBrush.setScale(0.025d);
        worldModeledBrush.setDebugDrawing(false);

        PhysCube entityPropInfo = new PhysLightCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 1.0d), new Vector3f(1.0d), 0.1d, new Vector3f(33.0f, -12.71f, -56.8f), new Vector3f(0.0d));
        JGems.get().getProxy().addItemInWorlds(entityPropInfo, JGemsResourceManager.renderDataAssets.entityLamp);
        entityPropInfo.getBulletObject().makeStatic();
        PointLight pointLight = (PointLight) new PointLight().setLightColor(new Vector3f(1.0f, 0.0f, 0.0f));
        pointLight.setBrightness(1.0f);
        JGems.get().getProxy().addLight(entityPropInfo, pointLight);

        PointLight pointLight2 = (PointLight) new PointLight(new Vector3f(38.25f, -6.1f, -10.65f)).setLightColor(new Vector3f(1.0f, 0.0f, 0.0f));
        pointLight2.setBrightness(1.0f);
        JGems.get().getProxy().addLight(pointLight2);

        PointLight pointLight3 = (PointLight) new PointLight(new Vector3f(-40.0f, -7.7f, -26.5f)).setLightColor(new Vector3f(1.0f, 0.0f, 0.0f));
        pointLight3.setBrightness(1.0f);
        JGems.get().getProxy().addLight(pointLight3);

        //Graph graph = news Graph();
        //TerrainGraphGenerator terrainGraphGenerator = news TerrainGraphGenerator(world_forward.getDynamicsWorld(), graph);
        //Graph.GVertex vertex = terrainGraphGenerator.startPos(42.0d, 7.5d, -9.5d);
        //terrainGraphGenerator.generate(vertex);
        //Graph.saveInFile(graph, this.levelInfo().getLevelName());
    }

    @Override
    public void addLiquids(World world) {
        Liquid water = new Liquid(new Zone(new Vector3f(-1.5d, -19.0d, -23.0d), new Vector3f(new Vector3f(35.0d, 1.0d, 35.0d))));
        JGems.get().getProxy().addLiquidInWorlds(water, JGemsResourceManager.renderDataAssets.water);
    }

    @Override
    public void addTriggers(World world) {
    }

    @Override
    public void addSounds(World world) {
        JGems.get().getSoundManager().playSoundAt(JGemsResourceManager.soundAssetsLoader.drips, SoundType.WORLD_AMBIENT_SOUND, 1.5f, 5.0f, 4.0f, new Vector3f(-1.5d, -19.0d, -23.0d));
        JGems.get().getSoundManager().playSoundAt(JGemsResourceManager.soundAssetsLoader.map_ambience1, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 3.0f, 3.0f, new Vector3f(20.5d, 10.0d, -25.0d));
    }

    @Override
    public void addNavMesh(World world) {
        Graph graph = Graph.readFromFile(this.getLevelInfo().getLevelName());
        world.setGraph(graph);
    }

    @Override
    public MapInfo getLevelInfo() {
        return new MapInfo(new Vector4f(0.0d, 0.0d, 0.0d, 0.1375d), false, new Vector3f(43.0d, -7.5d, -10.0d), 0.25f, new Vector3f(0.5f, 0.5f, 1.0f), "map01");
    }
     */
}
