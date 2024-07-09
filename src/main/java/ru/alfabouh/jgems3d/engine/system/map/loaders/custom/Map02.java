package ru.alfabouh.jgems3d.engine.system.map.loaders.custom;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.common.PhysStaticEntity;
import ru.alfabouh.jgems3d.engine.physics.objects.materials.Materials;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
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
        PhysStaticEntity worldModeledBrush = (PhysStaticEntity) new PhysStaticEntity(world, "grass", RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3f(0.0f), JGemsResourceManager.modelAssets.ground2).setCanBeDestroyed(false);
        JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(JGemsResourceManager.renderDataAssets.ground, JGemsResourceManager.modelAssets.ground2));
        worldModeledBrush.setDebugDrawing(false);
    }

    @Override
    public void postLoad(World world) {

    }

    @Override
    public MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
