package ru.jgems3d.engine.system.map.loaders.custom;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.liquids.Water;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;

import java.io.IOException;

public class DefaultMap implements IMapLoader {
    public DefaultMap() {
    }

    @Override
    public void createMap(GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        BtStaticMeshBody worldModeledBrush = (BtStaticMeshBody) new BtStaticMeshBody(JGemsResourceManager.globalModelAssets.ground2, world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new RenderEntityData(JGemsResourceManager.globalRenderDataAssets.ground, JGemsResourceManager.globalModelAssets.ground2));
        worldModeledBrush.setPosition(new Vector3f(0, -5, 0));

        Water water = new Water(new Zone(new Vector3f(14.0f, -10.0f, 10.0f), new Vector3f(20.0f, 8.0f, 18.0f)));
        JGemsHelper.WORLD.addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
