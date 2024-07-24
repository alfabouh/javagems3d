package ru.alfabouh.jgems3d.engine.system.map.loaders.custom;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.liquids.Water;
import ru.alfabouh.jgems3d.engine.system.JGemsHelper;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.map_sys.save.objects.MapProperties;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.SkyProp;

public class Map02 implements IMapLoader {
    public Map02() {
    }

    @Override
    public void createMap(World world) {
        BtStaticMeshBody worldModeledBrush = (BtStaticMeshBody) new BtStaticMeshBody(JGemsResourceManager.modelAssets.ground2, world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        JGemsHelper.addItemInWorlds(worldModeledBrush, new RenderObjectData(JGemsResourceManager.renderDataAssets.ground, JGemsResourceManager.modelAssets.ground2));
        worldModeledBrush.setPosition(new Vector3f(0, -5, 0));
       // worldModeledBrush.setDebugDrawing(false);

        Water water = new Water(new Zone(new Vector3f(0.0f, -5.0f, 0.0f), new Vector3f(8.0f, 6.0f, 8.0f)));
        JGemsHelper.addLiquidInWorlds(water, JGemsResourceManager.renderDataAssets.water);
    }

    @Override
    public void postLoad(World world) {

    }

    @Override
    public MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
